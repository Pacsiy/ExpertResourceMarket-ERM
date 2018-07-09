package edu.buaa.server.serviceInterface.resourceService;

import edu.buaa.server.dataLayer.domain.Resource;
import edu.buaa.server.util.IntegerWrapper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public interface ExpertResourceServiceIF {
    void addResource(Resource resource, ArrayList<BigInteger> tagList);

    void deleteResource(String resID, String expertID);

    void modifyResource(ArrayList<BigInteger> tagList, Resource resource);

    List<Resource> getOwnResource(String expertID);

    List<Resource> getResFromUserAspect(String index, String size, IntegerWrapper pageNum, String expertID);
}
