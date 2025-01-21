package p03_statement.crud;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import p02_util.JDBCUtils;

/**
 * ClassName: PreparedStatementTest.java
 * PackageName: p03_statement.crud
 * Description: 演示 PreparedStatement 替換 Statement 解決 sql 注入問題
 * 				除了解決 Statement 的拚串，sql 問題之外，PreparedStatement 的其他優點
 * 				1. PreparedStatement 操作 Blob 資料, 而 Statement 做不到
 * 				2. PreparedStatement 可以實現更高效的批量操作
 */
public class PreparedStatementTest {
	
	@Test
	public void testLogin() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("請輸入帳號: ");
		String user = scanner.nextLine();
		System.out.print("請輸入密碼: ");
		String password = scanner.nextLine();
		String sql = "SELECT user, password FROM user_table WHERE user = ? AND password = ?";
		User returnUser = getInstance(User.class, sql, user, password);
		if (returnUser != null) {
			System.out.println("登入成功");
		} else {
			System.out.println("帳號不存在或密碼錯誤");
		}
	
	}
	
	public <T> T getInstance(Class<T> clazz, String sql, Object ...args){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// 1. 獲取資料庫的連接
			conn = JDBCUtils.getConnection();
			// 2. 預編譯 sql 語句，返回 PreparedStatement 實例
			ps = conn.prepareStatement(sql);
			// 3. 填充佔位符
			for(int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]); // 注意索引
			}
			
			// 4. 執行並返回結果集
			rs = ps.executeQuery();
			// 獲取結果集的元數據: ResultSetMetaData
			ResultSetMetaData rsmd = rs.getMetaData();
			// 透過 ResultSetMetaData 獲取結果集的 column 數
			int columnCount = rsmd.getColumnCount();
			// 5. 處理結果集
			if(rs.next()) {
				// 建議先用無參建構子造對象，再用setter
				T t = clazz.getDeclaredConstructor().newInstance();
				// T t = clazz.newInstance(); //已過時			
				// 處理結果集一條數據中的每一個 column
				for (int i = 0; i < columnCount; i++) {
					// 獲取 column 值
					Object columnValue = rs.getObject(i + 1);
					
					// 獲取 column 別名
					String columnLabel = rsmd.getColumnLabel(i + 1);
					
					// 透過反射將 t 對象指定名 columnName 的屬性賦值為指定的值 columnValue
					Field field = clazz.getDeclaredField(columnLabel);
					field.setAccessible(true);
					field.set(t, columnValue);
				}
				return t;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 5. 資源的關閉
			JDBCUtils.closeResource(conn, ps, rs);			
		}
		return null;
	}
}
