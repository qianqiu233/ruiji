package com.qianqiu.ruiji_take_out.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.dto.DishDto;
import com.qianqiu.ruiji_take_out.pojo.Category;
import com.qianqiu.ruiji_take_out.pojo.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表:dish，dish_flavor
    public void addDishWithFlavor(DishDto dishDto);

    R<Page> DishByPage(int page, int pageSize, String name);

    DishDto EchoDishDataById(Long id);

    void updateWithFlavor(DishDto dishDto);

//    List<Dish> DishList(Dish dish);

    List<DishDto> DishList(Dish dish);

    void deleteDish(String[] ids);
}
