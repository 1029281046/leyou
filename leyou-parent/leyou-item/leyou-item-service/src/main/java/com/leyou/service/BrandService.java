package com.leyou.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.mapper.BrandMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {

        Example example=new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("name","%"+key+"%").orEqualTo("letter",key);
        }
        PageHelper.startPage(page,rows);
        if (StringUtils.isNotBlank(sortBy)){
            example.setOrderByClause(sortBy+" "+(desc?"desc":"asc"));
        }
        List<Brand> brands = brandMapper.selectByExample(example);
        PageInfo<Brand> pageInfo=new PageInfo<>(brands);

        return new  PageResult<Brand>(pageInfo.getTotal(),pageInfo.getList());
    }

    public void addBrand(Brand brand, List<Long> cids) {
        boolean flag=  this.brandMapper.insertSelective(brand)==1;
        if (flag){
            cids.forEach(cid->{
                this.brandMapper.addCategoryAndBrand(cid,brand.getId());
            });
        }
    }


    public void editBrand(Brand brand, List<Long> cids) {
        boolean flag=  this.brandMapper.updateByPrimaryKey(brand)==1;
        if (flag){
            cids.forEach(cid->{
                this.brandMapper.delCategoryAndBrand(brand.getId());

                this.brandMapper.addCategoryAndBrand(cid,brand.getId());
            });
        }
    }

    public void deleteBrand(Long bid) {
        this.brandMapper.delCategoryAndBrand(bid);
        Brand brand=new Brand();
        brand.setId(bid);
        this.brandMapper.delete(brand);
    }

    public List<Brand> queryByCid(Long cid) {
        return this.brandMapper.queryByCids(cid);
    }

    public Brand queryById(Long id) {
        return this.brandMapper.selectByPrimaryKey(id);
    }
}
