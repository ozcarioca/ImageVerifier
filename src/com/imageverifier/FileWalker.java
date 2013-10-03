package com.imageverifier;

import java.io.File;

import javafx.util.Callback;

import org.apache.commons.io.FilenameUtils;

public class FileWalker {

    public static void walk( String drive,String path, DB db, Callback<Entry , Void> callback ) throws Exception{

        File root = new File( path );
        File[] list = root.listFiles();

        if (list == null) return;

        for ( File f : list ) {
            if ( f.isDirectory() ) {
                walk( drive, f.getAbsolutePath(), db, callback );
                System.out.println( "Dir:" + f.getAbsoluteFile() );
            }
            else {
            	String ext = FilenameUtils.getExtension(f.getName());
            	if ("jpg".equals(ext) || "JPG".equals(ext))
            	{
            		//System.out.println( "File:" + f.getAbsoluteFile() );
            		callback.call(new Entry(drive,f));
            		
            	}
            	
                
            }
        }
    }

}