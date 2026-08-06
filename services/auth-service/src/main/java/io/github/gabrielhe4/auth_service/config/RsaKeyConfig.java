package io.github.gabrielhe4.auth_service.config;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class RsaKeyConfig {

    @Value("classpath:keys/private_pkcs8.pem")
    private Resource privateKeyResource;

    PrivateKey privateKey() throws Exception {
        String pem = new String(
            privateKeyResource.getInputStream().readAllBytes(), 
            StandardCharsets.UTF_8);

        String base64 = pem.replaceFirst("-----BEGIN PRIVATE KEY-----", "")
                .replaceFirst("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        
        byte[] decoded = Base64.getDecoder().decode(base64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

}
