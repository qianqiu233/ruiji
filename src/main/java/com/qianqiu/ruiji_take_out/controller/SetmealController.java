package com.qianqiu.ruiji_take_out.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.dto.DishDto;
import com.qianqiu.ruiji_take_out.dto.SetmealDto;
import com.qianqiu.ruiji_take_out.pojo.Setmeal;
import com.qianqiu.ruiji_take_out.service.SetmealDishService;
import com.qianqiu.ruiji_take_out.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.qianqiu.ruiji_take_out.utils.ErrorConstant.ERROR_DELETE;
import static com.qianqiu.ruiji_take_out.utils.SuccessConstant.*;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 添加套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> addSetmealWithDish(@RequestBody SetmealDto setmealDto) {
        setmealService.addSetmealWithDish(setmealDto);
        return R.success(SUCCESS_ADD);
    }

    /**
     * 分页查询套餐
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> SetmealByPage(int page, int pageSize, String name) {
        return setmealService.SetmealByPage(page, pageSize, name);
    }

    /**
     * 删除
     * 同时删除对应的套餐和菜品的关联数据
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @Transactional
    public R<String> deleteSetmeal(@RequestParam List<Long> ids) {
        if(ids.isEmpty()){
            return R.error("请选择删除对象");
        }
        setmealService.deleteWithDish(ids);
        return R.success(SUCCESS_DELETE);
    }

    /**
     * 修改状态
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    @Transactional
    public R<String> updateSetmealStatus(@PathVariable int status, String[] ids) {
        for (String id : ids) {
            Setmeal setmealById = setmealService.getById(id);
            setmealById.setStatus(status);
            setmealService.updateById(setmealById);
        }
        return R.success(SUCCESS_UPDATE);
    }

    /**
     * 修改  回显数据
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> EchoSetmealDataById(@PathVariable Long id) {
        SetmealDto setmealDtoData = setmealService.EchoSetmealDataById(id);
        return R.success(setmealDtoData);
    }

    /**
     * 修改
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> updateSetmeal(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        return R.success(SUCCESS_UPDATE);
    }

    /**
     * 查询套餐集合
     * 在客户端显示出对应的界面数据，可+1套餐
     * @param setmeal
     * @return
     */
//    @GetMapping ("/list")
//    public R<List<SetmealDto>> SetmealList(Setmeal setmeal){
//          List<SetmealDto> setmealDtoList=setmealService.SetmealList(setmeal);
//          return R.success(setmealDtoList);
//    }
    @GetMapping ("/list")
    public R<List<Setmeal>> SetmealList(Setmeal setmeal){
        List<Setmeal> setmealList=setmealService.SetmealList(setmeal);
        return R.success(setmealList);
    }

}
