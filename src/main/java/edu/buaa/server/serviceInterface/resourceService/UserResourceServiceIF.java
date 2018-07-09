package edu.buaa.server.serviceInterface.resourceService;

import edu.buaa.server.dataLayer.domain.Resource;
import edu.buaa.server.dataLayer.domain.TradeResource;
import edu.buaa.server.dataLayer.domain.TransferPatent;

import java.util.List;

public interface UserResourceServiceIF {
    List<TradeResource> getTradeRecord(String userID);

    TradeResource doTrade(String type, String resID, String buyer_id);

    TransferPatent doTransfer(String fromID, String resID);

    List<Resource> getResourceByExpertID(String expertID);

    String commentRes(int score, String content, String userID, String resID);
}
