package com.qianqiu.ruiji_take_out.config;

import com.qianqiu.ruiji_take_out.common.JacksonObjectMapper;
import com.qianqiu.ruiji_take_out.filter.LoginInterceptor;
import com.qianqiu.ruiji_take_out.filter.RefreshTokenInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

/**
 * 设置静态资源映射
 * (我想直接丢static包里-_-!)
 */
@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport{
    /**
     * 设置金泰资源映射
     * @param registry
     */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开启静态资源映射");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * 拓展mvc框架的消息转换器
     * @param converters
     */
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters){
        log.info("拓展消息转换器");
        //创建消息转换器
        MappingJackson2HttpMessageConverter messageConverter=new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用jackson将java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0,messageConverter);
    }
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // 登录拦截器
//        registry.addInterceptor(new LoginInterceptor())
//                .excludePathPatterns(
//                        "/employee/login",
//                        "/employee/logout",
//                        "/backend/**",
//                        "/front/**",
//                        "/common/**",
//                        "/user/sendMsg",//移动端发送短信
//                        "/user/login"//移动端登录
//                ).order(1);
//        // token刷新的拦截器，拦截所有请求，
//        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).addPathPatterns("/**").order(0);
//    }

}
