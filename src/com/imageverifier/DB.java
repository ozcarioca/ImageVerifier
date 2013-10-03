package com.imageverifier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DB {
	
	Connection connection = null;
	
	
	public static void main(String[] args) throws Exception{
		DB db =  new DB();
		db.deleteAll();
	}
	public DB() {
		
		System.out.println("-------- Initializing Connection ------------");
 
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Unable to load driver...");
			e.printStackTrace();
			return;
		}
 
 
		try {
			connection = DriverManager.getConnection(
					"jdbc:h2:file:./db/files", "sa",
					"");
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}
 
		if (connection != null) {
			System.out.println("Connection successful.");
		} else {
			System.out.println("Failed to make connection!");
		}
	}
	
	public void insert (Entry entry) throws Exception
	{
		String sql = "insert into file (drive, path, filename, md5) VALUES ('"+entry.getDrive()+"', '"+entry.getPath()+"','"+entry.getFilename()+"','"+entry.getMd5()+"')";
		System.out.println(sql);
		Statement stmt = connection.createStatement();
		stmt.execute(sql);
		stmt.close();
	}
	
	public void deleteAll () throws Exception
	{
		String sql = "delete from file";
		Statement stmt = connection.createStatement();
		stmt.execute(sql);
		stmt.close();
	}	
	
	public Map<String,List<Entry>> getDuplicates(String drive) throws Exception
	{
		String sql = "select path, filename, drive, md5 from file where drive = '"+drive+"' and  md5 in ( " +
						" select md5 from file group by md5 having count(*) >= 2 "+
						")";
		return group(implGetData(sql));
	}
	
	public List<Entry> getAll() throws Exception
	{
		String sql = "select path, filename, drive, md5 from file ";
		return implGetData(sql);
	}
	
	private Map<String,List<Entry>> group(List<Entry> entries)
	{
		Map<String,List<Entry>> map = new HashMap<String,List<Entry>> ();
		for(Entry entry: entries)
		{
			List<Entry> temp = map.get(entry.getMd5());
			if (temp == null)
			{
				temp = new ArrayList<Entry>();
				map.put(entry.getMd5(),temp);
			}
			temp.add(entry);		
		}
		return map;
	}
	
	private List<Entry> implGetData(String sql) throws Exception
	{
		Statement stmt = connection.createStatement();
		ResultSet rs =  stmt.executeQuery(sql);
		List<Entry> returnList = new ArrayList<Entry>();
		while (rs.next())
		{
			Entry entry = new Entry(rs.getString("drive"),rs.getString("filename"), rs.getString("path"),rs.getString("md5"));
			returnList.add(entry);
		}
		rs.close();
		stmt.close();
		return returnList;		
	}
	
}
