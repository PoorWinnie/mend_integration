package com.example.back.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.back.domain.ProductBean;
import com.example.back.domain.ProductTypeBean;
import com.example.back.repository.ProductRepository;
import com.example.back.repository.ProductTypeRepository;

import jakarta.annotation.PostConstruct;

@Service
@Transactional
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductTypeRepository productTypeRepository;
    
    
    @Value("${app.image.file-dir}")
    private String imageFileDir;

    @Value("${app.image.url-prefix}")
    private String imageUrlPrefix;

    // 添加圖片處理相關屬性和方法
    private byte[] defaultImage = null;
    
    @PostConstruct
    public void initialize() {
        try {
            File defaultFile = new File(imageFileDir+"no-image.jpg");
            if (defaultFile.exists()) {
                this.defaultImage = Files.readAllBytes(defaultFile.toPath());
                System.out.println("成功載入預設圖片：" + this.defaultImage.length + " bytes");
            } else {
                System.out.println("找不到預設圖片：uploads/img/product/no-image.jpg");
                this.defaultImage = new byte[0];
            }
        } catch (Exception e) {
            System.out.println("載入預設圖片時發生錯誤：" + e.getMessage());
            this.defaultImage = new byte[0];
            e.printStackTrace();
        }
    }
    
    /**
     * 獲取產品圖片
     * @param productId 產品ID
     * @return 圖片的字節數組
     */
    public byte[] getProductImage(Integer productId) {
        if(productId != null) {
            ProductBean product = this.findById(productId);
            if(product != null) {
                try {
                    // 檢查產品的 imgUrl 路徑
                    String imgPath = product.getImgUrl();
                    String filePath;
                    
                    if (imgPath != null && !imgPath.isEmpty()) {
                        // 使用實際儲存的路徑，去掉開頭的斜線
                        filePath = "." + imgPath;
                    } else {
                        // 沒有路徑時使用預設命名規則和副檔名
                        String fileName = "product" + String.format("%02d", productId) + ".png";
                        filePath = imageFileDir + fileName;
                    }
                    
                    File file = new File(filePath);
                    System.out.println("嘗試讀取圖片：" + filePath);
                    
                    if (file.exists()) {
                        System.out.println("找到圖片檔案");
                        return Files.readAllBytes(file.toPath());
                    } else {
                        System.out.println("找不到圖片檔案：" + filePath);
                    }
                } catch (Exception e) {
                    System.out.println("讀取產品圖片時發生錯誤：" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return this.defaultImage;
    }

    public long count(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            return productRepository.count(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public List<ProductBean> find(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            return productRepository.find(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ProductBean findById(Integer productId) {
        if(productId != null) {
            Optional<ProductBean> optional = this.productRepository.findById(productId);
            if(optional.isPresent()) {
                return optional.get();
            }
        }
        return null;
    }

    public boolean exists(Integer productId) {
        if(productId != null) {
            return this.productRepository.existsById(productId);
        }
        return false;
    }
    
    public ProductBean create(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            //Integer productId = obj.isNull("productId") ? null : obj.getInt("productId");
            String name = obj.isNull("name") ? null : obj.getString("name");
            Integer productTypeId = obj.isNull("productTypeId") ? null : obj.getInt("productTypeId");
            String description = obj.isNull("description") ? null : obj.getString("description");
            BigDecimal price = obj.isNull("price") ? null : new BigDecimal(obj.getString("price"));
            Integer stock = obj.isNull("stock") ? null : obj.getInt("stock");
            String imgUrl = obj.isNull("imgUrl") ? null : obj.getString("imgUrl");
            
            //if(productId != null) {
                //Optional<ProductBean> optional = this.productRepository.findById(productId);
                //if(optional.isEmpty()) {
                    ProductBean insert = new ProductBean();
                    //insert.setProductId(productId);
                    insert.setName(name);
                    
                    // 設置商品類型
                    if(productTypeId != null) {
                        Optional<ProductTypeBean> typeOptional = productTypeRepository.findById(productTypeId);
                        if(typeOptional.isPresent()) {
                            insert.setProductType(typeOptional.get());
                        }
                    }
                    
                    insert.setDescription(description);
                    insert.setPrice(price);
                    insert.setStock(stock);
                    insert.setImgUrl(imgUrl);
                    insert.setClickCount(0);
                    insert.setCreatedAt(LocalDateTime.now());
                    
                    return this.productRepository.save(insert);
                
        
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public ProductBean modify(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            Integer productId = obj.isNull("productId") ? null : obj.getInt("productId");
            String name = obj.isNull("name") ? null : obj.getString("name");
            Integer productTypeId = obj.isNull("productTypeId") ? null : obj.getInt("productTypeId");
            String description = obj.isNull("description") ? null : obj.getString("description");
            BigDecimal price = obj.isNull("price") ? null : new BigDecimal(obj.getString("price"));
            Integer stock = obj.isNull("stock") ? null : obj.getInt("stock");
            String imgUrl = obj.isNull("imgUrl") ? null : obj.getString("imgUrl");

            if(productId != null) {
                Optional<ProductBean> optional = this.productRepository.findById(productId);
                if(optional.isPresent()) {
                    ProductBean update = optional.get();
                    update.setName(name);
                    
                    // 更新商品類型
                    if(productTypeId != null) {
                        Optional<ProductTypeBean> typeOptional = productTypeRepository.findById(productTypeId);
                        if(typeOptional.isPresent()) {
                            update.setProductType(typeOptional.get());
                        }
                    }
                    
                    update.setDescription(description);
                    update.setPrice(price);
                    update.setStock(stock);
                    update.setImgUrl(imgUrl);
                    
                    return this.productRepository.save(update);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean remove(Integer productId) {
        if(productId != null) {
            Optional<ProductBean> optional = this.productRepository.findById(productId);
            if(optional.isPresent()) {
                try {
                    this.productRepository.deleteById(productId);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }    
            }
        }
        return false;
    }
    
    public List<ProductBean> select(ProductBean bean) {
        List<ProductBean> result = null;
        if(bean != null && bean.getProductId() != null && bean.getProductId() != 0) {
            Optional<ProductBean> optional = this.productRepository.findById(bean.getProductId());
            if(optional.isPresent()) {
                result = new ArrayList<ProductBean>();
                result.add(optional.get());
            }
        } else {
            result = this.productRepository.findAll();
        }
        return result;
    }
    
    public ProductBean insert(ProductBean bean) {
        if(bean != null && bean.getProductId() != null) {
            Optional<ProductBean> optional = this.productRepository.findById(bean.getProductId());
            if(optional.isEmpty()) {
                if(bean.getClickCount() == null) {
                    bean.setClickCount(0);
                }
                if(bean.getCreatedAt() == null) {
                    bean.setCreatedAt(LocalDateTime.now());
                }
                return this.productRepository.save(bean);
            }
        }
        return null;
    }
    
    public ProductBean update(ProductBean bean) {
        if(bean != null && bean.getProductId() != null) {
            Optional<ProductBean> optional = this.productRepository.findById(bean.getProductId());
            if(optional.isPresent()) {
                ProductBean existingProduct = optional.get();
                
                // 保留原有的資料，只更新非null的欄位
                if(bean.getName() != null) {
                    existingProduct.setName(bean.getName());
                }
                
                // 更新商品類型（如果新值不為null）
                if(bean.getProductType() != null) {
                    existingProduct.setProductType(bean.getProductType());
                }
                
                if(bean.getDescription() != null) {
                    existingProduct.setDescription(bean.getDescription());
                }
                
                if(bean.getPrice() != null) {
                    existingProduct.setPrice(bean.getPrice());
                }
                
                if(bean.getStock() != null) {
                    existingProduct.setStock(bean.getStock());
                }
                
                // 不覆蓋imgUrl，這由saveProductImage方法處理
                // 如果前端直接設置了imgUrl且非null，則更新
                if(bean.getImgUrl() != null) {
                    existingProduct.setImgUrl(bean.getImgUrl());
                }
                
                if(bean.getClickCount() != null) {
                    existingProduct.setClickCount(bean.getClickCount());
                }
                
                // createdAt保持不變，這是產品創建時設置的
                
                return this.productRepository.save(existingProduct);
            }
        }
        return null;
    }
    
    public boolean delete(ProductBean bean) {
        if(bean != null && bean.getProductId() != null) {
            Optional<ProductBean> optional = this.productRepository.findById(bean.getProductId());
            if(optional.isPresent()) {
                this.productRepository.delete(optional.get());
                return true;
            }
        }
        return false;
    }
    
    public ProductBean incrementClickCount(Integer productId) {
        if(productId != null) {
            Optional<ProductBean> optional = this.productRepository.findById(productId);
            if(optional.isPresent()) {
                ProductBean product = optional.get();
                product.setClickCount(product.getClickCount() + 1);
                return this.productRepository.save(product);
            }
        }
        return null;
    }

    /**
     * 將上傳的圖片保存到特定位置，替換產品原有圖片
     * @param productId 產品ID
     * @param file 上傳的多部分檔案
     * @return 是否成功保存
     * @throws IOException 如果檔案處理過程中出錯
     */
    public boolean saveProductImage(Integer productId, MultipartFile file) throws IOException {
        if (productId == null || file == null || file.isEmpty()) {
            return false;
        }
        
        // 檢查產品是否存在
        if (!exists(productId)) {
            return false;
        }
        
        // 確保資料夾存在
        String uploadDir = imageFileDir;
        Path uploadPath = Paths.get(uploadDir);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 獲取原始檔案的副檔名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = ".png"; // 默認副檔名
        
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // 根據 productId 創建檔案名稱 (包含副檔名)
        String fileName = "product" + String.format("%02d", productId) + fileExtension;
        
        try {
            // 保存圖片
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());
            
            // 更新產品的 imgUrl 屬性 (包含副檔名)
            ProductBean product = findById(productId);
            if (product != null) {
                // 保存路徑時包含副檔名
                product.setImgUrl(imageUrlPrefix  + fileName);
                productRepository.save(product);
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("保存圖片時出錯：" + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}