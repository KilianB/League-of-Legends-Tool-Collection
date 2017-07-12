package modules;

public enum ModuleEvent {

	HALT_OPERATION(1),
	RESUME_OPERATION(2),
	EVENT_HIDE_WINDOW(3),
	EVENT_DISPLAY_WINDOW(4),
	CUSTOM_EVENT(5);
	
	private final int value;
	
	private ModuleEvent(int value) {
		this.value = value;
	}
	
}
