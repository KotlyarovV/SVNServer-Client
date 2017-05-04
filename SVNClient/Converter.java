package com.company;


import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Created by vitaly on 26.04.17.
 */
public class Converter {

    public static byte[] intToByte(int number) {
        byte [] bytes = ByteBuffer.allocate(4).putInt(number).array();
        return bytes;
    }


    public static  int byteToInt (byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static String byteToString (byte[] bytes) throws UnsupportedEncodingException {
        return new String(bytes, "utf-8");};
}