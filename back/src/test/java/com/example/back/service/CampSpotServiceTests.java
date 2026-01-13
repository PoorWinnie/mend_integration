//package com.example.back.service;
//
//import com.example.back.domain.CampSiteBean;
//import com.example.back.domain.CampSpotBean;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//
//@SpringBootTest
//public class CampSpotServiceTests {
//
//    @Autowired
//    private CampSpotService campSpotService;
//
//    @Autowired
//    private CampSiteService campSiteService;
//
////    @Test
//    public void testFindAll() {
//        List<CampSpotBean> list = campSpotService.findAll();
//        System.out.println("目前共有 " + list.size() + " 筆營位");
//        list.forEach(spot -> System.out.println(spot.getCampSpotId() + " - " + spot.getSpotName()));
//    }
//
////    @Test
//    public void testFindById() {
//        Optional<CampSpotBean> optional = campSpotService.findById(1);
//        optional.ifPresent(spot -> System.out.println("找到營位：" + spot.getSpotName()));
//    }
//
////    @Test
//    public void testSave() {
//        Optional<CampSiteBean> siteOpt = campSiteService.findById(1);
//        if (siteOpt.isEmpty()) {
//            System.out.println("CampSite ID=1 不存在，請先建立！");
//            return;
//        }
//
//        CampSpotBean spot = new CampSpotBean();
//        spot.setSpotName("測試營位A1");
//        spot.setSpotNumber("A1");
//        spot.setAreaSize("5x5米");
//        spot.setMaxTents(2);
//        spot.setMaxPeople(4);
//        spot.setPricePerNight(new BigDecimal("1800"));
//        spot.setPriceWeekend(new BigDecimal("2200"));
//        spot.setPriceHoliday(new BigDecimal("2500"));
//        spot.setHasElectricity(true);
//        spot.setHasWater(true);
//        spot.setCampSite(siteOpt.get());
//
//        CampSpotBean saved = campSpotService.save(spot);
//        System.out.println("成功儲存 CampSpot ID: " + saved.getCampSpotId());
//    }
//
//    @Test
//    public void testDeleteById() {
//        int id = 21; // 替換成你想刪的 ID
//        campSpotService.deleteById(id);
//        System.out.println("已刪除營位 ID: " + id);
//    }
//}
