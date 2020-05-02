package peing.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordVo {
    @NotNull
    private String oldPassword;
    @NotNull
    private String newPassword;
}
