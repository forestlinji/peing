package peing.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * rbac模型里的角色
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @TableId
    @JsonSerialize(using= ToStringSerializer.class)
    private Long roleId;
    private String roleName;
    private String description;
}
