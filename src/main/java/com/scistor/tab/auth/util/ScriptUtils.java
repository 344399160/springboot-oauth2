package com.scistor.tab.auth.util;

import com.scistor.tab.auth.common.Constants;
import com.scistor.tab.auth.common.HttpStatusException;
import org.apache.commons.lang.StringUtils;

import java.io.*;

public class ScriptUtils {
	
    /**
     * 生成spark-submit参数
     * @return 生成的参数
     */
    public static String generateSparkSubmitArg(String coreOfExecutor, String memoryOfExecutor, String numberOfExecutor, String dependJarList, String className, String otherJavaOpt, String mainName, String mainArg, String queueName) {
		String runCommand = Constants.SPARK_SUBMIT;
		if (coreOfExecutor != null) {
			runCommand += " ";
			runCommand += Constants.EXECUTOR_CORES_ARG;
			runCommand += " ";
			runCommand += coreOfExecutor;
		}
		if (memoryOfExecutor != null) {
			runCommand += " ";
			runCommand += Constants.EXECUTOR_MEMORY_ARG;
			runCommand += " ";
			runCommand += memoryOfExecutor;
		}
		if (numberOfExecutor != null) {
			runCommand += " ";
			runCommand += Constants.NUM_EXECUTORS_ARG;
			runCommand += " ";
			runCommand += numberOfExecutor;
		}
		if (StringUtils.isNotEmpty(queueName)) {
			runCommand += " ";
			runCommand += Constants.QUEUE_ARG;
			runCommand += " ";
			runCommand += queueName;
		}
		if (StringUtils.isNotEmpty(dependJarList)) {
			runCommand += " ";
			runCommand += Constants.JARS_ARG;
			runCommand += " ";
			runCommand += dependJarList;
		}
		if (StringUtils.isNotEmpty(className)) {
			runCommand += " ";
			runCommand += Constants.CLASS_ARG;
			runCommand += " ";
			runCommand += className;
		}
		if (StringUtils.isNotEmpty(otherJavaOpt)) {
			runCommand += " ";
			runCommand += Constants.DRIVER_JAVA_OPTIONS;
			runCommand += " ";
			runCommand += otherJavaOpt;
		}
		if (StringUtils.isEmpty(mainName)) {
			throw new HttpStatusException.BadRequest("mainName is null");
		} else {
			runCommand += " ";
			runCommand += mainName;
		}
		if (StringUtils.isNotEmpty(mainArg)) {
			runCommand += " ";
			runCommand += mainArg;
		}
		
		return runCommand;
    }

    /**
     * 运行spark提交脚本
     * @param workingPath 工作目录
     * @return
     * @throws IOException
     */
    public static Process runScript(String workingPath, String[] cmd) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(cmd);
        builder.redirectErrorStream(true);
        return builder.directory(new File(workingPath)).start();
    }
    
    /**
     * 运行crontab脚本
     * @param workingPath 工作目录
     * @return
     * @throws IOException
     */
    public static Process runCrontabScript(String workingPath, String[] cmd, String sparkScript, String frequency) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(cmd);
        builder.redirectErrorStream(true);
        builder.environment().put("cmd_path", sparkScript);
        builder.environment().put("frequency", frequency);
        return builder.directory(new File(workingPath)).start();
    }
    
    
    /**
     * 将日志写到文件
     * @param process
     * @param logPath
     * @return
     * @throws IOException
     */
    public static String writeLogToFile(Process process, String logPath) throws IOException {
    	BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));  
        String line = br.readLine();
        FileWriter writer = new FileWriter(logPath);
        boolean founded = false;
        String applicationId =null;
        while (line != null) {
        	writer.write(line + System.lineSeparator());
            if (!founded && !(line == null || line.length() == 0)) {
                String[] strs = line.split("\\W");
                for (String str : strs) {
                    if (str.startsWith("application_")) {
                        applicationId = str;
                        founded = true;
                        break;
                    }
                }
            }
            line = br.readLine();
        }
        writer.close();
        return applicationId;
    }
}
