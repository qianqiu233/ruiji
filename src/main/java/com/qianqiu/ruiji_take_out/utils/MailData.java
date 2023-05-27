package com.qianqiu.ruiji_take_out.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
public class MailData {
    private String Sender="2799611325@qq.com";//发送人
    private String recipient;//接收人
    private String title;//标题
    private String messageBody;//正文

    public MailData(String title,String recipient,String messageBody) {
        this.recipient =recipient ;
        this.title = title;
        this.messageBody = messageBody;
    }

}
