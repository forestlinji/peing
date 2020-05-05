package peing.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 消息类，前端未实现
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    /**
     * 消息id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using= ToStringSerializer.class)
    private Long messageId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String content;
    /**
     * 发布时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishDate;
    /**
     * 是否已读
     */
    private Boolean isRead;
}
