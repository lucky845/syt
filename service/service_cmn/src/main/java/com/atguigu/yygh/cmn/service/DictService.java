package com.atguigu.yygh.cmn.service;

import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
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

    /**
     * 导出数据字典Excel
     */
    void exportData(HttpServletResponse response);

    /**
     * 读取Excel文件
     *
     * @param inputStream 读取Excel文件的输入流
     */
    void importData(InputStream inputStream);

    /**
     * 根据上级编码与值获取数据字典名称
     * @param parentDictCode 上级编码
     * @param value 值
     */
    String getNameByParentDictCodeAndValue(String parentDictCode,String value);

    /**
     * 根据dictCode获取下级节点
     * @param dictCode 节点编码
     */
    List<Dict> findByDictCode(String dictCode);
}
