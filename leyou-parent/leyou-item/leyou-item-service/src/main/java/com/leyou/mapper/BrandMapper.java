package com.leyou.mapper;


import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {
    @Insert("INSERT INTO tb_category_brand(category_id,brand_id) VALUES(#{cid},#{id})")
    void addCategoryAndBrand(@Param("cid") Long cid,  @Param("id") Long id);

    @Delete("DELETE FROM tb_category_brand WHERE brand_id=#{id}")
    void delCategoryAndBrand(@Param("id") Long id);

    @Select("SELECT * FROM tb_brand b INNER JOIN tb_category_brand  cb ON b.id=cb.brand_id WHERE cb.category_id=#{cid}")
    List<Brand> queryByCids(@Param("cid") Long cid);
}
