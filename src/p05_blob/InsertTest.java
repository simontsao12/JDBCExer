package p05_blob;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.junit.jupiter.api.Test;

import p02_util.JDBCUtils;

/**
 * ClassName: InsertTest.java
 * PackageName: p05_blob
 * Description: 使用 PreparedStatement 實現批量數據的操作
 * 				update, delete 本身就具有批量操作的效果
 * 				此時的批量操作，主要指的是批量插入，使用 PreparedStatement 如何實現更高效的批量插入?
 * 				題目: 造一張表, 插入 20000 條資料
 * 				CREATE TABLE goods (
 * 				id INT PRIMARY KEY AUTO_INCREMENT, 
 * 				NAME VARCHAR(25)
 * 				);
 *
 * 				SELECT COUNT(*) 
 * 				FROM goods;				
 * 				方式一: 使用 Statement
 * 				Connection conn = JDBCUtils.getConnection();
 * 				Statement st = conn.createStatement();
 * 				for (int i = 1; i <= 20000; i++) {
 * 					String sql = "INSERT INTO goods(name) VALUES('name_" + i +"')";
 * 					st.execute(sql);
 * 				}
 * 				JDBCUtils.closeResource(conn, st);
 */
public class InsertTest {
	// 批量插入的方式二: 使用 PreparedStatement
	@Test
	public void InsertTest1() {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			long start = System.currentTimeMillis();
			conn = JDBCUtils.getConnection();
			String sql = "INSERT INTO goods(name) VALUES(?)";
			ps = conn.prepareStatement(sql);
			for (int i = 1; i <= 20000; i++) {
				ps.setObject(1, "name_" + i);
				ps.execute(); // 這邊會與資料庫交互一次，共交互 20000 次，過多的交互效率不會太高
			}
			long end = System.currentTimeMillis();
			System.out.println("花費時間: " + (end - start)); // 花費時間: 81731
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, ps);			
		}
	}
	
	/*
	 * 批量插入的方式三: 
	 * 1. addBatch(), executeBatch(), clearBatch() (注意 MySQL 預設情況下不支持 Batch)
	 * 2. MySQL 伺服器預設是關閉批次處理的，需要通過一個參數讓 MySQL 開啟批次處理的支持， 
	 *    將 ?rewriteBatchedStatements=true 寫在設定檔的url後面
	 * 3. 版本 5.1.7 不支持批次處理, 後來導入更新的驅動: 
	 * 	  mysql-connector-java-5.1.37-bin.jar 
	 * 	  自己練習本身使用 8.0.33 所以沒有這個問題 
	 */
	@Test
	public void InsertTest2() {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			long start = System.currentTimeMillis();
			conn = JDBCUtils.getConnection();
			String sql = "INSERT INTO goods(name) VALUES(?)";
			ps = conn.prepareStatement(sql);
			// for (int i = 1; i <= 20000; i++) {
			for (int i = 1; i <= 1000000; i++) {
				ps.setObject(1, "name_" + i);
				// 1. "累積" sql
				ps.addBatch();
				if (i % 500 == 0) { // 每 500 次執行一遍
					// 2. 執行 Batch
					ps.executeBatch(); // 這邊會與資料庫交互一次，共交互 40 次，過多的交互效率不會太高
					// 3. 清空 Batch
					ps.clearBatch();
				}
			}
			long end = System.currentTimeMillis();
			// System.out.println("20000條花費時間: " + (end - start)); // 20000 條花費時間: 632
			System.out.println("1000000條花費時間: " + (end - start)); // 1000000 條花費時間: 17307
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, ps);			
		}
	}
	
	// 批量插入的方式四: 設置連接不允許自動提交
	@Test
	public void InsertTest3() {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			long start = System.currentTimeMillis();
			conn = JDBCUtils.getConnection();
			
			// 設置不允許自動提交
			conn.setAutoCommit(false);
			
			String sql = "INSERT INTO goods(name) VALUES(?)";
			ps = conn.prepareStatement(sql);
			// for (int i = 1; i <= 20000; i++) {
			for (int i = 1; i <= 1000000; i++) {
				ps.setObject(1, "name_" + i);
				// 1. "累積"sql
				ps.addBatch();
				if (i % 500 == 0) { // 每 500 次執行一遍
					// 2. 執行 Batch
					ps.executeBatch(); //這邊會與資料庫交互一次，共交互 40 次，過多的交互效率不會太高
					// 3. 清空 Batch
					ps.clearBatch();
				}
			}
			
			//提交
			conn.commit();
			long end = System.currentTimeMillis();
			// System.out.println("20000條花費時間: " + (end - start)); // 20000 條花費時間: 432
			System.out.println("1000000條花費時間: " + (end - start)); // 1000000 條花費時間: 5372
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, ps);			
		}
	}
}
