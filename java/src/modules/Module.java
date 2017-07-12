package modules;



import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import javafx.event.EventHandler;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Every extension is implemented as a module. A module can only run once at a time and has the ability to send 
 * data back and forth
 * @author Kilian
 *
 */
public abstract class Module {

	protected String name;					//The descriptive name of the module used by the manager gui to display running info
	protected String shortName;  			//Manager gui tabbed pane title
	protected TitledPane managerPanel;		//The panel which will be sent displayed in the manager gui; No Panel if null
	protected ModuleManager moduleManager;	//Reference to the moduel manager to implement global listeners or send notofications to other modules

	protected boolean isActive = false;		//Applications gets set active and inactive by the module manager. Display hide window and cease operation
	protected boolean isHalted = false;		//Temporarily cease operation. No need to destroy external gui windows
	
	private Module self;
	
	public Module(ModuleManager moduleManager, String name, String shortName) {
		self = this;
		this.moduleManager = moduleManager;
		this.name = name;
		this.shortName = shortName;
	}
	
	/**
	 * Toggle the functionality of the module. Inactivity is not defined as destorying every gui window
	 * @param active
	 */
	protected abstract void setActive();
	protected void haltUpdate() {
		System.out.println("Module: " + this.getName() + " HALT UPDATED " + " not implemented.");
	};
	
	
	
	public boolean isActive(){
		return isActive;
	}
	
	/**
	 * Max return null if no gui panel is present at the manager.
	 * The manager panel has to be initalized in the modules constructor
	 * @return
	 */
	public final TitledPane getManagerGuiPanel(){
		return managerPanel;
	};

	public String getName(){
		return name;
	}
	
	public String getShortName(){
		return shortName;
	}
	
	
	/**
	 * We could do some ugly reflection invocation here to avoid the single cast later on and actually spawn the window here
	 * protected <E extends JFrame> E spawnExternalWindow(Class<E> frame)
	 * @param frame
	 * @return
	 */
	protected void attachWindowListener(JFrame frame) {
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				moduleManager.moduleWindowClose(self);
			}
		});
	}
	
	protected void attachWindowListener(Stage stage) {
		stage.setOnCloseRequest(new EventHandler<javafx.stage.WindowEvent>() {
			@Override
			public void handle(javafx.stage.WindowEvent event) {
				moduleManager.moduleWindowClose(self);
			}
		});
	}
	
	
	/**
	 * Override if you need to pass events between certain modules.
	 * @param sender
	 * @param id
	 * @param value
	 */
	protected void handleCustomEvent(Module sender, ModuleEvent id, Object value) {
		System.out.println("Module: " + this.getName() + " " + id.CUSTOM_EVENT.name() + " not implemented.");
		System.out.println("Message: " + sender.getName() + " " + value.toString());
	}
	
	public void receiveUpdate(Module sender, ModuleEvent id,Object value) {
		
		switch(id) {
			case HALT_OPERATION :  
				isHalted = true;
				haltUpdate();
				break;
			case RESUME_OPERATION :
				isHalted = false;
				haltUpdate();
				break;
			case EVENT_HIDE_WINDOW :
				if(!isActive)
					return;
				isActive = false;
				setActive();
				break;
			case EVENT_DISPLAY_WINDOW :  
				if(isActive)
					return;
				isActive = true;
				setActive();
				break;
			case CUSTOM_EVENT : 
				handleCustomEvent(sender,id,value);
				break;
		}
		
		
	}
	
	
}
