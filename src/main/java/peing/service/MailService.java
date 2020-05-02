package peing.service;

/**
 * 邮件服务
 */
public interface MailService {
    /**
     * 发送激活邮件
     * @param email
     * @param userId
     * @param token
     */
    public void SendActiveMail(String email,Long userId,String token);

    /**
     * 发送重置密码邮件
     * @param email
     * @param userId
     * @param changeToken
     */
    public void SendForgetMail(String email,Long userId,String changeToken);
}
