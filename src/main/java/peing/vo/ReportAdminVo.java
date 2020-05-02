package peing.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import peing.pojo.Report;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportAdminVo extends Report {
    private String reporterName;
    private String reportedName;

}
