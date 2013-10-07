package com.imageverifier.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;


public class ThumbnailController {

	@FXML
	private ListView<String> container;
	@SuppressWarnings("unused")
    @FXML 
    private ResourceBundle resources;
    
    @SuppressWarnings("unused")
	@FXML 
    private URL location;

    ObservableList<String> data = FXCollections.observableArrayList(
            "./test/folder1/IMGP0039.JPG","./test/folder1/IMGP0040.JPG","./test/folder1/IMGP0049.JPG");
    @FXML
	 void initialize() {
		 container.setItems(data);
		 
		 container.setCellFactory(new Callback<ListView<String>, 
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
	            //Rectangle rect = new Rectangle(100, 20);
	           
	            if (item != null) {
	            	ImageView image = new ImageView();
	    	    	image.setImage(new Image("file:"+ item,100,0,true,false));

	    	    	image.setPreserveRatio(true);
	    	    	image.setSmooth(true);
	    	    	image.setCache(true);
	            	// ImageView img = new ImageView(new Image("file:/" + item));
	               
	                setGraphic(image);
	            }
	        }
	    }
	 
	    public void check(MouseEvent event)
	    {
	    	if (data.size() > 0)
	    	{
	    		data.clear();
	    	}
	    	else
	    	{
	    		data.add("./test/folder1/IMGP0039.JPG");
	    		data.add("./test/folder1/IMGP0040.JPG");
	    	}
	    }
	 
}
