package com.qianqiu.ruiji_take_out.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qianqiu.ruiji_take_out.mapper.CategoryMapper;
import com.qianqiu.ruiji_take_out.mapper.DishFlavorMapper;
import com.qianqiu.ruiji_take_out.pojo.Category;
import com.qianqiu.ruiji_take_out.pojo.DishFlavor;
import com.qianqiu.ruiji_take_out.service.CategoryService;
import com.qianqiu.ruiji_take_out.service.DishFlavorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
