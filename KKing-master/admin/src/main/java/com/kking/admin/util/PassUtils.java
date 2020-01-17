package com.kking.admin.util;

import org.apache.shiro.crypto.hash.Sha256Hash;

public class PassUtils {


    public static String enCode(String userName,String password){

        return new Sha256Hash(password, userName).toString();

    }
}