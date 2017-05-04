package com.company;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by vitaly on 26.04.17.
 */
public class CommandHandler {

    public static String handleCommand(byte[] bytes) throws IOException{
        if (bytes[0] == 1) return init(bytes);
        if (bytes[0] == 2) return commit(bytes);
        if (bytes[0] == 3) return delete(bytes);
        if (bytes[0] == 4) return log(bytes);
        if (bytes[0] == 5) return switchCommand(bytes);
        if (bytes[0] == 6) return update(bytes);
        if (bytes[0] == 7) return clone(bytes);
        return null;
    }

    private static String init (byte[] bytes)  {
        byte[] nameBytesLength = Arrays.copyOfRange(bytes,1, 5);
        byte[] nameBytes = Arrays.copyOfRange(bytes, 5, bytes.length);
        DataConnector dataConnector = new DataConnector();
        dataConnector.makeDirectory(new String(nameBytes));
        return "Directory " + new String(nameBytes) + " was made.";
    }

    private static String log (byte[] bytes) throws IOException{
        byte[] nameBytesLength = Arrays.copyOfRange(bytes,1, 5);
        byte[] nameBytes = Arrays.copyOfRange(bytes, 5, bytes.length);
        String directory = new String(nameBytes);
        File file = new File("./" + directory + "/history");
        if (!file.exists()) return "Repisitory is empty";

        List<String> list = DataConnector.getStrings("./" + directory + "/history");
        String answer = "";
        for (String s : list) {
            answer = answer + s + "\n";
        }
        System.out.println(answer);
        return answer;
    }

    private static String switchCommand (byte[] bytes) throws IOException{
        byte[] nameBytesLength = Arrays.copyOfRange(bytes,1, 5);
        byte[] nameBytes = Arrays.copyOfRange(bytes, 5, 5 + Converter.byteToInt(nameBytesLength));
        int i = Converter.byteToInt(nameBytesLength) + 5;
        String directory = new String(nameBytes);
        byte[] numberBytes = Arrays.copyOfRange(bytes, i, bytes.length);
        int number = Converter.byteToInt(numberBytes);
        String numberStr = String.valueOf(number);
        DataConnector.writeFile(directory + "/lastVersion", numberStr.getBytes());
    //    System.out.println(DataConnector.getVersion(directory));
        return "Switch version to " + numberStr;
    }


    private static String delete (byte[] bytes) {
        byte[] nameBytesLength = Arrays.copyOfRange(bytes,1, 5);
        byte[] nameBytes = Arrays.copyOfRange(bytes, 5, bytes.length);
        DataConnector dataConnector = new DataConnector();

        dataConnector.delete(new File("./" + new String(nameBytes)));
        return "Directory " + new String(nameBytes) + " was deleted.";
    }

    private static String commit (byte[] bytes) throws IOException{

        byte[] nameBytesLength = Arrays.copyOfRange(bytes,1, 5);
        byte[] nameBytes = Arrays.copyOfRange(bytes, 5, 5 + Converter.byteToInt(nameBytesLength));
        String directoryName = new String(nameBytes);
        String version = DataConnector.chooseVersion(directoryName);

        if (DataConnector.getVersion(directoryName) + 1 != Integer.parseInt(version))
            return "Cannot commit at this folder version switched";

        int i = 5 + Converter.byteToInt(nameBytesLength);

        if (i != bytes.length)
            DataConnector.makeFile(directoryName + "/lastVersion", version.getBytes());


        DataConnector.writeVersion(version, directoryName);
        directoryName = directoryName + "/" + version;

        while (i != bytes.length) {
            boolean isFile = (bytes[i] == 1);
            i= i +1;

            byte[] byteFileNameLength = Arrays.copyOfRange(bytes,i, i + 4);
            int lengthFileName = Converter.byteToInt(byteFileNameLength);
            i = i + 4;

            byte[] byteFileName = Arrays.copyOfRange(bytes,i, i + lengthFileName);
            String fileName = new String(byteFileName);
            i = i + lengthFileName;

            if (isFile) {
                byte[] dataLengthBytes = Arrays.copyOfRange(bytes,i, i + 4);
                int dataLength = Converter.byteToInt(dataLengthBytes);
                i = i + 4;

                byte[] data = Arrays.copyOfRange(bytes,i, i + dataLength);
                i = i + data.length;
                DataConnector.makeFile(directoryName + fileName.substring(1), data);
            }
            else DataConnector.makeFolder(directoryName + fileName.substring(1));
            //создание дирректории или файла


        }
        return "All saved";
    }

    private static String update  (byte[] bytes) throws IOException{
        byte[] nameBytesLength = Arrays.copyOfRange(bytes,1, 5);
        byte[] nameBytes = Arrays.copyOfRange(bytes, 5, bytes.length);
        String directory = new String(nameBytes);
        Integer i = DataConnector.getVersion(directory);
        FileWorker.update(directory, i);
        DataConnector.writeFile(directory + "/lastVersion", "1".toString().getBytes());

        return "Was updated";
    }


    private static String clone (byte[] bytes) throws IOException{
        byte[] nameBytesLength = Arrays.copyOfRange(bytes,1, 5);
        byte[] nameBytes = Arrays.copyOfRange(bytes, 5, 5 + Converter.byteToInt(nameBytesLength));
        int i = Converter.byteToInt(nameBytesLength) + 5;
        String directoryCopyFrom = new String(nameBytes);

        byte[] nameToBytesLength = Arrays.copyOfRange(bytes,i, i + 4);
        i = i + 4;
        byte[] nameToBytes = Arrays.copyOfRange(bytes, i, i + Converter.byteToInt(nameToBytesLength));
        String directoryCopyTo = new String(nameToBytes);

        List<Integer> list = new ArrayList<>();
        DataConnector.getVersions(list, directoryCopyFrom);
        list.sort(Integer::compareTo);
        FileWorker.update(directoryCopyFrom, list.get(list.size() - 1));

        File fileTo = new File(directoryCopyTo);
        if (fileTo.exists())
            DataConnector.delete(fileTo);
        fileTo.mkdir();

        File fileFrom = new File(directoryCopyFrom);
        List<String> filesFrom = new ArrayList<>();
        FileWorker.processFilesFromFolder(fileFrom, filesFrom);

        for (String file : filesFrom) {
            String[] way = file.split("/");
            way[0] = directoryCopyTo;
            String joined = String.join("/", way);
            DataConnector.makeCopy(file, joined);
            //System.out.println(joined);
            //System.out.println(file);
        }
        System.out.println(list);

        return directoryCopyFrom + " was copied to " + directoryCopyTo;
    }
}
