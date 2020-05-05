package peing.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import peing.pojo.*;
import peing.service.MailService;
import peing.service.MessageService;
import peing.service.impl.SysCaptchaServiceImpl;
import peing.service.UserService;
import peing.utils.AesUtil;
import peing.utils.JwtTokenUtils;
import peing.vo.ChangeEmailVo;
import peing.vo.ChangePasswordVo;
import peing.vo.RegisteVo;
import peing.vo.ResetVo;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 登录，鉴权相关接口
 */
@RequestMapping("/auth")
@RestController
@CrossOrigin
@Slf4j
public class AuthController {
    @Autowired
    private SysCaptchaServiceImpl captchaService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private CurrentUser currentUser;
    @Qualifier("fakeMailServiceImpl")
    @Autowired
    private MailService mailService;
    @Autowired
    private MessageService messageService;

    @GetMapping("/captcha.jpg")
    public void captcha(HttpServletResponse response, @RequestParam(required = true) String uuid)throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        //获取图片验证码
        BufferedImage image = captchaService.getCaptcha(uuid);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        IOUtils.closeQuietly(out);
    }

    @PostMapping("/signup")
    public ResponseJson signup(@Valid @RequestBody RegisteVo registeVo){
        //校验验证码
        boolean validate = captchaService.validate(registeVo.getUuid(), registeVo.getCode());
        if(!validate){
            return new ResponseJson(ResultCode.ERRORCAPTCHA);
        }
//        密码解码
        try {
            String password = AesUtil.aesDecrypt(registeVo.getPassword());
            if(password.length()<6||password.length()>16||StringUtils.isNumeric(password)){
                return new ResponseJson(ResultCode.UNVALIDPARAMS);
            }
            registeVo.setPassword(passwordEncoder.encode(password));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(userService.selectUserByUsername(registeVo.getUsername())!=null){
            return new ResponseJson(ResultCode.USEDUSERNAME);
        }
        if(userService.selectUserByEmail(registeVo.getEmail())!=null){
            return new ResponseJson(ResultCode.USEDEMAIL);
        }
        User user = new User();
        //通过DefaultIdentifierGenerator获取雪花id
        DefaultIdentifierGenerator identifierGenerator = new DefaultIdentifierGenerator(1,1);
        user.setUserId(identifierGenerator.nextId(user));
        user.setUsername(registeVo.getUsername());
        user.setEmail(registeVo.getEmail());
        user.setPassword(registeVo.getPassword());
        user.setIsActive(false);
        user.setIsBan(false);
        Date current = new Date();
        user.setSignupDate(current);
        user.setUpdateDate(current);
        userService.register(user);
        return new ResponseJson(ResultCode.SUCCESS);
    }

    @GetMapping("/active")
    public ResponseJson active(@RequestParam(required = true) Long userId,@RequestParam(required = true) String activeCode){
        boolean success = userService.active(userId,activeCode);
        if(!success){
            return new ResponseJson((ResultCode.UNVALIDPARAMS));
        }
        Message message = new Message();
        DefaultIdentifierGenerator identifierGenerator = new DefaultIdentifierGenerator(1,1);
        message.setMessageId(identifierGenerator.nextId(message));
        message.setTitle("peing欢迎您的使用");
        message.setContent("您已经成功激活peing的账号，祝您使用愉快");
        message.setPublishDate(new Date());
        messageService.publishMessage(userId,message);
        return new ResponseJson(ResultCode.SUCCESS);
    }

    @PostMapping("/forgetPassword")
    public ResponseJson forget(@RequestBody String email){
        email = JSONObject.parseObject(email).getString("email");
        if(email == null){
            return new ResponseJson(ResultCode.UNVALIDPARAMS);
        }
        User user = userService.selectUserByEmail(email);
        if (user == null) {
            return new ResponseJson(ResultCode.WRONGINFO);
        }
        userService.resetPassword(user.getUserId(),email);
        return new ResponseJson(ResultCode.SUCCESS);
    }

    @PostMapping("/reset")
    public ResponseJson reset(@Valid @RequestBody ResetVo resetVo){
        String changeToken = resetVo.getChangeToken();
        String password = resetVo.getPassword();
        Long userId = resetVo.getUserId();
        //判断token是否正确
        Object o = redisTemplate.opsForValue().get("change:" + userId);
        if(o == null){
            return new ResponseJson(ResultCode.UNVALIDPARAMS);
        }
        String ans = (String) o;
        if(!ans.equals(changeToken)){
            return new ResponseJson(ResultCode.UNVALIDPARAMS);
        }
        try {
            password = AesUtil.aesDecrypt(password);
            if(password.length()<6||password.length()>16||StringUtils.isNumeric(password)){
                return new ResponseJson(ResultCode.UNVALIDPARAMS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        User user = new User();
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode(password));
        Date current = new Date();
        user.setUpdateDate(current);
        userService.update(user);
        redisTemplate.delete("change:"+userId);
        //将当前用户存入token黑名单，使原有token失效
        redisTemplate.opsForValue().set("blacklist:"+userId,current);
        redisTemplate.expire("blacklist:"+userId,30, TimeUnit.DAYS);
        return new ResponseJson(ResultCode.SUCCESS);
    }

    @PostMapping("/changePassword")
    public ResponseJson changePassword(@Valid @RequestBody ChangePasswordVo changePasswordVo){
        String oldPassword = changePasswordVo.getOldPassword();
        String newPassword = changePasswordVo.getNewPassword();
        JwtUser currentUser = this.currentUser.getCurrentUser();
        //解码并判断密码合法性
        try {
            oldPassword = AesUtil.aesDecrypt(oldPassword);
            newPassword = AesUtil.aesDecrypt(newPassword);
            if(oldPassword.length()<6||oldPassword.length()>16||StringUtils.isNumeric(oldPassword)||newPassword.length()<6||newPassword.length()>16||StringUtils.isNumeric(newPassword)){
                return new ResponseJson(ResultCode.UNVALIDPARAMS);
            }
            if(!passwordEncoder.matches(oldPassword, currentUser.getPassword())){
                return new ResponseJson(ResultCode.WRONGINFO);
            }
            Long userId = Long.parseLong(currentUser.getUsername());
            User user = new User();
            user.setUserId(userId);
            user.setPassword(passwordEncoder.encode(newPassword));
            Date current = new Date();
            user.setUpdateDate(current);
            userService.update(user);
            redisTemplate.opsForValue().set("blacklist:"+userId,current);
            redisTemplate.expire("blacklist:"+userId,30, TimeUnit.DAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseJson(ResultCode.SUCCESS);
    }

    @GetMapping("/checkEmail")
    public ResponseJson checkEmail(@RequestParam(required = true) String email){
        User user = userService.selectUserByEmail(email);
        if (user != null) {
            return new ResponseJson(ResultCode.USEDEMAIL);
        }
        return new ResponseJson(ResultCode.SUCCESS);
    }

    @GetMapping("checkUsername")
    public ResponseJson checkUsername(@RequestParam(required = true) String username){
        User user = userService.selectUserByUsername(username);
        if (user != null) {
            return new ResponseJson(ResultCode.USEDUSERNAME);
        }
        return new ResponseJson(ResultCode.SUCCESS);
    }

    @PostMapping("/username")
    public ResponseJson changeUsername(@RequestBody Map map){
        //就一个参数，不想写vo了
        String username = (String) map.get("username");
        if(username == null || username.length()<3||username.length()>16){
            return new ResponseJson(ResultCode.UNVALIDPARAMS);
        }
        User userByUsername = userService.selectUserByUsername(username);
        if (userByUsername != null) {
            return new ResponseJson(ResultCode.USEDUSERNAME);
        }
        User user = new User();
        user.setUserId(Long.parseLong(currentUser.getCurrentUser().getUsername()));
        user.setUsername(username);
        userService.update(user);
        return new ResponseJson(ResultCode.SUCCESS);
    }

    @PostMapping("/email")
    public ResponseJson changeEmail(@RequestBody @Valid ChangeEmailVo changeEmailVo){
        String email = changeEmailVo.getEmail();
        String password = changeEmailVo.getPassword();
        try {
            password = AesUtil.aesDecrypt(password);
            if(!passwordEncoder.matches(password,currentUser.getCurrentUser().getPassword())){
                return new ResponseJson(ResultCode.WRONGINFO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseJson(ResultCode.WRONGINFO);
        }
        User userByEmail = userService.selectUserByEmail(email);
        if (userByEmail != null) {
            return new ResponseJson(ResultCode.USEDEMAIL);
        }
        long userId = Long.parseLong(currentUser.getCurrentUser().getUsername());
        User user = userService.selectUserById(userId);
        user.setEmail(email);
        user.setIsActive(false);
        userService.update(user);
        String activeToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("active:"+user.getUserId(),activeToken);
        mailService.SendActiveMail(user.getEmail(),user.getUserId(),activeToken);
        //修改邮箱，同样进入token黑名单，需要重新登录
        redisTemplate.opsForValue().set("blacklist:"+userId,new Date());
        redisTemplate.expire("blacklist:"+userId,30, TimeUnit.DAYS);
        log.info(user.getUserId()+"修改邮箱");
        return new ResponseJson(ResultCode.SUCCESS);
    }


}
