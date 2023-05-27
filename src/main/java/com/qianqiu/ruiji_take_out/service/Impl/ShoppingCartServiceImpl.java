package com.qianqiu.ruiji_take_out.service.Impl;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qianqiu.ruiji_take_out.mapper.ShoppingCartMapper;
import com.qianqiu.ruiji_take_out.pojo.ShoppingCart;
import com.qianqiu.ruiji_take_out.service.ShoppingCartService;
import com.qianqiu.ruiji_take_out.utils.Holder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    /**
     * 向购物车内添加
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart addShoppingCart(ShoppingCart shoppingCart) {
        //设置用户id，指定当前是哪个用户的购物车
        //更具ThreadLocal获取当前用户id
        Long userId = Holder.getId();
        shoppingCart.setUserId(userId);
        //查询当前菜品或套餐是否已经在购物车内，在，就增加一份\
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        if (dishId != null) {
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        }
        if (setmealId != null) {
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
        }
        ShoppingCart userShoppingCart = shoppingCartMapper.selectOne(queryWrapper);
        //查询套餐是否已经存在，存在则+1
        if (userShoppingCart != null) {
            //疑问 口味不同怎么办
            //更具dishid查询对应的口味数据，然后进行比较
            Integer number = userShoppingCart.getNumber();
            userShoppingCart.setNumber(number + 1);
            shoppingCartMapper.updateById(userShoppingCart);
        } else {
            //不存在，则添加到购物车
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
            userShoppingCart = shoppingCart;
        }
        return userShoppingCart;

    }

    /**
     * 不要这个菜品了 减掉
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart subShoppingCart(ShoppingCart shoppingCart) {
        //设置用户id，指定当前是哪个用户的购物车
        //更具ThreadLocal获取当前用户id
        Long userId = Holder.getId();
        shoppingCart.setUserId(userId);
        //查询当前菜品或套餐是否已经在购物车内
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        if (dishId != null) {
            //是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        }
        if (setmealId != null) {
            //是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
        }
        ShoppingCart userShoppingCart = shoppingCartMapper.selectOne(queryWrapper);
        if(userShoppingCart.getNumber()>0){
            Integer number = userShoppingCart.getNumber();
            userShoppingCart.setNumber(number - 1);
            shoppingCartMapper.updateById(userShoppingCart);
        }
        if (userShoppingCart.getNumber()==0){
            Long id = userShoppingCart.getId();
            shoppingCartMapper.deleteById(id);
        }
        return userShoppingCart;
    }

    /**
     * 查询购物车内容
     * @return
     */
    @Override
    public List<ShoppingCart> ShoppingCartList() {
        Long userId = Holder.getId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(queryWrapper);
        return shoppingCarts;
    }

    /**
     * 清空购物车
     */
    @Override
    public void clearShoppingCart() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        Long userId = Holder.getId();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        shoppingCartMapper.delete(queryWrapper);
    }
}
