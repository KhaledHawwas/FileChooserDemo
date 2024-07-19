package com.hawwas.videofilechooser;

public class FileInfo {
    //data class FileInfo(val name: String, val size: Long, val identifier: String)
    private String name;
    private long size;

   public  FileInfo(String name, long size) {
        this.name = name;
        this.size = size;
    }
}
