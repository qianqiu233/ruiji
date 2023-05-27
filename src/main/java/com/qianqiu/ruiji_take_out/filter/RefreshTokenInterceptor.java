package com.qianqiu.ruiji_take_out.filter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;

import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.qianqiu.ruiji_take_out.dto.UserDto;
import com.qianqiu.ruiji_take_out.pojo.Employee;
import com.qianqiu.ruiji_take_out.pojo.User;
import com.qianqiu.ruiji_take_out.utils.Holder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.qianqiu.ruiji_take_out.utils.RedisConstant.*;

@Slf4j
public class RefreshTokenInterceptor implements HandlerInterceptor {
    private StringRedisTemplate stringRedisTemplate;
    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String UserToken = request.getHeader("authorizationUser");
        log.info("token--{}",UserToken);
        if (StrUtil.isBlank(UserToken)) {
            return true;
        }
        String userTokenKey=REDIS_USER_LOGIN_KEY+UserToken;
        //获取redis中的id
        String userId = stringRedisTemplate.opsForValue().get(userTokenKey);
        // 3.判断用户是否存在
        if (userId==null) {
            //存在
            return true;
        }
        // 5.将查询到的hash数据转为User
        // 6.存在，保存用户id到 ThreadLocal

        Holder.saveId(Long.valueOf(userId));
        stringRedisTemplate.expire(userTokenKey, REDIS_USER_LOGIN_TTL, TimeUnit.MINUTES);
        //不存在，也放行，在下一个拦截器一样拦截
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户id
        Holder.removeId();

    }
}
