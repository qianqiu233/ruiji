package com.qianqiu.ruiji_take_out.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.mapper.EmployeeMapper;
import com.qianqiu.ruiji_take_out.pojo.Employee;
import com.qianqiu.ruiji_take_out.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import static com.qianqiu.ruiji_take_out.utils.RedisConstant.REDIS_EMPLOYEE_LOGIN_KEY;

@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public R<Page> EmployeeByPage(int page, int pageSize, String name) {
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //1.构造分页构造器
        Page<Employee> pageInfo = new Page<Employee>(page, pageSize);
        //2.构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //3.添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //4.添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeMapper.selectPage(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 添加员工
     * @param employee
     */
    @Override
    @Transactional
    public void addEmployee(Employee employee) {
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employeeMapper.insert(employee);
    }

    /**
     * 编辑员工，编辑状态
     * @param employee
     */
    @Override
    @Transactional
    public void updateEmployee(Employee employee) {
        long id = Thread.currentThread().getId();
        log.info("线程id为:{}",id);
        employeeMapper.updateById(employee);
    }
}
