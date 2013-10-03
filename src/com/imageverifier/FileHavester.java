package com.imageverifier;

import java.io.File;
import java.util.Collection;

import javafx.util.Callback;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class FileHavester {

	private static DB db = new DB();
	public static void main(String[] args) {
		try{
			//db.deleteAll();
			//havestFile("./test/folder1");
			//havestFile("./test/folder2");
			//harvestFile("C:/Users/Guilherme");
			
			//recursiveHarvest("E","E:/Dados/Fotos");
			
			//recursiveHarvest("C","C:/Users/Guilherme/Pictures");
			//recursiveHarvest("C","./images");

			recursiveHarvest("C","./test");
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	private static void recursiveHarvest(String drive,String folder) throws Exception
	{
		FileWalker.walk(drive,folder, db, new Callback<Entry, Void>() {
			
			@Override
			public Void call(Entry e) {
				System.out.println(e.getPath());
				try{
					db.insert(e);
				}catch(Exception ex)
				{
					ex.printStackTrace();
				}
        		return null;
			}
		});
	}
	
	/*private static void harvestFile(String drive,String folder) throws Exception
	{
			
			Collection<File>  files =  FileUtils.listFiles(new File(folder), new String[] {"jpg","JPG"}, true);

			System.out.println("Total size: " + files.size());
			long i = 0;
			for (File file : files)
			{
				double prog = i++*100/files.size();
				System.out.print("\r" + prog + "%");
				db.insert(new Entry(drive,file));
			}
		
	}*/
}
