package com.wuming.tool.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wuming.model.UserExtData;

import java.util.List;
import java.util.Map;

/**
 * @author manji
 * Created on 2023/10/26 16:52
 */
public class UserExtExcelListener extends AnalysisEventListener<UserExtData> {


    int index = 1;

    private List<String> ll = Lists.newArrayList();

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

        ll.add("[" + JSON.toJSONString(m) + "],");

        if (ll.size() >= 5) {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < ll.size(); i++) {
                s.append(ll.get(i)).append(System.lineSeparator());
            }
            System.out.println("write data:" + s);

//            EasyExcel.write("/Users/manji/Downloads/data_" + index + ".txt").sheet().doWrite(ll);
            EasyExcel.write("/Users/manji/Downloads/data_" + index + ".xlsx").sheet("Sheet1").doWrite(ll);
            ll.clear();
            index++;
        }

    }



    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println(ll.size());
        if (CollectionUtils.isEmpty(ll)) {
            return;
        }

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < ll.size(); i++) {
            s.append(ll.get(i)).append(System.lineSeparator());
        }
        System.out.println(s.toString());

//        WriteFileTest.testWriteFileWithContent("/Users/celen/Desktop/userExt/fixData_" + index + ".txt", s.toString());
        EasyExcel.write("/Users/manji/Downloads/data_" + index + ".xlsx").sheet(0).doWrite(ll);

        System.out.println("----------doAfterAllAnalysed--------");

    }
}
