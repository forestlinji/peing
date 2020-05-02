package peing.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 拉黑，多对多
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ban {
    /**
     * 拉黑者id
     */
    @TableId
    @JsonSerialize(using= ToStringSerializer.class)
    private Long banerId;
    /**
     * 被拉黑者id
     */
    @TableId
    private Long banedId;
    /**
     * 拉黑次数
     * 这里允许的重复拉黑是考虑到多个问题的，如果用户b向用户a提了多个问题
     * 如果这么多个问题有不止一个被用户a拉黑，那么这里的count++
     * 而直至count减为0位置都算作拉黑状态
     */
    private Integer count;
}
