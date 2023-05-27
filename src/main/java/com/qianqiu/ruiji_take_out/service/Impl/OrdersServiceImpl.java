package com.qianqiu.ruiji_take_out.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qianqiu.ruiji_take_out.common.CustomException;
import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.dto.OrdersDto;
import com.qianqiu.ruiji_take_out.mapper.AddressBookMapper;
import com.qianqiu.ruiji_take_out.mapper.OrderDetailMapper;
import com.qianqiu.ruiji_take_out.mapper.OrdersMapper;
import com.qianqiu.ruiji_take_out.mapper.ShoppingCartMapper;
import com.qianqiu.ruiji_take_out.pojo.*;
import com.qianqiu.ruiji_take_out.service.AddressBookService;
import com.qianqiu.ruiji_take_out.service.OrderDetailService;
import com.qianqiu.ruiji_take_out.service.OrdersService;
import com.qianqiu.ruiji_take_out.service.UserService;
import com.qianqiu.ruiji_take_out.utils.Holder;
import org.jacoco.agent.rt.internal_f3994fa.core.internal.flow.IFrame;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 提交下单
     *
     * @param orders
     */
    @Override
    @Transactional
    public void submitOrders(Orders orders) {
        //获取当前用户id
        Long userId = Holder.getId();
        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(queryWrapper);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new CustomException("购物车为空，请添加菜品或套餐");
        }
        //查询用户数据
        User user = userService.getById(userId);
        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookMapper.selectById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("地址信息有误,无法下单");
        }
        //订单号
        long orderId = IdWorker.getId();
        //算金额
        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        //向订单表插入数据,一条数据
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        ordersMapper.insert(orders);
        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);
        //清空购物车数据
        shoppingCartMapper.delete(queryWrapper);

    }

    @Override
    public R<Page> userOrdersListByPage(int page, int pageSize) {
        //1.构造分页构造器
        Page<Orders> orderPageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        //2.构造条件构造器,根据sort排序
        LambdaQueryWrapper<Orders> ordersQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据下单时间排序
        ordersQueryWrapper.orderByDesc(Orders::getOrderTime);
        ordersMapper.selectPage(orderPageInfo, ordersQueryWrapper);
        //对象拷贝，将分页数据和订单数据拷贝过去，除了records
        BeanUtils.copyProperties(orderPageInfo, ordersDtoPage, "records");
        //重新设置records的数据
        List<Orders> records = orderPageInfo.getRecords();
        List<OrdersDto> list = records.stream().map((item) -> {
            //准备将数据都装到ordersDto中
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            //获取下单id
            Long id = item.getId();
            //根据id查寻订单一个数据，遍历了会一个个查
            Orders orders = ordersMapper.selectById(id);
            //获取orders的订单id
            String number = orders.getNumber();
            lambdaQueryWrapper.eq(OrderDetail::getOrderId, number);
            //获取了全部的订单菜品数据
            List<OrderDetail> orderDetailList = orderDetailMapper.selectList(lambdaQueryWrapper);
            //初始化订单数量
            int num = 0;
            for (OrderDetail l : orderDetailList) {
                num += l.getNumber().intValue();
            }
            //设置订单数量
            ordersDto.setSumNum(num);
            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(list);

        return R.success(ordersDtoPage);
    }
}
