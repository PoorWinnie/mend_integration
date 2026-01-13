package com.example.back.controller;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.back.domain.ProductBean;
import com.example.back.dto.ProductResponse;
import com.example.back.service.ProductService;
import com.example.back.util.DatetimeConverterProduct;

@RestController
@RequestMapping("/ajax/products")
public class ProductAjaxController {
    
    @Autowired
    private ProductService productService;

    @DeleteMapping("/{productId}")
    public ProductResponse remove(@PathVariable Integer productId) {
        ProductResponse response = new ProductResponse();
        if(productId == null) {
            response.setSuccess(false);
            response.setMessage("商品ID是必要欄位");
        } else if(!productService.exists(productId)) {
            response.setSuccess(false);
            response.setMessage("商品ID不存在");
        } else {
            if(!productService.remove(productId)) {
                response.setSuccess(false);
                response.setMessage("刪除失敗");
            } else {
                response.setSuccess(true);
                response.setMessage("刪除成功");
            }
        }
        return response;
    }

    @PutMapping("/{productId}")
    public ProductResponse modify(@PathVariable Integer productId, @RequestBody ProductBean body) {
        ProductResponse response = new ProductResponse();
        if(productId == null) {
            response.setSuccess(false);
            response.setMessage("商品ID是必要欄位");
        } else if(!productService.exists(productId)) {
            response.setSuccess(false);
            response.setMessage("商品ID不存在");
        } else {
            body.setProductId(productId); // 確保ID一致
            ProductBean product = productService.update(body);
            if(product == null) {
                response.setSuccess(false);
                response.setMessage("修改失敗");
            } else {
                response.setSuccess(true);
                response.setMessage("修改成功");
                response.setProduct(product);
            }
        }
        return response;
    }

    @PostMapping
    public ProductResponse create(@RequestBody String entity) {
        ProductResponse response = new ProductResponse();
        try {
            ProductBean product = productService.create(entity);
            if(product == null) {
                response.setSuccess(false);
                response.setMessage("新增失敗");
            } else {
                response.setSuccess(true);
                response.setMessage("新增成功");
                response.setProduct(product);
                }
            } catch (Exception e){
                e.printStackTrace();
                response.setSuccess(false);
                response.setMessage("新增過程發生錯誤:" + e.getMessage());
            }
            return response;
    }

    @PostMapping("/find")
    public ProductResponse find(@RequestBody String entity) {
        ProductResponse response = new ProductResponse();

        long count = productService.count(entity);
        response.setCount(count);

        List<ProductBean> products = productService.find(entity);
        if(products != null && !products.isEmpty()) {
            response.setList(products);
        } else {
            response.setList(new ArrayList<>());
        }
        return response;
    }

    @GetMapping("/{productId}")
    public String findById(@PathVariable Integer productId) {
        JSONObject responseBody = new JSONObject();
        JSONArray array = new JSONArray();
        if(productId != null) {
            ProductBean product = productService.findById(productId);
            if(product != null) {
                String createdAt = DatetimeConverterProduct.format(product.getCreatedAt(), "yyyy-MM-dd HH:mm:ss");
                
                JSONObject item = new JSONObject()
                        .put("productId", product.getProductId())
                        .put("name", product.getName() != null ? product.getName() : "")
                        .put("price", product.getPrice() != null ? product.getPrice() : 0)
                        .put("description", product.getDescription() != null ? product.getDescription() : "")
                        .put("stock", product.getStock() != null ? product.getStock() : 0)
                        .put("imgUrl", product.getImgUrl() != null ? product.getImgUrl() : "")
                        .put("clickCount", product.getClickCount() != null ? product.getClickCount() : 0)
                        .put("createdAt", createdAt);
                
                if(product.getProductType() != null) {
                    item.put("productTypeId", product.getProductType().getId())
                        .put("typeName", product.getProductType().getTypeName());
                }
                array.put(item);
            }
        }
        responseBody.put("list", array);
        return responseBody.toString();
    }

    @PostMapping(
        path = "/images/upload/{productId}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> uploadProductImage(
            @PathVariable Integer productId,
            @RequestParam("image") MultipartFile file) {
        
        JSONObject response = new JSONObject();
        
        if(productId == null) {
            response.put("success", false);
            response.put("message", "商品ID是必要欄位");
            return ResponseEntity.badRequest().body(response.toString());
        }
        
        if(file.isEmpty()) {
            response.put("success", false);
            response.put("message", "請選擇要上傳的圖片");
            return ResponseEntity.badRequest().body(response.toString());
        }
        
        try {
            boolean result = productService.saveProductImage(productId, file);
            
            if(result) {
                response.put("success", true);
                response.put("message", "圖片上傳成功");
                return ResponseEntity.ok(response.toString());
            } else {
                response.put("success", false);
                response.put("message", "圖片上傳失敗");
                return ResponseEntity.internalServerError().body(response.toString());
            }
        } catch(Exception e) {
            response.put("success", false);
            response.put("message", "處理圖片時發生錯誤: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response.toString());
        }
    }

    @GetMapping(
        path = "/images/{productId}",
        produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE}
    )
    public @ResponseBody byte[] getProductImage(@PathVariable Integer productId) {
        return productService.getProductImage(productId);
    }
}
