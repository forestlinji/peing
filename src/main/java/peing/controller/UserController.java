package peing.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import peing.pojo.*;
import peing.service.BanService;
import peing.service.QuestionService;
import peing.service.ReportService;
import peing.service.UserService;
import peing.vo.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 用户相关接口
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private CurrentUser currentUser;
    @Autowired
    private UserService userService;
    @Autowired
    private BanService banService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取当前登录用户信息(含权限)
     * @return
     */
    @GetMapping("/info")
    public ResponseJson<UserInfo> getCurrentUserInfo(){
        JwtUser currentUser = this.currentUser.getCurrentUser();
        UserInfo userInfo = userService.getCurrentUserInfo(Long.parseLong(currentUser.getUsername()));
        return new ResponseJson<UserInfo>(ResultCode.SUCCESS,userInfo);
    }

    @PostMapping("/avatar")
    public ResponseJson avatar(@RequestParam("file") MultipartFile avatar){
        //判断文件格式
        String suffixList = "jpg,gif,png,bmp";
        String uploadFileName = avatar.getOriginalFilename();
        String suffix = uploadFileName.substring(uploadFileName.lastIndexOf(".")
                + 1, uploadFileName.length());
        if(!suffixList.contains(suffix)){
            return new ResponseJson(ResultCode.WRONGFORMAT);
        }
        Long userId = Long.parseLong(currentUser.getCurrentUser().getUsername());
        String filename =userId + "." + suffix;
        UserInfo currentUserInfo = userService.getCurrentUserInfo(userId);
        try {
            avatar.transferTo(new File("avatar/"+filename));
            currentUserInfo.setAvatar(filename);
            userService.updateInfo(currentUserInfo);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseJson(ResultCode.ERROR);
        }
        return new ResponseJson(ResultCode.SUCCESS);
    }

    /**
     * 根据用户id获取信息
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public ResponseJson<UserInfo> getUserInfo(@PathVariable("userId") Long userId){
        UserInfo userinfo = userService.getUserInfoById(userId);
        if (userinfo == null) {
            return new ResponseJson<>(ResultCode.WRONGINFO);
        }
        return new ResponseJson<>(ResultCode.SUCCESS,userinfo);
    }

    @PostMapping("/introduction")
    public ResponseJson changeIntroduction(@RequestBody String introduction){
        introduction = JSONObject.parseObject(introduction).getString("introduction");
        if(StringUtils.isEmpty(introduction)){
            return new ResponseJson(ResultCode.UNVALIDPARAMS);
        }
        long userId = Long.parseLong(currentUser.getCurrentUser().getUsername());
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setIntroduction(introduction);
        userService.updateInfo(userInfo);
        return new ResponseJson(ResultCode.SUCCESS);
    }

    /**
     * 拉黑某人
     * @param questionId
     * @return
     */
    @GetMapping("/reject")
    public ResponseJson reject(Long questionId){
        if (questionId == null) {
            return new ResponseJson(ResultCode.UNVALIDPARAMS);
        }
        Question question = questionService.selectQuestionById(questionId);
        if(question == null){
            return new ResponseJson(ResultCode.UNVALIDPARAMS);
        }
        //mybatis plus提供的service层crud接口
        Ban ban = banService.getOne(new QueryWrapper<Ban>().eq("baner_id",question.getQuestionedId()).eq("baned_id",question.getQuestionerId()));
        if(ban == null){
            ban = new Ban(question.getQuestionedId(),question.getQuestionerId(),1);
            banService.save(ban);
        }
        else if(!question.getIsBan()){
            ban.setCount(ban.getCount()+1);
            banService.updateById(ban);
        }
        question.setDeleted(true);
        question.setDeleteDate(new Date());
        question.setIsBan(true);
        questionService.updateQuestion(question);
        return new ResponseJson(ResultCode.SUCCESS);
    }

    /**
     * 举报某人
     * @param reportVo
     * @return
     */
    @PostMapping("/report")
    public ResponseJson report(@Valid @RequestBody ReportVo reportVo){
        Long questionId = reportVo.getQuestionId();
        String reason = reportVo.getReason();
        Question question = questionService.selectQuestionById(questionId);
        if(question == null||!question.getQuestionedId().equals(Long.parseLong(currentUser.getCurrentUser().getUsername()))){
            return new ResponseJson(ResultCode.UNVALIDPARAMS);
        }
        if(question.getIsReport()){
            return new ResponseJson(ResultCode.REPORTED);
        }
        //举报同时删除问题
        question.setDeleted(true);
        question.setDeleteDate(new Date());
        question.setIsReport(true);
        questionService.updateQuestion(question);
        Report report = new Report(question,reason);
        reportService.saveReport(report);
        return new ResponseJson(ResultCode.SUCCESS);
    }

    /**
     * 获取举报列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/reportList")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ROOT')")
    public ResponseJson<PageResult<ReportAdminVo>> getReportList(@RequestParam(defaultValue = "1") int pageNum,@RequestParam(defaultValue = "10") int pageSize){
        if(pageNum<=0||pageSize<=0||pageSize>50){
            return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
        }
        PageResult<ReportAdminVo> reportList = reportService.getReportList(pageNum, pageSize);
        return new ResponseJson<PageResult<ReportAdminVo>>(ResultCode.SUCCESS,reportList);
    }

    /**
     * 处理举报
     * @param reportVo
     * @return
     */
    @PostMapping("/dealReport")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ROOT')")
    public ResponseJson dealReport(@RequestBody @Valid DealReportVo reportVo){
        Report report = reportService.getReportById(reportVo.getReportId());
        UserInfo userInfo = userService.getCurrentUserInfo(report.getReportedId());
        if (report == null || userInfo == null) {
            return new ResponseJson(ResultCode.UNVALIDPARAMS);
        }
        //不能封禁管理员
        if(reportVo.getStatus()==1 && userInfo.getRoles().contains("ROLE_ADMIN")){
            return new ResponseJson(ResultCode.BANADMIN);
        }
        report.setResult(reportVo.getStatus());
        reportService.update(report);
        if(reportVo.getStatus()==1){
            Date current = new Date();
            userInfo.setIsBan(true);
            userInfo.setBanDate(current);
            userService.updateInfo(userInfo);
            redisTemplate.opsForValue().set("blacklist:"+userInfo.getUserId(),current);
            redisTemplate.expire("blacklist:"+userInfo.getUserId(),30, TimeUnit.DAYS);
        }
        return new ResponseJson(ResultCode.SUCCESS);
    }

    /**
     * 获取封禁列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/banList")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ROOT')")
    public ResponseJson<PageResult<Report>> getBanList(@RequestParam(defaultValue = "1") int pageNum,@RequestParam(defaultValue = "10") int pageSize){
        if(pageNum<=0||pageSize<=0||pageSize>50){
            return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
        }
        Page<BanListVo> banList = userService.selectBanUser(pageNum,pageSize);
        return new ResponseJson<>(ResultCode.SUCCESS, new PageResult<>(banList));
    }

    /**
     * 取消拉黑
     * @param userId
     * @return
     */
    @DeleteMapping("/ban/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ROOT')")
    public ResponseJson deleteBan(@PathVariable(value = "userId",required = true) Long userId){
        UserInfo userInfo = userService.getUserInfoById(userId);
        if (userInfo == null || !userInfo.getIsBan()) {
            return new ResponseJson(ResultCode.UNVALIDPARAMS);
        }
        userInfo.setIsBan(false);
        userService.updateInfo(userInfo);
        return new ResponseJson(ResultCode.SUCCESS);
    }

    /**
     * 随机展示用户
     * @param num
     * @return
     */
    @GetMapping("showRandomUser")
    public ResponseJson<List<SimpleUserInfo>> showRandomUser(@RequestParam(value = "num",defaultValue = "5") int num){
        if(num<=0||num>=30){
            return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
        }
        List<SimpleUserInfo> userInfos = userService.getRandomUser(num);
        return new ResponseJson<>(ResultCode.SUCCESS,userInfos);
    }

    @GetMapping("selectUser")
    public ResponseJson<PageResult<SimpleUserInfo>> selectUserLikeUsername(@RequestParam(required = true) String username,
                                                                         @RequestParam(defaultValue = "1")int pageNum,
                                                                         @RequestParam(defaultValue = "5")int pageSize){
        if(pageNum<=0||pageSize<=0||pageSize>50|| StringUtils.isEmpty(username)){
            return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
        }
        Page<SimpleUserInfo> simpleUserInfoPage = userService.selectUserLikeUsername(username,pageNum,pageSize);
        return new ResponseJson<>(ResultCode.SUCCESS,new PageResult<>(simpleUserInfoPage));
    }

    @GetMapping("showAllUsers")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ROOT')")
    public ResponseJson<PageResult<SimpleUserInfo>> showAllUser(@RequestParam(defaultValue = "1")int pageNum,
                                                                @RequestParam(defaultValue = "5")int pageSize){
        if(pageNum<=0||pageSize<=0||pageSize>50){
            return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
        }
        Page<SimpleUserInfo> simpleUserInfoPage = userService.showAllUser(pageNum,pageSize);
        return new ResponseJson<>(ResultCode.SUCCESS,new PageResult<>(simpleUserInfoPage));
    }

    @GetMapping("admin/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_ROOT')")
    public ResponseJson addAdmin(@PathVariable(value = "userId",required = true)Long userId){
        UserInfo userInfoById = userService.getUserInfoById(userId);
        if (userInfoById == null) {
            return new ResponseJson(ResultCode.UNVALIDPARAMS);
        }
        userService.addAdmin(userId);
        return new ResponseJson(ResultCode.SUCCESS);
    }

    @DeleteMapping("admin/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_ROOT')")
    public ResponseJson deleteAdmin(@PathVariable(value = "userId",required = true)Long userId){
        UserInfo userInfoById = userService.getUserInfoById(userId);
        if (userInfoById == null) {
            return new ResponseJson(ResultCode.UNVALIDPARAMS);
        }
        //简单处理一下，不能取消超管的管理员权限
        if(userInfoById.getUserId().equals(0L)){
            return new ResponseJson(ResultCode.SELFDELETE);
        }
        userService.deleteAdmin(userId);
        return new ResponseJson(ResultCode.SUCCESS);
    }

    @GetMapping("admin/getAdminList")
    @PreAuthorize("hasAnyRole('ROLE_ROOT')")
    public ResponseJson<PageResult<AdminVo>> getAdminList(@RequestParam(defaultValue = "1")int pageNum,
                                                          @RequestParam(defaultValue = "5")int pageSize){
        if(pageNum<=0||pageSize<=0||pageSize>50){
            return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
        }
        Page<AdminVo> adminVoPage = userService.selectAdmin(pageNum,pageSize);
        return new ResponseJson<>(ResultCode.SUCCESS,new PageResult<>(adminVoPage));
    }



    @GetMapping("/avatar/{userId}")
    public void captcha(HttpServletResponse response,@PathVariable(value = "userId",required = true) Long userId)throws IOException {
        UserInfo userInfo = userService.getUserInfoById(userId);
        if (userInfo == null) {
            return;
        }
        String filename = userInfo.getAvatar();
        //没有上传头像，返回默认头像
        if(StringUtils.isEmpty(filename)){
            filename = "default.png";
        }
        String suffix = filename.substring(filename.lastIndexOf(".")
                + 1, filename.length());
        response.setHeader("Cache-Control", "no-store, no-cache");
        BufferedImage image = ImageIO.read(new FileInputStream(new File("avatar/"+filename)));
        ServletOutputStream out = response.getOutputStream();
        //头像是jpg格式
        if(suffix.equals("jpg")){
            response.setContentType("image/jpeg");
            ImageIO.write(image, "jpg", out);

        }
        //头像是png格式
        else if(suffix.equals("png")){
            response.setContentType("image/png");
            ImageIO.write(image, "png", out);
        }
        //其他图像格式没做
        IOUtils.closeQuietly(out);
    }

    /**
     * 取消封禁
     * @param questionId
     * @return
     */
    @GetMapping("cancelBan")
    public ResponseJson cancelBan(@RequestParam(required = true)Long questionId){
        Question question = questionService.selectQuestionById(questionId);
        long userId = Long.parseLong(currentUser.getCurrentUser().getUsername());
        if(question == null || !question.getQuestionedId().equals(userId)){
            return new ResponseJson(ResultCode.UNVALIDPARAMS);
        }
        //取消拉黑屎更新问题状态
        question.setIsBan(false);
        question.setDeleted(false);
        question.setDeleteDate(null);
        questionService.updateQuestion(question);
        QueryWrapper<Ban> queryWrapper = new QueryWrapper<Ban>().eq("baner_id", userId).eq("baned_id", question.getQuestionerId());
        Ban ban = banService.getOne(queryWrapper);
        Integer count = ban.getCount();
        //拉黑次数-1
        if(count == 1){
            banService.remove(queryWrapper);
        }
        else {
            count--;
            ban.setCount(count);
            banService.update(ban,queryWrapper);
        }
        return new ResponseJson(ResultCode.SUCCESS);
    }

    @GetMapping("ban/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ROOT')")
    public ResponseJson banUser(@PathVariable(value = "userId",required = true)Long userId){
        UserInfo userInfo = userService.getCurrentUserInfo(userId);
        if (userInfo == null) {
            return new ResponseJson(ResultCode.UNVALIDPARAMS);
        }
        if(userInfo.getRoles().contains("ROLE_ADMIN")){
            return new ResponseJson(ResultCode.BANADMIN);
        }
        UserInfo updateUserInfo = new UserInfo();
        updateUserInfo.setUserId(userId);
        updateUserInfo.setIsBan(true);
        updateUserInfo.setBanDate(new Date());
        userService.updateInfo(updateUserInfo);
        return new ResponseJson(ResultCode.SUCCESS);
    }

    /**
     * 获取用户的拉黑列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("getUserBanList")
    public ResponseJson<PageResult<Question>> getUserBanList(@RequestParam(defaultValue = "1")int pageNum,
                                                             @RequestParam(defaultValue = "5")int pageSize){
        if(pageNum<=0||pageSize<=0||pageSize>50){
            return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
        }
        Long userId = Long.parseLong(currentUser.getCurrentUser().getUsername());
        Page<Question> questionPage = questionService.selectBanQuestion(userId,pageNum,pageSize);
        questionPage = deleteQuestioner(questionPage);
        return new ResponseJson<>(ResultCode.SUCCESS,new PageResult<>(questionPage));
    }

    /**
     * 用户管理接口，获取用户信息
     * @param pageNum
     * @param pageSize
     * @param username
     * @return
     */
    @GetMapping("getUserAdminInfo")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ROOT')")
    public ResponseJson<PageResult<UserAdminVo>> getUserAdminInfo(@RequestParam(defaultValue = "1")int pageNum,
                                                                  @RequestParam(defaultValue = "5")int pageSize,
                                                                  @RequestParam(required = true) String username){
        if(pageNum<=0||pageSize<=0||pageSize>50){
            return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
        }
        Page<UserAdminVo> userAdminVoPage = userService.getUserAdminVoPage(pageNum, pageSize, username);
        return new ResponseJson<>(ResultCode.SUCCESS,new PageResult<>(userAdminVoPage));
    }

    /**
     * 删除提问者信息
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
}
