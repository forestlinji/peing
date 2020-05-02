package peing.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import peing.pojo.Question;
import peing.vo.QuestionedAdminVo;
import peing.vo.QuestionerAdminVo;

public interface QuestionService {
    /**
     * 根据问题id查找问题
     * @param questionId
     * @return
     */
    Question selectQuestionById(Long questionId);

    /**
     * 更新问题状态(回复，删除等等)
     * @param question
     */
    void updateQuestion(Question question);

    /**
     * 增加问题
     * @param question
     */
    void insertQuestion(Question question);

    /**
     * 查询被删除的问题
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<Question> selectDeletedQuestion(Long userId,int pageNum,int pageSize);

    /**
     * 查询用户收到的问题
     * @param userId
     * @param state 回复状态，0未回复,1已回复,2全部
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<Question> selectQuestionByUser(Long userId, int state, int pageNum, int pageSize);

    /**
     * 真实删除问题
     * @param questionId
     */
    void realDeleted(Long questionId);

    /**
     * 查询用户提出的问题
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<QuestionedAdminVo> selectMyQuestion(Long userId, int pageNum, int pageSize);

    /**
     * 查询被拉黑的问题
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<Question> selectBanQuestion(Long userId, int pageNum, int pageSize);

    /**
     * 管理员查询问题
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<QuestionerAdminVo> selectQuestionerAdminVo(Long userId,int pageNum,int pageSize);
}
