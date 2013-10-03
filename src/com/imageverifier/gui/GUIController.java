package com.imageverifier.gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import com.imageverifier.DB;
import com.imageverifier.Entry;
import com.imageverifier.FileWalker;

@SuppressWarnings("rawtypes")
public class GUIController {
	
	@SuppressWarnings("unused")
    @FXML 
    private ResourceBundle resources;
    
    @SuppressWarnings("unused")
	@FXML 
    private URL location;
  //  @FXML 
   // private ImageView thumb_image;
    
    @FXML 
    private HBox all_thumbs_container; 
    
    
	@FXML 
    private TableView tree;
	
	@FXML 
    private ScrollPane scroll;
	
//	@FXML 
 //   private Button find;
//	@FXML 
//    private Button scan;
	
	@FXML 
    private TextArea log;
	
	@FXML 
    private TextField drive;
	
	@FXML 
    private TextField scan_directory;
	
    @FXML
    TableColumn<Map, String> col_md5;
    @FXML
    TableColumn<Map, String> col_image1;
    @FXML
    TableColumn<Map, String> col_image2;
    
    
    Group thumbnails = new Group();
    DB db = new DB();
    Map<String, Image> imageCache = new HashMap<String, Image>();
    
	 @SuppressWarnings( "unchecked" )
	@FXML // This method is called by the FXMLLoader when initialization is complete
	    void initialize() {
		 
		// all_thumbs_container.get
		 scroll.setContent(all_thumbs_container);
		 col_md5.setCellValueFactory(new MapValueFactory("MD5"));
		 col_image1.setCellValueFactory(new MapValueFactory("IMAGE1"));
		 col_image2.setCellValueFactory(new MapValueFactory("IMAGE2"));
		 
		 
		 Callback<TableColumn<Map, String>, TableCell<Map, String>>
         cellFactoryForMap = new Callback<TableColumn<Map, String>,TableCell<Map, String>>() {
                 @Override
                 public TableCell call(TableColumn p) {
                     return new TextFieldTableCell(new StringConverter() {
                         @Override
                         public String toString(Object t) {
                             return t.toString();
                         }
                         @Override
                         public Object fromString(String string) {
                             return string;
                         }                                    
                     });
                 }
     };
     col_md5 .setCellFactory(cellFactoryForMap);
     col_image1.setCellFactory(cellFactoryForMap);
     col_image2.setCellFactory(cellFactoryForMap);

     EventHandler<MouseEvent> handler = new EventHandler<MouseEvent>() {
    	 public void handle(MouseEvent e) {
    		// e.
    		if (tree.getSelectionModel() != null){
    			

    			
    		 Map<String,Object> row = (Map)tree.getSelectionModel().getSelectedItem();
    		 System.out.println("clicked " + row.get("MD5"));
    		 Platform.runLater(new UpdateThumbnail(row));
    		 System.out.println("After run later");
    		 /*clearChildren(all_thumbs_container);
    		 List<Entry> images = (List<Entry>)row.get("IMAGES");
    		 if (row.get("THUMBS") == null)
    		 {
	    		 List<Group> thumbsCache = new ArrayList<Group>();
	    		 for(Entry item: images){
	    			 thumbsCache.add(createThumbNail(item));
	    		 }
	    		 row.put("THUMBS", thumbsCache);
    		 }
    		 
    		 all_thumbs_container.getChildren().addAll(( List<Group>)row.get("THUMBS"));
    		*/
    		 }
    	 		}
     		};
     		tree.setOnMouseClicked(handler );
     
   //  find.setOnAction()
     
	 }	
	 
	    private ObservableList<Map> generateDataInMap2(String drive) {
	        ObservableList<Map> allData = FXCollections.observableArrayList();
	        Map<String,List<Entry>> duplicates;
	        try{
	        	duplicates = db.getDuplicates(drive); 
	        }catch(Exception e )
	        {
	        	return allData;
	        }
	        
	        LoadImagesCacheTask loadTask = new LoadImagesCacheTask();
	        loadTask.setDuplicates(duplicates);
	        new Thread(loadTask).start();
			
	        
	        for (java.util.Map.Entry<String,List<Entry>> entry : duplicates.entrySet()) {
	        	System.out.println(entry.getKey() + " - " + entry.getValue());
                Map<String, Object> dataRow = new HashMap<String, Object>();
	 
	 
	            dataRow.put("MD5", entry.getKey());
	            dataRow.put("IMAGES", entry.getValue());
	            
	    		/* List<Entry> entries = entry.getValue();
	    		 List<Group> thumbsCache = new ArrayList<Group>();
	    		 for(Entry item: entries){
	    			 thumbsCache.add(createThumbNail(item));
	    		 }
	            dataRow.put("THUMBS", thumbsCache);
	       	 */
	            dataRow.put("THUMBS", null);
	            allData.add(dataRow);
	        }
	        
	        
	        return allData;
	    } 
	
	    private Group createThumbNail(Entry entry)
	    {
	    	Group g = new Group();
	    	VBox vbox = new VBox();
	    	ImageView image = new ImageView();
	    	image.setImage(new Image("file:/"+ entry.getPath() + entry.getFilename()));
	    	image.setFitWidth(250);
	    	image.setFitHeight(250);
	    	image.setPreserveRatio(true);
	    	image.setSmooth(true);
	    	image.setCache(true);
	    	vbox.getChildren().add(image);
	    	
	    	g.getChildren().add(vbox);
	    	return g;
	    }
	    
	    
	    private void clearChildren(Pane node)
	    {
	    	int size = node.getChildrenUnmodifiable().size();
	    	for (int i = 0 ; i < size; i++) {
	    		node.getChildren().remove(0);
			}
	    }
	    
	    public void scanDirectory(ActionEvent event)
	    {
	    	try{
	    		FileWalker.walk("C",scan_directory.getText(), db,
	    				new Callback<Entry, Void>() {
	    				    			@Override
						    			public Void call(Entry e) {
						    				System.out.println(e.getPath());
						    				try{
						    					db.insert(e);
						    				}catch(Exception ex)
						    				{
						    					ex.printStackTrace();
						    				}
						    				log.setText(log.getText() + "\n" + e.getPath());
						            		return null;
						    			}
						    		}		
	    		);
	    	}catch(Exception e)
	    	{
	    		StringWriter sw = new StringWriter();
	    		PrintWriter pw = new PrintWriter(sw);
	    		e.printStackTrace(pw);
	    		
	    		log.setText(sw.toString());
	    	}
	    }
	    
	    public void findDuplicates(ActionEvent event)
	    {
	    	System.out.println("clicked");
	    	tree.setItems(generateDataInMap2(drive.getText()));
	    }
	    
	    
        class LoadImagesCacheTask extends Task<Void> {
       	 	Map<String,List<Entry>> duplicates;
       	
       	 	public void setDuplicates(Map<String, List<Entry>> duplicates) {
				this.duplicates = duplicates;
			}
       	 
	       	@Override
	       	protected Void call() throws Exception {
	       		imageCache.put("", null);
	       	     List<Entry> images = (List<Entry>)row.get("IMAGES");
	    		 List<Group> thumbsCache = new ArrayList<Group>();
	    		 for(Entry item: images){
	    			 thumbsCache.add(createThumbNail(item));
	    		 }
	       		
	       		
	       		return null;
	       	}
		};
	   
	    class UpdateThumbnail implements Runnable
	    {
	    	Map row;
	    	
	    	public UpdateThumbnail(Map row) {
		    	this.row = row;
			}
	    	
	    	@SuppressWarnings("unchecked")
			@Override
	    	public void run() {
	    		System.out.println("running run later");
	    		 clearChildren(all_thumbs_container);
	    		 List<Entry> images = (List<Entry>)row.get("IMAGES");
	    		 if (row.get("THUMBS") == null)
	    		 {
		    		 List<Group> thumbsCache = new ArrayList<Group>();
		    		 for(Entry item: images){
		    			 thumbsCache.add(createThumbNail(item));
		    		 }
		    		 row.put("THUMBS", thumbsCache);
	    		 }
	    		 
	    		 all_thumbs_container.getChildren().addAll(( List<Group>)row.get("THUMBS"));
	    		
	    		
	    	}
	    }

	    
}
