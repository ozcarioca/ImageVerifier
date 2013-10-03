package com.imageverifier;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Main {
	
	public static void main(String[] args) {
		try{
		generateFileList("./test/folder1", "folder1.txt");
		generateFileList("./test/folder2", "folder2.txt");
		
		List<Entry> entries1 = readEntries("folder1.txt");
		List<Entry> entries2 = readEntries("folder2.txt");
		
		diff(entries1,entries2);
		diff(entries2,entries1);
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	private static void diff (List<Entry> list1, List<Entry> list2)
	{
		for(Entry entry: list1)
		{
			if (!list2.contains(entry)){
				System.out.println("Entry not found: " + entry.getPath());
			}
		}
	}
	
	
	private static List<Entry> readEntries(String filename) throws Exception
	{
		List<String> lines = FileUtils.readLines(new File(filename));
		List<Entry> entries1 = new ArrayList<Entry>();
		for(String line : lines)
		{
			Entry entry = new Entry(line);
			if(entries1.contains(entry))
			{
				System.out.println("Duplicate entry:" + entry.getPath());
			}
			entries1.add(entry);
		}
		return entries1;
	}
	
	
	private static void generateFileList(String folder, String outputFile)
	{
		try {
			Collection<File>  files =  FileUtils.listFiles(new File(folder), new String[] {"jpg","JPG"}, true);
			File output = new File(outputFile);
			FileUtils.deleteQuietly(output);
			System.out.println("Total size: " + files.size());
			long i = 0;
			for (File file : files)
			{
				double prog = i++*100/files.size();
				
				System.out.print("\r" + prog + "%");
				FileUtils.writeStringToFile(output, printFileDesc(file) + "\n", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private static String printFileDesc(File file) throws Exception
	{
		FileInputStream fis = new FileInputStream(file);
		String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
		
		return file.getPath() + "," + file.getName().toLowerCase() + ","  + md5;
	}
	


}
