package com.qianqiu.ruiji_take_out.common;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.qianqiu.ruiji_take_out.utils.Holder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * mybatis-plus 自动填充
 */
@Slf4j
@Component
public class RuiJiMetaObjectHandler implements MetaObjectHandler {
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//    //获取创建人和更新人的id,都是一个人，就是现在的登入人，直接redis中拿出来
    private Long employeeId;
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段填充 insert");
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        employeeId= Holder.getId();
        metaObject.setValue("createUser",employeeId);
        metaObject.setValue("updateUser",employeeId);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        long id = Thread.currentThread().getId();
        log.info("线程id为:{}",id);
        metaObject.setValue("updateTime", LocalDateTime.now());
        employeeId= Holder.getId();
        metaObject.setValue("updateUser",employeeId);
    }
}
