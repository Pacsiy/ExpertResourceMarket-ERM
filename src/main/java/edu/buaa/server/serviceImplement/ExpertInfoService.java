package edu.buaa.server.serviceImplement;

import edu.buaa.server.dataLayer.domain.Expert;
import edu.buaa.server.dataLayer.mapper.ExpertMapper;
import edu.buaa.server.dataLayer.mapper.TagMapper;
import edu.buaa.server.serviceInterface.infoService.ExpertInfoServiceIF;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;

@Service
public class ExpertInfoService implements ExpertInfoServiceIF {
    private final ExpertMapper expertMapper;
    private final TagMapper tagMapper;

    public ExpertInfoService(ExpertMapper expertMapper, TagMapper tagMapper) {
        this.expertMapper = expertMapper;
        this.tagMapper = tagMapper;
    }

    /**
     * 根据用户名查找对应的专家信息，
     *
     * @param userID 用户ID
     * @return 查找成功返回Expert对象，失败返回null;
     */
    @Override
    @Nullable
    public Expert UserToExpert(String userID) {
        try {
            return expertMapper.getByUserID(userID);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据专家ID获取专家信息
     *
     * @param expertID 专家ID
     * @return 专家信息，没找到返回null
     */
    @Override
    @Nullable
    public Expert getExpertInfo(String expertID) {
        try {
            return expertMapper.getByID(expertID);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 修改专家门户信息
     *
     * @param expertID      专家ID
     * @param phone         手机
     * @param email         邮箱
     * @param constitution  机构
     * @param intro         简介
     * @param backgroundImg 背景大图路径
     * @param tagList       专家的标签修改
     */
    @Override
    public void modifyExpertInfo(String expertID, String phone, String email, String constitution, String intro, String backgroundImg, ArrayList<BigInteger> tagList) {
        try {
            expertMapper.updateExpertInfo(expertID, phone, email, constitution, intro, backgroundImg);
            tagMapper.deleteExpertTag(expertID);
            for (BigInteger tagID : tagList) {
                tagMapper.addExpertTag(tagID.toString(), expertID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
