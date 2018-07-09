package edu.buaa.server.serviceImplement.resourceService;

import com.github.pagehelper.PageHelper;
import edu.buaa.server.dataLayer.domain.Resource;
import edu.buaa.server.dataLayer.mapper.Resource.ExpertResourceMapper;
import edu.buaa.server.dataLayer.mapper.TagMapper;
import edu.buaa.server.serviceImplement.EnumType.Role;
import edu.buaa.server.serviceInterface.resourceService.ExpertResourceServiceIF;
import edu.buaa.server.util.IntegerWrapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ExpertResourceService implements ExpertResourceServiceIF {
    private final ExpertResourceMapper resourceMapper;
    private final TagMapper tagMapper;
    private final CommonResourceService commonResourceService;

    public ExpertResourceService(ExpertResourceMapper resourceMapper, TagMapper tagMapper, CommonResourceService commonResourceService) {
        this.resourceMapper = resourceMapper;
        this.tagMapper = tagMapper;
        this.commonResourceService = commonResourceService;
    }

    public Resource getResourceByID(String resID) {
        return commonResourceService.getResourceByID(Role.EXPERT, resID);
    }

    /**
     * 专家添加资源
     *
     * @param resource 资源
     * @param tagList  标签ID列表
     */
    @Override
    public void addResource(Resource resource, ArrayList<BigInteger> tagList) {
        try {
            switch (resource.type) {
                case PAPER:
                    resourceMapper.addPaper(resource);
                    break;
                case PATENT:
                    resourceMapper.addPatent(resource);
                    break;
                case PROJECT:
                    resourceMapper.addProject(resource);
                    break;
            }
            for (BigInteger tagID : tagList) {
                tagMapper.addResourceTag(tagID.toString(), resource.id.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 删除资源
     *
     * @param resID    资源ID
     * @param expertID 专家ID,由于资源里面已经有拥有者ID了，
     *                 这里传一个是为了验证是不是同一个专家，如果不是就不能删除
     */
    @Override
    public void deleteResource(String resID, String expertID) {
        try {
            resourceMapper.deleteResource(resID, expertID);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 修改资源信息
     *
     * @param tagList  资源标签ID列表
     * @param resource 资源
     */
    @Override
    public void modifyResource(ArrayList<BigInteger> tagList, Resource resource) {
        try {
            //更新资源基本信息
            resourceMapper.updateResource(resource);
            //更新资源标签
            tagMapper.deleteResourceTag(resource.id.toString());
            for (BigInteger i : tagList) {
                tagMapper.addResourceTag(i.toString(), resource.id.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @NotNull
    public List<Resource> getOwnResource(String expertID) {
        try {
            return resourceMapper.getResourceByOwnerID(expertID);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 用户角度获取专家资源
     *
     * @param index    第几页
     * @param size     大小
     * @param pageNum  有几页
     * @param expertID 专家ID
     * @return 资源列表
     */
    @Override
    @NotNull
    public List<Resource> getResFromUserAspect(String index, String size, IntegerWrapper pageNum, String expertID) {
        try {
            Integer indexs = Integer.valueOf(index);
            Integer sizes = Integer.valueOf(size);
            pageNum.value = (int) Math.ceil(1.0 * resourceMapper.getExpertResCountFromUserAspect(expertID) / sizes);
            PageHelper.startPage(indexs, sizes);
            return resourceMapper.getResByExpertIDFromUserAspect(expertID);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
