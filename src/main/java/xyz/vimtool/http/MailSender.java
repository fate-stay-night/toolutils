package xyz.vimtool.http;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;

import javax.mail.internet.MimeUtility;
import java.util.ArrayList;
import java.util.List;

/**
 * 邮件发送
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019/12/17
 */
public class MailSender {

    public static void main(String[] args) throws Exception {
        HtmlEmail email = new HtmlEmail();
        email.setHostName("mail.163.com");
        email.setFrom("kezy-5566@163.com", "fateNight");
        email.setAuthentication("kezy-5566@163.com", "password");
        email.setCharset("UTF-8");
        email.setSubject("测试");
        email.setHtmlMsg("测试");

        // 添加附件
        EmailAttachment attachment = new EmailAttachment();
        String filePath = "/Users/xiao/test.txt";
        attachment.setPath(filePath);
        String[] fileNames = filePath.split("/");
        String fileName = fileNames[fileNames.length - 1];
        attachment.setName(MimeUtility.encodeWord(fileName));
        email.attach(attachment);

        // 收件人
        List<String> mails = new ArrayList<>();
        mails.add("kezy-5566@163.com");
        for (String address : mails) {
            email.addTo(address);
        }

        // 抄送人
        List<String> ccMails = new ArrayList<>();
        mails.add("kezy-5566@163.com");
        for (String address : ccMails) {
            email.addCc(address);
        }
        email.send();
    }
}
