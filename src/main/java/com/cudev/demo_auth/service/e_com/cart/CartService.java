package com.cudev.demo_auth.service.e_com.cart;

import com.cudev.demo_auth.dao.e_com.cart.CartDao;
import com.cudev.demo_auth.model.ReponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    @Autowired
    CartDao cartDao;

    public ReponseObject insertProduct(Map<String, Object> params) {
        int data = cartDao.insertCart(params);
        return new ReponseObject(true, "Thành công", data);
    }

    public ReponseObject getListCartUser(Long userId) {
        List<Map<String, Object>> listData = cartDao.getListCartUser(userId);
        return new ReponseObject(true, "", listData);
    }

    public ReponseObject updateCartNumber(Map<String, Object> params) {

        int numberCart = Integer.parseInt(params.get("numberCart").toString());
        int idCart = Integer.parseInt(params.get("idCart").toString());

        Map<String, Object> map = new HashMap<>();
        map.put("numberCart", numberCart);
        map.put("idCart", idCart);

        int cart = cartDao.getNumberCart(idCart);
        if (cart == 1) {
            int delete = cartDao.deleteCart(idCart);
            return new ReponseObject(true, "Cập nhật thành công", delete);
        } else {
            int data = cartDao.updateCartNumber(map);
            return new ReponseObject(true, "Cập nhật thành công", data);
        }

    }
}
