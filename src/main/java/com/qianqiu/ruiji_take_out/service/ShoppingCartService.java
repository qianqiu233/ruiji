package com.qianqiu.ruiji_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qianqiu.ruiji_take_out.pojo.ShoppingCart;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {


    ShoppingCart addShoppingCart(ShoppingCart shoppingCart);

    ShoppingCart subShoppingCart(ShoppingCart shoppingCart);


    List<ShoppingCart> ShoppingCartList();

    void clearShoppingCart();
}
