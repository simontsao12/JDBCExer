package p09_connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.jupiter.api.Test;

/**
 * ClassName: DBCPTest.java
 * PackageName: p09_connection
 * Description: 測試 DBCP 的資料庫連接池技術
 */
public class DBCPTest {
	// 方式一: 不推薦
	@Test
	public void testGetConnection() throws SQLException {
		// 創建了 DBCP 的資料庫連接池
		// 直接宣告為 BasicDataSource 看到更多方法
		// DataSource source = new BasicDataSource();
		BasicDataSource source = new BasicDataSource();
		
		// 設定基本訊息
		source.setDriverClassName("com.mysql.cj.jdbc.Driver");
		source.setUrl("jdbc:mysql://localhost:3306/test?rewriteBatchedStatements=true");
		source.setUsername("");
		source.setPassword("");
		
		// 可以設定其他涉及資料庫連接池管理的相關屬性
		source.setInitialSize(10);
		source.setMaxTotal(10); // 在 DBCP 2.x 中, setMaxActive(int maxActive) 被 setMaxTotal(int maxTotal)  所取代.
		
		Connection conn = source.getConnection();
		System.out.println(conn);
	}
	
	// 方式二: (推薦)使用設定檔
	@Test
	public void testGetConnection1() throws Exception {
		Properties pros = new Properties();
		// 方式1: 
		// InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("dbcp.properties");
		// 方式2: 
		FileInputStream is = new FileInputStream(new File("src/dbcp.properties"));
		pros.load(is);
		BasicDataSource source = BasicDataSourceFactory.createDataSource(pros);
		Connection conn = source.getConnection();
		System.out.println(conn);
	}
}
