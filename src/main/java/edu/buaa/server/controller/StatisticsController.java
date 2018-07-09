package edu.buaa.server.controller;

import edu.buaa.server.dataLayer.domain.Expert;
import edu.buaa.server.dataLayer.domain.HotTagsBean;
import edu.buaa.server.dataLayer.domain.Paper;
import edu.buaa.server.dataLayer.domain.Tag;
import edu.buaa.server.serviceImplement.ExpertInfoService;
import edu.buaa.server.serviceImplement.StatisticsService;
import edu.buaa.server.util.JSON;
import edu.buaa.server.util.RetData;
import edu.buaa.server.util.constant.R;
import org.json.JSONArray;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;
    private final ExpertInfoService expertInfoService;

    public StatisticsController(StatisticsService statisticsService, ExpertInfoService expertInfoService) {
        this.statisticsService = statisticsService;
        this.expertInfoService = expertInfoService;
    }

    /**
     * 交易热度
     */
    @RequestMapping(value = "tradeNumber", method = RequestMethod.GET)
    public String getTop5Experts() {
        RetData retData;
        try {
            List<Expert> experts = statisticsService.getTop5Experts();
            JSONArray data = new JSONArray();
            for (Expert expert : experts) {
                List<Integer> ans = statisticsService.getRecent7DaysTradeByExpertID(expert.id.toString());
                JSONArray num = new JSONArray(ans);
                JSON item = new JSON();
                item.put("name", expert.name);
                item.put("value", num);
                data.put(item);
            }
            retData = new RetData(R.STATE_SUCC, "", data);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 获取主题热度
     */
    @RequestMapping(value = "tagHeat", method = RequestMethod.GET)
    public String getTop10Tags() {
        RetData retData;
        try {
            List<HotTagsBean> tagList = statisticsService.getTop10Tags();
            JSONArray data = new JSONArray();
            for (HotTagsBean tag : tagList) {
                data.put(tag.toJson());
            }
            retData = new RetData(R.STATE_SUCC, "", data);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 确定某个主题的专家关系
     */
    @RequestMapping(value = "/theme_experts/{theme_id}", method = RequestMethod.GET)
    public String getExpertTagRelation(HttpSession session, @PathVariable("theme_id") String id) {
        RetData retData;
        try {
            Tag tag = new Tag();
            List<Expert> experts = statisticsService.getRelationExpertByTag(id, tag);
            ArrayList<String> expList = new ArrayList<>();
            for (Expert expert : experts) {
                expList.add(expert.name);
            }
            JSONArray data = new JSONArray(expList);
            JSON ret = new JSON();
            ret.put("theme", tag.name);
            ret.put("experts", data);
            retData = new RetData(R.STATE_SUCC, "", ret);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 获得相关论文
     */
    @RequestMapping(value = "relatedpapers/{id}", method = RequestMethod.GET)
    public String getRelationPaper(@PathVariable("id") Integer id) {
        RetData retData;
        try {
            List<Paper> papers = statisticsService.getRelationPapers(id.toString());
            JSONArray data = new JSONArray();
            for (Paper paper : papers) {
                JSON item = new JSON();
                JSON paperItem = new JSON();
                paperItem.put("name", paper.title);
                paperItem.put("id", paper.id);
                paperItem.put("publisher", paper.publish);
                paperItem.put("citation", paper.citation_times);
                paperItem.put("keywords", paper.keywords);
                item.put("papers", paperItem);
                item.put("authors", paper.generateAuthors());
                data.put(item);
            }
            retData = new RetData(R.STATE_SUCC, "", data);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 获取某个主题相关top5专家被引用量年度变化
     */
    @RequestMapping(value = "theme/expert_quote/{theme_id}", method = RequestMethod.GET)
    public String getTop5ExpertsByCitationsAndTagID(@PathVariable("theme_id") Integer id) {
        RetData retData;
        try {
            List<Expert> experts = statisticsService.getTop5ExpertsByTagID(id);
            JSONArray data = new JSONArray();
            for (Expert expert : experts) {
                JSON item = new JSON();
                item.put("name", expert.name);
                item.put("value", expert.history_citation_times);
                data.put(item);
            }
            retData = new RetData(R.STATE_SUCC, "", data);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 获取前五专家被引用量变化图
     */
    @RequestMapping(value = "experts_references", method = RequestMethod.GET)
    public String getTop5ExpertsByCitations() {
        RetData retData;
        try {
            List<Expert> experts = statisticsService.getTop5ExpertsByCitations();
            JSONArray data = new JSONArray();
            for (Expert expert : experts) {
                JSON item = new JSON();
                item.put("value", expert.history_citation_times);
                item.put("name", expert.name);
                data.put(item);
            }
            retData = new RetData(R.STATE_SUCC, "", data);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 某个专家的被引用量变化
     */
    @RequestMapping(value = "reference_expert/{expert_id}", method = RequestMethod.GET)
    public String getExpertAnnualCitations(@PathVariable("expert_id") Integer id) {
        RetData retData;
        try {
            Expert expert = expertInfoService.getExpertInfo(id.toString());
            if (expert == null) throw R.EXPERT_NOT_EXIST;
            JSON data = new JSON();
            data.put("name", expert.name);
            data.put("value", expert.history_citation_times);
            retData = new RetData(R.STATE_SUCC, "", data);
        } catch (Exception e) {
            retData = new RetData(R.STATE_SUCC, e, null);
            e.printStackTrace();
        }
        return retData.toString();
    }

    /**
     * 获取某个论文近多年被引用量变化
     */
    @RequestMapping(value = "reference_resource/{resource_id}", method = RequestMethod.GET)
    public String getPaperAnnualCitations(@PathVariable("resource_id") Integer resID) {
        RetData retData;
        try {
            Paper paper = statisticsService.getPaperByID(resID);
            if (paper == null) throw R.RESOURCE_NOT_EXIST;
            JSON data = new JSON();
            data.put("name", paper.title);
            data.put("value", paper.annual_citation);
            retData = new RetData(R.STATE_SUCC, "", data);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 获得主题的领域集合
     */
    @RequestMapping(value = "relatedthemes/{id}", method = RequestMethod.GET)
    public String getSimilarFields(@PathVariable("id") Integer id) {
        RetData retData;
        try {
            List<Tag> tagList = statisticsService.getSimilarTags(id);
            JSONArray tagArray = new JSONArray();
            for (Tag tag : tagList) {
                tagArray.put(tag.toJson());
            }
            retData = new RetData(R.STATE_SUCC, "", tagArray);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 获得最热的十篇论文
     */
    @RequestMapping(value = "hottest_paper", method = RequestMethod.GET)
    public String getHottestPapers() {
        RetData retData;
        try {
            List<Paper> papers = statisticsService.getHottestPapers();
            JSONArray data = new JSONArray();
            for (Paper paper : papers) {
                JSON item = new JSON();
                item.put("name", paper.title);
                item.put("id", paper.id);
                item.put("value", paper.citation_times);
                data.put(item);
            }
            retData = new RetData(R.STATE_SUCC, "", data);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 获取最新的十篇论文
     */
    @RequestMapping(value = "newest_paper", method = RequestMethod.GET)
    public String getNewestPapers() {
        RetData retData;
        try {
            List<Paper> papers = statisticsService.getNewestPapers();
            JSONArray data = new JSONArray();
            for (Paper paper : papers) {
                JSON item = new JSON();
                item.put("name", paper.title);
                item.put("time", paper.created_at.toString());
                item.put("id", paper.id);
                data.put(item);
            }
            retData = new RetData(R.STATE_SUCC, "", data);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }
}
