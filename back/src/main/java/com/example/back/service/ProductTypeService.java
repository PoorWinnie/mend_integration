package com.example.back.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.back.domain.ProductTypeBean;
import com.example.back.repository.ProductTypeRepository;

@Service
@Transactional
public class ProductTypeService {
    @Autowired
    private ProductTypeRepository productTypeRepository;

    public ProductTypeBean findById(Integer id) {
        if(id != null) {
            Optional<ProductTypeBean> optional = this.productTypeRepository.findById(id);
            if(optional.isPresent()) {
                return optional.get();
            }
        }
        return null;
    }

    public List<ProductTypeBean> findAll() {
        return productTypeRepository.findAll();
    }

    public boolean exists(Integer id) {
        if(id != null) {
            return this.productTypeRepository.existsById(id);
        }
        return false;
    }
    
    public ProductTypeBean create(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            Integer id = obj.isNull("id") ? null : obj.getInt("id");
            String typeName = obj.isNull("typeName") ? null : obj.getString("typeName");
            
            if(id != null) {
                Optional<ProductTypeBean> optional = this.productTypeRepository.findById(id);
                if(optional.isEmpty()) {
                    ProductTypeBean insert = new ProductTypeBean();
                    insert.setId(id);
                    insert.setTypeName(typeName);
                    return this.productTypeRepository.save(insert);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public ProductTypeBean modify(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            Integer id = obj.isNull("id") ? null : obj.getInt("id");
            String typeName = obj.isNull("typeName") ? null : obj.getString("typeName");

            if(id != null) {
                Optional<ProductTypeBean> optional = this.productTypeRepository.findById(id);
                if(optional.isPresent()) {
                    ProductTypeBean update = optional.get();
                    update.setTypeName(typeName);
                    return this.productTypeRepository.save(update);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean remove(Integer id) {
        if(id != null) {
            Optional<ProductTypeBean> optional = this.productTypeRepository.findById(id);
            if(optional.isPresent()) {
                try {
                    this.productTypeRepository.deleteById(id);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }    
            }
        }
        return false;
    }
    
    public List<ProductTypeBean> select(ProductTypeBean bean) {
        List<ProductTypeBean> result = null;
        if(bean != null && bean.getId() != null && bean.getId() != 0) {
            Optional<ProductTypeBean> optional = this.productTypeRepository.findById(bean.getId());
            if(optional.isPresent()) {
                result = new ArrayList<ProductTypeBean>();
                result.add(optional.get());
            }
        } else {
            result = this.productTypeRepository.findAll();
        }
        return result;
    }
    
    public ProductTypeBean insert(ProductTypeBean bean) {
        if(bean != null && bean.getId() != null) {
            Optional<ProductTypeBean> optional = this.productTypeRepository.findById(bean.getId());
            if(optional.isEmpty()) {
                return this.productTypeRepository.save(bean);
            }
        }
        return null;
    }
    
    public ProductTypeBean update(ProductTypeBean bean) {
        if(bean != null && bean.getId() != null) {
            Optional<ProductTypeBean> optional = this.productTypeRepository.findById(bean.getId());
            if(optional.isPresent()) {
                return this.productTypeRepository.save(bean);
            }
        }
        return null;
    }
    
    public boolean delete(ProductTypeBean bean) {
        if(bean != null && bean.getId() != null) {
            Optional<ProductTypeBean> optional = this.productTypeRepository.findById(bean.getId());
            if(optional.isPresent()) {
                this.productTypeRepository.delete(optional.get());
                return true;
            }
        }
        return false;
    }
}
