package com.leyou.service;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.mapper.SpecGroupMapper;
import com.leyou.mapper.SpecParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificationService {
    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper specParamMapper;

    public List<SpecGroup> queryByCategoryId(Long cid) {
        SpecGroup specGroup=new SpecGroup();
        specGroup.setCid(cid);
        return     specGroupMapper.select(specGroup);
    }

    public List<SpecParam> queryBySpecParam(Long gid,Long cid,Boolean generic,Boolean searching) {
        SpecParam record=new SpecParam();
        record.setGroupId(gid);
        record.setCid(cid);
        record.setGeneric(generic);
        record.setSearching(searching);
      return   this.specParamMapper.select(record);
    }

    public void addSpecParam(SpecParam specParam) {
        this.specParamMapper.insertSelective(specParam);
    }

    public void editSpecSpecParam(SpecParam specParam) {
        this.specParamMapper.updateByPrimaryKeySelective(specParam);
    }

    public void deleteSpecParam(Long id) {
        SpecParam record=new SpecParam();
        record.setId(id);
        this.specParamMapper.deleteByPrimaryKey(record);
    }

    public List<SpecGroup> queryGroupWithCid(Long cid) {
        //根据Cid查询出来所有的规格参数组
        List<SpecGroup> specGroupList = this.queryByCategoryId(cid);
        //遍历每个规格参数组，根据组ID进行设置值,根据组ID，可以得到规格参数的列表，然后在将规格参数的列表集合设置到每个组内
        specGroupList.forEach(specGroup -> {
            List<SpecParam> specParamList = this.queryBySpecParam(specGroup.getId(), null, null, null);
            specGroup.setParams(specParamList);
        });
        return specGroupList;
    }
}
