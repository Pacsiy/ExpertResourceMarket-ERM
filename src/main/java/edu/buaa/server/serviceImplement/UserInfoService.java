package edu.buaa.server.serviceImplement;

import com.aliyuncs.exceptions.ClientException;
import edu.buaa.server.dataLayer.domain.User;
import edu.buaa.server.dataLayer.mapper.ApplyMapper;
import edu.buaa.server.dataLayer.mapper.TradeResourceMapper;
import edu.buaa.server.dataLayer.mapper.UserMapper;
import edu.buaa.server.serviceInterface.infoService.UserInfoServiceIF;
import edu.buaa.server.util.CheckPhone;
import org.jetbrains.annotations.Nullable;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService implements UserInfoServiceIF {
    private final UserMapper userMapper;
    private final ApplyMapper applyMapper;
    private final TradeResourceMapper tradeResourceMapper;

    public UserInfoService(UserMapper userMapper, ApplyMapper applyMapper, TradeResourceMapper tradeResourceMapper) {
        this.userMapper = userMapper;
        this.applyMapper = applyMapper;
        this.tradeResourceMapper = tradeResourceMapper;
    }

    /**
     * 登录
     *
     * @param cellphone 手机号
     * @param password  密码
     * @return 返回一个User
     */
    @Override
    @Nullable
    public User login(String cellphone, String password) {
        try {
            User user = userMapper.getByPhone(cellphone);
            if (user != null && BCrypt.checkpw(password, user.password)) {
                return user;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 注册
     *
     * @param username  用户名
     * @param password  密码
     * @param email     邮箱
     * @param cellphone 手机号
     */
    @Override
    public void register(String username, String password, String email, String cellphone) {
        try {
            User user = userMapper.getByPhone(cellphone);
            if (user != null) {
                throw new RuntimeException("this phone has been registered");
            } else {
                password = BCrypt.hashpw(password, BCrypt.gensalt());
                userMapper.setUserAccount(username, password, email, cellphone);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 给手机发送验证码
     *
     * @param phone 目的手机号
     * @return 是否发送成功，成功返回"succ"；失败的话，手机已存在返回"this phone has existed"，
     * 其他的原因返回"can't send code"
     */
    @Override
    public String sendVerificationCode(String phone) {
        try {
            User user = userMapper.getByPhone(phone);
            if (user != null) {
                throw new RuntimeException("this phone has been registered");
            } else {
                return CheckPhone.sendCode(phone);
            }
        } catch (ClientException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * 充值积分
     *
     * @param num    充值的积分数;
     * @param userID 用户ID
     */
    @Override
    public void recharge(String num, String userID) {
        try {
            userMapper.updateBalance(num, userID);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 修改用户基本信息
     *
     * @param username 用户名
     * @param email    邮箱
     * @param avator   用户头像路径
     * @param userID   用户的ID
     */
    @Override
    public void modifyUserInformation(String username, String email, String avator, String userID) {
        try {
            userMapper.updateUserInfo(username, email, avator, userID);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 修改密码
     *
     * @param originPassword 原始密码
     * @param password       新密码
     * @param passwordAgain  新密码
     * @param userID         用户ID
     */
    @Override
    public void modifyUserPassword(String originPassword, String password, String passwordAgain, String userID) {
        try {
            User user = userMapper.getByID(userID);
            if (!BCrypt.checkpw(originPassword, user.password)) {
                throw new RuntimeException("密码错误");
            } else if (!password.equals(passwordAgain)) {
                throw new RuntimeException("两次输入密码不一致");
            } else {
                password = BCrypt.hashpw(password, BCrypt.gensalt());
                userMapper.updatePassword(password, userID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 积分提现，相当于从数据库中减去这个数值吧。。
     *
     * @param num    提现数目
     * @param userID 用户名
     */
    @Override
    public void withdraw(String num, String userID) {
        try {
            userMapper.updateBalance("-" + num, userID);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 用户申请成为专家
     *
     * @param userID     用户ID
     * @param content    用户提交的认证信息
     * @param expertID   专家ID
     * @param expertName 专家名
     * @param userName   用户名
     */
    @Override
    public void applyToBeExpert(String userID, String content, String expertID, String expertName, String userName) {
        try {
            applyMapper.addApply(userID, content, expertID, expertName, userName);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 根据用户名查找用户
     *
     * @param userID 用户ID
     * @return 用户，未找到返回null
     */
    @Override
    public User getUserByID(String userID) {
        try {
            return userMapper.getByID(userID);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
