package edu.buaa.server.controller;

import edu.buaa.server.dataLayer.domain.*;
import edu.buaa.server.serviceImplement.ExpertInfoService;
import edu.buaa.server.serviceImplement.ManagerService;
import edu.buaa.server.serviceImplement.UserInfoService;
import edu.buaa.server.serviceImplement.resourceService.ManagerResourceService;
import edu.buaa.server.util.IntegerWrapper;
import edu.buaa.server.util.JSON;
import edu.buaa.server.util.RetData;
import edu.buaa.server.util.constant.R;
import org.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/api/admin")
public class ManagerController {
    private final ManagerService managerService;
    private final ManagerResourceService resourceService;
    private final UserInfoService userInfoService;
    private final ExpertInfoService expertInfoService;

    public ManagerController(ManagerService managerService, ManagerResourceService resourceService, UserInfoService userInfoService, ExpertInfoService expertInfoService) {
        this.managerService = managerService;
        this.resourceService = resourceService;
        this.userInfoService = userInfoService;
        this.expertInfoService = expertInfoService;
    }

    /**
     * 管理员登录
     */
    @RequestMapping(value = "session", method = RequestMethod.POST)
    @ResponseBody
    public String login(@RequestBody String request, HttpSession session) {
        JSON req = new JSON(request);
        String phone = req.getString("cellphone");
        String password = req.getString("password");
        RetData retData;
        try {
            Manager manager = managerService.login(phone, password);
            if (manager != null) {
                session.setAttribute(R.ADMIN_INFO, manager);
                JSON info = new JSON();
                info.put("email", manager.email);
                retData = new RetData(R.STATE_SUCC, "", info);
            } else {
                throw new RuntimeException("登录失败");
            }
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 查看后台留言
     */
    @RequestMapping(value = "messages", method = RequestMethod.GET)
    @ResponseBody
    public String getBackgroundMessages(
            @RequestParam(value = "index") String index,
            @RequestParam(value = "size") String size, HttpSession session) {
        RetData retData;
        try {
            if (session.getAttribute(R.ADMIN_INFO) == null) throw R.USER_NOT_LOGIN;
            IntegerWrapper pageNum = new IntegerWrapper();
            List<Message> messages = managerService.checkBackgroundMessages(index, size, pageNum);
            JSONArray messageWrapped = new JSONArray();
            for (Message message : messages) {
                User user = userInfoService.getUserByID(message.user_id.toString());
                JSON mess = message.toJson();
                mess.put("user_name", user.nickname);
                messageWrapped.put(mess);
            }
            JSON info = new JSON();
            info.put("messages", messageWrapped);
            info.put("page_num", pageNum.value);
            retData = new RetData(R.STATE_SUCC, "", info);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 查看交易记录
     */
    @RequestMapping(value = "trade", method = RequestMethod.GET)
    @ResponseBody
    public String getTradeRecord(@RequestParam("index") String index, @RequestParam("size") String size, HttpSession session) {
        RetData retData;
        try {
            if (session.getAttribute(R.ADMIN_INFO) == null) throw R.USER_NOT_LOGIN;
            IntegerWrapper pageNum = new IntegerWrapper();
            List<TradeResource> tradeList = managerService.getTradeRecord(index, size, pageNum);
            JSONArray data = new JSONArray();
            for (TradeResource trade : tradeList) {
                User buyer = userInfoService.getUserByID(trade.buyer_id.toString());
                Expert seller = expertInfoService.getExpertInfo(trade.seller_id.toString());
                if (seller == null) throw R.USER_NOT_EXIST;
                Resource res = resourceService.getResourceByID(trade.resource_id.toString());
                JSON info = trade.toJsonInAdmin();
                info.put("file_path", res.file_path);
                info.put("resource_name", res.title);
                info.put("seller_name", seller.name);
                info.put("buyer_name", buyer.nickname);
                info.put("resource_type", res.type.toString().toLowerCase());
                data.put(info);
            }
            JSON ret = new JSON();
            ret.put("records", data);
            ret.put("page_num", pageNum.value);
            retData = new RetData(R.STATE_SUCC, "", ret);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 查看专家申请
     */
    @RequestMapping(value = "expapplys", method = RequestMethod.GET)
    @ResponseBody
    public String getAllExpertApplies(@RequestParam("index") String index,
                                      @RequestParam("size") String size, HttpSession session) {
        RetData retData;
        try {
            if (session.getAttribute(R.ADMIN_INFO) == null) throw R.USER_NOT_LOGIN;
            IntegerWrapper pageNum = new IntegerWrapper();
            List<ExpertApply> applies = managerService.getAllExpertApplies(index, size, pageNum);
            JSONArray array = new JSONArray();
            for (ExpertApply apply : applies) {
                array.put(apply.toJson());
            }
            JSON info = new JSON();
            info.put("applys", array);
            info.put("page_num", pageNum.value);
            retData = new RetData(R.STATE_SUCC, "", info);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 处理专家申请
     */
    @RequestMapping(value = "apply/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public String handleExpertApply(@RequestBody String request,
                                    @PathVariable("id") String id, HttpSession session) {
        JSON req = new JSON(request);
        String expertID = req.getBigInteger("expert_id").toString();
        RetData retData;
        try {
            if (session.getAttribute(R.ADMIN_INFO) == null) throw R.USER_NOT_LOGIN;
            managerService.handleExpertApply(expertID, id);
            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 封禁用户
     */
    @RequestMapping(value = "user", method = RequestMethod.PUT)
    @ResponseBody
    public String banUser(@RequestBody String request, HttpSession session) {
        JSON req = new JSON(request);
        String id = req.getBigInteger("id").toString();
        String type = req.getString("type");
        String operation = req.getString("operation");
        RetData retData;
        try {
            if (session.getAttribute(R.ADMIN_INFO) == null) throw R.USER_NOT_LOGIN;
            managerService.banUser(id, type, operation);
            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 处理待审资源
     */
    @RequestMapping(value = "expresource/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public String handlePendingResource(@RequestBody String request,
                                        @PathVariable("id") String id, HttpSession session) {
        JSON req = new JSON(request);
        String state = req.getString("check_state");
        RetData retData;
        try {
            if (session.getAttribute(R.ADMIN_INFO) == null) throw R.USER_NOT_LOGIN;
            managerService.handlePendingResource(id, state);
            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 查看待审资源
     */
    @RequestMapping(value = "expresources", method = RequestMethod.GET)
    @ResponseBody
    public String getAllPendingResource(@RequestParam("index") String index,
                                        @RequestParam("size") String size, HttpSession session) {
        RetData retData;
        try {
            if (session.getAttribute(R.ADMIN_INFO) == null) throw R.USER_NOT_LOGIN;
            IntegerWrapper pageNum = new IntegerWrapper();
            List<Resource> resources = managerService.getAllPendingResource(index, size, pageNum);
            JSONArray array = new JSONArray();
            for (Resource resource : resources) {
                Expert expert = expertInfoService.getExpertInfo(resource.leader_id.toString());
                if (expert == null) throw R.USER_NOT_EXIST;
                JSON data = resource.toJson();
                data.put("owner_name", expert.name);
                array.put(resource.toJson());
            }
            JSON info = new JSON();
            info.put("resources", array);
            info.put("page_num", pageNum.value);
            retData = new RetData(R.STATE_SUCC, "", info);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 获取管理员的session
     */
    @RequestMapping(value = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getManagerSession(HttpSession session) {
        RetData retData;
        try {
            Manager manager = (Manager) session.getAttribute(R.ADMIN_INFO);
            if (manager == null) throw R.USER_NOT_LOGIN;
            JSON info = new JSON();
            info.put("email", manager.email);
            retData = new RetData(R.STATE_SUCC, "", info);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }
}
