package edu.buaa.server.serviceImplement;

import edu.buaa.server.dataLayer.domain.*;
import edu.buaa.server.dataLayer.mapper.SpiderMapper;
import edu.buaa.server.serviceInterface.SpiderServiceIF;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpiderService implements SpiderServiceIF {
    private final SpiderMapper spiderMapper;

    public SpiderService(SpiderMapper spiderMapper) {
        this.spiderMapper = spiderMapper;
    }

    /**
     * 先把资源的Tag(领域)给放进去，然后获取到每个tag是自增长ID
     *
     * @param tag 标签列表
     */
    @Override
    public void insertTag(Tag tag) {
        try {
            Tag tag1 = spiderMapper.doesTagExist(tag);
            if (tag1 == null) {
                spiderMapper.insertTags(tag);
            } else {
                tag.id = tag1.id;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("插入领域/标签出错");
        }
    }

    /**
     * 插入专家，以获取到每个专家的ID,数据库中有的就直接获取ID，数据库中没有的，
     * 就把这个专家放进experts表中。(默认爬取到的专家的名字不会重名，毕竟爬虫
     * 不能判断相同名字的专家到底是几个人)
     *
     * @param experts 专家列表
     */
    @Override
    public void insertExpertsByRes(ArrayList<Expert> experts) {
        try {
            for (Expert expert1 : experts) {
                Expert expert = spiderMapper.doesExpertExist(expert1);
                if (expert == null) {
                    spiderMapper.insertExpertsOnlyName(expert1);
                } else {
                    expert1.id = expert.id;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("插入专家时出错");
        }
    }

    /**
     * 插入资源，这一步操作的时候设计到很多事情
     * 1. 首先插入到资源表中，获取到资源的自增长ID,
     * 2. 根据资源的标签ID和资源ID,插入tag-resource表中
     *
     * @param resource 资源
     * @param tagList  标签,当detail是patent时
     * @param experts  与该资源相关专家列表
     */
    @Override
    public void insertResource(Resource resource, List<Tag> tagList, ArrayList<Expert> experts) {
        resource.member_id_list = new Long[experts.size()];
        for (int i = 0; i < experts.size(); i++) {
            resource.member_id_list[i] = experts.get(i).id.longValue();
        }
        try {
            if (resource instanceof Paper) {
                Paper paper = (Paper) resource;
                spiderMapper.insertPaper(paper);
                for (Tag tag : tagList) {
                    spiderMapper.insertResTag(paper.id.toString(), tag.id.toString());
                }
            } else if (resource instanceof Patent) {
                Patent patent = (Patent) resource;
                spiderMapper.insertPatent(patent);
            } else if (resource instanceof Project) {
                Project project = (Project) resource;
                spiderMapper.insertProjects(project);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("插入资源时出错");
        }
    }

    /**
     * 插入专家，如果专家存在，更新信息；专家不存在，插入.
     * 插入专家-tag表
     *
     * @param expert  专家
     * @param tagList 专家的领域
     */
    @Override
    public void insertExpert(Expert expert, List<Tag> tagList) {
        try {
            spiderMapper.insertOrResetExpert(expert);
            for (Tag tag : tagList) {
                try {
                    //加trycatch的原因是为了，防止在插入专家-Tag时因出现重复的错误而抛异常。
                    spiderMapper.insertExpertTag(expert.id.toString(), tag.id.toString());
                } catch (Exception e) {
                    System.out.println("duplicate key in expert-tag");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("插入专家过程出错");
        }
    }
}
