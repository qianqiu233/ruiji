package com.qianqiu.ruiji_take_out.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianqiu.ruiji_take_out.common.R;
import com.qianqiu.ruiji_take_out.pojo.Category;
import com.qianqiu.ruiji_take_out.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.qianqiu.ruiji_take_out.utils.SuccessConstant.*;

/**
 * 菜品分类
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加分类
     *
     * @param category
     * @return
     */
    @PostMapping
    public R<String> addCategory(@RequestBody Category category) {
        categoryService.addCategory(category);
        return R.success(SUCCESS_ADD);
    }

    /**
     * 分类列表分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> CategoryByPage(int page, int pageSize) {
        return categoryService.CategoryByPage(page, pageSize);
    }

    /**
     * 删除分类
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteCategory(Long ids) {
        categoryService.deleteCategory(ids);
        return R.success(SUCCESS_DELETE);
    }

    /**
     * 修改分类
     * 这个数据回显前端已完成，大赞
     *
     * @param category
     * @return
     */
    @PutMapping
    public R<String> updateCategory(@RequestBody Category category) {
        categoryService.updateCategory(category);
        return R.success(SUCCESS_UPDATE);
    }

    /**
     * 根据条件查询新建菜品内的   菜品分类  数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> CategoryList(Category category) {
        List<Category> categoryList = categoryService.CategoryList(category);
        return R.success(categoryList);
    }


}
