package edu.buaa.server.serviceImplement;

import edu.buaa.server.dataLayer.domain.Expert;
import edu.buaa.server.dataLayer.domain.Resource;
import edu.buaa.server.dataLayer.mapper.RecommendMapper;
import edu.buaa.server.serviceInterface.RecommendServiceIF;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RecommendService implements RecommendServiceIF {
    private final RecommendMapper recommendMapper;

    public RecommendService(RecommendMapper recommendMapper) {
        this.recommendMapper = recommendMapper;
    }

    /**
     * 批量获取专家信息
     *
     * @param expertIDList 专家ID列表
     * @return 专家数组
     */
    @Override
    @NotNull
    public List<Expert> getExpertByIDArray(ArrayList<BigInteger> expertIDList) {
        try {
            StringBuilder buffer = new StringBuilder("(");
            for (BigInteger id : expertIDList) {
                buffer.append(id.toString());
                buffer.append(",");
            }
            buffer.replace(buffer.length() - 1, buffer.length(), ")");
//            System.out.println("array is "+buffer);
            return recommendMapper.getExpertArray(buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 批量获取资源信息
     *
     * @param resIDList 资源信息列表
     * @return 资源列表
     */
    @Override
    @NotNull
    public List<Resource> getResourceByResIDArray(ArrayList<BigInteger> resIDList) {
        try {
            StringBuilder list = new StringBuilder("(");
            for (BigInteger id : resIDList) {
                list.append(id.toString());
                list.append(",");
            }
            list.replace(list.length() - 1, list.length(), ")");
            return recommendMapper.getResourceArray(list.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
