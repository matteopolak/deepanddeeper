package com.deepanddeeper.deepanddeeper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
	private final Connection connection;

	Database(String uri) throws SQLException, ClassNotFoundException {
		this.connection = DriverManager.getConnection(uri);
	}

	public Connection getConnection() {
		return this.connection;
	}
}
