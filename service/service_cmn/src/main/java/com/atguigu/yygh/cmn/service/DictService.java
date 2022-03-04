package com.atguigu.yygh.cmn.service;

import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 组织架构表 服务类
 * </p>
 *
 * @author lucky845
 * @since 2022-03-02
 */
public interface DictService extends IService<Dict> {

    /**
     * 根据数据id查询子数据列表
     * @param pid 数据id
     */
    List<Dict> getChildListByParentId(Long pid);

}
