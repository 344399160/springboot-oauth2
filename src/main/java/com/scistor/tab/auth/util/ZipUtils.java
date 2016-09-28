package com.scistor.tab.auth.util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils{
    
    static final int BUFFER = 2048;   //数组大小，解压文件时用    
  
	/**
	 * 解压缩文件
	 * @param fileName  需要解压出了文件
	 * @param zipFile 要解压的zip文件
	 * @author WenSenlin
	 * @throws IOException 
	 */
	public static void unZip(File xmlFile, File zipFile, String fileName) throws IOException {
	    OutputStream out = new FileOutputStream(xmlFile);
	    FileInputStream fin = new FileInputStream(zipFile);
	    BufferedInputStream bin = new BufferedInputStream(fin);
	    ZipInputStream zin = new ZipInputStream(bin);
	    ZipEntry ze = null;
	    while ((ze = zin.getNextEntry()) != null) {
	        if (ze.getName().equals(fileName)) {
	            byte[] buffer = new byte[8192];
	            int len;
	            while ((len = zin.read(buffer)) != -1) {
	                out.write(buffer, 0, len);
	            }
	            out.close();
	            break;
	        }
	    }
	    zin.close();
	}
	

    /**
     * 创建ZIP文件
     * @param sourcePath 文件或文件夹路径
     * @param zipPath 生成的zip文件存在路径（包括文件名）
     */
    public static void createZip(String sourcePath, String zipPath) {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipPath);
            zos = new ZipOutputStream(fos);
            writeZip(new File(sourcePath), zos);
        } catch (FileNotFoundException e) {
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
            }

        }
    }
    
    private static void writeZip(File file, ZipOutputStream zos) {
        if(file.exists()){
            if(file.isDirectory()){//处理文件夹
                File [] files=file.listFiles();
                for(File f:files){
                    writeZip(f, zos);
                }
            }else{
                FileInputStream fis=null;
                try {
                    fis=new FileInputStream(file);
                    ZipEntry ze = new ZipEntry(file.getName());
                    zos.putNextEntry(ze);
                    byte [] content=new byte[1024];
                    int len;
                    while((len=fis.read(content))!=-1){
                        zos.write(content,0,len);
                        zos.flush();
                    }

                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }finally{
                    try {
                        if(fis!=null){
                            fis.close();
                        }
                    }catch(IOException e){
                    }
                }
            }
        }
    }
 }
