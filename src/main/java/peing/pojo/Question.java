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
 * 问题类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    /**
     * 问题id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using= ToStringSerializer.class)
    private Long questionId;

    /**
     * 被提问人id
     */
    private Long questionedId;

    /**
     * 提问人id
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long questionerId;

    /**
     * 内容
     */
    private String content;
    /**
     * 提问日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date QuestionDate;

    /**
     * 逻辑删除标记
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean deleted;

    /**
     * 删除时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date deleteDate;

    /**
     * 是否被拉黑
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isBan;

    /**
     * 是否被举报
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isReport;

    /**
     * 回复
     */
    private String reply;

    /**
     * 回复时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date replyDate;
}
