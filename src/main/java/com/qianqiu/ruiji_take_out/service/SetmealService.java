package com.qianqiu.ruiji_take_out.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.dto.SetmealDto;
import com.qianqiu.ruiji_take_out.pojo.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    R<Page> SetmealByPage(int page, int pageSize, String name);

    void addSetmealWithDish(SetmealDto setmealDto);

    SetmealDto EchoSetmealDataById(Long id);

    void updateWithDish(SetmealDto setmealDto);

    void deleteWithDish(List<Long> ids);

//    List<SetmealDto> SetmealList(Setmeal setmeal);
    List<Setmeal> SetmealList(Setmeal setmeal);
}
