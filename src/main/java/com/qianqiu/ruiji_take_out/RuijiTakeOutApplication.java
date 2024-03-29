package com.qianqiu.ruiji_take_out;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
//开启过滤器
@ServletComponentScan
//开启事务支持
@EnableTransactionManagement
//开启spring cache缓存
//@EnableCaching
public class RuijiTakeOutApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuijiTakeOutApplication.class, args);
    }

}
