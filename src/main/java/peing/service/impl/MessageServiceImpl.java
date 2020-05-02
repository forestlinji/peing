package peing.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import peing.mapper.MessageMapper;
import peing.mapper.UserInfoMapper;
import peing.mapper.UserMapper;
import peing.pojo.Message;
import peing.service.MessageService;
import peing.service.UserService;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public Page<Message> selectMessageByUserId(Long userId, int pageNum, int pageSize) {
        return messageMapper.selectByUserId(new Page<Message>(pageNum,pageSize),userId);
    }

    @Override
//    @Async
    public void publishAnnouncement(Message message) {
//        messageMapper.insert(message);
        List<Long> userIds = userInfoMapper.selectAllUserId();
        userIds.forEach(userId->{
            System.out.println(userId);
            messageMapper.insert2MessageUser(message.getMessageId(),userId);
        });
    }

    @Override
    public void setIsRead(Long userId, Long messageId) {
        messageMapper.setIsRead(userId,messageId);
    }

    @Override
    public Message selectByMessageId(Long userId,Long messageId) {
        return messageMapper.selectMessageById(userId,messageId);
    }

    @Override
    public Integer countUnreadMessage(Long userId) {
        return messageMapper.countUnreadMessage(userId);
    }
}
