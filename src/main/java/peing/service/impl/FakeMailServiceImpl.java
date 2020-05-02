package peing.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import peing.service.MailService;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 这个类是虚假的邮件发送类，会把所有邮件发到我的邮箱，而不是向指定的邮件地址发送
 */
@Service
@Slf4j
public class FakeMailServiceImpl implements MailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${baseUrl}")
    private String baseUrl;

    /**
     * 发送激活邮件
     * @param email
     * @param userId
     * @param token
     */
    @Override
    @Async
    public void SendActiveMail(String email,Long userId,String token) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        String url = baseUrl+"active?userId="+userId+"&activeCode="+token;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(new InternetAddress(username));
            helper.setTo("2770987304@qq.com");
            helper.setSubject("peing账户激活邮件");
            StringBuilder builder = new StringBuilder();
            builder.append("<!DOCTYPE html>\n" )
                    .append("<html lang=\"en\">\n")
                    .append("<head>\n" )
                    .append("    <meta charset=\"UTF-8\">\n" )
                    .append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" )
                    .append("    <title>激活邮件</title>\n" )
                    .append("</head>\n" )
                    .append("<body>\n" )
                    .append("亲爱的用户:\n" )
                    .append("<br/>\n" )
                    .append("&nbsp;&nbsp;&nbsp;&nbsp;欢迎您使用peing服务，这是一封由系统发送的激活邮件，请勿回复。请点击以下链接进行激活，否则，请忽略该邮件\n" )
                    .append("<br/>\n" )
                    .append("&nbsp;&nbsp;&nbsp;&nbsp;<a href='"+url+"'>激活链接</a>\n" )
                    .append("</body>\n" )
                    .append("</html>");
            helper.setText(builder.toString(), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送修改密码邮件
     * @param email
     * @param userId
     * @param changeToken
     */
    @Override
    @Async
    public void SendForgetMail(String email, Long userId,String changeToken) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        String url = baseUrl+"reset?userId="+userId+"&changeToken="+changeToken;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(new InternetAddress(username));
            helper.setTo("2770987304@qq.com");
            helper.setSubject("peing重置密码邮件");
            StringBuilder builder = new StringBuilder();
            builder.append("<!DOCTYPE html>\n" )
                    .append("<html lang=\"en\">\n")
                    .append("<head>\n" )
                    .append("    <meta charset=\"UTF-8\">\n" )
                    .append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" )
                    .append("    <title>激活邮件</title>\n" )
                    .append("</head>\n" )
                    .append("<body>\n" )
                    .append("亲爱的用户:\n" )
                    .append("<br/>\n" )
                    .append("&nbsp;&nbsp;&nbsp;&nbsp;欢迎您使用peing服务，这是一封由系统发送的重置密码邮件，请勿回复。请点击以下链接进行重置密码，否则，请忽略该邮件\n" )
                    .append("<br/>\n" )
                    .append("&nbsp;&nbsp;&nbsp;&nbsp;<a href='"+url+"'>重置密码链接</a>\n" )
                    .append("</body>\n" )
                    .append("</html>");
            helper.setText(builder.toString(), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
