package hu.congressline.pcs.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import hu.congressline.pcs.config.ApplicationProperties;
import hu.congressline.pcs.domain.enumeration.Currency;
import hu.congressline.pcs.service.dto.kh.SignBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentCryptoService {
    private static final String RSA = "RSA";
    private static final String SHA_256 = "SHA256withRSA";

    private final Map<Currency, PrivateKey> merchantPrivateKeyMap = new HashMap<>();
    private final Map<Currency, PublicKey> merchantPublicKeyMap = new HashMap<>();

    private final ApplicationProperties properties;

    private PrivateKey initPrivateKey(Currency currency) throws Exception {
        String keyFilePath = properties.getPayment().getGateway().getPaymentKeyFilePath();
        keyFilePath = keyFilePath.endsWith(File.separator) ? keyFilePath : keyFilePath + File.separator;

        String merchantId = getMerchantId(currency);
        byte[] keyBytes = Files.readAllBytes(new File(keyFilePath + "kh-private-key-" + merchantId + ".der").toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(RSA);
        return kf.generatePrivate(spec);
    }

    @SuppressWarnings("IllegalCatch")
    private PublicKey initPublicKey(Currency currency) {
        try {
            String keyFilePath = properties.getPayment().getGateway().getPaymentKeyFilePath();
            keyFilePath = keyFilePath.endsWith(File.separator) ? keyFilePath : keyFilePath + File.separator;

            String merchantId = getMerchantId(currency);
            String data = String.join("", Files.readAllLines(new File(keyFilePath + "kh-public-key-" + merchantId + ".pub").toPath()));
            data = StringUtils.remove(data, "-----BEGIN PUBLIC KEY-----");
            data = StringUtils.remove(data, "-----END PUBLIC KEY-----");
            data = StringUtils.remove(data, "\\n");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(data));
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid public key: ", e);
        }
    }

    @SuppressWarnings({"MissingJavadocMethod", "IllegalCatch"})
    public boolean isSignatureValid(Currency currency, SignBase signBase) {
        try {
            String data2Verify = signBase.toSign();
            log.info("data to verify: '{}'", data2Verify);
            log.info("signature: '{}'", signBase.getSignature());
            PublicKey publicKey = merchantPublicKeyMap.get(currency);
            if (publicKey == null) {
                publicKey = initPublicKey(currency);
                merchantPublicKeyMap.put(currency, publicKey);
            }
            return verify(publicKey, data2Verify, signBase.getSignature());
        } catch (Exception e) {
            log.warn("Invalid signature: ", e);
            return false;
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void createSignature(Currency currency, SignBase signBase) throws Exception {
        signBase.fillDttm();
        String data2Sign = signBase.toSign();
        PrivateKey privateKey = merchantPrivateKeyMap.get(currency);
        if (privateKey == null) {
            privateKey = initPrivateKey(currency);
            merchantPrivateKeyMap.put(currency, privateKey);
        }

        signBase.setSignature(sign(privateKey, data2Sign));
    }

    @SuppressWarnings("IllegalCatch")
    protected String sign(PrivateKey privateKey, String value) {
        try {
            log.debug("String to sign: {}", value);
            Signature rsa = Signature.getInstance(SHA_256);
            rsa.initSign(privateKey);
            rsa.update(value.getBytes(StandardCharsets.UTF_8));
            byte[] signature = rsa.sign();
            String result = Base64.getEncoder().encodeToString(signature);
            log.debug("Signed string: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error during the RSA signing process", e);
            return null;
        }
    }

    @SuppressWarnings("IllegalCatch")
    protected boolean verify(PublicKey key, String plainData, String signature) {
        try {
            byte[] sign = Base64.getDecoder().decode(signature);
            Signature instance = Signature.getInstance(SHA_256);
            instance.initVerify(key);
            instance.update(plainData.getBytes(StandardCharsets.UTF_8));
            return instance.verify(sign);
        } catch (Exception e) {
            log.error("Error during the RSA verifying process", e);
            return false;
        }
    }

    private String getMerchantId(Currency currency) {
        if (Currency.EUR.equals(currency)) {
            return properties.getPayment().getGateway().getMerchantIdForEUR();
        } else if (Currency.HUF.equals(currency)) {
            return properties.getPayment().getGateway().getMerchantIdForHUF();
        } else {
            throw new IllegalArgumentException("Payment currency has to be HUF or EUR!");
        }
    }

}
