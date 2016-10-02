package com.nearu.grierp.entity;

public class ModuleEntity {
	private String moduleName;
	private int moduleImg;
	private int requiredLevel;
	public  ModuleEntity(String _moduleName, int img ) {
		moduleName = _moduleName;
		moduleImg = img;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public int getModuleImg() {
		return moduleImg;
	}
	public void setModuleImg(int moduleImg) {
		this.moduleImg = moduleImg;
	}
	public int getRequiredLevel() {
		return requiredLevel;
	}
	public void setRequiredLevel(int requiredLevel) {
		this.requiredLevel = requiredLevel;
	}
}
