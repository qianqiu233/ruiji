package com.qianqiu.ruiji_take_out.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.dto.DishDto;
import com.qianqiu.ruiji_take_out.pojo.Dish;
import com.qianqiu.ruiji_take_out.pojo.Employee;
import com.qianqiu.ruiji_take_out.service.DishFlavorService;
import com.qianqiu.ruiji_take_out.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.qianqiu.ruiji_take_out.utils.SuccessConstant.*;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 添加菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto) {
        dishService.addDishWithFlavor(dishDto);
//        Set<String> keys = stringRedisTemplate.keys("dish:*");
//        log.info("SET------{}",keys);
//        stringRedisTemplate.delete(keys);
        return R.success(SUCCESS_ADD);
    }

    /**
     * 菜品分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> DishByPage(int page, int pageSize, String name) {
        return dishService.DishByPage(page, pageSize, name);
    }

    /**
     * 菜品修改数据回显
     * 需要回显菜品信息和口味信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> EchoDishDataById(@PathVariable Long id) {
        DishDto dishDtoData = dishService.EchoDishDataById(id);
        return R.success(dishDtoData);
    }

    /**
     * 菜品修改
     * 更新菜品信息和口味信息
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto) {
        //更新菜品信息，同时更新口味信息
        dishService.updateWithFlavor(dishDto);
        //清理所有菜品的缓存数据

        return R.success(SUCCESS_UPDATE);
    }

    /**
     * 修改状态
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateDishStatus(@PathVariable int status, String[] ids) {
        for (String id : ids) {
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success(SUCCESS_UPDATE);
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @Transactional
    public R<String> deleteDish(String[] ids) {
        dishService.deleteDish(ids);
        return R.success(SUCCESS_DELETE);
    }

    /**
     * 查询菜品集合
     * 在客户端界面显示对应的数据，可添加规格口味
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> DishList(Dish dish) {
//        List<Dish> dishList = dishService.DishList(dish);
//        return R.success(dishList);
//    }
    @GetMapping("/list")
    public R<List<DishDto>> DishList(Dish dish) {
        List<DishDto> dishList = dishService.DishList(dish);
        return R.success(dishList);
    }

}
