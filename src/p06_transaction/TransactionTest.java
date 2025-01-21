package p06_transaction;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import p02_util.JDBCUtils;

/**
 * ClassName: TransactionTest.java
 * PackageName: p06_transaction
 * Description: 
 * 1. 什麼叫做資料庫交易? 
 *    交易: 一組邏輯操作單元, 使資料從一種狀態變換到另一種狀態.
 *    一組邏輯操作單元: 一個或多個 DML 操作.
 * 2. 交易處裡的原則: 保證所有交易都作為一個工作單元來執行, 即使出現了故障,
 *    都不能改變這種執行方式, 當一個交易中執行多個操作時, 要馬所有的交易都被提交, 
 *    那麼這些修改就永久地保存, 要馬資料庫管理系統將放棄所做的所有修改, 整個交易回滾到最初狀態.
 * 3. 資料一旦提交, 就不可回滾.
 * 4. 哪些操作會導致資料的自動提交(所以要避免就要全部都避免)?
 *    DDL 操作一旦執行都會自動提交, set autocommit=false 對 DDL 操作無效.
 *    DML 預設情況下, 一旦執行就會自動提交, 
 *    可以透過set autocommit=false 的方式取消 DML 操作的自動提交.
 *    預設在關閉連接時, 會自動提交資料.
 */
public class TransactionTest {
	// 未考慮資料庫交易情況下的轉帳操作
	/*
	 * 針對user_table表, AA給BB轉帳100元
	 * UPDATE user_table SET balance = balance - 100 WHERE user = 'AA';
	 * UPDATE user_table SET balance = balance + 100 WHERE user = 'BB';
	 * */
	@Test
	public void testUpdate() {
		String sql1 ="UPDATE user_table SET balance = balance - 100 WHERE user = ?";
		update(sql1, "AA");

		// 模擬網路異常
		System.out.println(10 / 0);
		
		String sql2 ="UPDATE user_table SET balance = balance + 100 WHERE user = ?";
		update(sql2, "BB");
		
		System.out.println("轉帳成功");
	}
	
	// 通用的增刪改操作 --- version 1.0
	public int update(String sql, Object ...args) { // sql 中佔位符的個數與可變形參的長度相同
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			// 1. 獲取資料庫的連接
			conn = JDBCUtils.getConnection();
			// 2. 預編譯 sql 語句, 返回 PreparedStatement 實例.
			ps = conn.prepareStatement(sql);
			// 3. 填充佔位符
			for(int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]); // 注意索引
			}
			// 4. 執行
			return ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 5. 資源的關閉
			JDBCUtils.closeResource(conn, ps);			
		}
		return 0;
	}
	
	// 考慮資料庫交易情況下的轉帳操作
	@Test
	public void testUpdateWithTx() {
		Connection conn = null;
		try {
			conn = JDBCUtils.getConnection();
			
			System.out.println(conn.getAutoCommit()); // 查看提交行為, 預設為 true.
			
			// 1. 取消自動提交
			conn.setAutoCommit(false);
			
			String sql1 ="UPDATE user_table SET balance = balance - 100 WHERE user = ?";
			update(conn, sql1, "AA");

			// 模擬網路異常
			// System.out.println(10 / 0);
			
			String sql2 ="UPDATE user_table SET balance = balance + 100 WHERE user = ?";
			update(conn, sql2, "BB");
						
			// 2. 提交資料
			conn.commit();
			
			System.out.println("轉帳成功");
		} catch (Exception e) {
			e.printStackTrace();
			// 3. 回滾資料
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			// 修改其為自動提交資料
			// 主要針對使用資料庫連接池的使用
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			JDBCUtils.closeResource(conn, null); // 關閉連接
		}
	}
	
	// 通用的增刪改操作 --- version 2.0 (考慮交易)
	public int update(Connection conn, String sql, Object ...args) { // sql 中佔位符的個數與可變形參的長度相同
		PreparedStatement ps = null;
		try {
			// 1. 預編譯 sql 語句, 返回 PreparedStatement 實例.
			ps = conn.prepareStatement(sql);
			// 2. 填充佔位符
			for(int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]); // 注意索引
			}
			// 3. 執行
			return ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 4. 資源的關閉
			JDBCUtils.closeResource(null, ps);	// 連接從外面傳, 不關閉.		
		}
		return 0;
	}
	
	// *********************************************************
	@Test
	public void testTransactionSelect() throws Exception {
		Connection conn = JDBCUtils.getConnection();
		
		// 獲取當前連接的隔離級別
		System.out.println(conn.getTransactionIsolation());
		// 設置當前連接的資料庫的隔離級別(MySQL 預設為 REPAETAABLE READ, Oracle 預設為 READ COMMITTED, 通常會使用 READ COMMITTED)
		conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		// 取消自動提交資料
		conn.setAutoCommit(false);
		
		String sql = "SELECT user, password, balance FROM user_table WHERE user = ?";
		User user = getInstance(conn, User.class, sql, "CC");
		System.out.println(user);
	}
	
	@Test
	public void testTransactionUpdate() throws Exception {
		Connection conn = JDBCUtils.getConnection();
		// 取消自動提交資料
		conn.setAutoCommit(false);
		String sql = "UPDATE user_table SET balance = ? WHERE user = ?";
		update(conn, sql, 5000, "CC");
		
		Thread.sleep(15000); // 睡 15 秒
		System.out.println("修改結束");
	}
	
	// 通用的查詢操作, 用於返回資料表中的一條紀錄(version 2.0, 考慮交易)
	public <T> T getInstance(Connection conn, Class<T> clazz, String sql, Object ...args){
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// 1. 預編譯 sql 語句, 返回 PreparedStatement 實例.
			ps = conn.prepareStatement(sql);
			// 2. 填充佔位符
			for(int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]); // 注意索引
			}
			
			// 3. 執行並返回結果集
			rs = ps.executeQuery();
			// 獲取結果集的元數據: ResultSetMetaData
			ResultSetMetaData rsmd = rs.getMetaData();
			// 透過 ResultSetMetaData 獲取結果集的 column 數
			int columnCount = rsmd.getColumnCount();
			// 4. 處理結果集
			if(rs.next()) {
				// 建議先用無參建構子造對象, 再用 setter.
				T t = clazz.getDeclaredConstructor().newInstance();
				// T t = clazz.newInstance(); // 已過時			
				// 處理結果集一條數據中的每一個 column
				for (int i = 0; i < columnCount; i++) {
					//獲取 column 值
					Object columnValue = rs.getObject(i + 1);
					
					// 獲取 column 別名
					String columnLabel = rsmd.getColumnLabel(i + 1);
					
					// 透過反射, 將 t 對象指定名 columnName 的屬性賦值為指定的值 columnValue
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
			JDBCUtils.closeResource(null, ps, rs);			
		}
		return null;
	}
	
}
