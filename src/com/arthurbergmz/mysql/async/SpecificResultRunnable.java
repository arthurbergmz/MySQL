package com.arthurbergmz.mysql.async;

/**
 * @author Arthur Arioli Bergamaschi
 */
public abstract class SpecificResultRunnable<T> implements ResultRunnable<T> {
	
	private Class<? extends T> type;
	
	public SpecificResultRunnable(Class<? extends T> typeClass){
		this.type = typeClass;
	}
	
	public abstract void onResult(T value);
	
	public Class<? extends T> getTypeClass(){
		return this.type;
	}
	
}
