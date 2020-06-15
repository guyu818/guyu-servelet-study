package com.guyu.mail;

import com.sun.mail.util.MailSSLSocketFactory;
import org.junit.Test;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.security.GeneralSecurityException;
import java.util.Properties;

//简单的邮件 ：没有附件和图片，纯文本邮件
//复杂邮件: 由附件和图片
//要发送邮件，需要获取协议和支持，开启POP3/STMP服务
//授权码：tcpsnkrzjncodfef
public class SendEmail {


    @Test
    /*
    简单的邮件
     */
    public void Test() throws GeneralSecurityException, MessagingException {
        Properties prop = new Properties();
        prop.setProperty("mail.host", "smtp.qq.com");//设置qq邮箱服务器
        prop.setProperty("mail.transport.protocol", "smtp");//邮件发送协议
        prop.setProperty("mail.smtp.auth", "true");//需要验证用户名密码

        //关于qq邮箱，还需要设置SSL加密，加上以下代码即可
        MailSSLSocketFactory sf = new MailSSLSocketFactory();//需要抛出异常
        sf.setTrustAllHosts(true);
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.socketFactory", sf);

        //使用javamail发送邮件需要5个步骤
        //1.创建定义整个应用程序所需的环境信息的session对象
        //QQ才有，其他邮箱不用
        Session session = Session.getDefaultInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //发件人邮箱用户名、授权码
                return new PasswordAuthentication("3040185132@qq.com", "tcpsnkrzjncodfef");
            }
        });
        //可以选择开启session的debug模式，这样就可以查看发送Email的运行状态了
        session.setDebug(true);

        //2.通过session得到transport对象
        Transport ts = session.getTransport();

        //3.使用邮箱的用户名和授权码连上邮件服务器
        ts.connect("smtp.qq.com", "3040185132@qq.com", "tcpsnkrzjncodfef");

        //4.创建邮件
        //注意需要传递session
        MimeMessage message = new MimeMessage(session);
        //指明邮件发送人
        message.setFrom(new InternetAddress("3040185132@qq.com"));
        //指明收件人
        message.setRecipient(Message.RecipientType.TO, new InternetAddress("guyu_818@163.com"));
        //邮件的主题
        message.setSubject("谷雨给你发邮件拉");
        //设置文本内容
        message.setContent("<h1 style='color:red'>你好啊</h1>", "text/html;charset=UTF-8");

        //5.发送邮件
        ts.sendMessage(message, message.getAllRecipients());

        //6.关闭连接
        ts.close();
    }

    @Test
    /*
    复杂的邮件：图片+文字
     */
    public void Test2() throws GeneralSecurityException, MessagingException {
        Properties prop = new Properties();
        prop.setProperty("mail.host", "smtp.qq.com");//设置qq邮箱服务器
        prop.setProperty("mail.transport.protocol", "smtp");//邮件发送协议
        prop.setProperty("mail.smtp.auth", "true");//需要验证用户名密码

        //关于qq邮箱，还需要设置SSL加密，加上以下代码即可
        MailSSLSocketFactory sf = new MailSSLSocketFactory();//需要抛出异常
        sf.setTrustAllHosts(true);
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.socketFactory", sf);

        //使用javamail发送邮件需要5个步骤
        //1.创建定义整个应用程序所需的环境信息的session对象
        //QQ才有，其他邮箱不用
        Session session = Session.getDefaultInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //发件人邮箱用户名、授权码
                return new PasswordAuthentication("3040185132@qq.com", "tcpsnkrzjncodfef");
            }
        });
        //可以选择开启session的debug模式，这样就可以查看发送Email的运行状态了
        session.setDebug(true);

        //2.通过session得到transport对象
        Transport ts = session.getTransport();

        //3.使用邮箱的用户名和授权码连上邮件服务器
        ts.connect("smtp.qq.com", "3040185132@qq.com", "tcpsnkrzjncodfef");

        //4.创建邮件
        //注意需要传递session
        MimeMessage message = new MimeMessage(session);
        //指明邮件发送人
        message.setFrom(new InternetAddress("3040185132@qq.com"));
        //指明收件人
        message.setRecipient(Message.RecipientType.TO, new InternetAddress("guyu_818@163.com"));
        //邮件的主题
        message.setSubject("谷雨给你发邮件拉");
        //===============复杂的邮件=====图片+文字
        //准备图片数据
        MimeBodyPart image = new MimeBodyPart();
        //图片需要经过数据处理。。。。。DataHandler:数据处理
        DataHandler dh = new DataHandler(new FileDataSource("F:\\smbms\\src\\main\\webapp\\images\\clock.jpg"));
        image.setDataHandler(dh);//在我们的body中放入这个处理的图片数据
        image.setContentID("guyu.jpg");//给图片设置一个Id，我们可以在后面使用

        //准备正文的数据
        MimeBodyPart text = new MimeBodyPart();
        text.setContent("这是一封邮件正文带图片<img src='cid:guyu.jpg'>的邮件","text/html;charset=UTF-8");

        //描述数据关系
        MimeMultipart mm = new MimeMultipart();
        mm.addBodyPart(text);
        mm.addBodyPart(image);
        mm.setSubType("related");//也可以使用mixed,并且默认是mixed
        //设置到消息中，保存修改
        message.setContent(mm);//把最后编辑好的邮件放到消息中
        message.saveChanges();

        //5.发送邮件
        ts.sendMessage(message, message.getAllRecipients());

        //6.关闭连接
        ts.close();
    }
    @Test
    /*
    复杂的邮件：图片+文字+附件
     */
    public void Test3() throws GeneralSecurityException, MessagingException {
        Properties prop = new Properties();
        prop.setProperty("mail.host", "smtp.qq.com");//设置qq邮箱服务器
        prop.setProperty("mail.transport.protocol", "smtp");//邮件发送协议
        prop.setProperty("mail.smtp.auth", "true");//需要验证用户名密码

        //关于qq邮箱，还需要设置SSL加密，加上以下代码即可
        MailSSLSocketFactory sf = new MailSSLSocketFactory();//需要抛出异常
        sf.setTrustAllHosts(true);
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.socketFactory", sf);

        //使用javamail发送邮件需要5个步骤
        //1.创建定义整个应用程序所需的环境信息的session对象
        //QQ才有，其他邮箱不用
        Session session = Session.getDefaultInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //发件人邮箱用户名、授权码
                return new PasswordAuthentication("3040185132@qq.com", "tcpsnkrzjncodfef");
            }
        });
        //可以选择开启session的debug模式，这样就可以查看发送Email的运行状态了
        session.setDebug(true);

        //2.通过session得到transport对象
        Transport ts = session.getTransport();

        //3.使用邮箱的用户名和授权码连上邮件服务器
        ts.connect("smtp.qq.com", "3040185132@qq.com", "tcpsnkrzjncodfef");

        //4.创建邮件
        //注意需要传递session
        MimeMessage message = new MimeMessage(session);
        //指明邮件发送人
        message.setFrom(new InternetAddress("3040185132@qq.com"));
        //指明收件人
        message.setRecipient(Message.RecipientType.TO, new InternetAddress("guyu_818@163.com"));
        //邮件的主题
        message.setSubject("谷雨给你发邮件拉");
        //===============复杂的邮件=======图片+文字+附件
        //准备图片数据
        MimeBodyPart image = new MimeBodyPart();
        //图片需要经过数据处理。。。。。DataHandler:数据处理
        DataHandler dh = new DataHandler(new FileDataSource("F:\\smbms\\src\\main\\webapp\\images\\clock.jpg"));
        image.setDataHandler(dh);//在我们的body中放入这个处理的图片数据
        image.setContentID("guyu.jpg");//给图片设置一个Id，我们可以在后面使用

        //准备正文的数据
        MimeBodyPart text = new MimeBodyPart();
        text.setContent("这是一封邮件正文带图片<img src='cid:guyu.jpg'>的邮件","text/html;charset=UTF-8");

        //描述数据关系
        MimeMultipart mm = new MimeMultipart();
        mm.addBodyPart(text);
        mm.addBodyPart(image);
        mm.setSubType("related");//也可以使用mixed,并且默认是mixed

        //++++++++++++将拼装好的文字和图片设为一个+++++++++++
        MimeBodyPart contentText = new MimeBodyPart();
        contentText.setContent(mm);

        //========准备一个附件，和准备图片比较类似
        MimeBodyPart body = new MimeBodyPart();
        DataHandler dh1 = new DataHandler(new FileDataSource("F:\\smbms\\src\\main\\java\\com\\guyu\\mail\\SendEmail.java"));
        body.setDataHandler(dh1);//在我们的body中放入这个处理的附件数据
        body.setFileName("guyujava");//附件设置名字
        //=========将附件和上面的拼装体一块拼到一起===========
        MimeMultipart allFile = new MimeMultipart();
        allFile.addBodyPart(body);//附件
        allFile.addBodyPart(contentText);//图片和文字的拼装体
        allFile.setSubType("mixed");//正文和邮件图片都存在，所以类型需要设置为mixed


        //设置到消息中，保存修改
        message.setContent(allFile);//把最后编辑好的邮件放到消息中
        message.saveChanges();

        //5.发送邮件
        ts.sendMessage(message, message.getAllRecipients());

        //6.关闭连接
        ts.close();
    }
}
