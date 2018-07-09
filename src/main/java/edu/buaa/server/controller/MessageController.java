package edu.buaa.server.controller;

import edu.buaa.server.dataLayer.domain.User;
import edu.buaa.server.serviceImplement.MessageService;
import edu.buaa.server.util.JSON;
import edu.buaa.server.util.RetData;
import edu.buaa.server.util.constant.R;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.math.BigInteger;

@Controller
@RequestMapping("/api")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * @return 留言的返回信息
     */
    @RequestMapping(value = "/message", method = RequestMethod.POST)
    @ResponseBody
    public String addMessage(@RequestBody String request, HttpSession session) {
        JSON req = new JSON(request);
        RetData retData;
        try {
            User user = (User) session.getAttribute(R.USER_INFO);
            if (user == null) throw R.USER_NOT_LOGIN;
            BigInteger user_id = user.id;
            String title = req.getString("title");
            String content = req.getString("content");
            content = Jsoup.clean(content, Whitelist.relaxed());
            messageService.addMessage(user_id.toString(), title, content);
            retData = new RetData(R.STATE_SUCC, "", null);
        } catch (Exception e) {
            retData = new RetData(R.STATE_FAIL, e, null);
        }
        return retData.toString();
    }
}

