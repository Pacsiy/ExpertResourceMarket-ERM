package edu.buaa.server.serviceInterface.infoService;

import edu.buaa.server.dataLayer.domain.User;

public interface UserInfoServiceIF {
    User login(String cellphone, String password);

    void register(String username, String password, String email, String cellphone);

    String sendVerificationCode(String phone);

    void recharge(String num, String userID);

    void withdraw(String num, String userID);

    void modifyUserInformation(String username, String email, String avator, String userID);

    void modifyUserPassword(String originPassword, String password, String passwordAgain, String userID);

    void applyToBeExpert(String userID, String content, String expertID, String expertName, String userName);

    User getUserByID(String userID);
}
