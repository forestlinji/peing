package peing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import peing.pojo.Question;
import peing.vo.QuestionedAdminVo;
import peing.vo.QuestionerAdminVo;

@Mapper
@Repository
public interface QuestionMapper extends BaseMapper<Question> {
    /**
     * 管理员查询用户提的问题
     * @param objectPage
     * @param userId
     * @return
     */
    @Select("SELECT q.*,u.username questionedName FROM `question` q ,`user` u where u.user_id = q.questioned_id and q.questioner_id = #{userId}")
    Page<QuestionedAdminVo> selectMyPage(Page<Object> objectPage, Long userId);
    //quesstioned是userid，questionername要查

    /**
     * 管理员查询用户收到的问题
     * @param objectPage
     * @param userId
     * @return
     */
    @Select("SELECT q.*,u.username questionerName FROM `question` q ,`user` u where u.user_id = q.questioner_id and q.questioned_id = #{userId}")
    Page<QuestionerAdminVo> selectQuestionedPage(Page<Object> objectPage, Long userId);
}
