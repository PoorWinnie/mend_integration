package com.example.back.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.back.domain.CouponBean;
import com.example.back.domain.OrderBean;
import com.example.back.domain.OrderItemBean;
import com.example.back.domain.ProductBean;
import com.example.back.domain.UserBean;
import com.example.back.dto.OrderItemRequest;
import com.example.back.dto.OrderRequest;
import com.example.back.dto.OrderResponse;
import com.example.back.dto.OrderUpdateRequest;
import com.example.back.repository.CouponRepository;
import com.example.back.repository.OrderItemRepository;
import com.example.back.repository.OrderRepository;
import com.example.back.repository.ProductRepository;
import com.example.back.repository.UserRepository;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CouponRepository couponRepository;
    
    public void createOrder(OrderRequest orderRequest) {
        // 根據 userName 找 user 實體
        UserBean user = userRepository.findByUsername(orderRequest.getUserName())
                .orElseThrow(() -> new RuntimeException("找不到使用者"));

        // 建立 OrderBean 並設關聯
        OrderBean order = new OrderBean();
        order.setUser(user);

        if (orderRequest.getCouponId() != null) {
            CouponBean coupon = couponRepository.findById(orderRequest.getCouponId())
                    .orElseThrow(() -> new RuntimeException("找不到優惠券"));
            order.setCoupon(coupon);
        }

        order.setTotalPrice(orderRequest.getTotalPrice());
        order.setDiscountAmount(orderRequest.getDiscountAmount());
        order.setFinalPrice(orderRequest.getFinalPrice());
        order.setStatus(orderRequest.getStatus());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setCreatedAt(LocalDateTime.now());

        OrderBean savedOrder = orderRepository.save(order);

        // 儲存訂單明細
        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            ProductBean product = productRepository.findByName(itemRequest.getProductName())
                    .orElseThrow(() -> new RuntimeException("找不到商品：" + itemRequest.getProductName()));

            OrderItemBean orderItem = new OrderItemBean();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(itemRequest.getPrice());
            orderItem.setTotalPrice(itemRequest.getTotalPrice());

            orderItemRepository.save(orderItem);
        }
    }

    public List<OrderResponse> findAll() {
        List<OrderBean> orders = orderRepository.findAll();
        List<OrderResponse> responseList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (OrderBean order : orders) {
            OrderResponse dto = new OrderResponse();
            dto.setId(order.getOrderId());
            dto.setUserName(order.getUser().getUsername());
            dto.setTotalprice(order.getTotalPrice());

            CouponBean coupon = order.getCoupon();
            dto.setCouponName(coupon != null ? coupon.getName() : "無");

            dto.setFinalprice(order.getFinalPrice());
            dto.setPaymentMethod(order.getPaymentMethod());
            dto.setStatus(order.getStatus());
            dto.setCreatedAt(order.getCreatedAt().toLocalDate().format(formatter));

            responseList.add(dto);
        }

        return responseList;
    }
    
    public void updateOrder(Integer id, OrderUpdateRequest orderUpdateRequest) {
        OrderBean order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到訂單"));

        order.setPaymentMethod(orderUpdateRequest.getPaymentMethod());
        order.setStatus(orderUpdateRequest.getStatus());

        orderRepository.save(order);
    }

    public Optional<OrderBean> findById(Integer id) {
        return orderRepository.findById(id);
    }

    public OrderBean save(OrderBean order) {
        return orderRepository.save(order);
    }

    public void deleteById(Integer id) {
        orderRepository.deleteById(id);
    }
}
