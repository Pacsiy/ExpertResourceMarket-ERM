package edu.buaa.server.serviceImplement.resourceService;

import edu.buaa.server.dataLayer.domain.Resource;
import edu.buaa.server.dataLayer.domain.TradeResource;
import edu.buaa.server.dataLayer.domain.TransferPatent;
import edu.buaa.server.dataLayer.mapper.CommentMapper;
import edu.buaa.server.dataLayer.mapper.Resource.UserResourceMapper;
import edu.buaa.server.dataLayer.mapper.TradeResourceMapper;
import edu.buaa.server.serviceImplement.EnumType.Role;
import edu.buaa.server.serviceInterface.resourceService.UserResourceServiceIF;
import edu.buaa.server.util.constant.R;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

@Service
public class UserResourceService implements UserResourceServiceIF {
    private final UserResourceMapper resourceMapper;
    private final TradeResourceMapper tradeResourceMapper;
    private final CommentMapper commentMapper;
    private final CommonResourceService commonResourceService;

    public UserResourceService(UserResourceMapper resourceMapper, TradeResourceMapper tradeResourceMapper, CommentMapper commentMapper, CommonResourceService commonResourceService) {
        this.resourceMapper = resourceMapper;
        this.tradeResourceMapper = tradeResourceMapper;
        this.commentMapper = commentMapper;
        this.commonResourceService = commonResourceService;
    }

    public Resource getResourceByID(String resID) {
        return commonResourceService.getResourceByID(Role.USER, resID);
    }


    /**
     * 获取用户的交易记录
     *
     * @param userID 用户ID
     * @return 该用户的交易记录
     */
    @Override
    @NotNull
    public List<TradeResource> getTradeRecord(String userID) {
        try {
            return tradeResourceMapper.getTradeByBuyerID(userID);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    /**
     * 用户获取资源
     *
     * @param expertID 专家ID
     * @return 这个专家的资源列表
     */
    @Override
    @NotNull
    public List<Resource> getResourceByExpertID(String expertID) {
        try {
            return resourceMapper.getResourceByExpertID(expertID);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }


    /**
     * 进行交易
     * 这里进行交易时，首先要判断这个资源是什么类型的，主要判断是否是专利，因为专利之间只能在专家之间转让。
     * 除此之外，进行交易时，还需要进行积分之间的更改，卖者加上相应积分，买者减去积分。
     *
     * @param type     "rent" 和 "buy"
     * @param resID    资源ID
     * @param buyer_id 购买者ID
     * @return 交易记录
     */
    @Override
    public TradeResource doTrade(String type, String resID, String buyer_id) {
        try {
            Timestamp current = new Timestamp(System.currentTimeMillis());
            Timestamp oneMonthLater = new Timestamp(System.currentTimeMillis() + R.ONE_DAY * 30);
            return tradeResourceMapper.tradeResource(
                    resID, type.toUpperCase(), buyer_id, current.toString(), oneMonthLater.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 评价资源
     *
     * @param score   评分
     * @param content 内容
     * @param userID  用户ID
     * @param resID   被评价的资源ID
     * @return 成功返回 "succ",失败返回"fail"
     */
    @Override
    public String commentRes(int score, String content, String userID, String resID) {
        try {
            commentMapper.addComment(String.valueOf(score), content, userID, resID);
            return R.STATE_SUCC;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 专家间专利转让
     *
     * @param toID  出售专家ID
     * @param resID 资源ID
     * @return 转让记录
     */
    @Override
    public TransferPatent doTransfer(String toID, String resID) {
        // TODO: 2018
        // fixme transfer price is different from buy price and rent price
        // fixme content is needed
        try {
            return tradeResourceMapper.transferResource("0", "transfer", resID, toID);
        } catch (Exception e) {
            return null;
        }
    }
}
