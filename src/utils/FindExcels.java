package utils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by myl
 * on 2015/1/22.
 */
public class FindExcels {
    public String[] getExcelsName(String path) {
        File file = new File(path);
        return file.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.endsWith(".xlsx") || name.endsWith(".xls"));
            }
        });
    }
}
