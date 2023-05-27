package com.qianqiu.ruiji_take_out.controller;

import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.pojo.ShoppingCart;
import com.qianqiu.ruiji_take_out.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.qianqiu.ruiji_take_out.utils.SuccessConstant.SUCCESS_ADD;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 查询购物车内容
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> ShoppingCartList() {
        List<ShoppingCart> list=shoppingCartService.ShoppingCartList();
        return R.success(list);
    }


    /**
     * 向购物车添加
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> addShoppingCart(@RequestBody ShoppingCart shoppingCart){
        ShoppingCart userShoppingCart = shoppingCartService.addShoppingCart(shoppingCart);
        return R.success(userShoppingCart);
    }

    /**
     * 购物车内删除商品
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> subShoppingCart(@RequestBody ShoppingCart shoppingCart){
        ShoppingCart userShoppingCart=shoppingCartService.subShoppingCart(shoppingCart);
        if (userShoppingCart==null){
            return R.error("删除失败");
        }
        return R.success(userShoppingCart);
    }

    /**
     *清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clearShoppingCart(){
        shoppingCartService.clearShoppingCart();
        return R.success("成功清除购物车");
    }
}
