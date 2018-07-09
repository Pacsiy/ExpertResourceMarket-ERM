package edu.buaa.server.serviceImplement;

import edu.buaa.server.dataLayer.mapper.MessageMapper;
import edu.buaa.server.serviceInterface.MessageServiceIF;
import org.springframework.stereotype.Service;

@Service
public class MessageService implements MessageServiceIF {
    private final MessageMapper messageMapper;

    public MessageService(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    /**
     * @param userID  用户名
     * @param title   题目
     * @param content 内容
     */
    @Override
    public void addMessage(String userID, String title, String content) {
        try {
            messageMapper.addMessage(userID, title, content);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
