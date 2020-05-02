package peing.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import peing.pojo.*;
import peing.service.QuestionService;
import peing.service.UserService;
import peing.vo.QuestionedAdminVo;
import peing.vo.QuestionPostVo;
import peing.vo.QuestionerAdminVo;
import peing.vo.ReplyVo;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 提问模块相关接口
 */
@RestController
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    private CurrentUser currentUser;
    @Autowired
    private UserService userService;
    @Autowired
    private QuestionService questionService;

    /**
     * 用于从数据中删除提问人
     * @param questionPage
     * @return
     */
    private Page<Question> deleteQuestioner(Page<Question> questionPage){
        List<Question> records = questionPage.getRecords();
        records = records.stream().map(question -> {
            question.setQuestionerId(null);
            return question;
        }).collect(Collectors.toList());
        questionPage.setRecords(records);
        return questionPage;
    }

    /**
     * 修改提问箱状态
     * @param acceptQuestion
     * @return
     */
    @GetMapping("changeState")
    public ResponseJson changeState(@RequestParam(required = true) boolean acceptQuestion){
        Long userId = Long.parseLong(currentUser.getCurrentUser().getUsername());
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setAcceptQuestion(acceptQuestion);
        userService.updateInfo(userInfo);
        return new ResponseJson(ResultCode.SUCCESS);
    }

    @PostMapping("addQuestion")
    public ResponseJson addQuestion(@RequestBody @Valid QuestionPostVo questionPostVo){
        Long questionedId = questionPostVo.getQuestionedId();
        String content = questionPostVo.getContent();
        Long userId = Long.parseLong(currentUser.getCurrentUser().getUsername());
        UserInfo userInfo = userService.getUserInfoById(questionedId);
        //数据校验，要查询问题是否存在，是否被拉黑
        if (userInfo == null||!userInfo.getAcceptQuestion()||StringUtils.isEmpty(content)||userInfo.getIsBan()) {
            return new ResponseJson(ResultCode.UNVALIDPARAMS);
        }
        Integer hasBan = userService.hasBan(questionedId,userId);
        if (hasBan != null) {
            return new ResponseJson(ResultCode.HASBANED);
        }
        Question question = new Question();
        question.setQuestionedId(questionedId);
        question.setQuestionerId(userId);
        question.setQuestionDate(new Date());
        question.setContent(content);
        question.setDeleted(false);
        question.setIsBan(false);
        question.setIsReport(false);
        questionService.insertQuestion(question);
        return new ResponseJson(ResultCode.SUCCESS);
    }

    @PostMapping("reply")
    public ResponseJson reply(@RequestBody @Valid ReplyVo replyVo){
        Long questionId = replyVo.getQuestionId();
        String reply = replyVo.getReply();
        Long userId = Long.parseLong(currentUser.getCurrentUser().getUsername());
        Question question = questionService.selectQuestionById(questionId);
        if(question == null ||question.getDeleted()||!question.getQuestionedId().equals(userId)){
            return new ResponseJson(ResultCode.UNVALIDPARAMS);
        }
        question.setReply(reply);
        question.setReplyDate(new Date());
        questionService.updateQuestion(question);
        return new ResponseJson(ResultCode.SUCCESS);
    }

    @DeleteMapping("{questionId}")
    public ResponseJson deleteQuestion(@PathVariable("questionId")Long questionId){
        Question question = questionService.selectQuestionById(questionId);
        Long userId = Long.parseLong(currentUser.getCurrentUser().getUsername());
        if (question == null || question.getDeleted()||!question.getQuestionedId().equals(userId)) {
            return new ResponseJson(ResultCode.UNVALIDPARAMS);
        }
        //逻辑删除
        question.setDeleted(true);
        question.setDeleteDate(new Date());
        questionService.updateQuestion(question);
        return new ResponseJson(ResultCode.SUCCESS);
    }

    /**
     * 获取删除的提问
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("getDeleted")
    public ResponseJson<PageResult<Question>> getDeleted(@RequestParam(defaultValue = "1")int pageNum,
                                                         @RequestParam(defaultValue = "5")int pageSize){
        if(pageNum<=0||pageSize<=0||pageSize>50){
            return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
        }
        Long userId = Long.parseLong(currentUser.getCurrentUser().getUsername());
        Page<Question> questionPage = questionService.selectDeletedQuestion(userId,pageNum,pageSize);
        questionPage = deleteQuestioner(questionPage);
        return new ResponseJson<>(ResultCode.SUCCESS,new PageResult<>(questionPage));
    }

    /**
     * 恢复删除的提问
     * @param questionId
     * @return
     */
    @GetMapping("resume")
    public ResponseJson resume(@RequestParam(required = true) Long questionId){
        Long userId = Long.parseLong(currentUser.getCurrentUser().getUsername());
        Question question = questionService.selectQuestionById(questionId);
        if(question == null || !question.getQuestionedId().equals(userId)|| !question.getDeleted()){
            return new ResponseJson(ResultCode.UNVALIDPARAMS);
        }
        question.setDeleted(false);
        question.setDeleteDate(null);
        questionService.updateQuestion(question);
        return new ResponseJson(ResultCode.SUCCESS);
    }

    /**
     * 获得当前账户收到的提问
     * @param pageNum
     * @param pageSize
     * @param state
     * @return
     */
    @GetMapping("getMyQuestioned")
    public ResponseJson<PageResult<Question>> getMyQuestioned(@RequestParam(defaultValue = "1")int pageNum,
                                                            @RequestParam(defaultValue = "5")int pageSize,
                                                            @RequestParam(defaultValue = "0") int state){
        if(pageNum<=0||pageSize<=0||pageSize>50||(state!=0&&state!=1)){
            return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
        }
        Long userId = Long.parseLong(currentUser.getCurrentUser().getUsername());
        Page<Question> questionPage = questionService.selectQuestionByUser(userId,state,pageNum,pageSize);
        questionPage = deleteQuestioner(questionPage);
        return new ResponseJson<>(ResultCode.SUCCESS,new PageResult<>(questionPage));
    }

    /**
     * 查看他人已回复的提问
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    @GetMapping("getOtherQuestion")
    public ResponseJson<PageResult<Question>> getOtherQuestion(@RequestParam(defaultValue = "1")int pageNum,
                                                               @RequestParam(defaultValue = "5")int pageSize,
                                                               @RequestParam(required = true) Long userId){
        if(pageNum<=0||pageSize<=0||pageSize>50||userService.getUserInfoById(userId)==null){
            return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
        }
        Page<Question> questionPage = questionService.selectQuestionByUser(userId, 1, pageNum, pageSize);
        questionPage = deleteQuestioner(questionPage);
        return new ResponseJson<>(ResultCode.SUCCESS,new PageResult<>(questionPage));
    }

    /**
     * 提问撤回
     * @param questionId
     * @return
     */
    @DeleteMapping("regret")
    public ResponseJson regret(@RequestParam(required = true) Long questionId){
//        Long questionIdL = Long.parseLong(questionId);
        Long userId = Long.parseLong(currentUser.getCurrentUser().getUsername());
        Question question = questionService.selectQuestionById(questionId);
        //提问者可以撤回提问当且仅当该提问还没有被回复
        if(question == null || question.getDeleted() || !question.getQuestionerId().equals(userId) || !StringUtils.isEmpty(question.getReply())){
            return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
        }
        questionService.realDeleted(questionId);
        return new ResponseJson(ResultCode.SUCCESS);
    }

    /**
     * 获取自己对他人的提问
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("getMyQuestion")
    public ResponseJson<PageResult<QuestionedAdminVo>> getMyQuestion(@RequestParam(defaultValue = "1")int pageNum,
                                                                     @RequestParam(defaultValue = "5")int pageSize){
        if(pageNum<=0||pageSize<=0||pageSize>50){
            return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
        }
        Long userId = Long.parseLong(currentUser.getCurrentUser().getUsername());
        Page<QuestionedAdminVo> questionPage = questionService.selectMyQuestion(userId, pageNum, pageSize);
        return new ResponseJson<>(ResultCode.SUCCESS,new PageResult<>(questionPage));
    }

    /**
     * 用户管理接口，获取收到的提问
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    @GetMapping("getAll")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ROOT')")
    public ResponseJson<PageResult<QuestionerAdminVo>> getAll(@RequestParam(defaultValue = "1")int pageNum,
                                                              @RequestParam(defaultValue = "5")int pageSize,
                                                              @RequestParam(required = true) Long userId){
        if(pageNum<=0||pageSize<=0||pageSize>50){
            return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
        }
        Page<QuestionerAdminVo> questionPage = questionService.selectQuestionerAdminVo(userId,pageNum,pageSize);
        return new ResponseJson<>(ResultCode.SUCCESS,new PageResult<>(questionPage));
    }

    /**
     * 用户管理接口，获取发出的提问
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    @GetMapping("getQuestions")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ROOT')")
    public ResponseJson<PageResult<QuestionedAdminVo>> getQuestions(@RequestParam(defaultValue = "1")int pageNum,
                                                                    @RequestParam(defaultValue = "5")int pageSize,
                                                                    @RequestParam(required = true) Long userId){
        if(pageNum<=0||pageSize<=0||pageSize>50){
            return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
        }
        Page<QuestionedAdminVo> questionPage = questionService.selectMyQuestion(userId, pageNum, pageSize);
        return new ResponseJson<>(ResultCode.SUCCESS,new PageResult<>(questionPage));
    }



}
