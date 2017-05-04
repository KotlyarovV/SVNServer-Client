package com.company;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by vitaly on 24.04.17.
 */
public class DataConnector {
    public  void writeFile (String fileName, byte[] bytes) throws IOException {
        Files.write(Paths.get("./" + fileName), bytes);
    }

    public  void addToFile (String fileName, byte[] bytes) throws IOException {
        Files.write(Paths.get("./" + fileName), bytes, StandardOpenOption.APPEND);
    }

    public static byte[] getFile(String fileName) throws IOException{
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(fileName);
        byte[] bytes = new byte[(int) file.length()];
        fis.read(bytes);
        fis.close();
        return bytes;
    }

    public static List<String> getStrings(String fileName) throws IOException{
        File file = new File(fileName);
        FileReader fileReader = new FileReader(file.getAbsoluteFile());
        BufferedReader reader = new BufferedReader(fileReader);
        List<String> list = new LinkedList<>();
        try {
            String s;
            while ((s = reader.readLine()) != null) {
                list.add(s);
            }
        }
        catch (Throwable e) {}
        return list;
    }

    public static HashMap<String, Long> getHashMap () throws IOException{
        if (!(new File("hashs").exists())) return new HashMap<String, Long>();

        HashMap<String , Long> hashMap = new HashMap<>();
        List<String> strings = getStrings("hashs");
        for (String string : strings) {
            String[] datas = string.split(" ");
            hashMap.put(datas[0], Long.parseLong(datas[1]));
        }
        return hashMap;
    }

    public static void writeHashMap (HashMap<String, Long> hashMap) throws  IOException{
        Files.write(Paths.get( "hashs"), ("").getBytes());
        for (String key : hashMap.keySet())
            Files.write(Paths.get( "hashs"), (key + " " + hashMap.get(key).toString() + "\n").getBytes(), StandardOpenOption.APPEND);
    }
}
