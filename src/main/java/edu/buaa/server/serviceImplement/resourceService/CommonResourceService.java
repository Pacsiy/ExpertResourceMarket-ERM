package edu.buaa.server.serviceImplement.resourceService;

import com.github.pagehelper.PageHelper;
import edu.buaa.server.dataLayer.domain.Comment;
import edu.buaa.server.dataLayer.domain.EnumType.ResourceType;
import edu.buaa.server.dataLayer.domain.Resource;
import edu.buaa.server.dataLayer.mapper.CommentMapper;
import edu.buaa.server.dataLayer.mapper.Resource.CommonResourceMapper;
import edu.buaa.server.serviceImplement.EnumType.Role;
import edu.buaa.server.serviceInterface.resourceService.CommonResourceServiceIF;
import edu.buaa.server.util.IntegerWrapper;
import edu.buaa.server.util.constant.R;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CommonResourceService implements CommonResourceServiceIF {
    private final CommonResourceMapper resourceMapper;
    private final CommentMapper commentMapper;

    public CommonResourceService(CommonResourceMapper resourceMapper, CommentMapper commentMapper) {
        this.resourceMapper = resourceMapper;
        this.commentMapper = commentMapper;
    }

    @NotNull
    @Contract(pure = true)
    private Resource permissionCheck(Role role, Resource resource) {
        switch (role) {
            case USER:
                if (!resource.is_user_visible)
                    return R.PERMISSION_ERROR;
                break;
            case EXPERT:
                if (!resource.is_expert_visible)
                    return R.PERMISSION_ERROR;
                break;
        }
        return resource;
    }


    /**
     * 根据资源ID获取资源
     *
     * @param resID 资源ID
     * @return 对应资源
     */
    @NotNull
    @Override
    public Resource getResourceByID(Role role, String resID) {
        try {
            Resource resource = resourceMapper.getResourceByID(resID);
            resource = permissionCheck(role, resource);
            return getDetailsByIDAndType(role, resID, resource.type.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return R.PERMISSION_ERROR;
        }
    }

    /**
     * 获取某资源的对应评论
     *
     * @param resID        资源ID
     * @param pageNum      第几页
     * @param pageSize     页面大小
     * @param pageNumCount 页面总数，out
     * @return 评论列表
     */
    @Override
    @NotNull
    public List<Comment> getCommentsByID(Role role, String resID, String pageNum, String pageSize, IntegerWrapper pageNumCount) {
        try {
            Integer pageNum_num = Integer.valueOf(pageNum);
            Integer pageSize_num = Integer.valueOf(pageSize);
            pageNumCount.value = (int) Math.ceil(1.0 * commentMapper.getRowCountByResID(resID) / pageSize_num);
            PageHelper.startPage(pageNum_num, pageSize_num);
            return commentMapper.getByResID(resID);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    /**
     * 通过资源的类型获取更多的信息
     *
     * @param resID 资源ID
     * @param type  资源类型，"paper","patent","project"
     * @return 一个对象，没找到就返回null
     */
    @Override
    @NotNull
    public Resource getDetailsByIDAndType(Role role, String resID, String type) {
        Resource result = null;
        ResourceType resourceType = ResourceType.valueOf(type.toUpperCase());
        try {
            switch (resourceType) {
                case PAPER:
                    result = resourceMapper.getPaperByID(resID);
                    break;
                case PATENT:
                    result = resourceMapper.getPatentByID(resID);
                    break;
                case PROJECT:
                    result = resourceMapper.getProjectByID(resID);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result == null) {
            result = R.PERMISSION_ERROR;
        }
        return result;
    }

    /**
     * 查找patent,paper,project三张表，找出与resID对应的条目
     *
     * @param resID 资源ID
     * @return 返回这三个类型中的一个 ，没找到返回null
     */
    @Override
    @NotNull
    public Object getDetailsByID(Role role, String resID) {
        try {
            Resource resource = getResourceByID(role, resID);
            return getDetailsByIDAndType(role, resource.id.toString(), resource.type.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return R.DETAIL_ERROR;
        }
    }
}
