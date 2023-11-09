package com.wuming.tool.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wuming.model.UserExtData;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author manji
 * Created on 2023/10/26 16:52
 */
public class UserExtExcelListener extends AnalysisEventListener<UserExtData> {


    int index = 1;

    /**
     * 定稿的数据对象，注意又列表格式不能变
     */
    List<List<String>> dataList = Lists.newArrayList();

    @Override
    public void invoke(UserExtData userExtData, AnalysisContext analysisContext) {

        //{"id":13727491,"userid":3475601349,"useraccounttype":0,"endtime":1711900799000}
        String content = userExtData.get_c0();
        System.out.println("------:" + content);

        JSONObject jsonObject = JSON.parseObject(content, JSONObject.class);
        String id = jsonObject.getString("id");
        String userid = jsonObject.getString("userid");
        String useraccounttype = jsonObject.getString("useraccounttype");
        String endtime = jsonObject.getString("endtime");

        Map<String, Long> m = Maps.newHashMap();

        m.put("id", Long.valueOf(id));
        m.put("userId", Long.valueOf(userid));
        m.put("userAccountType", Long.valueOf(useraccounttype));
        m.put("endTime", Long.valueOf(endtime));

        // 行数据
        List<String> rowDataList = Lists.newArrayList();
        rowDataList.add("[" + JSON.toJSONString(m) + "],");
        dataList.add(rowDataList);
        if (dataList.size() >= 5) {
            System.out.println(dataList.size() + "----" + JSON.toJSONString(dataList));
            EasyExcel.write("/Users/manji/Downloads/data_" + index + ".xlsx").sheet("模板").doWrite(dataList);
            dataList.clear();
            index++;
        }
    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println(dataList.size() + "----" + JSON.toJSONString(dataList));
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        EasyExcel.write("/Users/manji/Downloads/data_" + index + ".xlsx").sheet("模板").doWrite(dataList);
        System.out.println("----------doAfterAllAnalysed--------");

    }
}
