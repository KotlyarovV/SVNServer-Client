package com.company;

import java.util.List;

/**
 * Created by vitaly on 01.05.17.
 */
public class Util {

    public static boolean startsWithStrings (List<String> list, String string) {
        for (String prefix : list)
            if (string.startsWith(prefix))
                return true;
        return false;
    }
}
