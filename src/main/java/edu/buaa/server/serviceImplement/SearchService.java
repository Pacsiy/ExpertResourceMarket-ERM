package edu.buaa.server.serviceImplement;

import com.github.pagehelper.PageHelper;
import edu.buaa.server.dataLayer.domain.*;
import edu.buaa.server.dataLayer.mapper.ExpertMapper;
import edu.buaa.server.dataLayer.mapper.SearchMapper;
import edu.buaa.server.dataLayer.mapper.UserMapper;
import edu.buaa.server.serviceInterface.SearchServiceIF;
import edu.buaa.server.util.IntegerWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SearchService implements SearchServiceIF {
    private final UserMapper userMapper;
    private final ExpertMapper expertMapper;
    private final SearchMapper searchMapper;

    public SearchService(UserMapper userMapper, ExpertMapper expertMapper, SearchMapper searchMapper) {
        this.userMapper = userMapper;
        this.expertMapper = expertMapper;
        this.searchMapper = searchMapper;
    }

    /**
     * 管理员搜索
     *
     * @param keyword   专家姓名 或者 用户名 查询
     * @param type      查用户 还是 查专家,可有可无,如果是Null,表示两个都查，{"user","expert",null}
     * @param index     第几页
     * @param size      一页多少
     * @param userNum   查出的用户数量
     * @param expertNum 查出的专家数量
     * @return 搜索结果，要么是List<User> 要么是 List<Expert>，两者都有的话，是List<Object>
     */
    @Override
    @NotNull
    public List<Object> managerSearch(@NotNull String keyword, @Nullable String type,
                                      String index, String size,
                                      @NotNull IntegerWrapper userNum, @NotNull IntegerWrapper expertNum) {
        try {
            Integer pageNum_num = Integer.valueOf(index);
            Integer pageSize_num = Integer.valueOf(size);
            List<Object> data = new ArrayList<>();
            if (type == null || type.equals("user")) {
                userNum.value = (int) Math.ceil(1.0 * userMapper.getRowCountByKey(keyword) / pageSize_num);
                PageHelper.startPage(pageNum_num, pageSize_num);
                data.addAll(userMapper.getAllByKey(keyword));
            }
            if (type == null || type.equals("expert")) {
                expertNum.value = (int) Math.ceil(1.0 * expertMapper.getRowCountByKey(keyword) / pageSize_num);
                PageHelper.startPage(pageNum_num, pageSize_num);
                data.addAll(expertMapper.getAllByKey(keyword));
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 普通用户搜索论文
     *
     * @param keyword 关键字
     * @param index   索引
     * @param size    大小
     * @param pageNum 总页数
     * @return List<Resource>
     */
    @Override
    @NotNull
    public List<Paper> searchPapers(String keyword, String index, String size, IntegerWrapper pageNum) {
        try {
            Integer indexInt = Integer.valueOf(index);
            Integer sizeInt = Integer.valueOf(size);
            pageNum.value = (int) Math.ceil(1.0 * searchMapper.getPapersCount(keyword) / sizeInt);
            PageHelper.startPage(indexInt, sizeInt);
            return searchMapper.getSearchPapers(keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 普通用户搜索专利
     *
     * @param keyword 关键字
     * @param index   索引
     * @param size    页大小
     * @param pageNum 页数
     * @return 专利列表
     */
    @Override
    @NotNull
    public List<Patent> searchPatents(String keyword, String index, String size, IntegerWrapper pageNum) {
        try {
            Integer indexInt = Integer.valueOf(index);
            Integer sizeInt = Integer.valueOf(size);
            pageNum.value = (int) Math.ceil(1.0 * searchMapper.getPatentsCount(keyword) / sizeInt);
            PageHelper.startPage(indexInt, sizeInt);
            return searchMapper.getSearchPatents(keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 普通用户搜索项目
     *
     * @param keyword 关键字
     * @param index   索引
     * @param size    页大小
     * @param pageNum 页数
     * @return 项目列表
     */
    @Override
    @NotNull
    public List<Project> searchProjects(String keyword, String index, String size, IntegerWrapper pageNum) {
        try {
            Integer indexInt = Integer.valueOf(index);
            Integer sizeInt = Integer.valueOf(size);
            pageNum.value = (int) Math.ceil(1.0 * searchMapper.getProjectsCount(keyword) / sizeInt);
            PageHelper.startPage(indexInt, sizeInt);
            return searchMapper.getSearchProjects(keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 普通用户搜索专家
     *
     * @param keyword 关键字
     * @param index   索引
     * @param size    大小
     * @param pageNum 总页数
     * @return List<Expert>
     */
    @Override
    @NotNull
    public List<Expert> searchExperts(String keyword, String index, String size, IntegerWrapper pageNum) {
        try {
            StringBuilder keywords = new StringBuilder();
            for (int i = 0; i < keyword.length(); i++) {
                keywords.append("%");
                keywords.append(keyword.charAt(i));
            }
            keywords.append("%");
            Integer indexInt = Integer.valueOf(index);
            Integer sizeInt = Integer.valueOf(size);
            pageNum.value = (int) Math.ceil(1.0 * searchMapper.getExpCount(keywords.toString()) / sizeInt);
            PageHelper.startPage(indexInt, sizeInt);
            return searchMapper.getSearchExperts(keywords.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 统计结果
     */
    public JSONArray getTotal(String keyword) {
        ArrayList<Integer> total = new ArrayList<>();
        try {
            total.add(searchMapper.getPapersCount(keyword));
            total.add(searchMapper.getPatentsCount(keyword));
            total.add(searchMapper.getProjectsCount(keyword));
            StringBuilder keywords = new StringBuilder();
            for (int i = 0; i < keyword.length(); i++) {
                keywords.append("%");
                keywords.append(keyword.charAt(i));
            }
            keywords.append("%");
            total.add(searchMapper.getExpCount(keywords.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONArray(total);
    }

    /**
     * 搜索标签
     */
    public List<Tag> getTagByTagName(String keyword) {
        try {
            return searchMapper.getTagByName(keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
