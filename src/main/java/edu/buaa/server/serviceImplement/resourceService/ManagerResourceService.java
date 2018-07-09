package edu.buaa.server.serviceImplement.resourceService;

import edu.buaa.server.dataLayer.domain.Resource;
import edu.buaa.server.serviceImplement.EnumType.Role;
import edu.buaa.server.serviceInterface.resourceService.ManagerResourceServiceIF;
import org.springframework.stereotype.Service;

@Service
public class ManagerResourceService implements ManagerResourceServiceIF {
    private final CommonResourceService commonResourceService;

    public ManagerResourceService(CommonResourceService commonResourceService) {
        this.commonResourceService = commonResourceService;
    }

    public Resource getResourceByID(String resID) {
        return commonResourceService.getResourceByID(Role.MANAGER, resID);
    }
}
