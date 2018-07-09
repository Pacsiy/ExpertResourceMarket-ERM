package edu.buaa.server.serviceInterface;

import edu.buaa.server.dataLayer.domain.Tag;

import java.util.List;

public interface TagServiceIF {
    //用户与主题相关的
    List<Tag> getAllTags();

    void subscribeTag(String tagID, String userID);

    void dismissTag(String tagID, String userID);

    List<Tag> getUserTags(String userID);

    //根据资源ID查找标签
    List<Tag> getResTagByResID(String resID);

    //根据专家ID获取标签
    List<Tag> getExpertTagByExpertID(String expertID);

}
