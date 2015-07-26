package com.arthurbergmz.mysql.sync;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.arthurbergmz.mysql.Database;

/**
 * @author Arthur Bergamaschi
 */
public class SyncDatabase extends Database {
	
	private Connection connection;
	
	public SyncDatabase(String host, int port, String username, String password, String database){
		super(host, port, username, password, database);
		System.out.println("[" +  this.getHost() + ":" + this.getPort() + "] Estabelecendo conexão com o banco de dados...");
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
	
	public ResultSet query(String query) throws SQLException {
		Connection c = this.getConnection();
		ResultSet result = null;
		try{
			c.setAutoCommit(false);
			result = c.prepareStatement(query).executeQuery();
		}catch (SQLException e){
			e.printStackTrace();
			if(c != null){
				try{
					System.out.println("Não foi possível executar a query.");
					c.rollback();
				}catch (SQLException ex){
					ex.printStackTrace();
				}
			}
		}finally{
			c.setAutoCommit(true);
		}
		return result;
	}
	
	public void update(String update) throws SQLException {
		Connection c = this.getConnection();
		PreparedStatement s = null;
		try{
			c.setAutoCommit(false);
			s = c.prepareStatement(update);
			s.execute();
			c.commit();
		}catch (SQLException e){
			e.printStackTrace();
			if(c != null){
				try{
					System.out.println("Não foi possível executar o update.");
					c.rollback();
				}catch (SQLException ex){
					ex.printStackTrace();
				}
			}
		}finally{
			if(s != null) s.close();
			c.setAutoCommit(false);
		}
		this.closeConnection();
	}
	
	public void update(PreparedStatement preparedstatement) throws SQLException {
		Connection c = this.getConnection();
		try{
			c.setAutoCommit(false);
			preparedstatement.execute();
			c.commit();
		}catch (SQLException e){
			e.printStackTrace();
			if(c != null){
				try{
					System.out.println("Não foi possível executar o update.");
					c.rollback();
				}catch (SQLException ex){
					ex.printStackTrace();
				}
			}
		}finally{
			if(preparedstatement != null) preparedstatement.close();
			c.setAutoCommit(false);
		}
		this.closeConnection();
	}
	
	public Connection getConnection(){
		try{
			if((this.connection == null) || (!this.connection.isValid(10))) this.connection = this.openConnection();
		}catch (SQLException e){
			e.printStackTrace();
		}
		return this.connection;
	}
	
	public void closeConnection(){
		if(this.connection == null){
			System.out.println("Não há conexões para encerrar!");
		}else{
			try{
				this.connection.close();
				this.connection = null;
			}catch (SQLException e){
				e.printStackTrace();
				System.out.println("Ocorreu um erro ao encerrar a conexão ao banco de dados!");
			}
		}
	}
	
}
