package peing.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminVo {
     @JsonSerialize(using = ToStringSerializer.class)
     private Long userId;
     private String username;
     private String email;
     private Boolean isActive;
     private Boolean isBan;
     private Integer questionedNum;
     private Integer questionerNum;
     private Integer replyNum;
}
