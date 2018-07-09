package edu.buaa.server;

import edu.buaa.server.dataLayer.domain.Resource;
import edu.buaa.server.dataLayer.mapper.Resource.ExpertResourceMapper;
import edu.buaa.server.serviceImplement.resourceService.UserResourceService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
public class HelloController {
    private final UserResourceService userResourceService;
    private final ExpertResourceMapper expertResourceMapper;

    public HelloController(UserResourceService userResourceService, ExpertResourceMapper expertResourceMapper) {
        this.userResourceService = userResourceService;
        this.expertResourceMapper = expertResourceMapper;
    }

    @RequestMapping("/")
    public String index() {
        Resource resource = userResourceService.getResourceByID("1");
        return Arrays.toString(resource.authors);
//        Resource resource = new Resource();
//        resource.title = "liu's insert test";
//        resource.type = ResourceType.PATENT;
//        resource.leader_id = new BigInteger("1");
//        resource.file_path = "";
//        resource.html = "";
//        resource.url = "";
//        resource.member_id_list = new Long[10];
//        resource.member_id_list[0] = 0L;
//        resource.member_id_list[1] = 1L;
//        resource.authors = new String[10];
//        resource.authors[0] = "0";
//        resource.authors[1] = "1";
//        expertResourceMapper.addResource(resource);
//        return "success";
    }
}
