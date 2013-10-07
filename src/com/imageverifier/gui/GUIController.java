package com.imageverifier.gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
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
    
	@FXML 
    private ProgressBar progress;
	
    @SuppressWarnings("unused")
	@FXML 
    private URL location;
  //  @FXML 
   // private ImageView thumb_image;
    
    
	@FXML 
    private TableView tree;
	
	
//	@FXML 
 //   private Button find;
//	@FXML 
//    private Button scan;
	
	@FXML 
	 private ListView<String> console;

	@FXML 
	 private ListView<String> dupFolders;
	@FXML 
	 private ListView<String> selectedFolders;
	
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
    
    ObservableList<String> data = FXCollections.observableArrayList(
            "./test/folder1/IMGP0039.JPG","./test/folder1/IMGP0040.JPG","./test/folder1/IMGP0049.JPG");

    
	@FXML
	private ListView<String> thumbnails;
    
    //Group thumbnails = new Group();
    DB db = new DB();
    
	 @SuppressWarnings( "unchecked" )
	@FXML // This method is called by the FXMLLoader when initialization is complete
	    void initialize() {
		 
		 dupFolders.getItems().addAll(data);
		 ObservableList<String> empty = FXCollections.observableArrayList();
		 selectedFolders.setItems(empty);
		 initializeThumbnails();
		 
		 
	       console.getItems().addListener(new ListChangeListener<String>() {

	            @Override public void onChanged(Change<? extends String> change) {

	                while (change.next()) {

	                    if (change.getList().size() > 20) change.getList().remove(0);

	                }

	            }

	        });
		 
		 
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
	        	duplicates = db.getDuplicates(drive, false); 
	        }catch(Exception e )
	        {
	        	return allData;
	        }
	        
	        LoadImagesCacheTask loadTask = new LoadImagesCacheTask();
	        loadTask.setDuplicates(duplicates);
	        new Thread(loadTask).start();
			
	       

	        LoadPathsTask loadPathTask = new LoadPathsTask();
	        loadPathTask.setDuplicates(duplicates);
	        new Thread(loadPathTask).start();
	        
	        dupFolders.itemsProperty().bind(loadPathTask.valueProperty());
	        
	        
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
	    	
	        ScanFolder scan = new ScanFolder();
	      //  progress.progressProperty().bind(scan.progressProperty());
	        new Thread(scan).start();
	    }
	    
	    
	    public void mark(ActionEvent event)
	    {
	    	
	    	System.out.println("Clicked");
	    	List <String> selected = selectedFolders.getItems();
	    	for(String item : selected)
	    	{
	    		try{
	    			System.out.println("Marking " + item);
	    			db.mark(item);	
	    		}catch (Exception e)
	    		{
	    			e.printStackTrace();
	    		}
	    		
	    		
	    	}
	    	
	    }
	    
	    public void verify(ActionEvent event)
	    {
	    }
	    
	    public void copy(ActionEvent event)
	    {
	    }
	    
	    public void findDuplicates(ActionEvent event)
	    {
	    	System.out.println("clicked");
	    	tree.setItems(generateDataInMap2(drive.getText()));
	    }
	    
	    public void selectFolders(ActionEvent event)
	    {
	    	
	           ObservableList<String> selected = 
	                FXCollections.observableArrayList( //copy
	                		dupFolders.getSelectionModel().getSelectedItems());
	            if (selected != null) {
	            	selectedFolders.getItems().addAll(selected);
	            	dupFolders.getItems().removeAll(selected);
	            	dupFolders.getSelectionModel().clearSelection();
	            }
	    	
	    	
	     }
	    public void removeFolderSelection(ActionEvent event)
	    {
	           ObservableList<String> selected = 
	                FXCollections.observableArrayList( //copy
	                		selectedFolders.getSelectionModel().getSelectedItems());
	            if (selected != null) {
	            	dupFolders.getItems().addAll(selected);
	            	selectedFolders.getItems().removeAll(selected);
	            	selectedFolders.getSelectionModel().clearSelection();
	            }
	    }
	    
	    static final Map<String,Image> imagesCache = new HashMap<String,Image>();
		 
	    class ScanFolder extends Task<Void>
	    {
	    	@Override
	    	protected Void call() throws Exception {
		    	try{
		    		FileWalker.walk("C",scan_directory.getText(), db,
		    				new Callback<Entry, Void>() {
		    				    			@Override
							    			public Void call(final Entry e) {
							    				System.out.println(e.getPath());
							    				try{
							    					db.insert(e);
							    				}catch(Exception ex)
							    				{
							    					ex.printStackTrace();
							    				}
							    				Platform.runLater(new Runnable() {
													
													@Override
													public void run() {
														// TODO Auto-generated method stub
														console.getItems().add( "Scaning: " + e.getPath() + e.getFilename());
									    						
													}
												}
							    				);
							            		return null;
							    			}
							    		}		
		    		);
		    	}catch(Exception e)
		    	{
		    		StringWriter sw = new StringWriter();
		    		PrintWriter pw = new PrintWriter(sw);
		    		e.printStackTrace(pw);
		    		console.getItems().add( sw.toString());
		    		
		    	}
		    	return null;
	    	}
	    }

        class LoadPathsTask extends Task<ObservableList<String>> {
       	 	Map<String,List<Entry>> duplicates;
       	
       	 	public void setDuplicates(Map<String, List<Entry>> duplicates) {
				this.duplicates = duplicates;
			}
       	 
	       	@Override
	       	protected ObservableList<String> call() throws Exception {
	       	    // List<Entry> images = (List<Entry>)row.get("IMAGES");
	       		 ObservableList<String> returnList = FXCollections.observableArrayList();
	    		 for(List<Entry> list: duplicates.values()){
	    			 for(Entry entry: list)
	    			 {
	    				 if (!returnList.contains(entry.getPath()))
	    				 {
	    					 returnList.add(entry.getPath());
	    				 }
	    			 }
	    		 }
	       		
	       		
	       		return returnList;
	       	}
		};
	    
        class LoadImagesCacheTask extends Task<Void> {
       	 	Map<String,List<Entry>> duplicates;
       	
       	 	public void setDuplicates(Map<String, List<Entry>> duplicates) {
				this.duplicates = duplicates;
			}
       	 
	       	@Override
	       	protected Void call() throws Exception {
	       	    // List<Entry> images = (List<Entry>)row.get("IMAGES");
	    		 for(List<Entry> list: duplicates.values()){
	    			 for(Entry entry: list)
	    			 {
	    				 imagesCache.put(entry.getPath() + entry.getFilename(),createImage(entry.getPath() + entry.getFilename()));
	    			 }
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
	    		data.clear();
	    		List<Entry> images = (List<Entry>)row.get("IMAGES");
	    		 if (row.get("THUMBS") == null)
	    		 {
		    		 //List<Group> thumbsCache = new ArrayList<Group>();
		    		 for(Entry item: images){
		    			// thumbsCache.add(createThumbNail(item));
		    			 data.add( item.getPath() + item.getFilename());
		    		 }
		    		// row.put("THUMBS", thumbsCache);
	    		 }
	    		 
	    		 
	    		
	    		
	    	}
	    }

								 
	  
	    private void initializeThumbnails()
	    {
	    	thumbnails.setItems(data);
			 
	    	thumbnails.setCellFactory(new Callback<ListView<String>, 
		            ListCell<String>>() {
		                @Override 
		                public ListCell<String> call(ListView<String> list) {
		                    return new ThumbnailCell();
		                }
		            }
		        );	 
	    }
	    
	    
		 static class ThumbnailCell extends ListCell<String> {
		        @Override
		        public void updateItem(String item, boolean empty) {
		            super.updateItem(item, empty);
		           
		            if (item != null) {
		            	Image img = imagesCache.get(item);
		            	if(img == null)
		            	{
		            		img = createImage(item);
		            	}
		            	ImageView image = new ImageView();
		    	    	image.setImage(img);
						image.setFitWidth(img.getWidth());
		    	    	image.setPreserveRatio(true);
		    	    	image.setSmooth(true);
		    	    	image.setCache(true);
		                setGraphic(image);
		                setTooltip(new Tooltip(item));
		            }
		        }
		    }
		 
		 public static Image createImage(String name)
		 {
			 return new Image("file:/"+ name,200,0,true,false);
		 }
}
