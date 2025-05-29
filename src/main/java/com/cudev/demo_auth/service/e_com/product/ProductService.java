package com.cudev.demo_auth.service.e_com.product;

import com.cudev.demo_auth.dao.e_com.product.ProductDao;
import com.cudev.demo_auth.model.ReponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    @Autowired
    ProductDao productDao;

    public ReponseObject getAllProduct(Map<String, Object> param) {

        Map<String, Object> paramsRequest = new HashMap<>();

        int page = Integer.parseInt(param.get("page").toString());
//        int idCategory = Integer.parseInt(param.get("idCategory").toString());

        if(page == 0) {
            page = 1;
        }

        paramsRequest.put("offset", (page -1) * 10);
        paramsRequest.put("idCategory", param.get("idCategory"));

        Map<String, Object> result = new HashMap<>();
        result.put("page",Integer.parseInt(param.get("page").toString()));
        result.put("product",productDao.getAllProduct(paramsRequest));
        result.put("total",productDao.getTotalProduct(paramsRequest));
        return new ReponseObject(true, "", result);
    }

    public ReponseObject getProductById(int id) {
        Map<String, Object> product = productDao.getProductById(id);
        if (product == null) {
            return new ReponseObject(false, "Product not found", null);
        }

        List<Map<String, String>> imgDetailProduct = productDao.getImgDetailProduct(id);

        List<String> imgList = new ArrayList<>();

        for(Map<String, String> mapStr : imgDetailProduct) {
            String img = mapStr.get("linkImgDetail");
            if (img != null && !img.isEmpty()) {
                imgList.add(img);
            }
        }
        product.put("imgDetailProduct",imgList);
        return new ReponseObject(true, "", product);
    }

}
