package edu.buaa.server.controller;

import edu.buaa.server.dataLayer.domain.Expert;
import edu.buaa.server.dataLayer.domain.Resource;
import edu.buaa.server.dataLayer.domain.Tag;
import edu.buaa.server.dataLayer.domain.User;
import edu.buaa.server.serviceImplement.EnumType.Role;
import edu.buaa.server.serviceImplement.ExpertInfoService;
import edu.buaa.server.serviceImplement.TagService;
import edu.buaa.server.serviceImplement.UserInfoService;
import edu.buaa.server.serviceImplement.resourceService.CommonResourceService;
import edu.buaa.server.serviceImplement.resourceService.ExpertResourceService;
import edu.buaa.server.util.IntegerWrapper;
import edu.buaa.server.util.JSON;
import edu.buaa.server.util.RetData;
import edu.buaa.server.util.constant.R;
import org.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("api/expert/{id}")
public class ExpertController {
    private final ExpertInfoService expertInfoService;
    private final TagService tagService;
    private final ExpertResourceService expertResourceService;
    private final CommonResourceService commonResourceService;
    private final UserInfoService userInfoService;

    public ExpertController(ExpertInfoService expertInfoService, TagService tagService, ExpertResourceService expertResourceService, CommonResourceService commonResourceService, UserInfoService userInfoService) {
        this.expertInfoService = expertInfoService;
        this.tagService = tagService;
        this.expertResourceService = expertResourceService;
        this.commonResourceService = commonResourceService;
        this.userInfoService = userInfoService;
    }

    /**
     * 获得专家信息
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String getExpertInfoByExpertID(@PathVariable("id") String expertID, HttpSession session) {
        RetData retData;
        JSON info = null;
        try {
            Expert expert = expertInfoService.getExpertInfo(expertID);
            if (expert == null) {
                throw R.EXPERT_NOT_EXIST;
            }
            info = expert.toJson();
            List<Tag> tagList = tagService.getExpertTagByExpertID(expertID);
            JSONArray tagArray = new JSONArray();
            for (Tag tag : tagList) {
                tagArray.put(tag.toJson());
            }
            info.put("tags", tagArray);
            if (expert.user_id != null) {
                User user = userInfoService.getUserByID(expert.user_id.toString());
                info.put("avator", user == null ? R.DEFAULT_AVATOR : user.avator);
                info.put("user_name", user == null ? "" : user.nickname);
            } else {
                info.put("avator", R.DEFAULT_AVATOR);
                info.put("user_name", "");
            }
            retData = new RetData(R.STATE_SUCC, "", info);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, info);
        }
        return retData.toString();
    }

    /**
     * 修改专家信息
     */
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public String modifyExpertInfo(@RequestBody String request, @PathVariable("id") String expertID, HttpSession session) {
        RetData retData;
        JSON req = new JSON(request);
        String phone = req.getString("phone");
        String email = req.getString("email");
        String institution = req.getString("institution");
        String intro = req.getString("introduction");
        String backImg = req.getString("background_img");
        JSONArray tag = req.getJSONArray("tags");
        ArrayList<BigInteger> tagList = new ArrayList<>();
        for (int i = 0; i < tag.length(); i++) {
            tagList.add(tag.getBigInteger(i));
        }
        try {
            Expert expert = (Expert) session.getAttribute(R.EXPERT_INFO);
            expertInfoService.modifyExpertInfo(expert.id.toString(), phone, email, institution, intro, backImg, tagList);
            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 用户角度 获取专家资源
     */
    @RequestMapping(value = "/resources", method = RequestMethod.GET)
    @ResponseBody
    public String getExpertResources(@PathVariable("id") String id, @RequestParam("index") String index,
                                     @RequestParam("size") String size) {
        RetData retData;
        try {
            IntegerWrapper pageNum = new IntegerWrapper();
            List<Resource> ans = expertResourceService.getResFromUserAspect(index, size, pageNum, id);
            JSONArray resArray = new JSONArray();
            for (Resource res : ans) {
                JSON resource = res.toJson();
                resource.put("score", res.score);
                Role role = Role.EXPERT;
                Resource type = commonResourceService.getDetailsByIDAndType(role, res.id.toString(), resource.getString("type").toLowerCase());
                List<Tag> tagList = tagService.getResTagByResID(res.id.toString());
                JSONArray tagArray = new JSONArray();
                for (Tag tag : tagList) {
                    tagArray.put(tag.toJson());
                }
                resource.put("tags", tagArray);
                resource.put("detail", type.toJson());
                resArray.put(resource);
            }
            JSON data = new JSON();
            data.put("page_num", pageNum.value);
            data.put("resources", resArray);
            retData = new RetData(R.STATE_SUCC, "", data);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }
}
