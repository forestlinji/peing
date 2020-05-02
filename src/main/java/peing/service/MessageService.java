package peing.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import peing.pojo.Message;

/**
 * 消息系统的服务，前端未实现
 */
public interface MessageService {
    Page<Message> selectMessageByUserId(Long userId, int pageNum, int pageSize);

    void publishAnnouncement(Message message);

    void setIsRead(Long userId, Long messageId);

    Message selectByMessageId(Long userId,Long messageId);

    Integer countUnreadMessage(Long userId);

}
