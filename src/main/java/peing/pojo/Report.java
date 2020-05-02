package peing.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 举报
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    /**
     * 举报id
     */
    @TableId(type = IdType.AUTO)
    private Integer reportId;

    /**
     * 举报人id
     */
    private Long reporterId;

    /**
     * 被举报人id
     */
    private Long reportedId;

    /**
     * 举报时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reportDate;

    /**
     * 被举报的问题id
     */
    @JsonIgnore
    private Long questionId;

    /**
     * 被举报的问题内容，需要联表查
     */
    @TableField(exist = false)
    private String content;

    /**
     * 举报理由
     */
    private String reason;

    /**
     * 举报处理结果
     */
    private Integer result;

    public Report(Question question,String reason){
        this.reporterId = question.getQuestionedId();
        this.reportedId = question.getQuestionerId();
        this.questionId = question.getQuestionId();
        this.reason = reason;
        this.result = 0;
        this.reportDate = new Date();
    }


}
