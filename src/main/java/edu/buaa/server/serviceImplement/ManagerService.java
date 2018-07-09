package edu.buaa.server.serviceImplement;

import com.github.pagehelper.PageHelper;
import edu.buaa.server.dataLayer.domain.*;
import edu.buaa.server.dataLayer.mapper.*;
import edu.buaa.server.dataLayer.mapper.Resource.ManagerResourceMapper;
import edu.buaa.server.serviceInterface.ManagerServiceIF;
import edu.buaa.server.util.IntegerWrapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ManagerService implements ManagerServiceIF {
    private final ManagerMapper managerMapper;
    private final TradeResourceMapper tradeResourceMapper;
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;
    private final ExpertMapper expertMapper;
    private final ManagerResourceMapper resourceMapper;
    private final ApplyMapper applyMapper;
    private final ManagerResourceMapper managerResourceMapper;

    public ManagerService(ManagerMapper managerMapper, TradeResourceMapper tradeResourceMapper, MessageMapper messageMapper,
                          UserMapper userMapper, ExpertMapper expertMapper, ManagerResourceMapper resourceMapper,
                          ApplyMapper applyMapper, ManagerResourceMapper managerResourceMapper) {
        this.managerMapper = managerMapper;
        this.tradeResourceMapper = tradeResourceMapper;
        this.messageMapper = messageMapper;
        this.userMapper = userMapper;
        this.expertMapper = expertMapper;
        this.resourceMapper = resourceMapper;
        this.applyMapper = applyMapper;
        this.managerResourceMapper = managerResourceMapper;
    }

    /**
     * 管理员登录
     *
     * @param cellphone 手机号
     * @param password  密码
     * @return 管理员对象，失败返回null
     */
    @Override
    public Manager login(String cellphone, String password) {
        try {
            Manager manager = managerMapper.getByCellphone(cellphone);
            if (manager == null || !manager.password.equals(password)) {
                return null;
            } else {
                return manager;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查看后台留言
     *
     * @param pageNum      请求页面
     * @param pageSize     页面大小
     * @param pageNumCount 页面总数 out
     * @return 返回消息列表
     */
    @Override
    @NotNull
    public List<Message> checkBackgroundMessages(String pageNum, String pageSize, IntegerWrapper pageNumCount) {
        try {
            Integer pageNum_num = Integer.valueOf(pageNum);
            Integer pageSize_num = Integer.valueOf(pageSize);
            pageNumCount.value = (int) Math.ceil(1.0 * messageMapper.getRowCount() / pageSize_num);
            PageHelper.startPage(pageNum_num, pageSize_num);
            return messageMapper.getAll();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 获取某条交易的详细信息
     *
     * @param tradeID 交易ID
     * @return 一个交易对象，没找到返回null
     */
    @Override
    public TradeResource getTradeRecordByID(String tradeID) {
        try {
            return tradeResourceMapper.getTradeByID(tradeID);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取所有的用户对专家的申请
     *
     * @param pageNum      请求页面
     * @param pageSize     页面大小
     * @param pageNumCount 页面总数 out，表示我传入一个Integer对象，你们计算出页面总数后，放到这个数据里面。
     * @return 申请列表
     */
    @NotNull
    @Override
    public List<ExpertApply> getAllExpertApplies(String pageNum, String pageSize, IntegerWrapper pageNumCount) {
        try {
            Integer pageNum_num = Integer.valueOf(pageNum);
            Integer pageSize_num = Integer.valueOf(pageSize);
            pageNumCount.value = (int) Math.ceil(1.0 * applyMapper.getRowCount() / pageSize_num);
            PageHelper.startPage(pageNum_num, pageSize_num);
            return applyMapper.getAll();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 处理专家申请
     * 这里的逻辑是这样的：我和李国豪达成的共识是，用户只能和数据库里面存在的专家发生关系，不能新建一个专家
     * ，所以，这里会给你一个专家ID。如果是通过申请，那么expertID是>=0的，如果是拒绝申请，expertID = -1.
     * 通过申请后，可以将专家表中，UserID修改为对应申请表中user_ID
     *
     * @param expertID 专家ID
     * @param applyID  申请ID
     */
    @Override
    public void handleExpertApply(String expertID, String applyID) {
        try {
            if (expertID.equals("-1")) {
                expertMapper.updateApplyRejected(Integer.valueOf(applyID));
            } else {
                expertMapper.updateExpertUserID(expertID, applyID);
                expertMapper.updateApplyApproved(Integer.valueOf(applyID));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 封禁用户 或 专家
     * 封禁专家的话，把state 改为 -1，默认是0。。。
     *
     * @param id        用户或者专家id
     * @param type      封禁用户还是专家 {"user","expert"}
     * @param operation 这个参数先放在在里，目前没有啥用，作为以后的扩展（万一要添加解禁功能的话）。。
     */
    @Override
    public void banUser(String id, String type, String operation) {
        try {
            switch (type) {
                case "user":
                    userMapper.updateState("DISABLE", id);
                    break;
                case "expert":
                    expertMapper.updateState("-1", id);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 获取所有待审资源列表
     *
     * @param pageNum      请求页面
     * @param pageSize     页面大小
     * @param pageNumCount 页面总数，out;
     * @return 资源列表
     */
    @NotNull
    @Override
    public List<Resource> getAllPendingResource(String pageNum, String pageSize, IntegerWrapper pageNumCount) {
        try {
            Integer pageNum_num = Integer.valueOf(pageNum);
            Integer pageSize_num = Integer.valueOf(pageSize);
            pageNumCount.value = (int) Math.ceil(1.0 * resourceMapper.getPendingRowCount() / pageSize_num);
            PageHelper.startPage(pageNum_num, pageSize_num);
            return resourceMapper.getPendingAll();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 处理待审核资源
     *
     * @param resID 资源ID
     * @param state 操作，"approved"表示通过，"rejected"表示拒绝
     */
    @Override
    public void handlePendingResource(String resID, String state) {
        try {
            if (state.equals("approved")) {
                resourceMapper.approveResource(resID);
            } else if (state.equals("rejected")) {
                resourceMapper.rejectResource(resID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 获取所有交易记录，按时间最近排序
     *
     * @param index   第几页
     * @param size    页面大小
     * @param pageNum 一共有几页。
     * @return 交易记录列表
     */
    @Override
    @NotNull
    public List<TradeResource> getTradeRecord(String index, String size, IntegerWrapper pageNum) {
        try {
            Integer pageNum_num = Integer.valueOf(index);
            Integer pageSize_num = Integer.valueOf(size);
            pageNum.value = (int) Math.ceil(1.0 * managerResourceMapper.getTradeCount() / pageSize_num);
            PageHelper.startPage(pageNum_num, pageSize_num);
            return managerResourceMapper.getAllTradeResource();
        } catch (Exception e) {
            return Collections.emptyList();
        }

    }
}
