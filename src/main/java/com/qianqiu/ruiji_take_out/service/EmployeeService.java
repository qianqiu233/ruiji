package com.qianqiu.ruiji_take_out.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.pojo.Employee;

public interface EmployeeService extends IService<Employee> {
    void addEmployee(Employee employee);

    R<Page> EmployeeByPage(int page, int pageSize, String name);

    void updateEmployee(Employee employee);
}
