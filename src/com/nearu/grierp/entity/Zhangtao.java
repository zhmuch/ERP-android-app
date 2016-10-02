package com.nearu.grierp.entity;

import java.util.ArrayList;

public class Zhangtao {
	private String name = null;
	private ArrayList<String> moduleList = null;
	
	public Zhangtao() {
		moduleList = new ArrayList<String>();
		for(int i = 0; i < 10; i++) {
			moduleList.add("moulde " + i);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getModuleList() {
		return moduleList;
	}

	public void setModuleList(ArrayList<String> moduleList) {
		this.moduleList = moduleList;
	}
	
}
