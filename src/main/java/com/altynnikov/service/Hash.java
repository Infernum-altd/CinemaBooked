package com.altynnikov.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class Hash {
    static String generateHash(String wordToHash){
        StringBuilder hash = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[]hashedPassword = md.digest(wordToHash.getBytes(StandardCharsets.UTF_8));
            for (byte b : hashedPassword){
                hash.append(Integer.toHexString(b));
            }
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return hash.toString();
    }
}
