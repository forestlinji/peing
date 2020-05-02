package peing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import peing.pojo.PageResult;
import peing.pojo.Report;
import peing.vo.ReportAdminVo;

@Mapper
@Repository
public interface ReportMapper extends BaseMapper<Report> {
    //又是一坨遭的sql，同样不想写xml
    /**
     * 获取举报列表
     * @param reportPage
     * @return
     */
    @Select("SELECT\n" +
            "\tr.report_id,\n" +
            "\tr.reporter_id,\n" +
            "\tr.reported_id,\n" +
            "\tq.question_id,\n" +
            "\tq.content,\n" +
            "\tu1.username reporterName,\n" +
            "\tu2.username reportedName,\n" +
            "\tr.reason,\n" +
            "\tr.report_date,\n" +
            "\tr.result \n" +
            "FROM\n" +
            "\treport r\n" +
            "\tLEFT JOIN question q ON r.question_id = q.question_id\n" +
            "\tJOIN `user` u1 ON u1.user_id = r.reporter_id\n" +
            "\tJOIN `user` u2 ON u2.user_id = r.reported_id \n" +
            "ORDER BY\n" +
            "\tr.report_date DESC")
    Page<ReportAdminVo> queryReportList(Page<ReportAdminVo> reportPage);
}
