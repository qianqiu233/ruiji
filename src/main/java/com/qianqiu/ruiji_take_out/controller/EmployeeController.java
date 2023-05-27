package com.qianqiu.ruiji_take_out.controller;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.pojo.Employee;
import com.qianqiu.ruiji_take_out.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

import static com.qianqiu.ruiji_take_out.utils.RedisConstant.REDIS_EMPLOYEE_LOGIN_KEY;
import static com.qianqiu.ruiji_take_out.utils.ErrorConstant.*;
import static com.qianqiu.ruiji_take_out.utils.RedisConstant.REDIS_EMPLOYEE_LOGIN_TTL;
import static com.qianqiu.ruiji_take_out.utils.SuccessConstant.*;

/**
 * 员工登录
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 员工登入
     * @param employee
     * @param request
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request) {
        //1将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee queryEmployee = employeeService.getOne(queryWrapper);
        //3.如果没有查询到则返回登录失败结果
        if (queryEmployee == null) {
            return R.error(ERROR_EMPLOYEE_LOGIN_STRING);
        }
        //4.密码比对,如果不一致则返回登录失败结果
        if (!queryEmployee.getPassword().equals(password)) {
            return R.error(ERROR_EMPLOYEE_LOGIN_STRING);
        }
        //5.查看员工状态，如果已是禁用状态,则返回员工已禁言结果
        if (queryEmployee.getStatus() == 0) {
            return R.error(ERROR_ACCOUNT_DISABLED);
        }
        String token = UUID.randomUUID().toString(true);
//        //6.登录成功，将员工id存入Session并返回登录成功结果
//        request.getSession().setAttribute("employee",queryEmployee.getId());
        //6.登录成功，将员工id存入redis并返回登录成功结果
        String EmployeeTokenKey = REDIS_EMPLOYEE_LOGIN_KEY+token;
        log.info("EmployeeTokenKey-------------------{}",EmployeeTokenKey);
//        String EmployeeTokenKey = REDIS_EMPLOYEE_LOGIN_KEY;
        stringRedisTemplate.opsForHash().put(EmployeeTokenKey, "id", queryEmployee.getId().toString());
        stringRedisTemplate.expire(EmployeeTokenKey, REDIS_EMPLOYEE_LOGIN_TTL, TimeUnit.MINUTES);
        return R.success(queryEmployee,token);

    }

    /**
     * 员工登出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
//        request.getSession().getAttribute("employee");
        String token = request.getHeader("authorization");
        String EmployeeTokenKey = REDIS_EMPLOYEE_LOGIN_KEY+token;
        log.info("EmployeeTokenKey22-------------------{}",EmployeeTokenKey);
//        String EmployeeTokenKey = REDIS_EMPLOYEE_LOGIN_KEY;
        stringRedisTemplate.opsForHash().delete(EmployeeTokenKey, "id");
        return R.success(SUCCESS_LOGOUT);
    }

    /**
     * 分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> EmployeeByPage(int page, int pageSize, String name) {
          return employeeService.EmployeeByPage(page,pageSize,name);
    }

    /**
     * 添加员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> addEmployee(@RequestBody Employee employee) {
        employeeService.addEmployee(employee);
        return R.success(SUCCESS_ADD);
    }

    /**
     * 修改员工属性和状态
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> updateEmployee(@RequestBody Employee employee){
        employeeService.updateEmployee(employee);
        return R.success(SUCCESS_UPDATE);
    }

    /**
     * 根据id回显数据，然后可以编辑员工
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> EchoDataById(@PathVariable Long id){
        Employee byId = employeeService.getById(id);
        if (byId!=null) {
            return R.success(byId);
        }
            return R.error(ERROR_QUERY_IS_EMPTY);
    }

}
