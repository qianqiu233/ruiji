package com.qianqiu.ruiji_take_out.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

public class EmailUtils {

    public static SimpleMailMessage sendEmail(String title,String recipient,String messageBody,String code){
        SimpleMailMessage message=new SimpleMailMessage();
        MailData mailData=new MailData(title,recipient,messageBody);
        message.setFrom(mailData.getSender());
        message.setTo(mailData.getRecipient());
        message.setSubject(mailData.getTitle());
        message.setText(mailData.getMessageBody()+code);
        return message;
    }
}
