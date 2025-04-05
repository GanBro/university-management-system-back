package com.wubo.api.service;

public interface EmailService {
    /**
     * 发送简单文本邮件
     *
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     * @return 是否发送成功
     */
    boolean sendSimpleEmail(String to, String subject, String content);

    /**
     * 发送HTML格式邮件
     *
     * @param to 收件人
     * @param subject 主题
     * @param content HTML内容
     * @return 是否发送成功
     */
    boolean sendHtmlEmail(String to, String subject, String content);

    /**
     * 发送验证码邮件
     *
     * @param to 收件人
     * @param code 验证码
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String to, String code);
}
