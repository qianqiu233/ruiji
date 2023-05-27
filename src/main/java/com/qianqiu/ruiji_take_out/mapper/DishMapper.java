package com.qianqiu.ruiji_take_out.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qianqiu.ruiji_take_out.pojo.Category;
import com.qianqiu.ruiji_take_out.pojo.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品分类使用
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
