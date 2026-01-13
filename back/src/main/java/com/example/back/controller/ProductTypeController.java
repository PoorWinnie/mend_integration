package com.example.back.controller;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.back.domain.ProductTypeBean;
import com.example.back.dto.ProductTypeResponse;
import com.example.back.service.ProductTypeService;

@RestController
@RequestMapping("/ajax/product-types")
public class ProductTypeController {
    @Autowired
    private ProductTypeService productTypeService;

    @GetMapping
    public ProductTypeResponse findAll() {
        ProductTypeResponse response = new ProductTypeResponse();
        List<ProductTypeBean> types = productTypeService.findAll();
        if(types != null && !types.isEmpty()) {
            response.setSuccess(true);
            response.setList(types);
        } else {
            response.setSuccess(false);
            response.setMessage("沒有商品類型資料");
        }
        return response;
    }

    @GetMapping("/{id}")
    public ProductTypeResponse findById(@PathVariable Integer id) {
        ProductTypeResponse response = new ProductTypeResponse();
        if(id == null) {
            response.setSuccess(false);
            response.setMessage("ID是必要欄位");
            return response;
        }
        ProductTypeBean type = productTypeService.findById(id);
        if(type != null) {
            response.setSuccess(true);
            response.setProductType(type);
        } else {
            response.setSuccess(false);
            response.setMessage("找不到指定的商品類型");
        }
        return response;
    }

    @PostMapping
    public ProductTypeResponse create(@RequestBody String entity) {
        ProductTypeResponse response = new ProductTypeResponse();
        try {
            JSONObject obj = new JSONObject(entity);
            String typeName = obj.isNull("typeName") ? null : obj.getString("typeName");
            if(typeName == null || typeName.trim().isEmpty()) {
                response.setSuccess(false);
                response.setMessage("類型名稱是必要欄位");
                return response;
            }
            ProductTypeBean type = productTypeService.create(entity);
            if(type != null) {
                response.setSuccess(true);
                response.setMessage("新增成功");
                response.setProductType(type);
            } else {
                response.setSuccess(false);
                response.setMessage("新增失敗");
            }
        } catch(Exception e) {
            response.setSuccess(false);
            response.setMessage("處理失敗：" + e.getMessage());
        }
        return response;
    }

    @PutMapping("/{id}")
    public ProductTypeResponse modify(@PathVariable Integer id, @RequestBody ProductTypeBean body) {
        ProductTypeResponse response = new ProductTypeResponse();
        if(id == null) {
            response.setSuccess(false);
            response.setMessage("ID是必要欄位");
            return response;
        }
        if(!productTypeService.exists(id)) {
            response.setSuccess(false);
            response.setMessage("找不到指定的商品類型");
            return response;
        }
        body.setId(id); // 確保ID一致
        ProductTypeBean type = productTypeService.update(body);
        if(type != null) {
            response.setSuccess(true);
            response.setMessage("修改成功");
            response.setProductType(type);
        } else {
            response.setSuccess(false);
            response.setMessage("修改失敗");
        }
        return response;
    }

    @DeleteMapping("/{id}")
    public ProductTypeResponse remove(@PathVariable Integer id) {
        ProductTypeResponse response = new ProductTypeResponse();
        if(id == null) {
            response.setSuccess(false);
            response.setMessage("ID是必要欄位");
            return response;
        }
        if(!productTypeService.exists(id)) {
            response.setSuccess(false);
            response.setMessage("找不到指定的商品類型");
            return response;
        }
        if(productTypeService.remove(id)) {
            response.setSuccess(true);
            response.setMessage("刪除成功");
        } else {
            response.setSuccess(false);
            response.setMessage("刪除失敗");
        }
        return response;
    }
}
