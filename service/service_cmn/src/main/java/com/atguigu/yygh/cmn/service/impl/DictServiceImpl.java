package com.atguigu.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.StringUtils;
import com.atguigu.yygh.cmn.listener.DictEeVoListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务实现类
 * </p>
 *
 * @author lucky845
 * @since 2022-03-02
 */
@Slf4j
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    /**
     * 根据数据id查询子数据列表
     * <p>
     * 注解@Cacheable(value = "dict", key = "selectIndexList + #pid") 表示将该方法添加到Redis缓存
     *
     * @param pid 数据id
     */
    @Cacheable(value = "dict", key = "'selectIndexList'+#pid")
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

    /**
     * 导出数据字典Excel
     */
    @Override
    public void exportData(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("数据字典", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

            List<Dict> dictList = baseMapper.selectList(null);
            List<DictEeVo> dictVoList = new ArrayList<>(dictList.size());
            for (Dict dict : dictList) {
                DictEeVo dictVo = new DictEeVo();
                BeanUtils.copyProperties(dict, dictVo);
                dictVoList.add(dictVo);
            }
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("数据字典").doWrite(dictVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取Excel文件
     * 出现异常就回滚
     *
     * @param inputStream 读取Excel文件的输入流
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importData(InputStream inputStream) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(inputStream, DictEeVo.class, new DictEeVoListener(baseMapper)).sheet().doRead();
        log.info("Excel导入成功");
    }

    /**
     * 根据上级编码与值获取数据字典名称
     *
     * @param parentDictCode 上级编码
     * @param value          值
     */
    @Override
    public String getNameByParentDictCodeAndValue(String parentDictCode, String value) {
        // 如果value能唯一定位数据字典,parentDictCode可以传空,例如: 省市区的value值可以唯一确定
        if (StringUtils.isEmpty(parentDictCode)) {
            Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>().eq("value", value));
            if (dict != null) {
                return dict.getName();
            }
        } else {
            Dict parentDict = this.getDictByDictCode(parentDictCode);
            if (parentDict == null) {
                return "";
            }

            Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>()
                    .eq("parent_id", parentDict.getId()).eq("value", value));
            if (dict != null) {
                return dict.getName();
            }
        }
        return "";
    }

    /**
     * 根据dict_code 查询Dict
     *
     * @param dictCode 上层编码
     */
    private Dict getDictByDictCode(String dictCode) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_code", dictCode);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 根据dictCode获取下级节点
     *
     * @param dictCode 节点编码
     */
    @Override
    public List<Dict> findByDictCode(String dictCode) {
        Dict dict = this.getDictByDictCode(dictCode);
        if (dict == null) {
            return null;
        }
        return this.getChildListByParentId(dict.getId());
    }
}
