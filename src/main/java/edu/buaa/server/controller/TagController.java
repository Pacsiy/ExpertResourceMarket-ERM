package edu.buaa.server.controller;

import edu.buaa.server.dataLayer.domain.Paper;
import edu.buaa.server.dataLayer.domain.Tag;
import edu.buaa.server.dataLayer.domain.User;
import edu.buaa.server.serviceImplement.TagService;
import edu.buaa.server.util.JSON;
import edu.buaa.server.util.RetData;
import edu.buaa.server.util.constant.R;
import org.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/api")
public class TagController {
    private final TagService tagService;


    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * 获取所有的主题
     */
    @RequestMapping(value = "tags", method = RequestMethod.GET)
    @ResponseBody
    public String getAllTags() {
        RetData retData;
        try {
            List<Tag> tagList = tagService.getAllTags();
            JSONArray tags = new JSONArray();
            for (Tag tag : tagList) {
                JSON item = new JSON();
                item.put("tag_id", tag.id);
                item.put("name", tag.name);
                tags.put(item);
            }
            retData = new RetData(R.STATE_SUCC, "", tags);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 订阅某个主题
     */
    @RequestMapping(value = "user/tags", method = RequestMethod.PUT)
    @ResponseBody
    public String subscribeTag(@RequestBody String request, HttpSession session) {
        JSON req = new JSON(request);
        String tagID = req.getBigInteger("tag_id").toString();
        RetData retData;
        try {
            User user = (User) session.getAttribute(R.USER_INFO);
            tagService.subscribeTag(tagID, user.id.toString());
            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 删除某个主题
     */
    @RequestMapping(value = "user/tags/{tagid}", method = RequestMethod.DELETE)
    @ResponseBody
    public String dismissTag(@PathVariable("tagid") String tagID, HttpSession session) {
        RetData retData;
        try {
            User user = (User) session.getAttribute(R.USER_INFO);
            tagService.dismissTag(tagID, user.id.toString());
            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 获取用户所订阅的主题
     */
    @RequestMapping(value = "user/tags", method = RequestMethod.GET)
    @ResponseBody
    public String getAllUserTags(HttpSession session) {
        RetData retData;
        try {
            User user = (User) session.getAttribute(R.USER_INFO);
            List<Tag> userTags = tagService.getUserTags(user.id.toString());
            JSONArray jsonTags = new JSONArray();
            for (Tag tag : userTags) {
                JSON item = new JSON();
                item.put("tag_id", tag.id);
                item.put("name", tag.name);
                jsonTags.put(item);
            }
            retData = new RetData(R.STATE_SUCC, "", jsonTags);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 获取主题也的相关论文
     */
    @RequestMapping(value = "theme/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getPapersByTag(@PathVariable("id") Integer id) {
        RetData retData;
        try {
            List<Paper> papers = tagService.getPapersByTag(id);
            Tag tag = tagService.getTagByID(id);
            JSONArray paperArr = new JSONArray();
            for (Paper paper : papers) {
                JSON item = new JSON();
                item.put("title", paper.title);
                item.put("id", paper.id);
                item.put("introduction", paper.introduction);
                item.put("publisher", paper.publish);
                item.put("citation_times", paper.citation_times);
                item.put("anual_citation", paper.annual_citation);
                item.put("source", paper.getSource());
                item.put("keywords", paper.keywords);
                item.put("authors", paper.generateAuthors());
                paperArr.put(item);
            }
            JSON data = new JSON();
            data.put("name", tag.name);
            data.put("papers", paperArr);
            retData = new RetData(R.STATE_SUCC, "", data);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }
}
