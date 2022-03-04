package com.atguigu.yygh.cmn.controller.admin;


import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 组织架构表 前端控制器
 * </p>
 *
 * @author lucky845
 * @since 2022-03-02
 */
@Api(tags = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
@CrossOrigin
public class DictController {

    @Autowired
    private DictService dictService;

    /**
     * 根据数据id查询子数据列表
     *
     * @param pid 数据id
     */
    //
    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("/childList/{pid}")
    public R getChildListByParentId(
            @ApiParam(name = "pid", value = "数据id", required = true)
            @PathVariable Long pid) {
        List<Dict> list = dictService.getChildListByParentId(pid);
        return R.ok().data("list", list);
    }

    /**
     * 导出数据字典Excel
     */
    @ApiOperation(value = "导出数据字典Excel")
    @GetMapping("/exportData")
    public void exportData(HttpServletResponse response) {
        dictService.exportData(response);
    }

    /**
     * Excel数据的批量导入
     *
     * @param file 数据字典文件
     */
    @ApiOperation("Excel数据的批量导入")
    @PostMapping("/importData")
    public R batchImport(
            @ApiParam(name = "file", value = "数据字典文件", required = true)
            @RequestParam("file") MultipartFile file) {

        try {
            InputStream inputStream = file.getInputStream();
            dictService.importData(inputStream);
            return R.ok().message("数据字典批量导入成功");
        } catch (Exception e) {
            return R.error().message("数据字典批量导入失败");
        }
    }

}

