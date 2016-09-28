package com.scistor.tab.auth.common;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Wei Xing
 */
public class Constants {
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss z";
    public static final String DEFAULT_TIME_ZONE = "GMT+8";
    public static final String DEFAULT_DRIVER = "org.apache.hive.jdbc.HiveDriver";
    public static final String DEFAULT_DATABASE = "wsl";
    public static final String DEFAULT_ADMIN_NAME = "admin";
    public static final String DEFAULT_QUEUE_NAME = "default";
    public static final String DEFAULT_ADMIN_GROUP_ID = "0";
    public static final String QUERY_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String WEB_SERVER = "web_server";
    public static final String WEB_SERVER_PASSWORD = "d23bedc4-a856-4ccc-9c22-68675dc5ee27";
    public static final String SECRET_KEY = "A1B2C3D4E5F60708";
    public static final String DEFULT_JAVAOPT = "";
    public static final String RUN_SCRIPT_NAME = "run.sh";
    public static final String SPARK_SUBMIT = "spark-submit";
    public static final String EXECUTOR_CORES_ARG = "--executor-cores";
    public static final String EXECUTOR_MEMORY_ARG = "--executor-memory";
    public static final String NUM_EXECUTORS_ARG = "--num-executors";
    public static final String QUEUE_ARG = "--queue";
    public static final String JARS_ARG = "--jars";
    public static final String CLASS_ARG = "--class";
    public static final String DRIVER_JAVA_OPTIONS = "--driver-java-options";
    public static final String LOG_NAME="log.txt";
    public static final String LOG_LAST_NUM = "50";
    public static final String CRONTAB_SCRIPT_NAME="crontab.sh";
    public static final String DATAFLOW_SCHEDULE_NAME="dataflow_schedule.jar";
    public static final String DATFLOW_SCHEDULE_CLASS_NAME="com.scistor.dataflow.dataflowschedule.OneServer";
    public static final String DEFAULT_DURATIONMS="2000";
    public static enum DataType{
        FILE("文件"),
        DIR("目录"),
        TABLE("表");

        private String name;

        private DataType(String name) {
            this.name = name;
        }
        
        @JsonValue
        public String getName() {
            return name;
        }
    }
    
    public static enum SourceType{
        DIR("目录"),
        FILE("文件"),
        MYSQL("mysql"),
        ORACLE("oracle"),
        IDRILLER("hive"),
        ROCKETMQ("rocketmq"),
    	KAFKA("kafka"),
    	RABBITMQ("rabbitmq");
    	
        private String name;

        private SourceType(String name) {
            this.name = name;
        }
        
        @JsonValue
        public String getName() {
            return name;
        }
    }
    
    public static enum KnowLedgeType{
        TrainOperator("训练算子"),
        TrainModel("训练模型");

        private String name;

        private KnowLedgeType(String name) {
            this.name = name;
        }
        
        @JsonValue
        public String getName() {
            return name;
        }
    }
    
    public static enum Category {
        BATCH("batch"),
        STREAM("stream");
        
        private static Map<String, Category> categoryMap;

        private String name;

        private Category(String name) {
            this.name = name;
            putCategory(name, this);
        }

        public static void putCategory(String name, Category category) {
            synchronized (Category.class) {
                if (categoryMap == null) {
                    categoryMap = new HashMap<String, Category>();
                }
                categoryMap.put(name, category);
            }
        }

        public static Category getCategory(String name) {
            return categoryMap.get(name);
        }

        @JsonValue
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    
    public static enum JobStatus{
    	PREPARE("未启动"),
    	RUNNING("正在运行"),
    	FAILED("失败"),
    	KILLED("杀掉"),
    	SUCCESS("成功");

        private String name;

        private JobStatus(String name) {
            this.name = name;
        }
        
        @JsonValue
        public String getName() {
            return name;
        }
    }
}
