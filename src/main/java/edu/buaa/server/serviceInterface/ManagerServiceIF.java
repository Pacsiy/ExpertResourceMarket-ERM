package edu.buaa.server.serviceInterface;

import edu.buaa.server.dataLayer.domain.*;
import edu.buaa.server.util.IntegerWrapper;

import java.util.List;

public interface ManagerServiceIF {
    Manager login(String cellphone, String password);

    List<Message> checkBackgroundMessages(String pageNum, String pageSize, IntegerWrapper pageNumCount);

    TradeResource getTradeRecordByID(String tradeID);

    List<ExpertApply> getAllExpertApplies(String pageNum, String pageSize, IntegerWrapper pageNumCount);

    void handleExpertApply(String expertID, String applyID);

    void banUser(String id, String type, String operation);

    List<Resource> getAllPendingResource(String pageNum, String pageSize, IntegerWrapper pageNumCount);

    void handlePendingResource(String resID, String state);

    List<TradeResource> getTradeRecord(String index, String size, IntegerWrapper pageNum);

}
