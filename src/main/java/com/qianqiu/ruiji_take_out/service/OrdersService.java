package com.qianqiu.ruiji_take_out.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.pojo.Orders;

public interface OrdersService extends IService<Orders> {
    void submitOrders(Orders orders);

    R<Page> userOrdersListByPage(int page, int pageSize);
}
