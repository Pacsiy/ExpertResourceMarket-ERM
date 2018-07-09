package edu.buaa.server.controller;

import edu.buaa.server.dataLayer.domain.*;
import edu.buaa.server.serviceImplement.*;
import edu.buaa.server.util.IntegerWrapper;
import edu.buaa.server.util.JSON;
import edu.buaa.server.util.RetData;
import edu.buaa.server.util.constant.R;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class SearchController {
    private final SearchService searchService;
    private final ExpertInfoService expertInfoService;
    private final UserInfoService userInfoService;
    private final TagService tagService;
    private final StatisticsService statisticsService;

    public SearchController(SearchService searchService, ExpertInfoService expertInfoService, UserInfoService userInfoService, TagService tagService, StatisticsService statisticsService) {
        this.searchService = searchService;
        this.expertInfoService = expertInfoService;
        this.userInfoService = userInfoService;
        this.tagService = tagService;
        this.statisticsService = statisticsService;
    }

    /**
     * 管理员搜索
     */
    @RequestMapping(value = "/api/admin/search", method = RequestMethod.GET)
    public String managerSearch(@Nullable @RequestParam("keyword") String keyword, @Nullable @RequestParam(value = "type", required = false) String type,
                                @RequestParam(value = "index") String index, @RequestParam("size") String size,
                                HttpSession session) {
        IntegerWrapper userNum = new IntegerWrapper();
        IntegerWrapper expertNum = new IntegerWrapper();
        RetData retData;
        try {
            if (session.getAttribute(R.ADMIN_INFO) == null) throw R.USER_NOT_LOGIN;
            if (keyword == null) keyword = "*";
            List<Object> ansList = searchService.managerSearch(keyword, type, index, size, userNum, expertNum);
            JSONArray userArray = new JSONArray();
            JSONArray expertArray = new JSONArray();
            for (Object obj : ansList) {
                if (obj instanceof User) {
                    User user = (User) obj;
                    JSON userJson = user.toJsonInManagerSearch();
                    Expert expert = expertInfoService.UserToExpert(user.id.toString());
                    userJson.put("expert_id", expert == null ? -1 : expert.id);
                    userArray.put(userJson);
                }
                if (obj instanceof Expert) {
                    Expert expert = (Expert) obj;
                    JSON expertJson = expert.toJsonManageSearch();
                    if (expert.user_id != null) {
                        User user = userInfoService.getUserByID(expert.user_id.toString());
                        expertJson.put("avator", user.avator);
                    }else {
                        expertJson.put("avator",R.DEFAULT_AVATOR);
                    }
                    expertArray.put(expertJson);
                }
            }
            JSON page = new JSON();
            page.put("users", userNum.value);
            page.put("experts", expertNum.value);
            JSON data = new JSON();
            data.put("users", userArray);
            data.put("experts", expertArray);
            data.put("page_nums", page);
            retData = new RetData(R.STATE_SUCC, "", data);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }


    /**
     * 普通用户搜索
     */
    @RequestMapping(value = "/api/search", method = RequestMethod.GET)
    public String search(@RequestParam("keyword") String keywords, @RequestParam("index") String index,
                         @RequestParam("size") String size, @RequestParam("type") String type) {
        String keyword = keywords.replace(" ", "\\ ");
        RetData retData;
        try {
            IntegerWrapper pageNum = new IntegerWrapper();
            JSON ret = new JSON();
            JSONArray data = new JSONArray();
            switch (type) {
                case "papers":
                    List<Paper> papers = searchService.searchPapers(keyword, index, size, pageNum);
                    for (Paper paper : papers) {
                        JSON res = paper.detailToJson();
                        res.put("id", paper.id);
                        List<Tag> tagList = tagService.getResTagByResID(paper.id.toString());
                        JSONArray tagArr = new JSONArray();
                        for (Tag tag : tagList) {
                            tagArr.put(tag.toJson());
                        }
                        res.put("fields", tagArr);
                        res.put("citations", paper.citation_times);
                        res.put("public", paper.publish);
                        res.put("keywords", new JSONArray(paper.keywords));
                        data.put(res);
                    }
                    ret.put("papers", data);
                    break;
                case "patents":
                    List<Patent> patents = searchService.searchPatents(keyword, index, size, pageNum);
                    for (Patent patent : patents) {
                        JSON res = patent.detailToJson();
                        res.put("id", patent.id);
                        res.put("patent_main_class", patent.patent_main_class);
                        data.put(res);
                    }
                    ret.put("patents", data);
                    break;
                case "projects":
                    List<Project> projects = searchService.searchProjects(keyword, index, size, pageNum);
                    for (Project project : projects) {
                        JSON res = project.detailToJson();
                        res.put("id", project.id);
                        res.put("keywords", new JSONArray(project.keywords));
                        res.put("category", project.category);
                        data.put(res);
                    }
                    ret.put("projects", data);
                    break;
                default:
                    List<Expert> experts = searchService.searchExperts(keyword, index, size, pageNum);
                    for (Expert expert : experts) {
                        JSON exp = new JSON();
                        exp.put("expert_id", expert.id);
                        exp.put("name", expert.name);
                        exp.put("state", expert.state);
                        exp.put("institution", expert.constitution);
                        exp.put("email", expert.email);
                        exp.put("major", expert.email);
                        List<Tag> tagList = tagService.getExpertTagByExpertID(expert.id.toString());
                        JSONArray tagArr = new JSONArray();
                        for (Tag tag : tagList) {
                            tagArr.put(tag.toJson());
                        }
                        exp.put("fields", tagArr);
                        if (expert.user_id == null) {
                            exp.put("avator", R.DEFAULT_AVATOR);
                            data.put(exp);
                            continue;
                        }
                        User user = userInfoService.getUserByID(expert.user_id.toString());
                        exp.put("avator", user.avator);
                        data.put(exp);
                    }
                    ret.put("experts", data);
                    break;
            }

            ret.put("page_num", pageNum.value);
            ret.put("total_num", searchService.getTotal(keyword));

            List<Tag> tags = searchService.getTagByTagName("%".concat(keyword).concat("%"));
            JSONArray related = new JSONArray();
            if (tags.size() != 0) {
                for (Tag tag : tags) {
                    related.put(tag.toJson());
                    List<Tag> tagList = statisticsService.getSimilarTags(Integer.valueOf(tag.id.toString()));
                    for (Tag tag1 : tagList) {
                        related.put(tag1.toJson());
                    }
                }
                ret.put("related", related);
            } else {
                ret.put("related", related);
            }
            retData = new RetData(R.STATE_SUCC, "", ret);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }
}
