package com.example.back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上傳目錄的訪問路徑
        exposeDirectory("uploads", registry);
        
        // 配置產品圖片特定路徑
        registry.addResourceHandler("/ajax/products/images/**")
                .addResourceLocations("file:./uploads/products/")
                .setCachePeriod(3600) // 緩存一小時
                .resourceChain(true);
    }
    
    private void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(dirName);
        String uploadPath = uploadDir.toFile().getAbsolutePath();
        
        if (dirName.startsWith("../")) {
            dirName = dirName.replace("../", "");
        }
        
        registry.addResourceHandler("/" + dirName + "/**")
                .addResourceLocations("file:/" + uploadPath + "/")
                .setCachePeriod(3600) // 緩存一小時
                .resourceChain(true);
    }
}