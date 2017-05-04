package com.company;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by vitaly on 26.04.17.
 */
public class FileWorker {
    public static String getCurrentDirrectory () throws IOException{
        String path = new File(".").getCanonicalPath();
        Path p = Paths.get(path);
        return p.getFileName().toString();
    }

    public static void processFilesFromFolder(File folder, List<String> names,
                                              HashMap<String, Long> hashMap) throws IOException
    {
        File[] folderEntries = folder.listFiles();
        for (File entry : folderEntries)
        {
            String toAdd = entry.isDirectory() ? entry.getPath() + "/" : entry.getPath();
            if (entry.isDirectory() && entry.listFiles().length != 0) {
                processFilesFromFolder(entry, names, hashMap);
            }
            else if (!(hashMap.containsKey(toAdd) && hashMap.get(toAdd).equals(entry.lastModified()))) {
                names.add(toAdd);
                hashMap.put(toAdd, entry.lastModified());
            }
         }
    }
}
