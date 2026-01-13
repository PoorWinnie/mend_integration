package com.example.back;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import com.example.back.config.FileUploadProperties;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class SpringBootConfig implements WebMvcConfigurer {
	

    @Value("${app.frontend-url}")
	private String frontendUrl;

    @Autowired
    private FileUploadProperties uploadProps;

//    @Autowired
//    private ServletContext servletContext;
    
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
                .allowedOriginPatterns(frontendUrl); // 用變數，不寫死

	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName("locale");
		registry.addInterceptor(interceptor);
	}
	
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	registry.addResourceHandler("/static/uploads/img/campArea/**")
        .addResourceLocations("file:" + new File(uploadProps.getCampArea()).getAbsolutePath() + "/");//對應本機絕對路徑下的照片
        System.out.println("露營區圖片資料夾：" + new File(uploadProps.getCampArea()).getAbsolutePath());

        registry.addResourceHandler("/static/uploads/img/toilet/**")
                .addResourceLocations("file:" + new File(uploadProps.getToilet()).getAbsolutePath() + "/");
        System.out.println("露營區廁所圖片資料夾：" + new File(uploadProps.getToilet()).getAbsolutePath());
    	
    }
}


