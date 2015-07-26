package com.arthurbergmz.mysql.async;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Arthur Arioli Bergamaschi
 */
public interface ResultDatabaseRunnable<T> {
	
	public void run(Connection connection, ResultHolder<T> resultHolder) throws SQLException;
	
}
