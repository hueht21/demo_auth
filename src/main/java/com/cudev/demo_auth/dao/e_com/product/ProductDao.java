package com.cudev.demo_auth.dao.e_com.product;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductDao {
    List<Map<String, Object>> getAllProduct(Map<String, Object> params);

    Map<String, Object> getProductById(int id);

    int getTotalProduct(Map<String, Object> params);

    List<Map<String, String>> getImgDetailProduct(int idProduct);
}
