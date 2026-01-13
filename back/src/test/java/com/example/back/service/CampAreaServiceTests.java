//package com.example.back.service;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import com.example.back.domain.CampAreaBean;
//import com.example.back.domain.FeatureTagBean;
//import com.example.back.domain.UserBean;
//import com.example.back.dto.CampAreaDTO;
//import com.example.back.repository.CampAreaRepository;
//import com.example.back.repository.FeatureTagRepository;
//import com.example.back.repository.UserRepository;
//
//@SpringBootTest
////@Transactional
//public class CampAreaServiceTests {
//
//    @Autowired
//    private CampAreaService campAreaService;
//    @Autowired
//    private CampAreaRepository campAreaRepo;
//    @Autowired
//    private UserRepository userRepo;
//    @Autowired
//    private FeatureTagRepository featureRepo;
//
//    private Integer campAreaId;
//    
//    
////    @Test
//    public void testFindAll() {
//        List<CampAreaBean> list = campAreaService.findAll();
//        for (CampAreaBean bean : list) {
//            System.out.println("CampArea: " + bean.getCamp_area_id() + " - " + bean.getCamp_area_name());
//        }
//    }
//
////    @Test
//    public void testFindById() {
//        Optional<CampAreaBean> optional = campAreaService.findById(1);
//        optional.ifPresent(bean -> System.out.println("Found CampArea: " + bean.getCamp_area_name()));
//    }
//
////    @Test
//    public void testSave() {
//        CampAreaBean newBean = new CampAreaBean();
//        newBean.setCamp_area_name("測試營區");
//        newBean.setLocation("測試地點");
//        newBean.setDescription("這是一個測試營區");
//        newBean.setStatus("Active");
//        CampAreaBean saved = campAreaService.save(newBean);
//        System.out.println("Saved CampArea ID: " + saved.getCamp_area_id());
//    }
//
////    @Test
//    public void testDeleteById() {
//        // 這裡建議搭配 testSave() 產生的資料一起測試
//        campAreaService.deleteById(11);
//        System.out.println("Deleted CampArea ID: 11");
//    }
//    
//    @Test
//    public void init() {
//        // 準備一筆 User 和 CampArea
//        UserBean user = new UserBean();
//        user.setUser_name("newuser");
//        user.setPassword_hash("1234");
//        user.setEmail("1234");
//        user.setRole("admin");
//        userRepo.save(user);
//
//        CampAreaBean area = new CampAreaBean();
//        area.setCamp_area_name("測試營地");
//        area.setUser(user);
//        area.setLocation("桃園市桃園區");
//        campAreaRepo.save(area);
//
//        campAreaId = area.getCamp_area_id();
//
//        // 建立 feature tags
//        FeatureTagBean t1 = new FeatureTagBean();
//        t1.setTag_name("三重");
//        t1.setIs_active(true);
//
//        FeatureTagBean t2 = new FeatureTagBean();
//        t2.setTag_name("新莊");
//        t2.setIs_active(true);
//
//        featureRepo.saveAll(Arrays.asList(t1, t2));
//        System.out.println("成功新增= " + t1 + t2);
//    }
////    @Test
//    public void testUpdateBasicInfo() {
//        UserBean newUser = new UserBean();
//        newUser.setUser_name("newuser");
//        newUser.setPassword_hash("4321");
//        newUser.setEmail("4321");
//        newUser.setRole("superAdmin");
//        userRepo.save(newUser);
//
//        CampAreaDTO dto = new CampAreaDTO();
//        dto.setCamp_area_name("三重營地");
//        dto.setDescription("屁孩專區");
//        dto.setLocation("桃園市龜山區");
//        dto.setUserId(newUser.getUser_id());
//
//        campAreaService.updateBasicInfo(dto);
//        System.out.println("成功update= " + dto.toString());
//        
//    }
//
//
//    
//    
//    
//}
