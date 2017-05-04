package com.company;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by vitaly on 04.05.17.
 */
public class FileWorker {



    public static void processFilesFromFolder(File folder, List<String> names) throws IOException
    {
        File[] folderEntries = folder.listFiles();
        for (File entry : folderEntries)
        {
            String toAdd = entry.isDirectory() ? entry.getPath() + "/" : entry.getPath();
            if (entry.isDirectory() && entry.listFiles().length != 0) {
                processFilesFromFolder(entry, names);
            }
            else {
                names.add(toAdd);
            }
        }
    }

    public static void clipList (List<Integer> list, Integer a) {
        while (list.get(list.size() - 1) > a)
            list.remove(list.get(list.size() - 1));
    }



    public static void update (String directory, Integer lastVersion) throws IOException{
        List<Integer> list = new ArrayList<>();
        DataConnector.getVersions(list, directory);
        Integer currentVersion = DataConnector.getVersion(directory);


        if (currentVersion == 1) return;
        list.sort(Integer::compareTo);

        clipList(list, currentVersion);

        Integer lastI = list.get(list.size() - 1);
        Integer preLastI = list.get(list.size() - 2);
        File fileLast = new File("./" + directory + "/" + lastI.toString());
        List<String> lastFolderDirs = new ArrayList<>();
        processFilesFromFolder(fileLast, lastFolderDirs);

        for (String file : lastFolderDirs) {
            String[] way = file.split("/");
            way[2] = preLastI.toString();
            String joined = String.join("/", way);
            DataConnector.makeCopy(file, joined);
        }


        lastVersion = preLastI;

        DataConnector.writeFile(directory + "/lastVersion", lastVersion.toString().getBytes());

        DataConnector.delete(fileLast);
        update(directory, lastVersion);
    }
}
