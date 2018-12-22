package org.octank.claims.model;

public class Log implements java.io.Serializable {
	
	private static final long serialVersionUID = 6482975535673193430L;

	private String eventType;
	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getEventTmst() {
		return eventTmst;
	}

	public void setEventTmst(String eventTmst) {
		this.eventTmst = eventTmst;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	private String eventTmst;
	
	private Object object;
	
	private String objectType;
	
	private String context;
	
	private String level;
	
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public Log() {
		
	}
	

}
