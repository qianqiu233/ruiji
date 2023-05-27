package com.qianqiu.ruiji_take_out.controller;

import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.dto.UserDto;
import com.qianqiu.ruiji_take_out.pojo.User;
import com.qianqiu.ruiji_take_out.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.qianqiu.ruiji_take_out.utils.ErrorConstant.*;
import static com.qianqiu.ruiji_take_out.utils.RedisConstant.REDIS_USER_LOGIN_KEY;
import static com.qianqiu.ruiji_take_out.utils.SuccessConstant.SUCCESS_LOGOUT;
import static com.qianqiu.ruiji_take_out.utils.SuccessConstant.SUCCESS_SEND;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 发送电子邮箱验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user){
        if(userService.sendMsg(user)){
            return R.success(SUCCESS_SEND);
        }
        return R.error(ERROR_SEND);
    }
    @PostMapping("/login")
    public R<User> login(@RequestBody UserDto userDto){
     return userService.login(userDto);



    }
    @PostMapping("/loginout")
    public R<String> userLoginout(){
        String userKey=REDIS_USER_LOGIN_KEY;
        stringRedisTemplate.delete(userKey);
        return R.success(SUCCESS_LOGOUT);
    }
}
