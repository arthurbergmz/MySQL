package com.arthurbergmz.mysql.async;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.arthurbergmz.mysql.Database;

/**
 * @author Arthur Bergamaschi
 */
public class AsyncDatabase extends Database {
	
	private AsyncDatabaseScheduler scheduler;
	private Connection connection;
	
	public AsyncDatabase(String host, int port, String username, String password, String database){
		super(host, port, username, password, database);
		System.out.println("[" +  this.getHost() + ":" + this.getPort() + "] Estabelecendo conexão com o banco de dados...");
		this.scheduler = new AsyncDatabaseScheduler(this);
		this.connection = this.openConnection();
	}
	
	private Connection openConnection(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			this.connection = DriverManager.getConnection(this.getUrl() + "?autoReconnect=true", this.getUsername(), this.getPassword());
		}catch (SQLException e){
			System.out.println("Não foi possível conectar ao servidor MySQL! Motivo: " + e.getMessage());
			e.printStackTrace();
		}catch (ClassNotFoundException e){
			System.out.println("Driver não encontrado: " + e.getMessage());
			e.printStackTrace();
		}
		return this.connection;
	}
	
	public Connection getConnection(){
		try{
			if((this.connection == null) || (!this.connection.isValid(10))) this.connection = this.openConnection();
		}catch (SQLException e){
			e.printStackTrace();
			System.out.println("Não foi possível adquirir conexão com o banco de dados!");
		}
		return this.connection;
	}
	
	public void run(final String info, final DatabaseRunnable databaseRunnable){
		this.scheduler.run(new Runnable(){
			@Override
			public void run(){
				try {
					databaseRunnable.run(AsyncDatabase.this.getConnection());
				}catch (SQLException e) {
					e.printStackTrace();
					System.out.println("Ocorreu um erro ao executar uma tarefa SQL. Informações sobre a tarefa: \"" + info + "\"" + ". Exception message: \"" + e.getMessage() + "\". Realizando nova tentativa...");
					try {
						AsyncDatabase.this.closeConnection();
						databaseRunnable.run(AsyncDatabase.this.getConnection());
					}catch (SQLException ex){
						e.printStackTrace();
						System.out.println("Ocorreu um erro ao executar uma tarefa SQL. Informações sobre a tarefa: \"" + info + "\"" + ". Exception message: \"" + e.getMessage() + "\". Uma nova tentativa não será realizada!");
					}
				}
			}
		});
	}
	
	public <T> void run(final String info, final ResultDatabaseRunnable<T> resultDatabaseRunnable, final ResultRunnable<T> resultRunnable){
		this.scheduler.run(new Runnable(){
			@Override
			public void run(){
				ResultHolder<T> result = new ResultHolder<T>();
				try{
					resultDatabaseRunnable.run(AsyncDatabase.this.getConnection(), result);
				}catch (SQLException e){
					e.printStackTrace();
					System.out.println("Ocorreu um erro ao executar uma tarefa SQL. Informações sobre a tarefa: \"" + info + "\"" + ". Exception message: \"" + e.getMessage() + "\". Realizando nova tentativa...");
					try {
						AsyncDatabase.this.closeConnection();
						resultDatabaseRunnable.run(AsyncDatabase.this.getConnection(), result);
					}catch (SQLException ex){
						e.printStackTrace();
						System.out.println("Ocorreu um erro ao executar uma tarefa SQL. Informações sobre a tarefa: \"" + info + "\"" + ". Exception message: \"" + e.getMessage() + "\". Uma nova tentativa não será realizada!");
					}
				}finally{
					AsyncDatabase.this.runSync(resultRunnable, result.getResult());
				}
			}
		});
	}
	
	public synchronized <T> void runSync(final ResultRunnable<T> resultRunnable, final T result){
		if(resultRunnable != null) resultRunnable.onResult(result);
	}
	
	public void closeConnection(){
		if(this.connection == null){
			System.out.println("Não há para serem encerradas!");
		}else{
			try{
				this.connection.close();
				this.connection = null;
			}catch (SQLException e){
				e.printStackTrace();
				System.out.println("Ocorreu um erro ao encerrar a conexão com um banco de dados!");
			}
		}
	}
	
	public void closeQueue(){
		this.scheduler.close();
	}
	
}
