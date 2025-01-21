package p09_connection;

import java.sql.Connection;

import org.junit.jupiter.api.Test;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

/**
 * ClassName: C3P0Test.java
 * PackageName: p09_connection
 * Description: 測試 C3P0 的資料庫連接池技術
 */
public class C3P0Test {
	// 方式一: 
	@Test
	public void testGetConnection() throws Exception {
		// 獲取 C3P0 資料庫連接池
		ComboPooledDataSource cpds = new ComboPooledDataSource();
		cpds.setDriverClass("com.mysql.cj.jdbc.Driver");
		cpds.setJdbcUrl("jdbc:mysql://localhost:3306/test?rewriteBatchedStatements=true");
		cpds.setUser("");
		cpds.setPassword("");
		// 透過設置相關的參數, 對資料庫連接池進行管理.
		// 設置初始時資料庫連接池的連接數
		cpds.setInitialPoolSize(10);
		
		Connection conn = cpds.getConnection();
		System.out.println(conn);
		
		// 銷毀 C3P0 資料庫連接池
		// DataSources.destroy(cpds); // 一般情況下不會關連接池
	}
	// 方式二: 使用設定檔
	@Test
	public void testGetConnection1() throws Exception {
		ComboPooledDataSource cpds = new ComboPooledDataSource("C3P0");
		Connection conn = cpds.getConnection();
		// System.out.println(cpds.getInitialPoolSize()); //看設定檔設定情況
		System.out.println(conn);
	}
}
