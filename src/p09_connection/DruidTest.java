package p09_connection;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

/**
 * ClassName: DruidTest.java
 * PackageName: com.atsimoncc4.connection
 * Description: 測試 Druid 的資料庫連接池技術
 */
public class DruidTest {
	
	@Test
	public void getConnection() throws Exception {
		Properties pros = new Properties();
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
		pros.load(is);
		DataSource source = DruidDataSourceFactory.createDataSource(pros);
		Connection conn = source.getConnection();
		System.out.println(conn);
	} 
}
