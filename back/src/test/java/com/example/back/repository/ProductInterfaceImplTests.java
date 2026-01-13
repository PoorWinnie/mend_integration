package com.example.back.repository;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.back.domain.ProductBean;

@SpringBootTest
public class ProductInterfaceImplTests {
    @Autowired
    private ProductInterface productDao;
    
    @Test
    public void testBasicSearch() {
        JSONObject obj = new JSONObject()
                .put("name", "商品")           // 搜尋名稱包含"商品"的產品
                .put("start", 0)              // 從第一筆開始
                .put("rows", 3)               // 取得3筆資料
                .put("dir", true)             // 降序排序
                .put("sort", "productId");    // 依照productId排序
        
        // 測試計數功能
        long count = productDao.count(obj);
        System.out.println("符合條件的商品數量: " + count);
        
        // 測試查詢功能
        List<ProductBean> products = productDao.find(obj);
        if(products != null && !products.isEmpty()) {
            System.out.println("查詢結果：");
            for(ProductBean product : products) {
                System.out.println("商品資訊: " + product);
            }
        } else {
            System.out.println("沒有找到符合條件的商品");
        }
    }
    
    @Test
    public void testPriceRangeSearch() {
        JSONObject obj = new JSONObject()
                .put("startPrice", "100")     // 最低價格
                .put("endPrice", "1000")      // 最高價格
                .put("start", 0)
                .put("rows", 5)
                .put("dir", false)            // 升序排序
                .put("sort", "price");        // 依照價格排序
        
        long count = productDao.count(obj);
        System.out.println("價格區間內的商品數量: " + count);
        
        List<ProductBean> products = productDao.find(obj);
        if(products != null && !products.isEmpty()) {
            System.out.println("價格區間內的商品：");
            for(ProductBean product : products) {
                System.out.println("商品資訊: " + product);
            }
        } else {
            System.out.println("沒有找到此價格區間的商品");
        }
    }
    
    @Test
    public void testDateRangeSearch() {
        JSONObject obj = new JSONObject()
                .put("startDate", "2024-03-01")   // 開始日期
                .put("endDate", "2024-03-31")     // 結束日期
                .put("start", 0)
                .put("rows", 10)
                .put("dir", true)
                .put("sort", "createdAt");        // 依照建立日期排序
        
        long count = productDao.count(obj);
        System.out.println("指定日期範圍內的商品數量: " + count);
        
        List<ProductBean> products = productDao.find(obj);
        if(products != null && !products.isEmpty()) {
            System.out.println("指定日期範圍內的商品：");
            for(ProductBean product : products) {
                System.out.println("商品資訊: " + product);
            }
        } else {
            System.out.println("沒有找到指定日期範圍內的商品");
        }
    }
    
    @Test
    public void testComplexSearch() {
        JSONObject obj = new JSONObject()
                .put("name", "特價")              // 名稱包含"特價"
                .put("startPrice", "100")         // 最低價格
                .put("endPrice", "500")           // 最高價格
                .put("minStock", 10)              // 最低庫存
                .put("productTypeId", 1)          // 商品類型ID
                .put("start", 0)
                .put("rows", 5)
                .put("dir", true)
                .put("sort", "price");            // 依照價格排序
        
        long count = productDao.count(obj);
        System.out.println("符合複合條件的商品數量: " + count);
        
        List<ProductBean> products = productDao.find(obj);
        if(products != null && !products.isEmpty()) {
            System.out.println("符合複合條件的商品：");
            for(ProductBean product : products) {
                System.out.println("商品資訊: " + product);
            }
        } else {
            System.out.println("沒有找到符合複合條件的商品");
        }
    }
}
