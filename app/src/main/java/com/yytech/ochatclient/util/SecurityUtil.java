package com.yytech.ochatclient.util;

/**
 * @author Lee
 * @date 2018/1/14
 */
public class SecurityUtil {

    private static final int offset = 1;

    private SecurityUtil(){}

    public static String encode(String s){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++){
            sb.append((char)(s.charAt(i) + offset));
        }
        return sb.toString();
    }

    public static String decode(String s){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++){
            sb.append((char)(s.charAt(i) - offset));
        }
        return sb.toString();
    }
}
