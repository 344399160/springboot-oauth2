package com.scistor.tab.auth.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FileSystemUtils {
    public static final int BUFSIZE = 1024 * 8;
    public static class FileSystemInitializer {
        private static FileSystem fs;

        static {
            try {
                fs = FileSystem.get(new Configuration());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static FileSystem getFileSystem() {
            return fs;
        }
    }

    public static FileSystem getFileSystem() {
        return FileSystemInitializer.getFileSystem();
    }


    public static Long upload(InputStream in, String hdfsPath) throws IllegalArgumentException, Exception {
    	FSDataOutputStream fsDataOutputStream = null;
    	if (existFile(hdfsPath)) {
    		fsDataOutputStream = getFileSystem().append(new Path(hdfsPath));
    	} else {
    		fsDataOutputStream = getFileSystem().create(new Path(hdfsPath));
    	}
        IOUtils.copyBytes(in, fsDataOutputStream, 4096, true);
        return getFileSystem().getFileStatus(new Path(hdfsPath)).getLen();
    }
    
  public static Long uploadAndDelete(InputStream in, String hdfsPath) throws IllegalArgumentException, Exception {
	FSDataOutputStream fsDataOutputStream = null;
	if (existFile(hdfsPath)) {
		removeFileFromHdfs(hdfsPath);
	}
	fsDataOutputStream = getFileSystem().create(new Path(hdfsPath));
    IOUtils.copyBytes(in, fsDataOutputStream, 4096, true);
    return getFileSystem().getFileStatus(new Path(hdfsPath)).getLen();
}
    
    public static void createFile(String hdfsPath) throws IllegalArgumentException, Exception {
    	if (existFile(hdfsPath)) {
    		getFileSystem().create(new Path(hdfsPath));
    	}
    }

    /**
     *获取文件大小
     **/
    public static Long getFileLen(String hdfsPath) throws IOException {
        return getFileSystem().getFileStatus(new Path(hdfsPath)).getLen();
    }
    
    /**
     *获取目录大小
     **/
    public static Long getDirLen(String hdfsPath) throws IOException {
        return (long)getFileSystem().listStatus(new Path(hdfsPath)).length;
    }

    /**
     * 创建目录
     * @param hdfs目录
     * @author WenSenlin
     */
    public static boolean mkdirs(String dirPath) throws Exception {
        boolean flag = getFileSystem().mkdirs(new Path(dirPath));
        return flag;
    }


    /**
     * 从hdfs上删除文件
     * @param hdfsPath hdfs文件
     * @author WenSenlin
     */
    public static void removeFileFromHdfs(String hdfsPath) throws Exception {
        getFileSystem().delete(new Path(hdfsPath), true);
    }


    /**
     * 获取hdfs文件的URI
     * @param hdfsPath hdfs文件
     * @author WenSenlin
     */
    public static String getPathUri(String hdfsPath) throws Exception {
        return getFileSystem().getUri() + hdfsPath;
    }

    /**
     * 从hdfs上下载目录
     * @param srcPath 远程hdfs目录
     * @param dstPath 本地目录
     * @author WenSenlin
     */
    public static void downloadFolder(String srcPath, String dstPath) throws Exception
    {
        File dstDir = new File(dstPath);
        if (!dstDir.exists())
        {
            dstDir.mkdirs();
        }
        FileStatus[] srcFileStatus = getFileSystem().listStatus(new Path(srcPath));
        Path[] srcFilePath = FileUtil.stat2Paths(srcFileStatus);
        for (int i = 0; i < srcFilePath.length; i++)
        {
            String srcFile = srcFilePath[i].toString();
            int fileNamePosi = srcFile.lastIndexOf('/');
            String fileName = srcFile.substring(fileNamePosi + 1);
            download(srcPath + '/' + fileName, dstPath);
        }
    }
    
    /**
     * 从hdfs获取目录下所有文件
     * @param srcPath 远程hdfs目录
     * @author WenSenlin
     */
    public static List<String> ListDir(String srcPath) throws Exception
    {
    	List<String> fileNameList = new ArrayList<String>();
        FileStatus[] srcFileStatus = getFileSystem().listStatus(new Path(srcPath));
        Path[] srcFilePath = FileUtil.stat2Paths(srcFileStatus);
        for (int i = 0; i < srcFilePath.length; i++)
        {
            String srcFile = srcFilePath[i].toString();
            int fileNamePosi = srcFile.lastIndexOf('/');
            String fileName = srcFile.substring(fileNamePosi + 1);
            fileNameList.add(fileName);
        }
        return fileNameList;
    }

    /**
     * 从hdfs上下载目录或者文件
     * @param srcPath 远程hdfs目录或者文件
     * @param dstPath 本地目录或者文件
     * @author WenSenlin
     */
    public static void download(String srcPath, String dstPath) throws Exception
    {
        if (getFileSystem().isFile(new Path(srcPath)))
        {
            downloadFile(srcPath, dstPath);
        }
        else
        {
            downloadFolder(srcPath, dstPath);
        }
    }

    /**
     * 从hdfs上下载文件
     * @param srcPath 远程hdfs文件
     * @param dstPath 本地文件
     * @author WenSenlin
     */
    public static void downloadFile(String srcPath, String dstPath) throws Exception
    {
        File dstDir = new File(dstPath);
        if (!dstDir.exists())
        {
            dstDir.mkdirs();
        }
        FSDataInputStream in = null;
        FileOutputStream out = null;
        try
        {
            in = getFileSystem().open(new Path(srcPath));
            int fileNamePosi = srcPath.lastIndexOf('/');
            String fileName = srcPath.substring(fileNamePosi + 1);
            out = new FileOutputStream(dstPath + "/" + fileName);
            IOUtils.copyBytes(in, out, 4096, false);
        }
        finally
        {
            IOUtils.closeStream(in);
            IOUtils.closeStream(out);
        }
    }
    
    /**
     * 从hdfs上下载文件
     * @param srcPath 远程hdfs文件
     * @param dstPath 本地文件
     * @author WenSenlin
     */
    public static void downloadSigFile(String srcPath, String dstPath) throws Exception
    {
        FSDataInputStream in = null;
        FileOutputStream out = null;
        try
        {
            in = getFileSystem().open(new Path(srcPath));
            out = new FileOutputStream(dstPath);
            IOUtils.copyBytes(in, out, 4096, false);
        }
        finally
        {
            IOUtils.closeStream(in);
            IOUtils.closeStream(out);
        }
    }

    public static String readStringFromHdfs(String path, int  offset, int readSize) throws IllegalArgumentException, IOException {
        FSDataInputStream in = getFileSystem().open(new Path(path));
        in.seek(offset * readSize);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        char []data = new char[readSize];
        int len = reader.read(data, 0, readSize);
        reader.close();
        if (len == -1) {
        	return "";
        }
        return new String(data, 0, len);
    }

    public static String readAllStringFromHdfs(String Path) throws IllegalArgumentException, IOException {
        FSDataInputStream in = getFileSystem().open(new Path(Path));
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String data = "";
        String dataTmp = "";
        while ((dataTmp = reader.readLine()) != null) {
            data += dataTmp;
        }
        return data;
    }

    public static void delete(String hdfsPath) throws IOException {
        getFileSystem().deleteOnExit(new Path(hdfsPath));
    }
    
    public static boolean existFile(String filePath) throws Exception {
		boolean existFile = false;
		Path filepath = new Path(filePath);
		if (getFileSystem().exists(filepath)) {
			existFile = true;
		}
		return existFile;
	}

    public static void mergeFiles(String dirPath) {
        String outFile = dirPath + ".txt";
        File inFiles = new File(dirPath);
        FileChannel outChannel = null;
        try {
            outChannel = new FileOutputStream(outFile).getChannel();
            for(File file : inFiles.listFiles()){
                FileChannel fc = new FileInputStream(file).getChannel();
                ByteBuffer bb = ByteBuffer.allocate(BUFSIZE);
                while(fc.read(bb) != -1){
                    bb.flip();
                    outChannel.write(bb);
                    bb.clear();
                }
                fc.close();
            }
            inFiles.delete();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {if (outChannel != null) {outChannel.close();}} catch (IOException ignore) {}
        }
    }
}
