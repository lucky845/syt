package com.atguigu.yygh.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * easyExcel 监听器
 *
 * @author lucky845
 */
@Slf4j
@NoArgsConstructor
public class DictEeVoListener extends AnalysisEventListener<DictEeVo> {

    /**
     * 每隔5条存储数据库,实际使用中可以3000条,然后清理list,方便内存回收
     */
    private static final int BATCH_COUNT = 5;

    /**
     * 用于缓存数据
     */
    List<DictEeVo> list = new ArrayList<>();

    /**
     * mapper对象
     */
    private DictMapper dictMapper;

    /**
     * 传入mapper对象
     *
     * @param dictMapper 数据字典mapper对象
     */
    public DictEeVoListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    /**
     * 遍历每一行记录
     *
     * @param data    字典对象
     * @param context 上下文对象
     */
    @Override
    public void invoke(DictEeVo data, AnalysisContext context) {
        log.info("解析到一条数据:{}", data);
        // 蒋数据存入数据列表
        list.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (list.size() >= BATCH_COUNT) {
            // 保存到数据库
            saveData();
            // 清理list中的数据
            list.clear();
        }

    }

    /**
     * 所有数据解析完了都会来调用
     *
     * @param context 上下文对象
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成!");
    }

    /**
     * 存储数据到数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储到数据库！", list.size());
        //批量插入
        dictMapper.insertBatch(list);
        log.info("{}条数据，存储到数据库成功！", list.size());
    }
}
