package com.scistor.tab.auth.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by liukai on 2015/9/25.
 */
public class LicenceRunUtils {

    private static final Logger log = LoggerFactory.getLogger(LicenceRunUtils.class);

    public LicenceRunUtils() {
    }
    public Process run() throws IOException {
        // invoke run.sh
        String[] cmd = {"bash", "lic_check.sh", "/usr/scistor/lic"};
        ProcessBuilder builder = new ProcessBuilder(cmd);
        builder.redirectErrorStream(true);
        //TODO 设置用户名及队列名
        return builder.directory(new File("/usr/scistor/lic")).start();
    }
}

