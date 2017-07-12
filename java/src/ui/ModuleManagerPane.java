package ui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.TitledPane;

/**
 * A Titled pane which will be shown in the manager gui window. 
 * The fxml file located at the same location and with the same name as the invoking superclass is loaded.
 * 
 * @author Kilian
 */
public abstract class ModuleManagerPane extends TitledPane {

	public ModuleManagerPane(String description,Class<? extends ModuleManagerPane> subClass) {
		
		this.setText(description);
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);	
		loader.setRoot(this);
		loader.setLocation(getClass().getResource(subClass.getSimpleName()+".fxml"));	
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
