package com.Team5427.Networking;

import java.io.Serializable;

public class Task implements Serializable{

	private TaskDescription t;
	private Object o;

	public Task(TaskDescription t) {
		this.t = t;
	}

	public Task(TaskDescription t, Object o) {
		this.t = t;
		this.o = o;
	}

	public TaskDescription getTask() {
		return t;
	}

	public Object getObject() {
		return o;
	}
	@Override
	public String toString(){
		return "Team 5427 - Task " + t.toString();
	}

}
