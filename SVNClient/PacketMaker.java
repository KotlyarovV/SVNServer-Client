package com.company;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by vitaly on 26.04.17.
 */
public class PacketMaker {
    public static byte[] makePacket(String command) throws  IOException{
        String[] commands = command.split(" ");
        if (commands[0].equals("init")) return init();
        if (commands[0].equals("commit")) return commit(commands);
        if (commands[0].equals("delete")) return delete();
        if (commands[0].equals("log")) return log();
        if (commands[0].equals("switch")) return switchCommand(commands);
        if (commands[0].equals("update")) return update();
        if (commands[0].equals("clone")) return clone(commands);

        return null;
    }


    /**
     * код команды innit - 1
     * код пишем одним байтом
    */
    private static byte[] init () throws IOException{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write (new byte[] {1});
        byteArrayOutputStream.write(makeNamePacket());
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * код команды commit - 2 - 1 байт
     * длина названия текщей дирректории
     * название текущей дирректории
     * файл или папка - 1 или 2 - 1 байт
     * затем длина названия - 4 байта
     * если файл - длина содержимого - 4 байта - содержимое
     *
     * в файл при этом пишем все файлы и их хэши
    */
    private static byte[] commit (String[] commands) throws IOException{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byteArrayOutputStream.write (new byte[] {2});

        byteArrayOutputStream.write(Converter.intToByte(FileWorker.getCurrentDirrectory().getBytes().length));
        byteArrayOutputStream.write(FileWorker.getCurrentDirrectory().getBytes());

        List<String> files = new ArrayList<>();
        List<String> ignored = DataConnector.getStrings("ignore");

        File thisDirectory = new File(".");
        HashMap<String, Long> hashMap = DataConnector.getHashMap();


        if (commands.length == 1)
            FileWorker.processFilesFromFolder(thisDirectory, files, hashMap);
        else {
            for (int i = 1; i < commands.length; i ++)
            {
                File file = new File("./" + commands[i]);
                HashMap<String, Long> localHashMap = new HashMap<>();
                if (file.isDirectory() && file.listFiles().length != 0)
                    FileWorker.processFilesFromFolder(thisDirectory, files, localHashMap);
                else {
                    String toAdd = file.isDirectory() ? file.getPath() + "/" : file.getPath();
                    files.add(toAdd);
                    localHashMap.put(toAdd,file.lastModified());
                }
                hashMap.putAll(localHashMap);
            }
        }

        for (String file : files) {
            if (Util.startsWithStrings(ignored, file)) continue;
            boolean isDirectory = file.endsWith("/");
            byte one = 1;
            byte two = 2;
            byteArrayOutputStream.write(isDirectory ? two : one);

            byteArrayOutputStream.write(Converter.intToByte(file.getBytes().length));
            byteArrayOutputStream.write(file.getBytes());

            if (!isDirectory) {
                byte[] fileBites = DataConnector.getFile(file);
                byte[] size = Converter.intToByte(fileBites.length);
                byteArrayOutputStream.write(size);
                byteArrayOutputStream.write(fileBites);
            }
        }
        DataConnector.writeHashMap(hashMap);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }

    /**
     * код команды делит - 3
     * длина названия дирректории
     * название дирректории
    */
    private static byte[] delete () throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(new byte[]{3});
        byteArrayOutputStream.write(makeNamePacket());

        File hashs = new File("./hashs");
        hashs.delete();

        return byteArrayOutputStream.toByteArray();
    }

    /**
     * код команды log 4
     * высылается
     * длина названия дирректории
     * название
     */
    private static byte[] log () throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(new byte[]{4});
        byteArrayOutputStream.write(makeNamePacket());
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * код комманды switch 5
     * пишем пакет с именем
     * номер версии в переводе в байты
     */
    private static byte[] switchCommand (String[] strings) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(new byte[]{5});
        byteArrayOutputStream.write(makeNamePacket());
        int number = Integer.parseInt(strings[1]);
        byteArrayOutputStream.write(Converter.intToByte(number));
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * код команды update - 6
     * высылается
     * длина названия дирректории
     * название
     */
    private static byte[] update () throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(new byte[]{6});
        byteArrayOutputStream.write(makeNamePacket());
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * код команды clone - 7
     * высылается
     * длина названия дирректории
     * название
     * длина названия дирректрии для копирования
     * название
    */
    private static byte[] clone (String[] commands) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(new byte[]{7});
        byteArrayOutputStream.write(makeNamePacket());
        byteArrayOutputStream.write(Converter.intToByte(commands[1].length()));
        byteArrayOutputStream.write(commands[1].getBytes());

        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] makeNamePacket () throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        String name = FileWorker.getCurrentDirrectory();
        byteArrayOutputStream.write(Converter.intToByte(name.length()));
        byteArrayOutputStream.write(name.getBytes());

        return byteArrayOutputStream.toByteArray();
    }
}
