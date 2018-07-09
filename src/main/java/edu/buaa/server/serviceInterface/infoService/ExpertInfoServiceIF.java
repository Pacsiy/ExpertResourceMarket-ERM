package edu.buaa.server.serviceInterface.infoService;

import edu.buaa.server.dataLayer.domain.Expert;

import java.math.BigInteger;
import java.util.ArrayList;

public interface ExpertInfoServiceIF {
    Expert UserToExpert(String userID);

    Expert getExpertInfo(String expertID);

    void modifyExpertInfo(String expertID, String phone,
                          String email, String constitution, String intro, String backgroundImg, ArrayList<BigInteger> tagList);
}
