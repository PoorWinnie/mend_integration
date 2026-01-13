//package com.example.back.service;
//
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import com.example.back.domain.CampAreaBean;
//import com.example.back.domain.CampSiteBean;
//
//@SpringBootTest
//public class CampSiteServiceTests {
//
//	@Autowired
//	private CampSiteService campSiteService;
//	
//	@Autowired
//	private CampAreaService campAreaService;
//
////	@Test
//	public void testFindAll() {
//		List<CampSiteBean> list = campSiteService.findAll();
//		for (CampSiteBean bean : list) {
//			System.out.println("CampSite: " + bean.getCampSiteId() + " - " + bean.getCampSiteName());
//		}
//	}
//
////	@Test
//	public void testFindById() {
//		Optional<CampSiteBean> optional = campSiteService.findById(1);
//		optional.ifPresent(bean -> System.out.println("Found CampSite: " + bean.getCampSiteName()));
//	}
//
////	@Test
//	public void testSave() {
//	    // 1. 先查出一筆已存在的 CampArea（假設 id = 1 存在）
//	    Optional<CampAreaBean> optional = campAreaService.findById(1);
//	    if (optional.isEmpty()) {
//	        System.out.println("CampArea with id 1 不存在，請先建立！");
//	        return;
//	    }
//	    
//	    CampAreaBean area = optional.get();
//	    System.out.println("我是optional:" + area);
//
//	    // 2. 建立 CampSite 並設定關聯
//	    CampSiteBean site = new CampSiteBean();
//	    site.setCampSiteName("測試營地");
//	    site.setDescription("這是測試用的營地");
//	    site.setSiteType("草地");
//	    site.setMaxCapacity(20);
//	    site.setRecommendedFor("Family");
//
//	    // 關鍵：設定關聯
//	    site.setCampArea(area);
//
//	    // 3. 儲存
//	    CampSiteBean saved = campSiteService.save(site);
//	    System.out.println("儲存成功的 CampSite ID: " + saved.getCampSiteId());
//	}
//
//
//	@Test
//	public void testDeleteById() {
//		// 請先確保 id 存在或搭配 save() 使用
//		campSiteService.deleteById(33);
//		System.out.println("Deleted CampSite ID: 33");
//	}
//}
