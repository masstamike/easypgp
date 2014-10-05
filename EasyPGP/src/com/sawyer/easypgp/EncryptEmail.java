package com.sawyer.easypgp;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.util.Base64;

public class EncryptEmail {
   KeyPairGenerator kpg;
   KeyPair kp;
   PublicKey pubKey;
   PrivateKey privKey;
   byte[] encryptedBytes, decryptedBytes;
   Cipher cipher, cipher1;
   String[] encrypted;

   public String[] Encrypt(final String plainText)
         throws NoSuchAlgorithmException, NoSuchPaddingException,
         InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
      kpg = KeyPairGenerator.getInstance("RSA");
      kpg.initialize(1024);
      kp = kpg.genKeyPair();
      pubKey = kp.getPublic();
      privKey = kp.getPrivate();

      cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.ENCRYPT_MODE, pubKey);
      encryptedBytes = cipher.doFinal(plainText.getBytes());
      for (int i = 0; i<encryptedBytes.length; i++){
         System.out.println(encryptedBytes[i]);
      }
      try {
         if (encryptedBytes != null) {
            System.out.println("Yes, encryptedBytes exists.");
            encrypted[0] = new String(encryptedBytes);
            System.out.println(encrypted[0]);
         }
      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      try {
         encrypted[1] = new String(privKey.getEncoded());
      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      // Log.d("EasyPGP.Encrypt", encrypted[0]);
      return encrypted;
   }

}
