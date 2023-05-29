package com.qianqiu.ruiji_take_out.filter;

import com.alibaba.fastjson.JSON;
import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.utils.Holder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.qianqiu.ruiji_take_out.utils.RedisConstant.*;

//检查用户是否登录 过滤器
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String UserToken = request.getHeader("authorizationUser");
        String EmployeeToken = request.getHeader("authorizationEmployee");
//        log.info("header-------------------------------{}",token);
        //1.获取本次请求URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}", requestURI);
        //1.1不需要处理的路径
        String[] URIS = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",//移动端发送短信
                "/user/login",//移动端登录
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };
        //2.判断本次请求是否需要处理
        boolean checkURIResult = checkURI(URIS, requestURI);
        if (checkURIResult) {
            //3.如果不需要处理，直接放行
            log.info("本次请求路径：{}，不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        //4-1.判断登录状态，如果已经登录，放行    （后台管理端）
//        Object employeeId = request.getSession().getAttribute("employee");
        String EmployeeTokenKey = REDIS_EMPLOYEE_LOGIN_KEY+EmployeeToken;
        log.info("EmployeeTokenKey-------{}",EmployeeTokenKey);
//        String EmployeeTokenKey = REDIS_EMPLOYEE_LOGIN_KEY;
        String employeeId = (String) stringRedisTemplate.opsForHash().get(EmployeeTokenKey, "id");
        if(employeeId!=null){
            Holder.saveId(Long.valueOf(employeeId));
            log.info("用户已经登录，id为：{}", employeeId);
            filterChain.doFilter(request, response);
            return;
        }
//        4-1.判断登录状态，如果已经登录，放行    （移动端）
        String userTokenKey =REDIS_USER_LOGIN_KEY+UserToken;
        String userId = stringRedisTemplate.opsForValue().get(userTokenKey);
        if(userId!=null){
            Holder.saveId(Long.valueOf(userId));
            log.info("用户已经登录，id为：{}", userId);
            filterChain.doFilter(request, response);
            return;
        }
        //5.如果未登录，则返回未登录结果(根据前端的request.js的内容，这边直接输出流向客户端响应数据)
        log.info("用户未登录");
         response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
         return;


    }

    /**
     * * 路径匹配，检查此次请求是否需要放行
     *
     * @param URIS
     * @param requestURI
     * @return
     */
    public boolean checkURI(String[] URIS, String requestURI) {
        for (String uris : URIS) {
            boolean match = PATH_MATCHER.match(uris, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
