package com.qianqiu.ruiji_take_out.service.Impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qianqiu.ruiji_take_out.common.CustomException;
import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.dto.SetmealDto;
import com.qianqiu.ruiji_take_out.mapper.CategoryMapper;
import com.qianqiu.ruiji_take_out.mapper.SetmealDishMapper;
import com.qianqiu.ruiji_take_out.mapper.SetmealMapper;
import com.qianqiu.ruiji_take_out.pojo.*;
import com.qianqiu.ruiji_take_out.service.SetmealDishService;
import com.qianqiu.ruiji_take_out.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.qianqiu.ruiji_take_out.utils.RedisConstant.REDIS_DISH_SETMEAL_TTL;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public R<Page> SetmealByPage(int page, int pageSize, String name) {
        //1.构造分页构造器
        Page<Setmeal> pageInfo = new Page<Setmeal>(page, pageSize);
        Page<SetmealDto> setmealDtoPage=new Page<>();
        //2.构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //3.添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        //4.添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //5.执行查询
        setmealMapper.selectPage(pageInfo, queryWrapper);
        //拷贝对象
        //6.对象拷贝,把pageInfo中的所有都拷贝到,setmealDtoPage，除了Records
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"Records");
        //7.处理records中的数据，让CategoryId有值，从而获取分类名字
        List<Setmeal> records = pageInfo.getRecords();
        //通过流来遍历，找到自己需要修改的东西，然后修改完重新封装
        List<SetmealDto> setmealDtoList = records.stream().map((item) -> {
            SetmealDto setmealDto=new SetmealDto();
            //将其他数据拷贝到setmealDto中
            BeanUtils.copyProperties(item,setmealDto);
            //获取分类的id
            Long categoryId = item.getCategoryId();
            //根据id获取对应的分类
            Category category = categoryMapper.selectById(categoryId);
            if (categoryId!=null){
                //获取分类的名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(setmealDtoList);
        return R.success(setmealDtoPage);
    }

    /**
     * 添加套餐
     * @param setmealDto
     */
    @Override
    @Transactional
    public void addSetmealWithDish(SetmealDto setmealDto) {
        String setmealKey="setmeal:"+setmealDto.getCategoryId();
        stringRedisTemplate.delete(setmealKey);
        //保存套餐的基本数据到套餐表Setmeal中
        setmealMapper.insert(setmealDto);
        //保存套餐菜品到setmealDish表
        Long setmealDtoId = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //把套餐菜品id给到setmealDish中的SetmealId
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDtoId);
            return item;
        }).collect(Collectors.toList());//使用 collect() 方法将修改后的元素收集到一个新的列表中并返回。
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 修改   数据回显
     *
     * @param id
     * @return
     */
    @Override
    public SetmealDto EchoSetmealDataById(Long id) {
         //查询套餐数据
        Setmeal setmeal = setmealMapper.selectById(id);
        //查询套餐里的菜品数据
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getDishId,setmeal.getId());
        List<SetmealDish> setmealDishes = setmealDishMapper.selectList(queryWrapper);
        //将数据拷贝到SetmealDto中
        SetmealDto setmealDto=new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        setmealDto.setSetmealDishes(setmealDishes);
        return setmealDto;
    }

    /**
     * 修改套餐
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        String setmealKey="setmeal:"+setmealDto.getCategoryId();
        stringRedisTemplate.delete(setmealKey);
        //更新套餐信息
        setmealMapper.updateById(setmealDto);
        //更新套餐内菜品信息
        //清理当前套餐菜品信息
        //构造条件对象
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishMapper.delete(queryWrapper);
        //添加提交过来的套餐内菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐
     * 同时需要删除内部的菜品
     * @param ids
     */
    @Override
    public void deleteWithDish(List<Long> ids) {
        //查询套餐的状态，停售才能删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> setmeals = setmealMapper.selectList(queryWrapper);
        queryWrapper.eq(Setmeal::getStatus,1);
        Integer count = setmealMapper.selectCount(queryWrapper);
        if (count>0){
            throw new CustomException("套餐正在售卖中，无法删除");
        }
        //如果可以删除，先删除套餐表中的数据
        setmealMapper.deleteBatchIds(ids);
        //删除关系表的数据
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishMapper.delete(setmealDishLambdaQueryWrapper);
        //判断且准备删除redis的数据完成同步
        Iterator<Setmeal> iterator=setmeals.listIterator();
        while (iterator.hasNext()){
            Long categoryId = iterator.next().getCategoryId();
            String setmealKey="setmeal:"+categoryId;
            if (stringRedisTemplate.hasKey(setmealKey)){
                stringRedisTemplate.delete(setmealKey);
            }
        }
    }

    /**
     * 查询套餐集合
     * 我觉得应该用这个，可以查询到套餐内的菜品，然后根据喜好选择口味
     * @param setmeal
     * @return
     */
//    @Override
//    public List<SetmealDto> SetmealList(Setmeal setmeal) {
//        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
//        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
//        //添加排序条件
//        queryWrapper.orderByAsc(Setmeal::getPrice).orderByDesc(Setmeal::getUpdateTime);
//        List<Setmeal> setmeals = setmealMapper.selectList(queryWrapper);
//        List<SetmealDto>  setmealDtoList= setmeals.stream().map((item) -> {
//            SetmealDto setmealDto = new SetmealDto();
//            BeanUtils.copyProperties(item,setmealDto);
//            Long categoryId = item.getCategoryId();
//            //查询一下分类id
//            Category category = categoryMapper.selectById(categoryId);
//            if (categoryId!=null){
//                String categoryName = category.getName();
//                setmealDto.setCategoryName(categoryName);
//            }
//            //查询一下当前套餐id
//            Long setmealId = item.getId();
//            //根据id查询对应的菜品数据
//            LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();
//            setmealDishQueryWrapper.eq(SetmealDish::getSetmealId,setmealId);
//            List<SetmealDish> setmealDishes = setmealDishMapper.selectList(setmealDishQueryWrapper);
//            setmealDto.setSetmealDishes(setmealDishes);
//            return setmealDto;
//        }).collect(Collectors.toList());
//
//        return setmealDtoList;
//    }
    @Override
    public List<Setmeal> SetmealList(Setmeal setmeal) {
        List<Setmeal> setmeals=null;
        String setmealKey="setmeal:"+setmeal.getCategoryId();
        String setmealsRedisJson = stringRedisTemplate.opsForValue().get(setmealKey);
        setmeals = JSONUtil.toList(setmealsRedisJson, Setmeal.class);
        if(setmeals.size()!=0){
            return setmeals;
        }
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        //添加排序条件
        queryWrapper.orderByAsc(Setmeal::getPrice).orderByDesc(Setmeal::getUpdateTime);
        setmeals = setmealMapper.selectList(queryWrapper);
        String setmealsJson = JSONUtil.toJsonStr(setmeals);
        stringRedisTemplate.opsForValue().set(setmealKey,setmealsJson,REDIS_DISH_SETMEAL_TTL, TimeUnit.MINUTES);
        return setmeals;
    }
}
