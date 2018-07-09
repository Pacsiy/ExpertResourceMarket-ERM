package edu.buaa.server.serviceImplement;

import edu.buaa.server.dataLayer.domain.Paper;
import edu.buaa.server.dataLayer.domain.Tag;
import edu.buaa.server.dataLayer.mapper.TagMapper;
import edu.buaa.server.serviceInterface.TagServiceIF;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TagService implements TagServiceIF {
    private final TagMapper tagMapper;

    public TagService(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    /**
     * 获取所有的标签
     *
     * @return 标签列表，如果数据库中没有标签，就返回空列表，不用返回NULL
     */
    @Override
    @NotNull
    public List<Tag> getAllTags() {
        try {
            return tagMapper.getAllTag();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * 用户订阅某个主题
     *
     * @param tagID  主题ID
     * @param userID 用户ID
     */
    @Override
    public void subscribeTag(String tagID, String userID) {
        try {
            tagMapper.addUserTag(tagID, userID);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 用户取消订阅某个主题
     *
     * @param tagID  主题ID
     * @param userID 用户ID
     */
    @Override
    public void dismissTag(String tagID, String userID) {
        try {
            tagMapper.deleteUserTag(tagID, userID);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 获取用户已订阅的主题
     *
     * @param userID 用户ID
     * @return 该用户订阅的主题列表
     */
    @NotNull
    @Override
    public List<Tag> getUserTags(String userID) {
        try {
            return tagMapper.getTagByUserID(userID);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 根据资源ID查找资源的标签
     *
     * @param resID 资源ID
     * @return 标签列表
     */
    @NotNull
    @Override
    public List<Tag> getResTagByResID(String resID) {
        try {
            return tagMapper.getTagByResourceID(resID);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 根据专家ID获取专家的标签
     *
     * @param expertID 专家ID
     * @return 标签列表
     */
    @Override
    @NotNull
    public List<Tag> getExpertTagByExpertID(String expertID) {
        try {
            return tagMapper.getTagByExpertID(expertID);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 获取某个领域类的论文
     */
    @NotNull
    public List<Paper> getPapersByTag(Integer tagID) {
        try {
            return tagMapper.getPaperByTag(tagID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public Tag getTagByID(Integer tagID) {
        try {
            return tagMapper.getTagByID(tagID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
