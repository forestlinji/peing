package peing.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

public class AesUtil {

    //密钥 (需要前端和后端保持一致)十六位作为密钥
    private static final String KEY = "forestForTodoKey";

    //密钥偏移量 (需要前端和后端保持一致)十六位作为密钥偏移量
    private static final String IV = "forestForTodo_Iv";

    //算法
    private static final String ALGORITHMSTR = "AES/CBC/PKCS5Padding";


    /**
     * base 64 decode
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     * @throws Exception
     */
    public static byte[] base64Decode(String base64Code) throws Exception{
        /**sun.misc.BASE64Decoder是java内部类，有时候会报错，
         * 用org.apache.commons.codec.binary.Base64替代，效果一样。
         */
        //Base64 base64 = new Base64();
        //byte[] bytes = base64.decodeBase64(new String(base64Code).getBytes());
        //new BASE64Decoder().decodeBuffer(base64Code);

        return StringUtils.isEmpty(base64Code) ? null : new Base64().decodeBase64(new String(base64Code).getBytes());
    }

    /**
     * AES解密
     * @param encryptBytes 待解密的byte[]
     * @return 解密后的String
     * @throws Exception
     */
    public static String aesDecryptByBytes(byte[] encryptBytes) throws Exception {

        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);

        byte[] temp = IV.getBytes("UTF-8");
        IvParameterSpec iv = new IvParameterSpec(temp);

        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(KEY.getBytes(), "AES"), iv);
        byte[] decryptBytes = cipher.doFinal(encryptBytes);

//        System.out.print(new String(decryptBytes));
        return new String(decryptBytes);
    }

    /**
     * 将base 64 code AES解密
     * @param encryptStr 待解密的base 64 code
     * @return 解密后的string
     * @throws Exception
     */
    public static String aesDecrypt(String encryptStr) throws Exception {
        return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr));
    }

    public static boolean validPassword(String password) throws Exception {
        password = AesUtil.aesDecrypt(password);
        if(password.length()<6||password.length()>16||StringUtils.isNumeric(password)){
            return false;
        }
        return true;
    }
}