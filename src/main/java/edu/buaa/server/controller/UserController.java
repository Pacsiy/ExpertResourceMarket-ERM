package edu.buaa.server.controller;

import edu.buaa.server.dataLayer.domain.Expert;
import edu.buaa.server.dataLayer.domain.Resource;
import edu.buaa.server.dataLayer.domain.TradeResource;
import edu.buaa.server.dataLayer.domain.User;
import edu.buaa.server.serviceImplement.ExpertInfoService;
import edu.buaa.server.serviceImplement.UserInfoService;
import edu.buaa.server.serviceImplement.resourceService.CommonResourceService;
import edu.buaa.server.serviceImplement.resourceService.UserResourceService;
import edu.buaa.server.util.JSON;
import edu.buaa.server.util.RetData;
import edu.buaa.server.util.Token;
import edu.buaa.server.util.constant.R;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.util.List;

@Controller
@RequestMapping("/api")
public class UserController {
    private final UserInfoService userInfoService;
    private final ExpertInfoService expertInfoService;
    private final UserResourceService resourceService;
    private final CommonResourceService commonResourceService;


    public UserController(UserInfoService userInfoService, ExpertInfoService expertInfoService, UserResourceService resourceService, CommonResourceService commonResourceService) {
        this.userInfoService = userInfoService;
        this.expertInfoService = expertInfoService;
        this.resourceService = resourceService;
        this.commonResourceService = commonResourceService;
    }

    /**
     * 登录
     *
     * @return 返回相关信息
     */
    @RequestMapping(value = "/session", method = RequestMethod.POST)
    @ResponseBody
    public String login(@RequestBody String request, HttpSession session) {
        RetData retData;
        JSON req = new JSON(request);
        String phone = req.getString("cellphone");
        String password = req.getString("password");
        try {
            User ans = userInfoService.login(phone, password);
            if (ans != null) {
                Expert expert = expertInfoService.UserToExpert(ans.id.toString());
                session.setAttribute(R.USER_INFO, ans);
                JSON user = new JSON();
                user.put("user_name", ans.nickname);
                user.put("email", ans.email);
                user.put("avator", ans.avator);
                user.put("balance", ans.balance);
                user.put("user_id", ans.id);
                if (expert != null) {
                    user.put("expert_id", expert.id);
                    session.setAttribute(R.EXPERT_INFO, expert);
                } else {
                    user.put("expert_id", -1);
                }
                String token = Token.getToken();
                user.put("token", token);
                session.setAttribute(R.TOKEN, token);
                Boolean ai = Token.sendToken(token, ans.id.toString());
                //if(!ai) throw new RuntimeException("连接AI子系统出错");
                retData = new RetData(R.STATE_SUCC, "", user);
            } else {
                throw new RuntimeException("wrong username or password");
            }
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 注册
     *
     * @return 注册消息
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    @ResponseBody
    public String register(@RequestBody String request, HttpSession session) {
        JSON req = new JSON(request);
        String username = req.getString("user_name");
        String password = req.getString("password");
        String email = req.getString("email");
        String phone = req.getString("cellphone");
        RetData retData;
        try {
            userInfoService.register(username, password, email, phone);
            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 给手机发送验证码
     */
    @RequestMapping(value = "/validation_code", method = RequestMethod.POST)
    @ResponseBody
    public String sendCode(@RequestBody String request, HttpSession session) {
        RetData retData;
        try {
            JSON req = new JSON(request);
            String phone = req.getString("cellphone");
            String code = userInfoService.sendVerificationCode(phone);
            session.setAttribute(R.CODE, code);
            retData = new RetData(R.STATE_SUCC, code, null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 验证码确认
     */
    @RequestMapping(value = "/validation_code", method = RequestMethod.DELETE)
    @ResponseBody
    public String verifyCode(@RequestBody String request, HttpSession session) {
        JSON req = new JSON(request);
        String code = req.getString("validation_code");
        RetData retData;
        try {
            String savedCode = session.getAttribute(R.CODE).toString();
            if (savedCode.equals(code)) {
                retData = new RetData(R.STATE_SUCC, "", null);
            } else {
                throw new RuntimeException("wrong code");
            }
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 充值
     */
    @RequestMapping(value = "/user/balance", method = RequestMethod.POST)
    @ResponseBody
    public String recharge(@RequestBody String request, HttpSession session) {
        JSON req = new JSON(request);
        BigInteger count = req.getBigInteger("count");
        RetData retData;
        try {
            User user = (User) session.getAttribute(R.USER_INFO);
            userInfoService.recharge(count.toString(), user.id.toString());
            user.balance = user.balance.add(count);
            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 修改用户个人信息
     */
    @RequestMapping(value = "/user/information", method = RequestMethod.PUT)
    @ResponseBody
    public String modifyInformation(@RequestBody String request, HttpSession session) {
        JSON req = new JSON(request);
        RetData retData;
        try {
            User user = (User) session.getAttribute(R.USER_INFO);
            String username = req.getString("user_name");
            String email = req.getString("email");
            String avator = req.getString("avator");

            String username_ = username.equals("") ? user.nickname : username;
            String email_ = email.equals("") ? user.email : email;
            String avator_ = avator.equals("") ? user.avator : avator;
            userInfoService.modifyUserInformation(username_, email_, avator_, user.id.toString());
            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 修改用户密码
     */
    @RequestMapping(value = "/user/useless", method = RequestMethod.PUT)
    @ResponseBody
    public String modifyPassword(@RequestBody String request, HttpSession session) {
        JSON req = new JSON(request);
        RetData retData;
        try {
            User user = (User) session.getAttribute(R.USER_INFO);
            String originPassword = req.getString("origin_password");
            String password = req.getString("password");
            String passwordAgain = req.getString("repassword");
            userInfoService.modifyUserPassword(originPassword, password, passwordAgain, user.id.toString());
            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 获取保存在会话中的用户信息
     */
    @RequestMapping(value = "/session/user", method = RequestMethod.GET)
    @ResponseBody
    public String getUserInfo(HttpSession session) {
        RetData retData;
        try {
            User user = (User) session.getAttribute(R.USER_INFO);
            JSON ret = new JSON();
            ret.put("user_name", user.nickname);
            ret.put("email", user.email);
            ret.put("avator", user.avator);
            ret.put("balance", user.balance);
            ret.put("cellphone", user.phone);
            ret.put("user_id", user.id);
            ret.put("token", session.getAttribute(R.TOKEN).toString());
            Expert expert;
            try {
                expert = (Expert) session.getAttribute(R.EXPERT_INFO);
                ret.put("expert_id", expert.id);
            } catch (Exception e) {
                ret.put("expert_id", -1);
            }
            retData = new RetData(R.STATE_SUCC, "", ret);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /*
      获取推荐资源
      这里的TODO是我的。。你们不要管。。。
     */
//    @RequestMapping(value = "user/recommanded/resources", method = RequestMethod.GET)
//    @ResponseBody
//    public String getRecommendedResource(HttpSession session) {
//        return null;
//    }

    /**
     * 提现积分
     */
    @RequestMapping(value = "user/balance", method = RequestMethod.PUT)
    @ResponseBody
    public String withdraw(@RequestBody String request, HttpSession session) {
        JSON req = new JSON(request);
        BigInteger count = req.getBigInteger("count");
        RetData retData;
        try {
            User user = (User) session.getAttribute(R.USER_INFO);
            userInfoService.withdraw(count.toString(), user.id.toString());
            user.balance = user.balance.subtract(count);
            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 申请成为科技专家
     */
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    @ResponseBody
    public String applyToBeExpert(@RequestBody String request, HttpSession session) {
        RetData retData;
        try {
            JSON req = new JSON(request);
            String content = req.getString("content");
            content = Jsoup.clean(content, Whitelist.relaxed());
            String expertID = req.getBigInteger("expert_id").toString();
            String expertName = req.getString("expert_name");
            User user = (User) session.getAttribute(R.USER_INFO);
            userInfoService.applyToBeExpert(user.id.toString(), content, expertID, expertName, user.nickname);
            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 查看用户的已购资源
     */
    @RequestMapping(value = "/user/trades", method = RequestMethod.GET)
    @ResponseBody
    public String getTradeRecord(HttpSession session) {
        RetData retData;
        try {
            User user = (User) session.getAttribute(R.USER_INFO);
            List<TradeResource> trades = resourceService.getTradeRecord(user.id.toString());
            JSONArray tradeWrapped = new JSONArray();
            for (TradeResource trade : trades) {
                JSON json = trade.toJson();
                Resource res = resourceService.getResourceByID(trade.resource_id.toString());
                json.put("file_path", res.file_path);
                json.put("resource_name", res.title);
                json.put("resource_type", res.type.toString().toLowerCase());
                tradeWrapped.put(json);
            }
            retData = new RetData(R.STATE_SUCC, "", tradeWrapped);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 进行交易
     */
    @RequestMapping(value = "trade", method = RequestMethod.POST)
    @ResponseBody
    public String doTrade(@RequestBody String request, HttpSession session) {
        RetData retData;
        JSON req = new JSON(request);
        String resID = req.getBigInteger("resource_id").toString();
        String type = req.getString("type");
        try {
            User user = (User) session.getAttribute(R.USER_INFO);
            if (user == null) throw R.USER_NOT_LOGIN;
            TradeResource trade = resourceService.doTrade(type, resID, user.id.toString());
            if (trade == null) throw new RuntimeException("交易失败");
            Resource resource = resourceService.getResourceByID(resID);
            if (resource == null) throw R.RESOURCE_NOT_EXIST;
            JSON data = trade.toJson();
            data.remove("type");
            data.remove("resource_id");
            data.put("file_path", resource.file_path);
            retData = new RetData(R.STATE_SUCC, "", data);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

    /**
     * 用户登出
     */
    @RequestMapping(value = "session", method = RequestMethod.DELETE)
    @ResponseBody
    public String logout(HttpSession session) {
        RetData retData;
        try {
            if (session.getAttribute(R.USER_INFO) != null) {
                session.removeAttribute(R.USER_INFO);
            }
            if (session.getAttribute(R.EXPERT_INFO) != null) {
                session.removeAttribute(R.EXPERT_INFO);
            }
            if (session.getAttribute(R.USER_INFO) == null) {
                Token.removeToken(session.getAttribute(R.TOKEN).toString());
                session.removeAttribute(R.TOKEN);
                retData = new RetData(R.STATE_SUCC, "", null);
            } else {
                throw new RuntimeException("用户信息未清除");
            }
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }

}

