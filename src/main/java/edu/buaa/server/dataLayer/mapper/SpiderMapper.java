package edu.buaa.server.dataLayer.mapper;

import edu.buaa.server.dataLayer.domain.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SpiderMapper {
    @Insert("insert into tags(name) values(#{name})")
    @Options(useGeneratedKeys = true)
    void insertTags(Tag tag);

    @Select("select * from tags where name = #{name}")
    Tag doesTagExist(Tag tag);

    @Insert("insert into experts(name) values(#{name})")
    @Options(useGeneratedKeys = true)
    void insertExpertsOnlyName(Expert expert);

    @Insert("INSERT INTO experts (introduction, phone, email, constitution, name, major,\n" +
            "                      scholar_id, citation_times, article_numbers, h_index, g_index,\n" +
            "                     history_article_numbers, history_citation_times)\n" +
            "VALUES (#{introduction}, #{phone}, #{email}, #{constitution}, #{name}, #{major}, \n" +
            "                         #{scholar_id}, #{citation_times}, #{article_numbers}, #{h_index}, #{g_index},\n" +
            "        #{history_article_numbers}, #{history_citation_times}) " +
            "ON CONFLICT (name)\n" +
            "  DO UPDATE SET introduction = #{introduction}, phone= #{phone}, constitution= #{constitution}, name = #{name},\n" +
            "major=#{major}, scholar_id= #{scholar_id}, citation_times= #{citation_times},\n" +
            "article_numbers=#{article_numbers}, h_index= #{h_index}, g_index= #{g_index},\n" +
            "history_article_numbers= #{history_article_numbers}, " +
            "history_citation_times = #{history_citation_times}")
    @Options(useGeneratedKeys = true)
    void insertOrResetExpert(Expert expert);

    @Select("select * from experts where name = #{name} ")
    Expert doesExpertExist(Expert expert);

    @Insert("insert into resource_tag(resource_id,tag_id) " +
            " values(#{resID,jdbcType=BIGINT},#{tagID,jdbcType=BIGINT})")
    void insertResTag(@Param("resID") String resID,
                      @Param("tagID") String tagID);

    @Insert("INSERT INTO projects (title, leader_id, introduction, file_path, html, url, member_id_list, institution,\n" +
            "                      keywords, chinese_library_classification, subject_classification, category, level,\n" +
            "                      duration, evaluation_form, storage_year, authors, check_state)\n" +
            "VALUES (#{title}, #{leader_id,jdbcType=BIGINT}, #{introduction}, #{file_path}, #{html}, #{url}, #{member_id_list}, #{institution},\n" +
            "               #{keywords}, #{chinese_library_classification}, #{subject_classification}, #{category}, #{level},\n" +
            "        #{duration}, #{evaluation_form}, #{storage_year}, #{authors},#{check_state}::check_state)")
    void insertProjects(Project project);

    @Insert("INSERT INTO patents(title, leader_id, introduction, file_path, html, url, member_id_list,\n" +
            "                    application_number, publication_number, application_date, publication_date, address,\n" +
            "                    inventor, agency, agent, national_code, sovereignty_item, page_number, patent_main_class,\n" +
            "                    patent_class, authors,check_state) \n" +
            "VALUES (#{title}, #{leader_id,jdbcType=BIGINT}, #{introduction}, #{file_path}, #{html}, #{url}, #{member_id_list},\n" +
            "  #{application_number}, #{publication_number}, #{application_date}, #{publication_date}, #{address},\n" +
            "  #{inventor}, #{agency}, #{agent}, #{national_code}, #{sovereignty_item}, #{page_number}, #{patent_main_class},\n" +
            "  #{patent_class}, #{authors},#{check_state}::check_state) ")
    void insertPatent(Patent patent);

    @Insert("INSERT INTO papers (title, leader_id, introduction, file_path, html, url, member_id_list,\n" +
            "                     publish, citation_times, source, classification, keywords,\n" +
            "                    annual_citation, authors,check_state)\n" +
            "VALUES (#{title}, #{leader_id,jdbcType=BIGINT}, #{introduction}, #{file_path}, #{html}, #{url}, #{member_id_list},\n" +
            "                #{publish}, #{citation_times}, #{source}, #{classification}, #{keywords},\n" +
            "        #{annual_citation}, #{authors},#{check_state}::check_state)")
    @Options(useGeneratedKeys = true)
    void insertPaper(Paper paper);


    @Insert("INSERT INTO expert_tag(expert_id, tag_id) VALUES (#{expertID,jdbcType=BIGINT},#{tagID,jdbcType=BIGINT});")
    void insertExpertTag(@Param("expertID") String expertID, @Param("tagID") String tagID);

}
