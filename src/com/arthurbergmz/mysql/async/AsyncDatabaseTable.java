package com.arthurbergmz.mysql.async;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Arthur Bergamaschi
 * NOT TESTED
 */
public class AsyncDatabaseTable {
	
	private AsyncDatabase database;
	private String table;
	
	public AsyncDatabaseTable(AsyncDatabase database, String[] tables){
		if(tables == null) throw new IllegalArgumentException("É necessário um nome para criar conexão com uma TABLE através do banco de dados.");
		this.database = database;
		StringBuilder builder = new StringBuilder();
		for(String tableName : tables) builder.append("," + tableName);
		this.table = builder.toString().substring(1);
	}
	
	public AsyncDatabaseTable(AsyncDatabase database, String tableName){
		if(tableName == null) throw new IllegalArgumentException("É necessário um nome para criar conexão com uma TABLE através do banco de dados.");
		this.database = database;
		this.table = tableName;
	}
	
	public <T> void getAt(final int id, final String column, ResultRunnable<Object> onResult){
		this.database.run("get from table at", new ResultDatabaseRunnable<Object>(){
			@Override
			public void run(Connection connection, ResultHolder<Object> resultHolder) throws SQLException {
				ResultSet result = connection.prepareStatement("SELECT * FROM " + AsyncDatabaseTable.this.table + " WHERE id = " + id + ";").executeQuery();
				Object object = null;
				while(result.next()) if((object = result.getObject(column)) != null) resultHolder.setResult(object);
			}
		}, onResult);
	}
	
	public void getAll(final String key, ResultRunnable<ResultSet> onResult){
		this.database.run("get all from table", new ResultDatabaseRunnable<ResultSet>(){
			@Override
			public void run(Connection connection, ResultHolder<ResultSet> resultHolder) throws SQLException {
				ResultSet result = connection.prepareStatement("SELECT " + key + " FROM " + AsyncDatabaseTable.this.table + ";").executeQuery();
				resultHolder.setResult(result);
			}
		}, onResult);
	}
	
	public <T> void getWhere(final String key, final Object value, final String column, final SpecificResultRunnable<T> onResult){
		this.database.run("get from table where", new ResultDatabaseRunnable<T>(){
			@SuppressWarnings("unchecked")
			@Override
			public void run(Connection connection, ResultHolder<T> resultHolder) throws SQLException {
				ResultSet result = connection.prepareStatement("SELECT * FROM " + AsyncDatabaseTable.this.table + " WHERE " + key + " = " + (value instanceof String ? ("'" + value + "'") : value) + ";").executeQuery();
				Object object = null;
				while(result.next()) if((object = result.getObject(column, onResult.getTypeClass())) != null) resultHolder.setResult((T)object);
			}
		}, onResult);
	}
	
	public void getAllWhere(final String key, final Object value, ResultRunnable<ResultSet> onResult){
		this.database.run("get all from table where", new ResultDatabaseRunnable<ResultSet>(){
			@Override
			public void run(Connection connection, ResultHolder<ResultSet> resultHolder) throws SQLException {
				ResultSet result = connection.prepareStatement("SELECT * FROM " + AsyncDatabaseTable.this.table + " WHERE " + key + " = " + (value instanceof String ? ("'" + value + "'") : value) + ";").executeQuery();
				resultHolder.setResult(result);
			}
		}, onResult);
	}
	
	public void update(final String[] keys, final Object[] values, final String where, final Object whereValue){
		final StringBuilder builder = new StringBuilder();
		for(int i = 0; i < keys.length; i++){
			Object v = values[i];
			builder.append(", " + keys[i] + " = " + ((v instanceof String) ? ("'" + v + "'") : v));
		}
		this.database.run("update table", new DatabaseRunnable(){
			@Override
			public void run(Connection connection) throws SQLException {
				PreparedStatement statement = null;
				try{
					connection.setAutoCommit(false);
					statement = connection.prepareStatement("UPDATE " + AsyncDatabaseTable.this.table + " SET " + builder.toString().substring(2) + " WHERE " + where + " = " + ((whereValue instanceof String) ? ("'" + whereValue + "'") : whereValue) + ";");
					statement.execute();
					connection.commit();
				}catch (SQLException e){
					e.printStackTrace();
					if(connection != null){
						try{
							connection.rollback();
						}catch (SQLException ex){
							e.printStackTrace();
						}
					}
				}finally{
					if(statement != null) statement.close();
					connection.setAutoCommit(true);
				}
			}
		});
	}
	
	public void insert(final String[] keys, final Object[] values){
		final StringBuilder keysBuilder = new StringBuilder();
		final StringBuilder valuesBuilder = new StringBuilder();
		for(int i = 0; i < keys.length; i++){
			keysBuilder.append("," + keys[i]);
			Object v = values[i];
			valuesBuilder.append(((v instanceof String) ? (",'" + v + "'") : ("," + v)));
		}
		this.database.run("insert into table", new DatabaseRunnable(){
			@Override
			public void run(Connection connection) throws SQLException {
				PreparedStatement statement = null;
				try{
					connection.setAutoCommit(false);
					statement = connection.prepareStatement("INSERT INTO " + AsyncDatabaseTable.this.table + " (" + keysBuilder.toString().substring(1) + ") VALUES (" + valuesBuilder.toString().substring(1) + ");");
					statement.execute();
					connection.commit();
				}catch (SQLException e){
					e.printStackTrace();
					if(connection != null){
						try{
							connection.rollback();
						}catch (SQLException ex){
							e.printStackTrace();
						}
					}
				}finally{
					if(statement != null) statement.close();
					connection.setAutoCommit(true);
				}
			}
		});
	}
	
}
