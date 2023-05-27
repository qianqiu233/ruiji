package com.qianqiu.ruiji_take_out.dto;

import com.qianqiu.ruiji_take_out.pojo.User;
import lombok.Data;

@Data
public class UserDto extends User {
    private String code;
}
