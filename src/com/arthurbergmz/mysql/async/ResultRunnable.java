package com.arthurbergmz.mysql.async;

/**
 * @author Arthur Arioli Bergamaschi
 */
public interface ResultRunnable<T> {
	
	public void onResult(T value);
	
}
