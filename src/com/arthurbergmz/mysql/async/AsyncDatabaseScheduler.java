package com.arthurbergmz.mysql.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Arthur Bergamaschi
 */
public class AsyncDatabaseScheduler {
	
	private ExecutorService a;
	
	public AsyncDatabaseScheduler(AsyncDatabase database){
		final String name = ("Connection thread standing for " + database.getUrl());
		this.a = Executors.newSingleThreadExecutor(new ThreadFactory(){
			@Override
			public Thread newThread(Runnable runnable){
				return new Thread(runnable, name);
			}
		});
	}
	
	public void run(Runnable runnable){
		this.a.execute(runnable);
	}
	
	public void close(){
		try{
			if(!this.a.awaitTermination(5L, TimeUnit.MINUTES)) System.out.println("Não foi possível finalizar todas as tarefas, já que isso levaria mais que o tempo limite: 5 minutos.");
		}catch (InterruptedException e){
			e.printStackTrace();
		}
	}
	
}
