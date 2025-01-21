package p04_preparedstatement.crud;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import p02_util.JDBCUtils;

/**
 * ClassName: PreparedStatementUpdateTest.java
 * PackageName: p04_preparedstatement.crud
 * Description: 使用 PreparedStatement 替換 Statement 實現對資料表的增刪改操作
 */
public class PreparedStatementUpdateTest {

	// 向 customers 表中新增一條紀錄
	@Test
	public void testInsert() {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			// 1. 讀取設定檔中的4個基本訊息
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
			conn = DriverManager.getConnection(url, user, password);
			
			// 4. 預編譯 sql 語句，返回 PreparedStatement 實例
			String sql = "INSERT INTO customers(name, email, birth) VALUES(?, ?, ?)"; // ?: 佔位符，佔了一個位置
			ps = conn.prepareStatement(sql);
			// 5. 填充佔位符，注意資料庫索引從 1 開始
			ps.setString(1, "汪風9");
			ps.setString(2, "vv9@cc.com");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 注意 m 小寫是時間
			Date date = sdf.parse("1000-01-01");
			ps.setDate(3, new java.sql.Date(date.getTime()));

			// 6. 執行操作
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 7. 資源的關閉
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
	}
	
	// 使用工具類修改 customers 表的一條紀錄
	@Test
	public void testUpdate() {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			// 1. 獲取資料庫的連接
			conn = JDBCUtils.getConnection();
			// 2. 預編譯 sql 語句，返回 PreparedStatement 實例
			String sql = "UPDATE customers set name = ? WHERE id = ?";
			ps = conn.prepareStatement(sql);
			// 3. 填充佔位符
			ps.setObject(1, "莫札特"); // 使用通用的操作
			ps.setObject(2, 9); // 使用通用的操作
			// 4. 執行
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 5. 資源的關閉
			JDBCUtils.closeResource(conn, ps);			
		}
	}
	
	// 通用的增刪改操作
	public void update(String sql, Object ...args) { // sql 中佔位符的個數與可變形參的長度相同
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			// 1. 獲取資料庫的連接
			conn = JDBCUtils.getConnection();
			// 2. 預編譯 sql 語句，返回 PreparedStatement 實例
			ps = conn.prepareStatement(sql);
			// 3. 填充佔位符
			for(int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]); // 注意索引
			}
			// 4. 執行
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 5. 資源的關閉
			JDBCUtils.closeResource(conn, ps);			
		}
	}
	
	// 測試通用的增刪改操作
	@Test
	public void testCommonUpdate() {
		// String sql = "DELETE FROM customers WHERE id = ?";
		// update(sql, 3);
		
		// order 在 sql 是關鍵字，直接執行會報錯
		String sql = "UPDATE `order` SET order_name = ? WHERE order_id = ?";
		update(sql, "DD", 2);
	}
}
