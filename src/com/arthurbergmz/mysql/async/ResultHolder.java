package com.arthurbergmz.mysql.async;

/**
 * @author Arthur Arioli Bergamaschi
 */
public class ResultHolder<T> {
	
	private T result;
	
	public ResultHolder(T object){
		this.result = object;
	}
	
	public ResultHolder(){
		this.result = null;
	}
	
	public void setResult(T result){
		this.result = result;
	}
	
	public T getResult(){
		return this.result;
	}
	
}
