package p07_dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import p02_util.JDBCUtils;

/**
 * ClassName: BaseDAO.java
 * PackageName: p07_dao
 * Description: 封裝了針對資料表的通用操作(增刪改查)
 * 				DAO: data(base) access object
 */
public abstract class BaseDAO { // 不用於造對象, 用於提供通用方法, 針對具體表再提供具體的 DAO, 所以用 abstract 修飾.
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
					// 獲取 column 值
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
	
	// 通用的查詢操作, 用於返回資料表中的多條紀錄構成的集合(version 2.0, 考慮交易)
	public <T> List<T> getForList(Connection conn, Class<T> clazz, String sql, Object ...args){
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
			
			// 創建集合對象
			ArrayList<T> list = new ArrayList<T>();
			// 4. 處理結果集
			while(rs.next()) {
				// 建議先用無參建構子造對象, 再用 setter.
				T t = clazz.getDeclaredConstructor().newInstance();
				// T t = clazz.newInstance(); // 已過時			
				// 處理結果集一條數據中的每一個 column: 給 t 對象指定的屬性賦值的過程
				for (int i = 0; i < columnCount; i++) {
					// 獲取 column 值
					Object columnValue = rs.getObject(i + 1);
					
					// 獲取 column 別名
					String columnLabel = rsmd.getColumnLabel(i + 1);
					
					// 透過反射, 將 t 對象指定名 columnName 的屬性賦值為指定的值 columnValue
					Field field = clazz.getDeclaredField(columnLabel);
					field.setAccessible(true);
					field.set(t, columnValue);
				}
				list.add(t);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 5. 資源的關閉
			JDBCUtils.closeResource(null, ps, rs);			
		}
		return null;
	}
	
	// 用於查詢特殊值的通用方法
	public <E> E getValue(Connection conn, String sql, Object ...args) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]);
			}
			rs = ps.executeQuery();
			if(rs.next()) {
				return (E)rs.getObject(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {			
			JDBCUtils.closeResource(null, ps, rs);
		}
		return null;
	}
}
