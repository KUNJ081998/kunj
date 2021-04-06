/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easynet.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class EcCrypto {

    public String encryptPlainText(String argu_data) {

        EcCrypto EC = new EcCrypto();
        KeyAgreement aKeyAgree;

        PublicKey publicKey = null;
        PrivateKey privateKey = null;
        byte[] encText = null;

        Security.addProvider(new BouncyCastleProvider());

        publicKey = EC.loadPublicKeyFile(readXML.getXmlData("public_key"));
        privateKey = EC.loadPrivateKeyFile(readXML.getXmlData("private_key"));
        
        try {
            aKeyAgree = KeyAgreement.getInstance("ECDH", "BC");
            aKeyAgree.init(privateKey);
            aKeyAgree.doPhase(publicKey, true);

            byte[] aBys = aKeyAgree.generateSecret();
            KeySpec aKeySpec = new DESKeySpec(aBys);
            SecretKeyFactory aFactory = SecretKeyFactory.getInstance("DES");
            Key aSecretKey = aFactory.generateSecret(aKeySpec);

            Cipher aCipher = Cipher.getInstance(aSecretKey.getAlgorithm());

            aCipher.init(Cipher.ENCRYPT_MODE, aSecretKey);
            encText = aCipher.doFinal(argu_data.getBytes());

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return java.util.Base64.getEncoder().encodeToString(encText);
    }

    public String decryptECText(String argu_data) {

        
        EcCrypto EC = new EcCrypto();
        KeyAgreement aKeyAgree;

        String text = null;
        PublicKey publicKey = null;
        PrivateKey privateKey = null;

        Security.addProvider(new BouncyCastleProvider());

        publicKey = EC.loadPublicKeyFile(readXML.getXmlData("public_key"));
        privateKey = EC.loadPrivateKeyFile(readXML.getXmlData("private_key"));

        try {
            aKeyAgree = KeyAgreement.getInstance("ECDH", "BC");
            aKeyAgree.init(privateKey);
            aKeyAgree.doPhase(publicKey, true);

            byte[] aBys = aKeyAgree.generateSecret();
            KeySpec aKeySpec = new DESKeySpec(aBys);
            SecretKeyFactory aFactory = SecretKeyFactory.getInstance("DES");
            Key aSecretKey = aFactory.generateSecret(aKeySpec);

            Cipher aCipher = Cipher.getInstance(aSecretKey.getAlgorithm());
            aCipher.init(Cipher.DECRYPT_MODE, aSecretKey);

            byte[] decText = aCipher.doFinal(Base64.decodeBase64(argu_data.getBytes()));
            text = new String(decText);

            // System.out.println("Decoded=" + text);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return text;
    }

    public PrivateKey loadPrivateKeyFile(String data) {

        //data = "-----BEGIN PRIVATE KEY-----MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgEbVzfPnZPxfAyxqEZV05laAoJAl+/6Xt2O4mOB611sOhRANCAASgFTKjwJAAU95g++/vzKWHkzAVmNMItB5vTjZOOIwnEb70MsWZFIyUFD1P9Gwstz4+akHX7vI8BH6hHmBmfeQl-----END PRIVATE KEY-----";
        PrivateKey privateKey = null;

        data = data.replace("-----BEGIN PRIVATE KEY-----", "");
        data = data.replace("-----END PRIVATE KEY-----", "");
        try {
            byte[] privKeyByteArray = java.util.Base64.getDecoder().decode(data.getBytes("UTF-8"));
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privKeyByteArray);
            KeyFactory keyFactory = KeyFactory.getInstance("ECDH");
            privateKey = keyFactory.generatePrivate(keySpec);

        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return privateKey;
    }

    public PublicKey loadPublicKeyFile(String data) {

        //data = "-----BEGIN PUBLIC KEY-----MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEoBUyo8CQAFPeYPvv78ylh5MwFZjTCLQeb042TjiMJxG+9DLFmRSMlBQ9T/RsLLc+PmpB1+7yPAR+oR5gZn3kJQ==-----END PUBLIC KEY-----";
        PublicKey publicKey = null;

        data = data.replace("-----BEGIN PUBLIC KEY-----", "");
        data = data.replace("-----END PUBLIC KEY-----", "");
        try {
            byte[] publickKeyByteArray = java.util.Base64.getDecoder().decode(data.getBytes("UTF-8"));

            KeyFactory keyFactory = KeyFactory.getInstance("ECDH");
            
            publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publickKeyByteArray));
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            System.out.println("InvalidKeySpecException : " + e.getMessage());
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            System.out.println("NoSuchAlgorithmException : " + e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            System.out.println("UnsupportedEncodingException : " + e.getMessage());
            e.printStackTrace();
        }
        // publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }
}
