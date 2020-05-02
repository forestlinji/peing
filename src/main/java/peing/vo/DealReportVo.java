package peing.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DealReportVo {
    @NotNull
    private int reportId;
    @Min(value = 1)
    @Max(value = 2)
    @NotNull
    private int status;
}
