package peing.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 用户普通信息，例如提问箱状态，个人简介等
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class UserInfo {
    @TableId
    @JsonSerialize(using= ToStringSerializer.class)
    private Long userId;
    private String username;
    private String email;
    private Boolean isActive;
    private Boolean isBan;

    /**
     * 是否开启提问箱
     */
    private Boolean acceptQuestion;

    /**
     * 头像文件名
     */
    @JsonIgnore
    private String avatar;

    /**
     * 自我介绍
     */
    private String introduction;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date signupDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date banDate;
    @TableField(exist = false)
    private List<String> roles;
}
