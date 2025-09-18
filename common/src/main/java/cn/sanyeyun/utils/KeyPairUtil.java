package cn.sanyeyun.utils;

import cn.sanyeyun.cache.GlobalCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


/**
 * 模组 Ed25519 密钥对管理工具类
 * 用于生成、保存和加载本地公私钥
 * <p>
 * 适用于 Fabric / Forge
 *
 * @author 徐亚松
 * 2025/9/8 16:46
 */
public class KeyPairUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyPairUtil.class);

    private static final String CONFIG_DIR = "config";
    private static final String PRIVATE_KEY_FILE = "trileaf_private.key";
    private static final String PUBLIC_KEY_FILE = "trileaf_public.key";
    private static final Path PRIVATE_KEY_PATH = Path.of(CONFIG_DIR, PRIVATE_KEY_FILE);
    private static final Path PUBLIC_KEY_PATH = Path.of(CONFIG_DIR, PUBLIC_KEY_FILE);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        loadOrGenerateKeyPair();
    }

    /**
     * 加载本地密钥对，如果不存在则生成
     */
    public static void loadOrGenerateKeyPair() {
        try {
            Files.createDirectories(Path.of(CONFIG_DIR));

            if (Files.exists(PRIVATE_KEY_PATH) && Files.exists(PUBLIC_KEY_PATH)) {
                KeyPair keyPair = loadKeyPair();
                GlobalCache.setKeyPair(keyPair);
                LOGGER.info("成功加载本地密钥对。");
            } else {
                KeyPair keyPair = generateKeyPair();
                saveKeyPair(keyPair);
                GlobalCache.setKeyPair(keyPair);
                LOGGER.info("本地密钥对不存在，已生成并保存。");
            }
        } catch (Exception e) {
            LOGGER.error("加载或生成密钥对失败：", e);
        }
    }

    /**
     * 生成 Ed25519 密钥对
     */
    private static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("Ed25519");
        return kpg.generateKeyPair();
    }

    /**
     * 保存密钥对到本地
     */
    private static void saveKeyPair(KeyPair keyPair) throws IOException {
        saveKey(PRIVATE_KEY_PATH, keyPair.getPrivate().getEncoded(), "PRIVATE KEY");
        saveKey(PUBLIC_KEY_PATH, keyPair.getPublic().getEncoded(), "PUBLIC KEY");
    }

    private static void saveKey(Path path, byte[] keyBytes, String header) throws IOException {
        String base64 = Base64.getEncoder().encodeToString(keyBytes);
        try (Writer writer = Files.newBufferedWriter(path)) {
            writer.write("-----BEGIN " + header + "-----\n");
            writer.write(base64);
            writer.write("\n-----END " + header + "-----");
        }
    }

    /**
     * 从本地文件读取密钥对
     */
    private static KeyPair loadKeyPair() throws Exception {
        PrivateKey privateKey = loadPrivateKey(PRIVATE_KEY_PATH);
        PublicKey publicKey = loadPublicKey(PUBLIC_KEY_PATH);
        return new KeyPair(publicKey, privateKey);
    }

    private static PrivateKey loadPrivateKey(Path path) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(
                Files.readString(path).replaceAll("-----.*-----", "").replaceAll("\\s", "")
        );
        KeyFactory kf = KeyFactory.getInstance("Ed25519");
        return kf.generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }

    private static PublicKey loadPublicKey(Path path) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(
                Files.readString(path).replaceAll("-----.*-----", "").replaceAll("\\s", "")
        );
        KeyFactory kf = KeyFactory.getInstance("Ed25519");
        return kf.generatePublic(new X509EncodedKeySpec(bytes));
    }


    /**
     * 用私钥对消息签名
     *
     * @param message    待签名消息
     * @param privateKey 私钥
     * @return {@link String}
     * @author 徐亚松 2025/9/15 14:48
     */
    public static String sign(String message, PrivateKey privateKey) throws Exception {
        Signature sig = Signature.getInstance("Ed25519");
        sig.initSign(privateKey);
        sig.update(message.getBytes());
        byte[] signatureBytes = sig.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }
}
