package com.example.back.repository;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.back.domain.CampAreaBean;

@SpringBootTest
public class CampAreaInterfaceImplTests {

    @Autowired
    @Qualifier("campAreaInterfaceImpl")
    private CampAreaInterface campAreaInterface;

//    @Test
    public void testCount() {
        JSONObject obj = new JSONObject();

        // 模擬前端傳來的篩選條件
//        obj.put("region", "北部"); // 對應 location 前綴為北部城市
        obj.put("altitudeRange", new JSONArray().put(1)); // 501~800
//        obj.put("featureIds", new JSONArray().put(5).put(9)); // 同時具有 tag_id = 5 和 9 的營區

        long count = campAreaInterface.count(obj);

        System.out.println("符合條件的營區數量: " + count);
    }
    
    @Test
    public void testFindWithPagination() {
        JSONObject obj = new JSONObject();

        // 加入模擬查詢條件（可省略來測試全部查詢） //同時滿足
        obj.put("region", "中部");
        obj.put("altitudeRange", new JSONArray().put(2)); // 301~500m
        obj.put("featureIds", new JSONArray().put(5)); // 親子同樂

        // 分頁與排序參數
        obj.put("start", 0); // 從第0筆開始
        obj.put("rows", 5);  // 每頁5筆
        obj.put("sort", "camp_area_name");
        obj.put("dir", false); // 升冪排序

        List<CampAreaBean> result = campAreaInterface.find(obj);
        System.out.println("查詢結果筆數：" + result.size());

        for (CampAreaBean bean : result) {
            System.out.println("查詢結果=" +  bean.getCampAreaId() + " - " + bean.getCampAreaName() + " - " + bean.getAltitude());
        }
    }

}
