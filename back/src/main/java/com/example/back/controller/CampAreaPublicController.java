package com.example.back.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.domain.CampAreaBean;
import com.example.back.domain.FeatureTagBean;
import com.example.back.dto.CampAreaDTO;
import com.example.back.dto.CampAreaResponse;
import com.example.back.dto.CampAreaSimpleDTO;
import com.example.back.dto.FeatureTagSimpleDTO;
import com.example.back.repository.FeatureTagRepository;
import com.example.back.service.CampAreaService;

@RestController
@RequestMapping("/api/public/camp-areas/")
public class CampAreaPublicController {

	@Autowired
	private CampAreaService campAreaService;	
	@Autowired
    private FeatureTagRepository featureTagRepo;

	@GetMapping("/features") //抓資料庫的feature tag
    public List<FeatureTagSimpleDTO> findAllActiveFeatures() {
        List<FeatureTagBean> features = featureTagRepo.findAll()
                .stream()
                .filter(f -> Boolean.TRUE.equals(f.getIsActive())) 
                .sorted((f1, f2) -> { 
                    Integer order1 = f1.getDisplayOrder() != null ? f1.getDisplayOrder() : Integer.MAX_VALUE;
                    Integer order2 = f2.getDisplayOrder() != null ? f2.getDisplayOrder() : Integer.MAX_VALUE;
                    return order1.compareTo(order2);
                })
                .collect(Collectors.toList());

        // 把 Bean 轉成 SimpleDTO
        return features.stream()
                .map(f -> new FeatureTagSimpleDTO(f.getTagId(), f.getTagName()))
                .collect(Collectors.toList());
    }	
	
	@PostMapping("/filter") //多重篩選
	public ResponseEntity<CampAreaResponse<CampAreaSimpleDTO>> filterCampAreas(@RequestBody String entity) {
	    List<CampAreaSimpleDTO> list = campAreaService.find(entity);
	    long total = campAreaService.count(entity); // 查詢總數
	    CampAreaResponse<CampAreaSimpleDTO> response = new CampAreaResponse<>();

	    if (list == null || list.isEmpty()) {
	        response.setSuccess(false);
	        response.setMessage("查無符合條件的營區");
	        response.setList(List.of());
	        return ResponseEntity.ok(response);
	    }

	    response.setSuccess(true);
	    response.setMessage("查詢成功");
	    response.setList(list);
	    response.setCount(total);
	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<CampAreaResponse<CampAreaDTO>> findCampAreaById(@PathVariable Integer id) {
	    CampAreaResponse<CampAreaDTO> response = new CampAreaResponse<>();

	    Optional<CampAreaBean> optional = campAreaService.findById(id);
	    if (optional.isEmpty()) {
	        response.setSuccess(false);
	        response.setMessage("找不到指定 ID 的營地");
	        response.setList(List.of());
	        response.setCount(0L);
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	    }

	    CampAreaDTO dto = new CampAreaDTO(optional.get());
	    response.setSuccess(true);
	    response.setMessage("查詢成功");
	    response.setList(List.of(dto));
	    response.setCount(1L);
	    return ResponseEntity.ok(response);
	}



		
	@DeleteMapping("/{id}")
	public ResponseEntity<CampAreaResponse<Void>> deleteCampAreaById(@PathVariable Integer id) {
	    CampAreaResponse<Void> response = new CampAreaResponse<>();

	    if (!campAreaService.existsCampArea(id)) {
	        response.setSuccess(false);
	        response.setMessage("找不到指定 ID 的營地");
	        response.setCount(0L);
	        response.setList(List.of());
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	    }

	    campAreaService.deleteById(id);

	    response.setSuccess(true);
	    response.setMessage("刪除成功");
	    return ResponseEntity.ok(response);
	}


}
