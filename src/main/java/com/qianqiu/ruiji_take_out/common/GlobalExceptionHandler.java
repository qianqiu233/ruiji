package com.qianqiu.ruiji_take_out.common;

import com.qianqiu.ruiji_take_out.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import java.sql.SQLIntegrityConstraintViolationException;

import static com.qianqiu.ruiji_take_out.utils.ErrorConstant.ERROR_ADD;

/**
 * 全局异常处理器
 */
@Slf4j
@ControllerAdvice(annotations = {RestController.class})
@ResponseBody
public class GlobalExceptionHandler {
    /**
     * sql重复
     * @param exception
     * @return
     */
    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public R<String> SqlExceptionHandler(SQLIntegrityConstraintViolationException exception) {
        log.error(exception.getMessage());
        if(exception.getMessage().contains("Duplicate entry")){
            String[] split=exception.getMessage().split(" ");
            String msg=split[2]+"已经存在";
            return R.error(msg);
        }
        return R.error(ERROR_ADD);
    }

    /**
     * 分类关联了菜品，套餐
     * @param exception
     * @return
     */
    @ExceptionHandler({CustomException.class})
    public R<String> CustomExceptionHandler(CustomException exception) {
        log.error(exception.getMessage());
        return R.error(exception.getMessage());
    }

}
