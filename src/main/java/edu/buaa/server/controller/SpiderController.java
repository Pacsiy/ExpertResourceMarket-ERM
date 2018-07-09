package edu.buaa.server.controller;

import edu.buaa.server.dataLayer.domain.EnumType.CheckState;
import edu.buaa.server.dataLayer.domain.EnumType.ResourceType;
import edu.buaa.server.dataLayer.domain.*;
import edu.buaa.server.serviceImplement.SpiderService;
import edu.buaa.server.util.JSON;
import edu.buaa.server.util.RetData;
import edu.buaa.server.util.constant.R;
import org.json.JSONArray;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/spider")
public class SpiderController {
    // 签名用密钥，需通信双方持有相同密钥
    private static final String key = "gGwk3g8JwUJ7qAWKWNd9dGEX4HjNnshr";
    private final SpiderService spiderService;

    public SpiderController(SpiderService spiderService) {
        this.spiderService = spiderService;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte aSrc : src) {
            String hv = Integer.toHexString(Byte.toUnsignedInt(aSrc));
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private Boolean verify(String url, Map<String, String> queryMap, String text) {
        //序列化后的json
        //String text = "{\"title\": \"hello,world\", \"author\": \"foo\"}";

        //发送时间戳 "1528826226"
        String timestamp = queryMap.get("timestamp");
        long delta = System.currentTimeMillis() / 1000 - Long.valueOf(timestamp);
        //判断 (当前时间戳 - 发送时间戳) < 60秒
        if (delta > 60) {
            return false;
        }

        //随机字符串 "tPENyUS6"
        //判断Redis中是否存在此字符串。如果存在，则报错；如果不存在，则存入Redis，过期时间设置为60s
        String nonce = queryMap.get("nonce");
        Jedis jedis = new Jedis("localhost");
        if (jedis.getSet("nonce-" + nonce, timestamp) != null) {
            return false;
        }
        jedis.expire("nonce-" + nonce, 60);

        String inputSignature = queryMap.get("signature");
        String data = url + timestamp + nonce + text;
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            sha256_HMAC.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));
            byte[] digest = sha256_HMAC.doFinal(data.getBytes());
            String signature = bytesToHexString(digest);
            // 计算出的签名，需要和传入的签名做比较
            return inputSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 上传patent
     */
    @RequestMapping(value = "patent", method = RequestMethod.POST)
    public String uploadPatent(@RequestBody String request, HttpSession session, @RequestParam Map<String, String> queryMap,
                               HttpServletRequest servletRequest) {
        String requestURL = servletRequest.getRequestURL().toString();
        System.out.println(requestURL);
        if (!verify(requestURL, queryMap, request)) {
            return new RetData(R.STATE_FAIL, "认证失败", null).toString();
        }
        JSON req = new JSON(request);
        String url = req.getString("url");
        String html = req.getString("html");
        String title = req.getString("title");
        String intro = req.getString("introduction");
        JSONArray authors = req.getJSONArray("author");

        String appNumber = req.getString("application_number");
        String pubNumber = req.getString("publication_number");
        String appDate = req.getString("application_date");
        String pubDate = req.getString("publication_date");
        String address = req.getString("address");
        JSONArray inventor = req.getJSONArray("inventor");
        String agency = req.getString("agency");
        JSONArray agent = req.getJSONArray("agent");
        String nationalCode = req.getString("national_code");
        String sovereigntyItem = req.getString("sovereignty_item");
        Integer pageSize = req.getIntegerObject("page_number");
        String mainClass = req.getString("patent_main_class");
        JSONArray patentClasses = req.getJSONArray("patent_class");
        String filepath = req.getString("attachment");

        //剥离出专家名字
        Patent patent = new Patent();
        patent.authors = new String[authors.length()];
        ArrayList<Expert> experts = new ArrayList<>();
        for (int i = 0; i < authors.length(); i++) {
            Expert expert = new Expert();
            expert.name = authors.get(i).toString();
            experts.add(expert);
            patent.authors[i] = authors.getString(i);
        }
        //组装patent

        patent.url = url;
        patent.html = html;
        patent.introduction = intro;
        patent.file_path = filepath;
        patent.title = title;
        patent.application_number = appNumber;
        patent.application_date = Timestamp.from(Instant.parse(appDate));
        patent.publication_number = pubNumber;
        patent.publication_date = Timestamp.from(Instant.parse(pubDate));
        patent.address = address;
        //分离创造者
        patent.inventor = new String[inventor.length()];
        for (int i = 0; i < inventor.length(); i++) {
            patent.inventor[i] = inventor.getString(i);
        }
        //代理人
        patent.agency = agency;
        patent.agent = new String[agent.length()];
        for (int i = 0; i < agent.length(); i++) {
            patent.agent[i] = agent.getString(i);
        }

        patent.national_code = nationalCode;
        patent.sovereignty_item = sovereigntyItem;
        patent.page_number = pageSize;
        //分类号
        patent.patent_main_class = mainClass;
        patent.patent_class = new String[patentClasses.length()];
        for (int i = 0; i < patentClasses.length(); i++) {
            patent.patent_class[i] = patentClasses.getString(i);
        }

        patent.type = ResourceType.PATENT;
        patent.file_path = filepath;
        patent.check_state = CheckState.APPROVED;
        //封装完成，下面开始插入数据库
        RetData retData;
        try {
            //fixme 这里也没有领域？
            spiderService.insertExpertsByRes(experts);
            patent.leader_id = experts.get(0).id;
            spiderService.insertResource(patent, null, experts);

            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 上传项目
     */
    @RequestMapping(value = "project", method = RequestMethod.POST)
    public String uploadProject(@RequestBody String request, HttpSession session, @RequestParam Map<String, String> queryMap,
                                HttpServletRequest servletRequest) {
        String requestURL = servletRequest.getRequestURL().toString();
        System.out.println(requestURL);
        if (!verify(requestURL, queryMap, request)) {
            return new RetData(R.STATE_FAIL, "认证失败", null).toString();
        }
        JSON req = new JSON(request);
        String url = req.getString("url");
        String html = req.getString("html");
        String title = req.getString("title");
        String intro = req.getString("introduction");
        JSONArray authors = req.getJSONArray("author");

        String institution = req.getString("institution");
        JSONArray keywords = req.getJSONArray("keywords");
        String[] classifyNumber = Arrays.stream(req.getJSONArray("chinese_library_classification").toList().toArray()).toArray(String[]::new);
        String[] subjectClassifyNumber = Arrays.stream(req.getJSONArray("subject_classification").toList().toArray()).toArray(String[]::new);
        String category = req.getString("category");
        String level = req.getString("level");
        String duration = req.getString("duration");
        String evaluationForm = req.getString("evaluation_form");
        Integer storageYear = req.getIntegerObject("storage_year");
        String filepath = req.getString("attachment");

        //组建project
        Project project = new Project();
        //剥离专家姓名
        project.authors = new String[authors.length()];
        ArrayList<Expert> experts = new ArrayList<>();
        for (int i = 0; i < authors.length(); i++) {
            project.authors[i] = authors.getString(i);

            Expert expert = new Expert();
            expert.name = authors.getString(i);
            experts.add(expert);
        }

        project.title = title;
        project.check_state = CheckState.APPROVED;
        project.introduction = intro;
        project.file_path = filepath;
        project.type = ResourceType.PROJECT;

        project.institution = institution;
        //分离keywords
        project.keywords = new String[keywords.length()];
        for (int i = 0; i < keywords.length(); i++) {
            project.keywords[i] = keywords.getString(i);
        }
        project.chinese_library_classification = classifyNumber;
        project.subject_classification = subjectClassifyNumber;
        project.category = category;
        project.level = level;
        project.duration = duration;
        project.evaluation_form = evaluationForm;
        project.storage_year = storageYear;
        project.file_path = filepath;
        project.html = html;
        project.url = url;

        //封装完成，下面开始插入数据库
        RetData retData;
        try {
            //fixme 这里没有领域？
            spiderService.insertExpertsByRes(experts);
            project.leader_id = experts.get(0).id;
            spiderService.insertResource(project, null, experts);
            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 上传论文
     */
    @RequestMapping(value = "paper", method = RequestMethod.POST)
    public String uploadPaper(@RequestBody String request, HttpSession session, @RequestParam Map<String, String> queryMap,
                              HttpServletRequest servletRequest) {
        String requestURL = servletRequest.getRequestURL().toString();
        System.out.println(requestURL);
        if (!verify(requestURL, queryMap, request)) {
            return new RetData(R.STATE_FAIL, "认证失败", null).toString();
        }
        JSON req = new JSON(request);
        String url = req.getString("url");
        String html = req.getString("html");
        String title = req.getString("title");
        String intro = req.getString("introduction");
        JSONArray authors = req.getJSONArray("author");

        String publish = req.getString("publish");
        JSONArray source = req.getJSONArray("source");
        Integer citationTimes = req.has("citation_times") ? req.getIntegerObject("citation_times") : null;
        JSONArray classification = req.getJSONArray("classification");
        JSONArray field = req.getJSONArray("field");
        JSONArray keywords = req.getJSONArray("keywords");
        JSONArray annualCitation = req.getJSONArray("annual_citation");
        //组建paper
        Paper paper = new Paper();

        //剥离专家姓名
        paper.authors = new String[authors.length()];
        ArrayList<Expert> experts = new ArrayList<>();
        for (int i = 0; i < authors.length(); i++) {
            Expert expert = new Expert();
            expert.name = authors.getString(i);
            experts.add(expert);
            paper.authors[i] = authors.getString(i);
        }

        //得到关键字
        paper.keywords = new String[keywords.length()];
        for (int i = 0; i < keywords.length(); i++) {
            paper.keywords[i] = keywords.getString(i);
        }

        paper.title = title;
        paper.check_state = CheckState.APPROVED;
        paper.introduction = intro;
        paper.file_path = url;
        paper.type = ResourceType.PAPER;
        paper.html = html;
        paper.url = url;
        paper.citation_times = citationTimes;
        paper.classification = Arrays.stream(classification.toList().toArray()).toArray(String[]::new);
        paper.publish = publish;

        //分离来源
        paper.source = new String[source.length()];
        for (int i = 0; i < source.length(); i++) {
            paper.source[i] = source.getJSONObject(i).toString();
        }
        //分离年度引用
        paper.annual_citation = annualCitation;

        List<Tag> tagList = new ArrayList<>();
        for (Object tagName : field.toList()) {
            Tag tag = new Tag();
            tag.name = (String) tagName;
            tagList.add(tag);
        }
        //封装完成，下面开始插入数据库
        RetData retData;
        try {
            for (Tag tag : tagList) {
                spiderService.insertTag(tag);
            }
            spiderService.insertExpertsByRes(experts);
            paper.leader_id = experts.get(0).id;
            spiderService.insertResource(paper, tagList, experts);
            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }


    /**
     * 上传专家
     */
    @RequestMapping(value = "expert", method = RequestMethod.POST)
    public String uploadExpert(@RequestBody String request, HttpSession session, @RequestParam Map<String, String> queryMap,
                               HttpServletRequest servletRequest) {
        String requestURL = servletRequest.getRequestURL().toString();
        System.out.println(requestURL);
        if (!verify(requestURL, queryMap, request)) {
            return new RetData(R.STATE_FAIL, "认证失败", null).toString();
        }
        JSON re = new JSON(request);
        Expert expert = new Expert();
        expert.scholar_id = re.getString("ScholarID");
        expert.name = re.getString("name");
        expert.constitution = re.getString("institute");
        expert.citation_times = re.getIntegerObject("citation_times");
        expert.article_numbers = re.getIntegerObject("article_numbers");
        expert.h_index = re.getIntegerObject("h_index");
        expert.g_index = re.getIntegerObject("g_index");
        JSONArray field = re.getJSONArray("field");
        expert.history_article_numbers = re.getJSONArray("history_article_numbers");
        expert.history_citation_times = re.getJSONArray("history_citation_times");
        //封装Tag
        ArrayList<Tag> tagList = new ArrayList<>();
        for (int i = 0; i < field.length(); i++) {
            Tag tag = new Tag();
            tag.name = field.getString(i);
            tagList.add(tag);
        }
        //封装完成，下面开始插入数据库，首先插入tag,获得ID。
        RetData retData;
        try {
            for (Tag tag : tagList) {
                spiderService.insertTag(tag);
            }
            spiderService.insertExpert(expert, tagList);
            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

}
