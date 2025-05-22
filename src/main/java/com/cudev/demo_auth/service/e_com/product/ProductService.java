package com.cudev.demo_auth.service.e_com.product;

import com.cudev.demo_auth.dao.e_com.product.ProductDao;
import com.cudev.demo_auth.model.ReponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProductService {

    @Autowired
    ProductDao productDao;

    public ReponseObject getAllProduct(Map<String, Object> param) {

        Map<String, Object> paramsRequest = new HashMap<>();

        int page = Integer.parseInt(param.get("page").toString());

        paramsRequest.put("offset", (page) * 10);

        Map<String, Object> result = new HashMap<>();
        result.put("page",Integer.parseInt(param.get("page").toString()));
        result.put("product",productDao.getAllProduct(paramsRequest));
        result.put("total",productDao.getAllProduct(paramsRequest).size());
        return new ReponseObject(true, "", result);
    }

}
