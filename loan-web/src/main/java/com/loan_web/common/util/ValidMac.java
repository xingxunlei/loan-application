package com.loan_web.common.util;

import com.loan_web.common.constant.ErrorCode;
import com.loan_web.common.exception.CommonException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;

@Component
public class ValidMac {
    private static Log log = LogFactory.getLog(ValidMac.class);

    public static final Charset CHARSET = Charset.forName("utf-8");

    public static final byte keyStrSzie = 16;

    public static final String ALGORITHM = "AES";

    public static final String AES_CBC_NOPADDING = "AES/CBC/NoPadding";


    @Value("${rzj.merchantKey}")
    private String merchantKey;

    public String generatorMac(String[] keys, Map<String, String[]> params)
            throws CommonException, UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        for (String key : keys) {
            String param = params.get(key) != null ? params.get(key)[0] : null;
            if (StringUtils.isBlank(param)) {
                throw new CommonException(ErrorCode.PARAMETERS_ARE_MISSING,
                        String.format(ErrorCode.PARAMETERS_ARE_MISSING_VALUE,
                                key));
            }
            if (!key.equals("sign")) {
                sb.append(key).append("=").append(params.get(key)[0])
                        .append("&");
            }
        }
        String md5 = Md5Encrypt.md5(sb.toString().substring(0,sb.length() - 1));
        String md5str =Md5Encrypt.md5( md5 + merchantKey);
        log.debug("The md5 string is " + sb.toString() + "md5="+md5+";\nmd5str=" + md5str);
        return md5str;
    }

    public void checkMac(String[] keys,Map<String, String[]> params)
            throws CommonException, UnsupportedEncodingException {
        String nowmac = generatorMac(keys, params);
        if (!params.get("sign")[0].equalsIgnoreCase(nowmac)) {
            throw new CommonException(ErrorCode.CHECK_MAC_ERROR,
                    ErrorCode.CHECK_MAC_ERROR_VALUE);
        }
    }

    /**
     * 用 AES 算法加密 inputStr。
     * 使用 secretStr 作为 key，secretStr 的前 16 个字节作为 iv。
     * 并对加密后的字节数组调用 sun.misc.BASE64Encoder.encode 方法，
     * 转换成 base64 字符串返回。
     * <p>
     * （仅作为测试用途，具体加密流程以接口文档为准）
     *
     * @param secretStr
     * @param inputStr
     * @return
     * @throws CodecException
     * @throws IOException
     */
    public String base64StrDecode( String inputStr) {
        byte[] inputBytes;
        inputBytes = Base64.decodeBase64(inputStr);
        String outputStr = new String(decode(merchantKey, inputBytes), CHARSET);
        log.info("base64Decode > base64 decrypt " + outputStr);
        return outputStr;
    }

    /**
     * 用 AES 算法加密 inputStr。
     * 使用 secretStr 作为 key，secretStr 的前 16 个字节作为 iv。
     *
     * @param secretStr
     * @param inputStr
     * @return
     * @throws CodecException
     */
    public static byte[] decode(String secretStr, byte[] inputBytes) {
        if (keyStrSzie != secretStr.length()) {
            return null;
        }
        byte[] secretKeyBytes = secretStr.getBytes(CHARSET);
        byte[] ivBytes = Arrays.copyOfRange(secretKeyBytes, 0, 16);

        byte[] outputBytes = decryptCBCNoPadding(secretKeyBytes, ivBytes, inputBytes);
        return outputBytes;
    }

    /**
     * AES/CBC/NoPadding decrypt
     * 16 bytes secretKeyStr
     * 16 bytes intVector
     *
     * @param secretKeyStr
     * @param intVector
     * @param input
     * @return
     */
    public static byte[] decryptCBCNoPadding(byte[] secretKeyBytes, byte[] intVectorBytes, byte[] input) {
        try {
            IvParameterSpec iv = new IvParameterSpec(intVectorBytes);
            SecretKey secretKey = new SecretKeySpec(secretKeyBytes, ALGORITHM);

            Cipher cipher = Cipher.getInstance(AES_CBC_NOPADDING);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] encryptBytes = cipher.doFinal(input);
            return encryptBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
