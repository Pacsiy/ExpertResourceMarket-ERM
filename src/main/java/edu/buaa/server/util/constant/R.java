package edu.buaa.server.util.constant;

import edu.buaa.server.dataLayer.domain.Resource;

public class R {
    //与Session相关的常量
    public static final String USER_INFO = "userInfo";
    public static final String EXPERT_INFO = "expertInfo";
    public static final String CODE = "code";
    public static final String ADMIN_INFO = "adminInfo";

    //与状态相关，比如成功，失败啊
    public static final String STATE_SUCC = "succ";
    public static final String STATE_FAIL = "fail";

    public static final long ONE_DAY = 24 * 60 * 60 * 1000;

    public static final String DETAIL_ERROR = "{error:未填写详细信息}";

    public static final String TOKEN = "token";

    public static final String DEFAULT_AVATOR = "http://47.94.96.70:8080/ERM-WebIO-1.0/pics/avator.jpeg";
    public static final String DEFAULT_BACK = "http://47.94.96.70:8080/ERM-WebIO-1.0/pics/back.jpeg";

    public static final Resource PERMISSION_ERROR;


    public static final RuntimeException USER_NOT_EXIST = new RuntimeException("该用户不存在");
    public static final RuntimeException USER_NOT_LOGIN = new RuntimeException("用户未登录");
    public static final RuntimeException EXPERT_NOT_EXIST = new RuntimeException("该专家不存在");
    public static final RuntimeException RESOURCE_NOT_EXIST = new RuntimeException("该资源不存在");

    static {
        PERMISSION_ERROR = new Resource();
        PERMISSION_ERROR.introduction = "当前用户组没有权限访问";
    }

}
