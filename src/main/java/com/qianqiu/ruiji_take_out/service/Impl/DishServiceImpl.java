package com.qianqiu.ruiji_take_out.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.dto.DishDto;
import com.qianqiu.ruiji_take_out.mapper.CategoryMapper;
import com.qianqiu.ruiji_take_out.mapper.DishFlavorMapper;
import com.qianqiu.ruiji_take_out.mapper.DishMapper;
import com.qianqiu.ruiji_take_out.pojo.Category;
import com.qianqiu.ruiji_take_out.pojo.Dish;
import com.qianqiu.ruiji_take_out.pojo.DishFlavor;
import com.qianqiu.ruiji_take_out.pojo.Employee;
import com.qianqiu.ruiji_take_out.service.DishFlavorService;
import com.qianqiu.ruiji_take_out.service.DishService;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;


    /**
     * 新增菜品，同时保存对应的口味数据
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void addDishWithFlavor(DishDto dishDto) {
        //保存菜品的基本数据到菜品表Dish中
        this.save(dishDto);
        //保存菜品的口味到dish_flavor表
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        //把dishId给到DishFlavor中的dishId
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());//使用 collect() 方法将修改后的元素收集到一个新的列表中并返回。
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public R<Page> DishByPage(int page, int pageSize, String name) {
        //1.构造分页构造器
        Page<Dish> pageInfo = new Page<Dish>(page, pageSize);
        Page<DishDto> dishDtoPage=new Page<>();
        //2.构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //3.添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        //4.添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //5.执行查询
        dishMapper.selectPage(pageInfo, queryWrapper);
        //6.对象拷贝,把pageInfo中的所有都拷贝到,dishDtoPage，除了records
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        //7.处理records中的数据，让CategoryId有值，从而获取分类名字
        List<Dish> records = pageInfo.getRecords();
        //通过流来遍历，找到自己需要修改的东西，然后修改完重新封装
        List<DishDto> dishDtoList = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //将其他数据拷贝到dishDto中
            BeanUtils.copyProperties(item, dishDto);
            //获取分类的id
            Long categoryId = item.getCategoryId();
            //根据id获取对应的分类
            Category category = categoryMapper.selectById(categoryId);
            if (categoryId!=null){
                //获取分类的名称
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(dishDtoList);
        return R.success(dishDtoPage);
    }

    /**
     * 菜品修改数据回显
     *需要回显菜品信息和口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto EchoDishDataById(Long id) {
        //查询Dish中的菜品数据
        Dish dish = dishMapper.selectById(id);
        //查询DishFlavor中的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(queryWrapper);
        //将数据拷贝到DishDto中
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(dishFlavors);
        return dishDto;
    }

    /**
     * 菜品修改
     * 更新菜品信息和口味信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品信息
        dishMapper.updateById(dishDto);
        //更新口味信息
        //清理当前口味数据
        //构造条件对象
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorMapper.delete(queryWrapper);
        //添加提交过来的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

//    @Override
//    public List<Dish> DishList(Dish dish) {
//        //构造查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        //添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> dishList = dishMapper.selectList(queryWrapper);
//        return dishList;
//    }
@Override
public List<DishDto> DishList(Dish dish) {
    //构造查询条件
    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
    //添加排序条件
    queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
    List<Dish> dishList = dishMapper.selectList(queryWrapper);
    List<DishDto> dishDtoList = dishList.stream().map((item) -> {
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(item,dishDto);
        Long categoryId = item.getCategoryId();
        //根据categoryId查询分类对象
        Category category = categoryMapper.selectById(categoryId);
        if (category!=null){
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
        }
        //当前菜品id
        Long dishId = item.getId();
        //查询菜品获取口味数据
        LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorQueryWrapper.eq(DishFlavor::getDishId,dishId);
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(dishFlavorQueryWrapper);
        dishDto.setFlavors(dishFlavors);
        return dishDto;

    }).collect(Collectors.toList());
    return dishDtoList;
}

}
