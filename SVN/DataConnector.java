package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by vitaly on 24.04.17.
 */
public class DataConnector implements IDateProvider{

    public static void writeFile (String fileName, byte[] bytes) throws IOException {
        Files.write(Paths.get("./" + fileName), bytes);
    }

    public static void makeDirectory (String name) {
        if (name.startsWith("/")) name = name.substring(1);
        File dir = new File(name);
        dir.mkdir();
    }

    public static void makeFolder (String folderWay) {
        String[] folders = folderWay.split("/");
        String folderBegin = "" ;
        for (String folder : folders) {
            folderBegin = folderBegin + "/" + folder;
            if (!(new File(folderBegin).exists()))
                makeDirectory(folderBegin);
        }
    }

    public static void makeFile  (String string, byte[] bytes) throws IOException {
        String[] folders = string.split("/");
        if (folders.length == 1)
            writeFile(string, bytes);
        else {
            folders = Arrays.copyOfRange(folders, 0, folders.length - 1);
            StringBuilder builder = new StringBuilder();
            for(String s : folders) {
                builder.append(s + "/");
            }
            String folder = builder.toString();
            makeFolder(folder);
            writeFile(string, bytes);
        }
    }

    public static void writeVersion (String version, String folder) throws  IOException{

        byte[] bytes = ("version " + version + " - " + new Date().toString() + "\n").getBytes();
        if (!new File(folder + "/history").exists()) new File(folder + "/history").createNewFile();
        Files.write(Paths.get("./" +folder+  "/history"), bytes, StandardOpenOption.APPEND);

    }

    public static void copyFile (String from, String to) throws IOException{
        File t = new File(from);
        File t1 = new File(to);
        Files.copy(t.toPath(), t1.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void makeCopy (String from, String to) throws IOException {
        String[] folders = to.split("/");
        if (folders.length == 1)
            copyFile(from, to);
        else {
            folders = Arrays.copyOfRange(folders, 0, folders.length - 1);
            StringBuilder builder = new StringBuilder();
            for(String s : folders) {
                builder.append(s + "/");
            }
            String folder = builder.toString();
            makeFolder(folder);
            copyFile(from, to);
        }

    }

    public static int getVersion (String folder) throws  IOException{
        if (!new File(folder + "/lastVersion").exists()) return 0;
            List<String> list = getStrings(folder+"/lastVersion");
        return Integer.parseInt(list.get(0));
    }

    public static String chooseVersion (String folder) {
        File directory = new File("./" + folder);
        File[] folderEntries = directory.listFiles();
        if (folderEntries.length == 0) return  "1";

        int max = 0;
        for (File file : folderEntries) {
            String fileName = file.getName();
            if (fileName.equals("lastVersion")) continue;
            if (fileName.equals("history")) continue;
            if (max < Integer.parseInt(fileName))
                max = Integer.parseInt(fileName);
        }
        String answer = Integer.toString(max + 1);

        return answer;
    }

    public static void getVersions (List<Integer> list, String folder) {
        File directory = new File("./" + folder);
        File[] folderEntries = directory.listFiles();
        for (File file : folderEntries) {
            String fileName = file.getName();
            if (fileName.equals("lastVersion")) continue;
            if (fileName.equals("history")) continue;
            list.add(Integer.parseInt(fileName));
        }

    }

    public static void delete(File file)
    {
        if(!file.exists())
            return;
        if(file.isDirectory())
        {
            for(File f : file.listFiles())
                delete(f);
            file.delete();
        }
        else
        {
            file.delete();
        }
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
}
