package com.qianqiu.ruiji_take_out.service.Impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.dto.UserDto;
import com.qianqiu.ruiji_take_out.mapper.UserMapper;
import com.qianqiu.ruiji_take_out.utils.EmailUtils;
import com.qianqiu.ruiji_take_out.pojo.User;
import com.qianqiu.ruiji_take_out.service.UserService;
import com.qianqiu.ruiji_take_out.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.qianqiu.ruiji_take_out.utils.ErrorConstant.ERROR_USER_LOGIN_STRING;
import static com.qianqiu.ruiji_take_out.utils.RedisConstant.*;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserMapper userMapper;


    @Override
    public boolean sendMsg(User user) {
        //获取电子邮箱
        String userEmail = user.getPhone();
        if(StringUtils.isNotEmpty(userEmail)){
            //生成验证码
            String code= ValidateCodeUtils.generateValidateCode(4).toString();
            //发送验证码服务
            SimpleMailMessage mail = EmailUtils.sendEmail("测试",userEmail,"您的验证码为",code);
            javaMailSender.send(mail);
            //验证码存入redis
            stringRedisTemplate.opsForValue().set(REDIS_USER_LOGIN_CODE,code,REDIS_USER_LOGIN_CODE_TTL, TimeUnit.MINUTES);
            return true;
        }
        return false;

    }

    /**
     * 登录
     * 校验
     * @param userDto
     */
    @Override
    public R<User> login(UserDto userDto) {
        //获取邮箱
        String userEmail = userDto.getPhone();
        //获取输入的验证码
        String userCode = userDto.getCode();
        //从redis中获取发送的验证码
        String redisCode = stringRedisTemplate.opsForValue().get(REDIS_USER_LOGIN_CODE);
        //比对验证码
        if(redisCode!=null&&redisCode.equals(userCode)){
            //登录成功
            //删除保存的验证码
            stringRedisTemplate.delete(REDIS_USER_LOGIN_CODE);
            //判断当前用户是否为新用户，是则自动完成注册(此邮箱在数据库中是否存在)
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,userEmail);
            User user = userMapper.selectOne(queryWrapper);
            if(user==null){
                //是新用户
                user=new User();
                user.setPhone(userEmail);
                user.setStatus(1);
               userMapper.insert(user);
            }
            //将用户id存入redis
            String token = UUID.randomUUID().toString(true);
            Long userId = user.getId();
            stringRedisTemplate.opsForValue().set(REDIS_USER_LOGIN_KEY+token, String.valueOf(userId),REDIS_USER_LOGIN_TTL,TimeUnit.MINUTES);
            return R.success(user,token);
        }
        return R.error(ERROR_USER_LOGIN_STRING);
    }
}
