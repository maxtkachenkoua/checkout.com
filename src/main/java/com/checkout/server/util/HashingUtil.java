package com.checkout.server.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class HashingUtil {
    private static final String ENCODING_ALGO = "HmacSHA256";

    public static String computeHmacSha256(String data, String key) throws Exception {
        try {
            Mac hmacSha256 = Mac.getInstance(ENCODING_ALGO);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ENCODING_ALGO);
            hmacSha256.init(keySpec);
            byte[] hashBytes = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute hash", e);
        }
    }
}
