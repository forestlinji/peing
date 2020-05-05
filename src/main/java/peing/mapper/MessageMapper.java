package peing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import peing.pojo.Message;

@Mapper
@Repository
public interface MessageMapper extends BaseMapper<Message> {
    /**
     * 根据userid查询消息
     * @param messagePage
     * @param userId
     * @return
     */
    @Select("select m.message_id,m.title,m.publish_date,mu.is_read from message m,message_user mu where mu.user_id = #{userId} and mu.message_id = m.message_id order by m.message_id desc")
    Page<Message> selectByUserId(Page<Message> messagePage, Long userId);

    /**
     * 插入消息
     * @param messageId
     * @param userId
     */
    @Insert("insert into message_user value(#{messageId},#{userId},0)")
    void insert2MessageUser(Long messageId, Long userId);

    /**
     * 修改消息状态
     * @param userId
     * @param messageId
     */
    @Update("update message_user set is_read = 1 where user_id = #{userId} and message_id = #{messageId}")
    void setIsRead(Long userId, Long messageId);

    /**
     * 根据消息id查询消息详情
     * @param userId
     * @param messageId
     * @return
     */
    @Select("select m.message_id,m.title,m.content,m.publish_date,mu.is_read from message m,message_user mu where mu.user_id = #{userId} and mu.message_id = m.message_id and mu.message_id = #{messageId}")
    Message selectMessageById(Long userId, Long messageId);

    /**
     * 获取未读消息数量
     * @param userId
     * @return
     */
    @Select("select count(message_id) from message_user where user_id = #{userId} and is_read = 0")
    Integer countUnreadMessage(Long userId);
}
