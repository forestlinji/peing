package peing.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleUserInfo {
    @JsonSerialize(using= ToStringSerializer.class)
    private Long userId;
    private String username;
    private String introduction;
    private String avatar;
    private boolean acceptQuestion;
}
