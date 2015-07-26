package com.arthurbergmz.mysql;

import java.sql.Connection;

/**
 * @author Arthur Bergamaschi
 */
public abstract class Database {
	
	private String host;
	private int port;
	private String user;
	private String password;
	private String database;
	private String url;
	
	public Database(String host, int port, String username, String password, String database){
		this.host = host;
		this.port = port;
		this.user = username;
		this.password = password;
		this.database = database;
		this.url = ("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database);
	}
	
	public String getUrl(){
		return this.url;
	}
	
	public String getHost(){
		return this.host;
	}
	
	public int getPort(){
		return this.port;
	}
	
	public String getUsername(){
		return this.user;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	public String getDatabase(){
		return this.database;
	}
	
	public abstract Connection getConnection();
	
}
