package com.qianqiu.ruiji_take_out.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qianqiu.ruiji_take_out.mapper.EmployeeMapper;
import com.qianqiu.ruiji_take_out.mapper.SetmealDishMapper;
import com.qianqiu.ruiji_take_out.pojo.Employee;
import com.qianqiu.ruiji_take_out.pojo.SetmealDish;
import com.qianqiu.ruiji_take_out.service.EmployeeService;
import com.qianqiu.ruiji_take_out.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
