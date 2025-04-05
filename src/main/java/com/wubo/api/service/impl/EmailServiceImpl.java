package com.wubo.api.service.impl;

import com.wubo.api.service.EmailService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.application.name:全国高等院校管理系统}")
    private String applicationName;

    @Override
    public boolean sendSimpleEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            log.info("简单邮件发送成功，收件人: {}", to);
            return true;
        } catch (Exception e) {
            log.error("简单邮件发送失败, 收件人: {}, 错误信息: {}", to, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean sendHtmlEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
            log.info("HTML邮件发送成功，收件人: {}", to);
            return true;
        } catch (MessagingException e) {
            log.error("HTML邮件发送失败, 收件人: {}, 错误信息: {}", to, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean sendVerificationCode(String to, String code) {
        String subject = applicationName + " - 密码重置验证码";
        String htmlContent = generateVerificationEmailTemplate(code);
        return sendHtmlEmail(to, subject, htmlContent);
    }

    /**
     * 生成验证码邮件模板
     */
    private String generateVerificationEmailTemplate(String code) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>密码重置验证码</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }\n" +
                "        .container { border: 1px solid #e0e0e0; border-radius: 5px; padding: 20px; background-color: #f9f9f9; }\n" +
                "        .header { text-align: center; margin-bottom: 20px; }\n" +
                "        .code-container { background-color: #ffffff; padding: 15px; border-radius: 5px; border: 1px dashed #ccc; text-align: center; margin: 20px 0; }\n" +
                "        .verification-code { font-size: 24px; font-weight: bold; letter-spacing: 5px; color: #0071e3; }\n" +
                "        .footer { margin-top: 30px; font-size: 12px; color: #999; text-align: center; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h2>" + applicationName + "</h2>\n" +
                "            <h3>密码重置验证码</h3>\n" +
                "        </div>\n" +
                "        <p>您好，</p>\n" +
                "        <p>您正在进行密码重置操作，请使用以下验证码完成验证：</p>\n" +
                "        <div class=\"code-container\">\n" +
                "            <div class=\"verification-code\">" + code + "</div>\n" +
                "        </div>\n" +
                "        <p>此验证码将在 15 分钟后失效，请及时使用。</p>\n" +
                "        <p>如果您没有请求此验证码，请忽略此邮件。</p>\n" +
                "        <p>谢谢！</p>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>此邮件由系统自动发送，请勿回复。</p>\n" +
                "            <p>&copy; " + java.time.Year.now().getValue() + " " + applicationName + "</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
