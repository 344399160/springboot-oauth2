package com.scistor.tab.auth.util;

import com.scistor.tab.auth.common.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by liukai on 2015/9/25.
 */
public class LicenceCheckUtil {

    private static final Logger log = LoggerFactory.getLogger(LicenceCheckUtil.class);
    public static void licCheck() throws IOException, InterruptedException {        
    	 Process process = new LicenceRunUtils().run();
         BufferedReader bufferedReader =
         		new BufferedReader(new InputStreamReader(process.getInputStream()));
 		String line = bufferedReader.readLine();
 		process.waitFor();
 		if (line != null) {
 	 		if (line.contains("return 0")) {
 	 		} else if (line.contains("return 1")){
 	            throw new HttpStatusException.Forbidden("licence is incorrect");
 	 		} else {
 	            throw new HttpStatusException.Forbidden("licence is expire");
 	 		}
 		    System.out.println("licences++++++++++++++" + line);
 		}

    }
}

