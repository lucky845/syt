package com.atguigu.yygh.cmn.service.impl;

import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 组织架构表 服务实现类
 * </p>
 *
 * @author lucky845
 * @since 2022-03-02
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    /**
     * 根据数据id查询子数据列表
     *
     * @param pid 数据id
     */
    @Override
    public List<Dict> getChildListByParentId(Long pid) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", pid);
        List<Dict> dictList = baseMapper.selectList(queryWrapper);
        // 向list集合每个dict对象中设置hasChildren
        for (Dict dict : dictList) {
            Long id = dict.getId();
            boolean isChildren = this.isChildren(id);
            dict.setHasChildren(isChildren);
        }
        return dictList;
    }

    /**
     * 判断pid下面是否还有子节点
     *
     * @param pid 父节点id
     */
    private boolean isChildren(Long pid) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", pid);
        Integer count = baseMapper.selectCount(wrapper);
        return count > 0;
    }

}
