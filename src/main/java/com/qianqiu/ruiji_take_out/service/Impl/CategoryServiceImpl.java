package com.qianqiu.ruiji_take_out.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qianqiu.ruiji_take_out.common.CustomException;
import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.mapper.CategoryMapper;
import com.qianqiu.ruiji_take_out.mapper.DishMapper;
import com.qianqiu.ruiji_take_out.mapper.EmployeeMapper;
import com.qianqiu.ruiji_take_out.mapper.SetmealMapper;
import com.qianqiu.ruiji_take_out.pojo.Category;
import com.qianqiu.ruiji_take_out.pojo.Dish;
import com.qianqiu.ruiji_take_out.pojo.Employee;
import com.qianqiu.ruiji_take_out.pojo.Setmeal;
import com.qianqiu.ruiji_take_out.service.CategoryService;
import com.qianqiu.ruiji_take_out.service.DishService;
import com.qianqiu.ruiji_take_out.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.qianqiu.ruiji_take_out.utils.ErrorConstant.ERROR_DELETE;

@Slf4j
@Service
@Transactional
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
//    @Autowired
//    private DishMapper dishMapper;
//    @Autowired
//    private SetmealMapper setmealMapper;


//    也可以调用Service
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 添加分类
     * @param category
     */
    @Override

    public void addCategory(Category category) {
        log.info("Category{}",category);
       categoryMapper.insert(category);
    }

    /**
     * 分类分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public R<Page> CategoryByPage(int page, int pageSize) {
        //1.构造分页构造器
        Page<Category> CategoryPageInfo = new Page<Category>(page, pageSize);
        //2.构造条件构造器,根据sort排序
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //3.添加排序条件,type相同的请款下，按sort排序

        queryWrapper.orderByAsc(Category::getType).orderByAsc(Category::getSort);
        //4.执行查询
        categoryMapper.selectPage(CategoryPageInfo,queryWrapper);
        return R.success(CategoryPageInfo);
    }

    /**
     * 根据id删除分类，删除前需要判断是否关联了菜品套餐
     * @param ids
     */
    @Override
    public void deleteCategory(Long ids) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件，根据id进行分类
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        int SetmealCount = setmealService.count(setmealLambdaQueryWrapper);
        //查询当前分类是否关联菜品，关联了就抛出一个业务异常
        if (dishCount>0){
            //已经关联菜品，抛出异常
            throw new CustomException("当前分类关联了菜品,"+ERROR_DELETE);
        }
        //查询当前分类是否关联套餐，关联了就抛出一个业务异常
        if (SetmealCount>0){
            //已经关联套餐，抛出异常
            throw new CustomException("当前分类关联了套餐,"+ERROR_DELETE);
        }
        //正常删除分类
        categoryMapper.deleteById(ids);
    }

    @Override
    public void updateCategory(Category category) {
        categoryMapper.updateById(category);
    }

    @Override
    public List<Category> CategoryList(Category category) {
        //条件构造器
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加条件查询
        categoryLambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加条件排序
        categoryLambdaQueryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);
        List<Category> categoryList = categoryMapper.selectList(categoryLambdaQueryWrapper);
        return categoryList;
    }

}
