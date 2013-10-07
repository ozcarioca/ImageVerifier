package com.imageverifier;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;


public class Entry
{
	private String path;
	private String filename;
	private String md5;
	private String drive;
	private long size;
	private boolean duplicate;
	
	public Entry(String line) {
		String[] split = line.split(",");
		path = split[0];
		filename = split[1];
		md5 = split[2];
	}
	
	public Entry(String drive, File file) throws Exception{
		FileInputStream fis = new FileInputStream(file);
		this.md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
		this.path = FilenameUtils.separatorsToUnix(FilenameUtils.getPath(file.getPath()));
		this.filename = file.getName().toLowerCase();
		this.drive = drive;
		this.size = FileUtils.sizeOf(file);
		//System.out.println(FilenameUtils.separatorsToUnix(f.getAbsolutePath()));
	}	
	
	public Entry(String drive, String filename, String path, String md5, long size, boolean dup) throws Exception{
		this.md5 = md5;
		this.path = path;
		this.filename = filename;
		this.drive = drive;
		//System.out.println(FilenameUtils.separatorsToUnix(f.getAbsolutePath()));
	}	
	
	@Override
	public boolean equals(Object obj) {
		return md5.equals(((Entry)obj).md5);
	}
	
	
	@Override
	public int hashCode() {
		return path.hashCode();
	}



	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getDrive() {
		return drive;
	}

	public void setDrive(String drive) {
		this.drive = drive;
	}
	
	public boolean isDuplicate() {
		return duplicate;
	}
	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}
	
	public long getSize() {
		return size;
	}
	
	public void setSize(long size) {
		this.size = size;
	}
	
}