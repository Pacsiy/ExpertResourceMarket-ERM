package edu.buaa.server.serviceInterface;

import edu.buaa.server.dataLayer.domain.Expert;
import edu.buaa.server.dataLayer.domain.Resource;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public interface RecommendServiceIF {
    List<Expert> getExpertByIDArray(ArrayList<BigInteger> expertIDList);

    List<Resource> getResourceByResIDArray(ArrayList<BigInteger> resIDList);
}
