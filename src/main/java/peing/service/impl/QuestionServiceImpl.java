package peing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import peing.mapper.QuestionMapper;
import peing.pojo.Question;
import peing.service.QuestionService;
import peing.vo.QuestionedAdminVo;
import peing.vo.QuestionerAdminVo;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Question selectQuestionById(Long questionId) {
        return questionMapper.selectById(questionId);
    }

    @Override
    public void updateQuestion(Question question) {
        questionMapper.updateById(question);
    }

    @Override
    public void insertQuestion(Question question) {
        questionMapper.insert(question);
    }

    @Override
    public Page<Question> selectDeletedQuestion(Long userId,int pageNum,int pageSize) {
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        //查找已被删除的问题
        questionQueryWrapper.eq("questioned_id",userId).eq("deleted",true);
        return questionMapper.selectPage(new Page<>(pageNum,pageSize),questionQueryWrapper);
    }

    @Override
    public Page<Question> selectQuestionByUser(Long userId, int state, int pageNum, int pageSize) {
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("questioned_id",userId);
        //查找已被删除的问题
        questionQueryWrapper.eq("deleted",false);
        if(state == 0){
            questionQueryWrapper.isNull("reply");
        }
        else if(state == 1){
            questionQueryWrapper.isNotNull("reply");
        }
        return questionMapper.selectPage(new Page<Question>(pageNum, pageSize), questionQueryWrapper);
    }

    @Override
    public void realDeleted(Long questionId) {
        questionMapper.deleteById(questionId);
    }

    @Override
    public Page<QuestionedAdminVo> selectMyQuestion(Long userId, int pageNum, int pageSize) {
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("questioner_id",userId);
//        questionQueryWrapper.eq("deleted",false);
        return questionMapper.selectMyPage(new Page<>(pageNum,pageSize),userId);
    }

    @Override
    public Page<Question> selectBanQuestion(Long userId, int pageNum, int pageSize) {
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("questioned_id",userId).eq("is_ban",true);
        return questionMapper.selectPage(new Page<>(pageNum,pageSize),questionQueryWrapper);
    }

    @Override
    public Page<QuestionerAdminVo> selectQuestionerAdminVo(Long userId, int pageNum, int pageSize) {
        return questionMapper.selectQuestionedPage(new Page<>(pageNum,pageSize),userId);
    }
}
