package com.qianqiu.ruiji_take_out.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.pojo.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    void addCategory(Category category);

    R<Page> CategoryByPage(int page, int pageSize);

    void deleteCategory(Long ids);

    void updateCategory(Category category);

    List<Category> CategoryList(Category category);
}
