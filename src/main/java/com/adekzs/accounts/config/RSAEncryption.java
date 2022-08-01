package com.adekzs.accounts.config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Stream;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;

public class RSAEncryption {

    private static String getKey(String filename) throws IOException {
        // Read key from file
        String strKeyPEM = "";
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            strKeyPEM += line + "\n";
        }
        br.close();
        return strKeyPEM;
    }
    public static RSAPrivateKey getPrivateKey(String filename) throws IOException, GeneralSecurityException {
        String privateKeyPEM = getKey(filename);
        return getPrivateKeyFromString(privateKeyPEM);
    }

    public static RSAPrivateKey getPrivateKeyFromString(String key) throws IOException, GeneralSecurityException {
        String privateKeyPEM = key;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
        byte[] encoded = Base64.decodeBase64(privateKeyPEM);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
        return privKey;
    }


    public static RSAPublicKey getPublicKey(String filename) throws IOException, GeneralSecurityException {
        String publicKeyPEM = getKey(filename);
        return getPublicKeyFromString(publicKeyPEM);
    }


    public static RSAPublicKey getPublicKeyFromString(String key) throws IOException, GeneralSecurityException {
        String publicKeyPEM = key;
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
        byte[] encoded = Base64.decodeBase64(publicKeyPEM);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
        return pubKey;
    }

    public static String sign(PrivateKey privateKey, String message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
        Signature sign = Signature.getInstance("SHA1withRSA");
        sign.initSign(privateKey);
        sign.update(message.getBytes("UTF-8"));
        return new String(Base64.encodeBase64(sign.sign()), "UTF-8");
    }


    public static boolean verify(PublicKey publicKey, String message, String signature) throws SignatureException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        Signature sign = Signature.getInstance("SHA1withRSA");
        sign.initVerify(publicKey);
        sign.update(message.getBytes("UTF-8"));
        return sign.verify(Base64.decodeBase64(signature.getBytes("UTF-8")));
    }

    public static String encrypt(String rawText, PublicKey publicKey) throws IOException, GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.encodeBase64String(cipher.doFinal(rawText.getBytes("UTF-8")));
    }

    public static String decrypt(String cipherText, PrivateKey privateKey) throws IOException, GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.decodeBase64(cipherText)), "UTF-8");
    }
























    public static PublicKey getPublicCertKey(String fileName) throws FileNotFoundException {
        FileInputStream fin = new FileInputStream(fileName);
        CertificateFactory f = null;
        PublicKey pk = null;
        try {
            f = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate)f.generateCertificate(fin);
            pk = certificate.getPublicKey();
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
        return pk;
    }

    public static void helloTest(){
        String str1 = new String("I Love Java");
        String str2 = new String("I Love Java");
        String str3 = "I Love Java";
        String str4 = "I I Love Java";
        long j =  str4.chars().distinct().count();
        System.out.println("j is"+ j);
        System.out.println("1, "+ str1.hashCode());
        System.out.println("2, "+ str2.hashCode());
        System.out.println("3, "+ str3.hashCode());
        System.out.println("4, "+ str4.hashCode());
    }

    public static PrivateKey getPrivateKeyFromFile(String filename)
            throws Exception {

        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

        PKCS8EncodedKeySpec spec =
                new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }


    public static String decryptString(String text, String filePath) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(filePath));

        return new String(cipher.doFinal(Base64.decodeBase64(text)), "UTF-8");
    }


    public static void main(String[] args) {
        try {
            String secretMessage = "admin@polaris.com";
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE,getPublicCertKey(
                    "C:\\Users\\Michael.Afolabi\\Desktop\\polaris\\adminpolarispublic.key") );
            byte[] secretMessageBytes = secretMessage.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedMessageBytes = cipher.doFinal(secretMessageBytes);
            String encodedMessage = Base64.encodeBase64String(encryptedMessageBytes);
            System.out.println("The encoded Message is "+ encodedMessage);

//            String result = decryptString("mlc+R5kc43fGoAPk5oIlkgffkSZhhsafY7CWYQjLeOswaLlq9EHPPWent2FurTAjBGmOAdE7k7wVGcJ6njW2WC5WweamaAxm6RAONE0Ybr6X20SqunIOqghkebJLV+I2vlJ79UV2wPO6M+fDe2kZW7BjzBAxoOpUC+vV12D2DONRbQ+K1ltxNKPwJr0vnfwkHy3VsFtghY+JRgfWVKNYsnwDlySFP6Ar8syTmY6vkz6hIzru0EvZFDDbnDKurxx++534sxk/uRvH2+C26unxcyqSiqh4TReIXfxVqQ3JIUhJ38Tj9gsdDsS9rHJNwdmY2FSbzweKXWyssjD1CaYH7g==",
//                    "C:\\Users\\Michael.Afolabi\\Desktop\\firs\\rossmichaelprivate.key");
//
//            String result = decryptString("IitRHPTe/cm695faPWB6GizoN5d8yTvZ892jnItCRvQ/cSfTi7+asFabMsFMTP/GLNKtGcqJ3d+lkT5xpLylv+J38a1c+VsTbKfFqJmoliPuVKgJ91i0y0NPe344s+9iszYJDQ70CxTv5OPNMJojinbYzc7SdIOHLA+77kUHeDnVynBihEcnvn9U62QG42wz1jo1fZE21lgqNzfxhVS7TjlyVdjF1KXNrfhGPWNGITyeKtzctYZIurcv6ZJEtbvzUlSFkfL+jWYoCFDQSPmjHKUyKca+1e/+5CSWkWc1ojGGtBuOGx9f6N1+qARA3ET9zRSuc3F2HN7JHqFzTcwrrw==",
//                    "C:\\Users\\Michael.Afolabi\\Desktop\\polaris\\adminpolarisprivate.key");
//            System.out.println(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
    public String hello() {
        return "";
    }
}
