package p09_util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.commons.dbutils.DbUtils;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * ClassName: JDBCUtils.java
 * PackageName: p09_util
 * Description: 
 */
public class JDBCUtils {
	/**
	 * 使用 c3p0 的資料庫連接池技術獲取資料庫連接
	 * */
	// 資料庫連接池只需提供一個即可
	private static ComboPooledDataSource cpds = new ComboPooledDataSource("C3P0");
	public static Connection getConnection1() throws SQLException {
		Connection conn = cpds.getConnection();
		return conn;
	}
	
	/**
	 * 使用 DBCP 的資料庫連接池技術獲取資料庫連接
	 * */
	// 創建一個 DBCP 資料庫連接池(使用靜態程式碼區塊)
	private static BasicDataSource source;
	static {		
		FileInputStream is = null;
		try {
			Properties pros = new Properties();
			is = new FileInputStream(new File("src/dbcp.properties"));
			pros.load(is);
			source = BasicDataSourceFactory.createDataSource(pros);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static Connection getConnection2() throws SQLException {
		Connection conn = source.getConnection();
		return conn;
	}	
	
	/**
	 * 使用 Druid 的資料庫連接池技術獲取資料庫連接
	 * */
	//創建一個 Druid 資料庫連接池(使用靜態程式碼區塊)
	private static DataSource source1;
	static {		
		InputStream is = null;
		try {
			Properties pros = new Properties();
			is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
			pros.load(is);
			source1 = DruidDataSourceFactory.createDataSource(pros);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	
	public static Connection getConnection3() throws SQLException {
		Connection conn = source1.getConnection();
		return conn;
	}
	
	// 將先前的其他操作留著比較不會出錯(便宜行事而已)
	/**
	 * 獲取資料庫的連接
	 * */
	public static Connection getConnection() throws Exception {
		// 1. 讀取設定檔中的 4 個基本訊息
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties"); // 獲取系統類加載器再去讀取設定檔
		Properties pros = new Properties();
		pros.load(is);
		String user = pros.getProperty("user");
		String password = pros.getProperty("password");
		String url = pros.getProperty("url");
		String driverClass = pros.getProperty("driverClass");
		// 2. 加載驅動
		Class.forName(driverClass);
		// 3. 獲取連接
		Connection conn = DriverManager.getConnection(url, user, password);

		return conn;
	}
	
	/**
	 * 關閉 Connection 和 Statement 的操作
	 * */
	public static void closeResource(Connection conn, Statement ps) {
		try {
			if (conn != null) // 避免 null pointer
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (ps != null)
				ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 關閉 Connection, Statement 和 ResultSet 的操作.
	 * */
	public static void closeResource(Connection conn, Statement ps, ResultSet rs) {
		try {
			if (conn != null) // 避免 null pointer
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (ps != null)
				ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 使用 dbutils.jar 中提供的 DbUtils 工具類, 實現資源的關閉.
	 * */
	public static void closeResource1(Connection conn, Statement ps, ResultSet rs) {
		// 方式一
		// try {
		// 	DbUtils.close(conn);
		// } catch (SQLException e) {
		// 	e.printStackTrace();
		// }
		// try {
		// 	DbUtils.close(ps);
		// } catch (SQLException e) {
		// 	e.printStackTrace();
		// }
		// try {
		// 	DbUtils.close(rs);
		// } catch (SQLException e) {
		// 	e.printStackTrace();
		// }
		// 方式二
		DbUtils.closeQuietly(conn);
		DbUtils.closeQuietly(ps);
		DbUtils.closeQuietly(rs);
	}
}
