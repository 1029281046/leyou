package com.leyou.service;

import com.leyou.item.pojo.Category;
import com.leyou.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> CategoryqueryByPid(Long pid){
        Category category=new Category();
        category.setParentId(pid);
        return categoryMapper.select(category);
    }

    public Integer Categoryadd(Category category){
        Integer i = categoryMapper.insertSelective(category);

        return i;
    }

    public Integer editCategory(Long id, String name) {
        Category category=new Category();
        category.setId(id);
        List<Category> categories = categoryMapper.select(category);
        Category category1 = categories.get(0);
        Long parentId = category1.getParentId();
        Boolean isParent = category1.getIsParent();
        Integer sort = category1.getSort();
        category.setSort(sort);
        category.setIstParent(isParent);
        category.setParentId(parentId);
        category.setName(name);
        Integer i = categoryMapper.updateByPrimaryKey(category);
        return i;
    }

    public Integer delete(Long id) {
        Category category=new Category();
        category.setId(id);
        int i = categoryMapper.deleteByPrimaryKey(category);
        return i;
    }
    public List<Category> queryCategoryBid(Long bid) {
        return this.categoryMapper.queryCategoryList(bid);
    }

    public List<String> queryCategoryByNames(List<Long> ids) {
        List<String> names=new ArrayList<>();
        List<Category> categoryList = this.categoryMapper.selectByIdList(ids);
        for (Category category : categoryList) {
            names.add(category.getName());
        }
        return names;
    }

    public List<Category> queryAllByCid3(Long id) {
        Category c3 = this.categoryMapper.selectByPrimaryKey(id);
        Category c2 = this.categoryMapper.selectByPrimaryKey(c3.getParentId());
        Category c1 = this.categoryMapper.selectByPrimaryKey(c2.getParentId());
        return Arrays.asList(c1,c2,c3);
    }
}
