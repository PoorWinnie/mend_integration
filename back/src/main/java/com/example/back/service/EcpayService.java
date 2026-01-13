package com.example.back.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.back.dto.OrderRequest;

import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutALL;

@Service
public class EcpayService {
	
	@Value("${app.frontend-url}")
    private String frontendUrl;

    public String generateEcpayCheckoutForm(OrderRequest orderRequest) {
        String merchantTradeNo = UUID.randomUUID().toString().replace("-", "").substring(0, 20);

        AioCheckOutALL obj = new AioCheckOutALL();
        obj.setMerchantTradeNo(merchantTradeNo);
        obj.setMerchantTradeDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        obj.setTotalAmount(String.valueOf(orderRequest.getFinalPrice()));
        obj.setTradeDesc("購物車結帳");
        
        String itemNames = orderRequest.getItems().stream()
                .map(item -> item.getProductName() + "    x" + item.getQuantity() + "    $" + item.getTotalPrice())
                .reduce((a, b) -> a + " #" + b)
                .orElse("購物商品");
            if (itemNames.length() > 200) {
                itemNames = itemNames.substring(0, 200); // 綠界規定長度限制
            }
            
        obj.setItemName(itemNames);

        obj.setReturnURL("https://yourdomain.com/return");
        obj.setNeedExtraPaidInfo("N");
        
        obj.setClientBackURL(frontendUrl + "/orderItems/success"); // 你的前端網址

        AllInOne all = new AllInOne("");
        return all.aioCheckOut(obj, null);
    }
}
