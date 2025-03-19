package com.taskbuddy.api.presentation.secure;

import com.taskbuddy.api.config.ApplicationProfile;
import com.taskbuddy.api.error.exception.InvalidSecretKeyException;
import com.taskbuddy.api.presentation.ResultCodes;
import com.taskbuddy.api.utils.JsonUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

// Issue : Client -> Server 통신할 때 암호화하는 이유
// Issue : Server가 Client 데이터를 신뢰하도록 세탁하는 과정
// Issue : 운영환경이라면 Key 관리 방법
@Profile({ApplicationProfile.DEV, ApplicationProfile.PROD})
@Component
public class ClientDataRSADecryptor implements SecureDataDecryptor {
    private static final String ENV_VAR = "RSA_PRIVATE_KEY_PATH";

    public <R> R decrypt(String encryptedData, Class<R> returnClass) throws InvalidSecretKeyException {
        PrivateKey privateKey = loadPrivateKey();

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            String decryptedData = new String(decryptedBytes);

            return JsonUtils.deserialize(decryptedData, returnClass);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new InvalidSecretKeyException(ResultCodes.A0002);
        }
    }

    private static PrivateKey loadPrivateKey() {
        final String path = System.getenv(ENV_VAR);

        try {
            byte[] keyBytes = Files.readAllBytes(Paths.get(path));
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new InvalidSecretKeyException(ResultCodes.A0002);
        }
    }
}
