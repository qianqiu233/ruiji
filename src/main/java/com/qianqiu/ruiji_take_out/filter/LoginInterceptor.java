package com.qianqiu.ruiji_take_out.filter;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.qianqiu.ruiji_take_out.pojo.Employee;
import com.qianqiu.ruiji_take_out.utils.Holder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.qianqiu.ruiji_take_out.utils.RedisConstant.REDIS_EMPLOYEE_LOGIN_KEY;
import static com.qianqiu.ruiji_take_out.utils.RedisConstant.REDIS_EMPLOYEE_LOGIN_TTL;
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.判断是否需要拦截(ThreadLocal中是否有用户)
        Long id = Holder.getId();
        if (id==null) {
            //没有？，拦截
            response.setStatus(401);
            return false;
        }
        //有用户，则放行
        return true;
    }
}
