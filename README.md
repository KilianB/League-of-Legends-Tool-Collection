# League-of-Legends-Tool-Collection

League of legends tool collection of scripts I have written over the years. All modules are written in java with the help of quering some external apis and looking at the log files the client produces. No memory reading or process hooking takes place. All windows are simple transparent overlay windows or windows overlayed with OBS Studio's chroma key filter.

<p align="center">
   <img height="700" src="https://user-images.githubusercontent.com/9025925/28111940-835a3172-66f7-11e7-9890-846564d63f0b.png"/>
</p>

A sample rundown of the faetures can be found in this youtube video:


[![Youtube Video](https://user-images.githubusercontent.com/9025925/28113713-fb5d3d9e-66fd-11e7-901d-fc78c9c34c30.png)](https://www.youtube.com/watch?v=8fZTSRDxpeA
 "Video Title")

### Implemented modules

## Animated loading screen

  - animated loading screen for almost every champion
  - live elo retrival utilizing the riot api
  - common item paths from champion.gg
  - lore text to speech utilizing google tts

![thumb](https://user-images.githubusercontent.com/9025925/28111814-16f7ee3e-66f7-11e7-940c-7c386c2630ca.jpg)

<p align="center">
<image src="https://user-images.githubusercontent.com/9025925/28114781-920981c2-6702-11e7-8945-37dd82afefe7.png" />
</p>

## Heatmap

  - create custom gradients
  - display a heatmap based on mouse clicks, mouse position 
  - overlay over background image
  - export image

![heatmap](https://user-images.githubusercontent.com/9025925/28112067-fb3d2104-66f7-11e7-80d6-4350f7b7a75b.png)
Image ovelay - transparent gradient
![heatmapmouseclicks](https://user-images.githubusercontent.com/9025925/28112005-c2dee3a6-66f7-11e7-99f4-ce512292125a.png)
Custom on the fly gradient
![heatmaps](https://user-images.githubusercontent.com/9025925/28112015-c810662e-66f7-11e7-8b47-7058e86ae54a.png)
Mouse movement

## League Chat To Teamspeak Text To Speech

.
.
.

<p align="center">
<image src="https://user-images.githubusercontent.com/9025925/28114849-e20d3e7a-6702-11e7-9a63-b376708b9c08.png" />
</p>

## Auto login and decay manager
- Displays the days until an account will deacay in every queue. 
- A click in the login cell will terminate the currently open lol client process, open a new instance 
	and move the mouse to the username and password field to type in the correct values as well as submitting the query with enter

<p align="center">
<img src="https://user-images.githubusercontent.com/9025925/28115647-122bee00-6706-11e7-83de-51bee32b4f46.jpg" />
</p>


## Automatic rune and mastery switcher based on picked champion
.
.
.

## OBS Studio League Scene Switcher
.
.
.
## Functional Twitch Chat Overlay
 - Functional Twitch Chat overlay
 - Toggle Chat with CRTL Enter
 - Choose Background Alpha
 - Custom League Emoticions
 - Twitch Emoticon Support

## Ability Count Overlay
.
.
.
<p align="center">
<image src="https://user-images.githubusercontent.com/9025925/28114792-9cd2a282-6702-11e7-8eda-00970cb859ea.png" />
</p>



## APM And Mouse Distance Ovrlay
.
.
.
<p align="center">
<image src="https://user-images.githubusercontent.com/9025925/28114833-d01f1436-6702-11e7-9caa-a7d854c84fbf.png" />
</p>

## Keyboard And Mouse Overlay
.
.
.

## League Group Chat Mirror Bot
 - mirrors all messages written to the bot to all users on the friendlist
 - global and local mute list
 - moderator commands to bann users and filter keywords
 - game search feature. !lf ELO (MODIFIER) MESSAGE will send a message to every person joining the chatroom (comming online or leaving a game). Once !full is called, the lobby owner joins the queue or the lobby is destroyed the search is deemed invalid.
 - Whisper feature for members
 - Auto accepts friend requests
 - Multi bot and region support allows chats between different regions, and gets rid of chat room size limitations.

http://liga-der-gentlemen.de/index.php/Thread/2965-Gruppensuche-2-0



### Modular Management Gui

The management gui is a sceleton javafx application which allows individual modules to register itself for easy extensibility.
It provides global information regarding keyboard and mouse states, the league game state, the current username 
and champion, the path to the league directory and initalizes Apis (caching) and logging facades. 


#### Hello World Module

A module usually consits out of 3 components
1. A class which extends the Module class and takes care of the logic
2. (Optional) a javafx pane which will be displayed in the manager gui for user settings 
3. (Optional) a javafx/swing windows which will be spawned once the user actives the module

<p align="center>
<img src="https://user-images.githubusercontent.com/9025925/28113267-36781b58-66fc-11e7-9f81-a277d206d5da.png" />
</p>

```java
public class HelloWorldModule extends Module /* optional */ implements GlobalKeyListener, LolGameEventListener {
	
	public HelloWorldModule(ModuleManager moduleManager) {
		super(moduleManager, "Module Long Name", "Module Short Name");
		this.managerPanel = new HelloWorldPane(shortName);
    
    		//Do we want to work with apis? Simply create an new instance whenever needed
    		RiotAPI riot = new RiotAPI();
   		OpGGAPI = opGG = new OpGGAPI();
    		GoogleTTS tts = new GoogleTTS();
	}

	/**
	 * Gets called once the user clicks on the chatbox in the gui manager
	 * 
	 */
	@Override
	protected void setActive() {
		
		//optional
		if(isActive) {
			//Do we want key and mouse listener?
			moduleManager.registerGlobalKeyboardListener(this);
			//Do we want to be notified if the user joins a game?
			moduleManager.registerLoLGameEventListener(this);
			//Spawn overlay window
		}else {
			//Do we want key and mouse listener?
			moduleManager.unregisterGlobalKeyboardListener(this);
			//Do we want to be notified if the user joins a game?
			moduleManager.unregisterLoLGameEventListener(this);
			//Hide overlay window
		}
		
	}

	//Do event based logic 
	
	@Override
	public void CurrentGameDataReady(CurrentGameInfo gameInfo) {}

	@Override
	public void LoadingScreenEntered(String[] champNames, int mapID) {}

	@Override
	public void GameEntered() {}

	@Override
	public void GameEndScreenShown() {}

	@Override
	public void GameWindowExited() {}

	@Override
	public void keyPressed(GlobalKeyEvent arg0) {}

	@Override
	public void keyReleased(GlobalKeyEvent arg0) {}
	
}
```
###### HelloWorldPane.java

Pane which will be integrated into the manager

```java
//Controller class for the javafx pane
public class HelloWorldPane extends ModuleManagerPane{

	@FXML 
	private Button btn;
	
	public HelloWorldPane(String description) {
		//Load fxml and set controler
		super(description, HelloWorldPane.class);
		
		btn.setOnAction((event) -> System.out.println("Hello World"));
	}
}
```
###### HelloWorldPane.fxml

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import java.lang.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.layout.AnchorPane?>

<fx:root maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" type="TitledPane" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1">
   <content>
      <Button fx:id="btn" mnemonicParsing="false" text="Button" />
   </content>
</fx:root>
```

### Inter module communication

Modules have the ability to send requests to other modules without explizitly knowing of them. For example the login manager asks all modules
which keep track of keystrokes to halt operation for the duration the password is typed in.
```java
public HelloWorldModule(ModuleManager moduleManager) {
	super(moduleManager, "Module Long Name", "Module Short Name");
	this.managerPanel = new HelloWorldPane(shortName);
			
	//Suggest every module which implements GLobalKeyListener to halt operation
	moduleManager.notifyModuleGeneric(GlobalKeyListener.class, this, ModuleEvent.HALT_OPERATION, null);
		
	//Suggest the key board ovrlay module to halt operation
	moduleManager.notifyModule(KeyBoardMouseOverlayModule.class, this, ModuleEvent.CUSTOM_EVENT, new Object[] {"Custom value"});	
}
```



