package peing.service;

import peing.pojo.PageResult;
import peing.pojo.Report;
import peing.vo.ReportAdminVo;

public interface ReportService {
    /**
     * 新增举报
     * @param report
     */
    void saveReport(Report report);

    /**
     * 获取举报列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResult<ReportAdminVo> getReportList(int pageNum, int pageSize);

    /**
     * 根据举报id查询举报
     * @param reportId
     * @return
     */
    Report getReportById(int reportId);

    /**
     * 更新举报状态
     * @param report
     */
    void update(Report report);
}
