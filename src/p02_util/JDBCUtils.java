package p02_util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * ClassName: JDBCUtils.java
 * PackageName: p02_util
 * Description: 操作資料庫的工具類
 */
public class JDBCUtils {
	
	/**
	 * 獲取資料庫的連接
	 * */
	public static Connection getConnection() throws Exception {
		
		// 1. 讀取設定檔案中的 4 個基本訊息
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties"); //獲取系統類加載器再去讀取設定檔
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
			if (conn != null) //避免 null pointer
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
	 * 關閉 Connection, Statement和ResultSet 的操作
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
	
}
