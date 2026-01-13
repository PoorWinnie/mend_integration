package com.example.back.repository;

import java.util.List;
import org.json.JSONObject;
import com.example.back.domain.ProductBean;

public interface ProductInterface {
    long count(JSONObject obj);
    List<ProductBean> find(JSONObject obj);
}

