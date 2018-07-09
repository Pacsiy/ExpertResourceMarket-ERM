package edu.buaa.server.controller;

import edu.buaa.server.dataLayer.domain.Expert;
import edu.buaa.server.dataLayer.domain.Resource;
import edu.buaa.server.dataLayer.domain.Tag;
import edu.buaa.server.serviceImplement.EnumType.Role;
import edu.buaa.server.serviceImplement.RecommendService;
import edu.buaa.server.serviceImplement.TagService;
import edu.buaa.server.serviceImplement.UserInfoService;
import edu.buaa.server.serviceImplement.resourceService.CommonResourceService;
import edu.buaa.server.util.JSON;
import edu.buaa.server.util.RetData;
import edu.buaa.server.util.constant.R;
import org.json.JSONArray;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/user/recommend")
public class RecommendController {
    private final RecommendService recommendService;
    private final TagService tagService;
    private final UserInfoService userInfoService;
    private final CommonResourceService commonResourceService;

    public RecommendController(RecommendService recommendService, TagService tagService, UserInfoService userInfoService, CommonResourceService commonResourceService) {
        this.recommendService = recommendService;
        this.tagService = tagService;
        this.userInfoService = userInfoService;
        this.commonResourceService = commonResourceService;
    }

    /**
     * 推荐专家
     */
    @RequestMapping(value = "/experts", method = RequestMethod.GET)
    public String getRecommendExperts(@RequestParam("expert_ids") ArrayList<BigInteger> expertList,
                                      HttpSession session) {
        RetData retData;
        try {
            List<Expert> experts = recommendService.getExpertByIDArray(expertList);
            JSONArray data = new JSONArray();
            for (Expert expert : experts) {
                JSON item = new JSON();
                item.put("id", expert.id);
                item.put("name", expert.name);
                item.put("introduction", expert.introduction);
                item.put("institution", expert.constitution);
                item.put("major", expert.major);
                item.put("scholar_id", expert.scholar_id);
                item.put("citation_times", expert.citation_times);
                item.put("article_numbers", expert.article_numbers);
                item.put("h_index", expert.h_index);
                item.put("g_index", expert.g_index);
                List<Tag> tagList = tagService.getExpertTagByExpertID(expert.id.toString());
                JSONArray tagArr = new JSONArray();
                for(Tag tag:tagList){
                    tagArr.put(tag.toJson());
                }
                item.put("fields",tagArr);
                data.put(item);
            }
            retData = new RetData(R.STATE_SUCC, "", data);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 推荐资源
     */
    @RequestMapping(value = "/resources", method = RequestMethod.GET)
    public String getRecommendResources(@RequestParam("resource_ids") ArrayList<BigInteger> expertList,
                                        HttpSession session) {
        RetData retData;
        try {
            List<Resource> resources = recommendService.getResourceByResIDArray(expertList);
            // only have id and type
            JSONArray data = new JSONArray();
            Role role = Role.EXPERT;
            for (Resource r : resources) {
                Resource resource = commonResourceService.getDetailsByIDAndType(role, r.id.toString(), r.type.toString());
                JSON json = new JSON();
                json.put("id", resource.id);
                json.put("title", resource.title);
                json.put("introduction", resource.introduction);
                json.put("created_at", resource.created_at.toString());
                json.put("file_path", resource.file_path);
                json.put("score", resource.score);
                json.put("authors", resource.generateAuthors());
                List<Tag> tagList = tagService.getResTagByResID(resource.id.toString());
                JSONArray tagArray = new JSONArray();
                for (Tag tag : tagList) {
                    tagArray.put(tag.toJson());
                }
                json.put("tags", tagArray);
                json.put("detail", resource.subClassToJson());
                data.put(json);
            }
            retData = new RetData(R.STATE_SUCC, "", data);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

}
