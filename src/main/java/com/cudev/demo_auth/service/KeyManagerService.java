package com.cudev.demo_auth.service;

import com.cudev.demo_auth.util.RSAUtil;
import org.springframework.stereotype.Service;

@Service
public class KeyManagerService {


    public String getKeyPublic() {

        String publicKey = null;
        try {
            publicKey = RSAUtil.getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return publicKey;
    }
}
