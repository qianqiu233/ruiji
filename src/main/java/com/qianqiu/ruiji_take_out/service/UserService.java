package com.qianqiu.ruiji_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.dto.UserDto;
import com.qianqiu.ruiji_take_out.pojo.User;

public interface UserService extends IService<User> {
    boolean sendMsg(User user);

    R<User> login(UserDto userDto);
}
