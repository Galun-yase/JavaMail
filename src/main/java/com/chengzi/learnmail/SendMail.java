package com.chengzi.learnmail;


import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SendMail {

    final String smtpHost;
    final String username;
    final String password;
    final boolean debug;

    public SendMail(String smtpHost,String username,String password){
        this.smtpHost=smtpHost;
        this.username=username;
        this.password=password;
        this.debug=true;
    }
    public static void main(String[] args) throws Exception{
        final String smtpHost="smtp.qq.com";
        final String username="534897552@qq.com";
        final String password="qgnugsjfvvbgbiae";
        final String from="534897552@qq.com";
        final String to="534897552@qq.com";
        SendMail sender=new SendMail(smtpHost,username,password);
        Session session=sender.creatSSLSession();
        try(
                InputStream imageinput=SendMail.class.getResourceAsStream("/javamail.jpg");
                InputStream pdfinput=SendMail.class.getResourceAsStream("/java.pdf")
        ) {
            Message message = SendMail.creatTextMessage(session, from, to, "JavaSMTP邮件", "<h1>Hello!!!</h1><p><img src=\"cid:img01\"></p><p>这是一封内嵌图片的<u>javamail</u>邮件！</p>", "javamail.jpg","java.pdf", imageinput,pdfinput);
            Transport.send(message);
        }
    }

    static Message creatTextMessage(Session session, String from, String to, String subject, String body, String imagefilename,String pdffilename, InputStream imageinput,InputStream pdfinput)throws MessagingException, IOException {
        MimeMessage message=new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipient(Message.RecipientType.TO,new InternetAddress(to));
        message.setSubject(subject,"UTF-8");

        Multipart multipart=new MimeMultipart();
        message.setContent(multipart);

        //第一个bodypart为文本，第二个bodypart为附件
        BodyPart content=new MimeBodyPart();
        multipart.addBodyPart(content);
        BodyPart attach=new MimeBodyPart();
        multipart.addBodyPart(attach);

        //添加正文(text+image)
        Multipart bodymultipart=new MimeMultipart();
        content.setContent(bodymultipart);//body,"text/html;charset=utf-8");

        BodyPart textpart=new MimeBodyPart();
        bodymultipart.addBodyPart(textpart);
        BodyPart imagepart=new MimeBodyPart();
        bodymultipart.addBodyPart(imagepart);

        textpart.setContent(body,"text/html;charset=utf-8");
        imagepart.setFileName(imagefilename);
        imagepart.setDataHandler(new DataHandler(new ByteArrayDataSource(imageinput,"image/jpeg")));
        imagepart.setHeader("Content-ID","<img01>");

        //添加附件（pdf）
        attach.setFileName(pdffilename);
        attach.setDataHandler(new DataHandler(new ByteArrayDataSource(pdfinput,"application/pdf")));



        return message;
    }

    Session creatSSLSession(){
        Properties props=new Properties();
        props.setProperty("mail.smtp.host",this.smtpHost);//smtp主机名
        props.setProperty("mail.smtp.port","465");//主机端口号
        props.setProperty("mail.smtp.auth","true");//是否需要用户认证
        //启动SSL
        props.setProperty("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.port","465");
        Session session=Session.getInstance(props,new Authenticator(){//连接邮箱时验证用户和密码
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(SendMail.this.username,SendMail.this.password);
            }
        });
        session.setDebug(this.debug);
        return session;
    }


}
