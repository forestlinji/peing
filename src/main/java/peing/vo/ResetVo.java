package peing.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetVo {
    @NotNull
    private String changeToken;
    @NotNull
    private String password;
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
}
