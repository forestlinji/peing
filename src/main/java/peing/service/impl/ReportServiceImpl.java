package peing.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import peing.mapper.ReportMapper;
import peing.pojo.PageResult;
import peing.pojo.Report;
import peing.service.ReportService;
import peing.vo.ReportAdminVo;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;

    @Override
    public void saveReport(Report report) {
        reportMapper.insert(report);
    }

    @Override
    public PageResult<ReportAdminVo> getReportList(int pageNum, int pageSize) {
        Page<ReportAdminVo> reportResult = reportMapper.queryReportList(new Page<ReportAdminVo>(pageNum, pageSize));
        PageResult<ReportAdminVo> reportPageResult = new PageResult<>(reportResult);
        return reportPageResult;
    }

    @Override
    public Report getReportById(int reportId) {
        return reportMapper.selectById(reportId);
    }

    @Override
    public void update(Report report) {
        reportMapper.updateById(report);
    }
}
