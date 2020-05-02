package peing;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.coyote.http11.upgrade.UpgradeServletInputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import peing.mapper.MessageMapper;
import peing.mapper.RoleMapper;
import peing.mapper.UserInfoMapper;
import peing.mapper.UserMapper;
import peing.pojo.*;
import peing.service.MailService;
import peing.service.MessageService;
import peing.service.ReportService;
import peing.service.UserService;
import peing.utils.AesUtil;
import peing.utils.JwtTokenUtils;
import peing.vo.BanListVo;
import peing.vo.UserAdminVo;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class testPeing {
    @Autowired
    public RedisTemplate redisTemplate;
    @Autowired
    public UserMapper userMapper;
    @Autowired
    public UserInfoMapper userInfoMapper;
    @Qualifier("fakeMailServiceImpl")
    @Autowired
    public MailService mailService;
    @Autowired
    public UserService userService;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private ReportService reportService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageMapper messageMapper;
    @Test
    public void test(){
//        redisTemplate.opsForValue().set("captcha:"+"111","faf");
//        redisTemplate.expire("captcha:111",100, TimeUnit.SECONDS);
//        Object o = redisTemplate.opsForValue().get("213");
//        if (o == null) {
//            System.out.println(111);
//        }
//        redisTemplate.expire()
//        String o = (String)redisTemplate.opsForValue().get("captcha:111");
//        System.out.println(o);
    }

    @Test
    public void test02(){
//        System.out.println(userMapper.selectBan());
        try {
            System.out.println(AesUtil.aesDecrypt("sXXTgLFx8zwCF25KnvQZVrp5WwWQcEc0lrfVbw7/T7o="));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test03(){
        //snowflake
        DefaultIdentifierGenerator identifierGenerator = new DefaultIdentifierGenerator(1,1);
        Long eqwe = identifierGenerator.nextId("eqwe");
        System.out.println(eqwe);
    }

    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String username;
    @Test
    public void test04() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(new InternetAddress(username));
        helper.setTo("2770987304@qq.com");
        helper.setSubject("测试发送邮件");
        helper.setText("<h1>测试发送邮件<h1>", true);
        mailSender.send(message);

    }

    @Test
    public void test05(){
//        mailService.SendActiveMail("2770987304@qq.com","112233");
        String forest = JwtTokenUtils.createTempToken("forest");
        System.out.println(forest);
        System.out.println(JwtTokenUtils.getUsernameByToken(forest));
    }

    @Test
    public void test06(){
        User user = new User();
        user.setEmail("111");
        user.setUserId(311L);
        userService.register(user);
        try {
            Thread.sleep(10000000000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test07(){
        redisTemplate.opsForValue().set("test01",new Date());
        Date test01 = new Date((Long)redisTemplate.opsForValue().get("test01"));
        System.out.println(test01);
    }

    @Test
    public void test08(){
        userService.getCurrentUserInfo(0L);
    }

    @Test
    public void test09(){
//        System.out.println(passwordEncoder.encode("lj12100914"));
//        System.out.println(reportList.getRecords());
        Page<BanListVo> banListVoPage = userService.selectBanUser(1, 1);
        System.out.println(banListVoPage.getRecords());
    }

    @Test
    public void test10(){
        System.out.println(userService.getRandomUser(2));
    }

    @Test
    public void test11(){
//        UserInfo userInfo = userService.hasBan(0L, 1L);
//        System.out.println(userInfo);
    }

    @Test
    public void test12(){
//        Page<Message> messagePage = messageService.selectMessageByUserId(0L, 1, 5);
//        System.out.println(messagePage.getRecords());
//        messageService.publishAnnouncement(null);
//        messageMapper.insert2MessageUser(3L,3L);
        System.out.println(messageMapper.countUnreadMessage(0L));
    }

    @Test
    public void test13(){
        Page<UserAdminVo> userAdminVoPage = userInfoMapper.selectUserAdmin(new Page<UserAdminVo>(1, 5), "1");
        System.out.println(userAdminVoPage.getRecords());
    }

    @Test
    public void test14(){
//        User user = userService.selectUserById(0L);
//        System.out.println(reportService.getReportList(1, 5).getRecords());
    }

}
