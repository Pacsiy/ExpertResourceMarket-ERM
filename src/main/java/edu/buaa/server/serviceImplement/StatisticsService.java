package edu.buaa.server.serviceImplement;

import edu.buaa.server.dataLayer.domain.Expert;
import edu.buaa.server.dataLayer.domain.HotTagsBean;
import edu.buaa.server.dataLayer.domain.Paper;
import edu.buaa.server.dataLayer.domain.Tag;
import edu.buaa.server.dataLayer.mapper.StatisticMapper;
import edu.buaa.server.serviceInterface.StatisticsServiceIF;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class StatisticsService implements StatisticsServiceIF {
    private final StatisticMapper statisticMapper;

    public StatisticsService(StatisticMapper statisticMapper) {
        this.statisticMapper = statisticMapper;
    }

    /**
     * 获取Top5火热的专家，评价方法是该专家近七天他的资源被购买/租用的次数
     * 已经用一个视图RecentTrade实现。
     *
     * @return 长度为5的专家列表，
     * expert里面只需要ID和name就行，其他字段的可以不管
     */
    @Override
    @NotNull
    public List<Expert> getTop5Experts() {
        try {
            return statisticMapper.getTop5Expert();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * 根据专家ID获取该专家近七天每天的资源被购买/租用数
     *
     * @param expertID 专家ID
     * @return 长度为7的整数列表
     */
    @Override
    @NotNull
    public List<Integer> getRecent7DaysTradeByExpertID(String expertID) {
        try {
            ArrayList<Integer> ret = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                ret.add(statisticMapper.getXDayBefore(String.valueOf(i + 1), String.valueOf(i), expertID));
            }
            return ret;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * 获取Top10火热的标签和热度值
     * 热度值 = 相关专家数目(专家贴上了该标签)+相关资源数目(该资源贴上了该标签)+相关用户数目（已订阅）
     * 建议先写一个视图，后面就从视图中取数据
     *
     * @return size为10的键值对数组，其中key是标签名，value是热度值；
     */
    @Override
    @NotNull
    public List<HotTagsBean> getTop10Tags() {
        try {
            return statisticMapper.getTop10Tags();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * 获取所有与领域的相关专家
     *
     * @param tagID 主题ID
     * @param tag   标签
     * @return 专家列表
     */
    @Override
    @NotNull
    public List<Expert> getRelationExpertByTag(String tagID, Tag tag) {
        try {
            tag.name = statisticMapper.getTagByID(Integer.valueOf(tagID)).name;
            return statisticMapper.getRelationExperts(Integer.valueOf(tagID));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * 获取相关paper
     *
     * @param paperID 论文ID
     * @return 论文列表
     */
    @Override
    @NotNull
    public List<Paper> getRelationPapers(String paperID) {
        try {
            Integer[] tagID = statisticMapper.getTagIDByPaperID(Integer.valueOf(paperID));
            List<Paper> resData = new ArrayList<>();
            for (Integer id:tagID) {
                resData.addAll(statisticMapper.getRelationPapers(id, Integer.valueOf(paperID)));
            }
            if (resData.size() > 5)
                resData = resData.subList(0, 5);
            return resData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * 获取某个主题下被引量前五的论文
     *
     * @param tagID 主题ID
     * @return paper列表
     */
    @Override
    @NotNull
    public List<Expert> getTop5ExpertsByTagID(Integer tagID) {
        try {
            return statisticMapper.getTop5ExpertByCitationtimes(tagID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * 获取全部集合中 被引量前五的专家
     *
     * @return 专家列表
     */
    @Override
    @NotNull
    public List<Expert> getTop5ExpertsByCitations() {
        try {
            return statisticMapper.getTop5ExpertsByCite();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * 根据PaperID获取paper_id
     *
     * @param paperID paper
     * @return paper
     */
    @Override
    public Paper getPaperByID(Integer paperID) {
        try {
            return statisticMapper.getPaperByID(paperID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得相似领域
     * @param tagID tagID
     * @return 领域列表
     */
    @Override
    @NotNull
    public List<Tag> getSimilarTags(Integer tagID) {
        try{
            return statisticMapper.getSimilarTags(tagID);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * 获得最热的十个paper
     */
    @NotNull
    public List<Paper> getHottestPapers(){
        try{
            return statisticMapper.getHottestPapers();
        }catch (Exception e){
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * 获得最新的十个Paper
     */
    @NotNull
    public List<Paper> getNewestPapers(){
        try{
            return statisticMapper.getNewestPapers();
        }catch (Exception e){
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
