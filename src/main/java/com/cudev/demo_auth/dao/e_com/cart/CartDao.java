package com.cudev.demo_auth.dao.e_com.cart;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CartDao {
    int insertCart(Map<String, Object> params);

    List<Map<String, Object>> getListCartUser(Long userId);

    int updateCartNumber(Map<String, Object> params);

    int getNumberCart(int userId);

    int deleteCart(int userId);
}
