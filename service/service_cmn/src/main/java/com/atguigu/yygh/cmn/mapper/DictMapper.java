package com.atguigu.yygh.cmn.mapper;

import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 组织架构表 Mapper 接口
 * </p>
 *
 * @author lucky845
 * @since 2022-03-02
 */
public interface DictMapper extends BaseMapper<Dict> {

    /**
     * 批量插入数据到字典
     *
     * @param list 数据集合
     */
    void insertBatch(List<DictEeVo> list);

}
