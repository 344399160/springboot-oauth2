package com.scistor.tab.auth.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class IOUtils {
    private static final Logger log = LoggerFactory.getLogger(IOUtils.class);

    /**
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

        return sb.toString();
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
    
    public static boolean writeStringToFile(String filePath, String str) throws IOException {
        FileWriter fw = new FileWriter(filePath,false);
        fw.write(str);
        fw.flush();
        fw.close();
        return true;
    }
    
    public static String readStringFromFile(String filePath) throws IOException {
        FileReader fr =  new FileReader (filePath);
        BufferedReader br = new BufferedReader (fr);
        String str = "";
        String tmpStr;
         while ((tmpStr = br.readLine() )!=null) {
        	 str += tmpStr;
          }
        fr.close();
        return str;
    }
}
