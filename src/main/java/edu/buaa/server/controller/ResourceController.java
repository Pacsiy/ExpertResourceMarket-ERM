package edu.buaa.server.controller;

import edu.buaa.server.dataLayer.domain.*;
import edu.buaa.server.dataLayer.domain.EnumType.ResourceType;
import edu.buaa.server.serviceImplement.EnumType.Role;
import edu.buaa.server.serviceImplement.TagService;
import edu.buaa.server.serviceImplement.UserInfoService;
import edu.buaa.server.serviceImplement.resourceService.CommonResourceService;
import edu.buaa.server.serviceImplement.resourceService.ExpertResourceService;
import edu.buaa.server.serviceImplement.resourceService.UserResourceService;
import edu.buaa.server.util.IntegerWrapper;
import edu.buaa.server.util.JSON;
import edu.buaa.server.util.RetData;
import edu.buaa.server.util.constant.R;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api")
public class ResourceController {
    private final UserResourceService userResourceService;
    private final ExpertResourceService expertResourceService;
    private final CommonResourceService commonResourceService;
    private final TagService tagService;
    private final UserInfoService userInfoService;

    public ResourceController(UserResourceService userResourceService, ExpertResourceService expertResourceService, CommonResourceService commonResourceService, TagService tagService, UserInfoService userInfoService) {
        this.userResourceService = userResourceService;
        this.expertResourceService = expertResourceService;
        this.commonResourceService = commonResourceService;
        this.tagService = tagService;
        this.userInfoService = userInfoService;
    }

    /**
     * 用户对资源的评价
     */
    @RequestMapping(value = "/resource/{id}/comment", method = RequestMethod.POST)
    @ResponseBody
    public String commentRes(@RequestBody String request, @PathVariable("id") BigInteger resID, HttpSession session) {
        RetData retData;
        try {
            User user = (User) session.getAttribute(R.USER_INFO);
            JSON jsonObject = new JSON(request);
            String content = jsonObject.getString("content");
            content = Jsoup.clean(content, Whitelist.relaxed());
            int score = jsonObject.getIntegerObject("score");
            String ret = userResourceService.commentRes(score, content, user.id.toString(), resID.toString());
            retData = new RetData(ret, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 专家添加资源
     */
    @RequestMapping(value = "/resource", method = RequestMethod.POST)
    @ResponseBody
    public String addResource(@RequestBody String request, HttpSession session) {
        JSON jsonObject = new JSON(request);
        Resource resource = new Resource();

        resource.title = jsonObject.getString("name");
        resource.type = ResourceType.valueOf(jsonObject.getString("type").toUpperCase());
        resource.introduction = jsonObject.getString("introduction");
        resource.file_path = jsonObject.getString("file");
        Expert expert = (Expert) session.getAttribute(R.EXPERT_INFO);
        resource.member_id_list = new Long[]{expert.id.longValue()};
        resource.authors = new String[]{expert.name};
        JSONArray array = jsonObject.getJSONArray("tags");
        ArrayList<BigInteger> tagList = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            tagList.add(array.getBigInteger(i));
        }
        RetData retData;
        try {
            resource.leader_id = expert.id;
            expertResourceService.addResource(resource, tagList);
            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 专家删除资源
     */
    @RequestMapping(value = "/resource/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteResource(@PathVariable("id") String id, HttpSession session) {
        RetData retData;
        try {
            Expert expert = (Expert) session.getAttribute(R.EXPERT_INFO);
            expertResourceService.deleteResource(id, expert.id.toString());
            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 专家修改资源信息
     */
    @RequestMapping(value = "/resource/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public String modifyResource(@RequestBody String request, @PathVariable("id") String id, HttpSession session) {
        RetData retData;
        try {
            Resource resource = new Resource();
            JSON jsonObject = new JSON(request);
            resource.id = new BigInteger(id);
            resource.rent_price = jsonObject.getDoubleObject("rent_price");
            resource.buy_price = jsonObject.getDoubleObject("buy_price");
            resource.is_user_visible = jsonObject.getBooleanObject("enable");
            resource.introduction = jsonObject.getString("introduction");
            resource.file_path = jsonObject.getString("file");
            resource.state = jsonObject.getBigInteger("state");
            Expert expert = (Expert) session.getAttribute(R.EXPERT_INFO);
            resource.leader_id = expert.id;
            JSONArray tagIDs = jsonObject.getJSONArray("tags");
            ArrayList<BigInteger> tagIDList = new ArrayList<>();
            for (Object tagID : tagIDs) {
                tagIDList.add(new BigInteger(tagID.toString()));
            }
            expertResourceService.modifyResource(tagIDList, resource);
            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 专家获取属于自己的资源
     */
    @RequestMapping(value = "user/resources", method = RequestMethod.GET)
    @ResponseBody
    public String getResources(HttpSession session) {
        RetData retData;
        try {
            Expert expert = (Expert) session.getAttribute(R.EXPERT_INFO);
            List<Resource> ans = expertResourceService.getOwnResource(expert.id.toString());
            JSONArray resArray = new JSONArray();
            for (Resource res : ans) {
                JSON resource = res.toJson();
                Role role = Role.EXPERT;
                Resource type = commonResourceService.getDetailsByIDAndType(role, res.id.toString(),
                        resource.getString("type"));
                List<Tag> tagList = tagService.getResTagByResID(res.id.toString());
                JSONArray tagArray = new JSONArray();
                for (Tag tag : tagList) {
                    tagArray.put(tag.toJson());
                }
                resource.put("tags", tagArray);
                resource.put("detail", type.toJson());
                resArray.put(resource);
            }
            retData = new RetData(R.STATE_SUCC, "", resArray);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }


    /**
     * 查看资源的详细信息
     */
    @RequestMapping(value = "/resource/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getResourceDetails(@PathVariable("id") String id) {
        RetData retData;
        try {
            Role role = Role.USER;
            Resource resource = commonResourceService.getResourceByID(role, id);
            if (resource.type == null) throw new NumberFormatException("该资源不存在");
            List<Tag> tagList = tagService.getResTagByResID(id);
            JSONArray tagArray = new JSONArray();
            for (Tag tag : tagList) {
                tagArray.put(tag.toJson());
            }
            JSON data = resource.detailToJson();
            JSON detail = resource.subClassToJson();
            data.put("fields", tagArray);
            data.put("detail", detail);
            retData = new RetData(R.STATE_SUCC, "", data);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 获取资源的评论
     */
    @RequestMapping(value = "resource/{id}/comments", method = RequestMethod.GET)
    @ResponseBody
    public String getCommentsByResID(@PathVariable("id") String id,
                                     @RequestParam("index") String index,
                                     @RequestParam("size") String size) {
        RetData retData;
        try {
            IntegerWrapper pageNum = new IntegerWrapper();
            Role role = Role.USER;
            List<Comment> commentList = commonResourceService.getCommentsByID(role, id, index, size, pageNum);
            JSONArray commentArray = new JSONArray();
            for (Comment comment : commentList) {
                User user = userInfoService.getUserByID(comment.user_id.toString());
                JSON data = comment.toJson();
                data.put("user_name", user.nickname);
                data.put("avator", user.avator);
                commentArray.put(data);
            }
            JSON info = new JSON();
            info.put("comments", commentArray);
            info.put("page_num", pageNum.value);
            retData = new RetData(R.STATE_SUCC, "", info);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }
}
