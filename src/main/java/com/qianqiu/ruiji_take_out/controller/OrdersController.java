package com.qianqiu.ruiji_take_out.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.pojo.OrderDetail;
import com.qianqiu.ruiji_take_out.pojo.Orders;
import com.qianqiu.ruiji_take_out.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @PostMapping("/submit")
    public R<String> submitOrders(@RequestBody Orders orders) {
        ordersService.submitOrders(orders);
        return R.success("提交成功");
    }

    @GetMapping("/userPage")
    public R<Page> userOrdersListByPage(int page, int pageSize) {
        return ordersService.userOrdersListByPage(page,pageSize);
    }
}
