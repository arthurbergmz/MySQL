package com.arthurbergmz.mysql.async;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Arthur Arioli Bergamaschi
 */
public interface DatabaseRunnable {
	
	public void run(Connection connection) throws SQLException;
	
}
