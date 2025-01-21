package p08_dao;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
 * PackageName: p08_dao
 * Description: 封裝了針對資料表的通用操作(增刪改查)
 * 				DAO: data(base) access object
 */
// 將 CustomerDAOImpl 中 getCustomerById 的參數 Customer.class 去除
public abstract class BaseDAO<T> { // 不用於造對象, 用於提供通用方法, 針對具體表再提供具體的DAO, 所以用 abstract 修飾.
	
	private Class<T> clazz = null; // 顯示賦值無法達成, 因為一行搞不定.
	/*
	 * 要獲取當前對象的父類泛型
	 * 可以使用建構子或者程式碼區塊
	 */
	// public BaseDAO() { // 可以用建構子
	// 	
	// }
	
	{ // 或者程式碼區塊, 這邊 this 是子類的對象, 其調用 .getClass().getGenericSuperclass() 實現了 ParameterizedType 介面.
		// 獲取當前 BaseDAO 的子類繼承的父類中的泛型
		Type genericSuperclass = this.getClass().getGenericSuperclass(); // 取得含泛型參數的父類類型(獲取泛型類型的 Type)
		ParameterizedType paramType = (ParameterizedType) genericSuperclass; // 將取得類型強轉為介面 Type 的子介面 ParameterizedType
		Type[] typeArguments = paramType.getActualTypeArguments(); // 獲取了父類的泛型參數
		clazz = (Class<T>) typeArguments[0]; // 泛型的第一個參數
	}
	
	// 通用的增刪改操作 --- version 2.0 (考慮交易)
	public int update(Connection conn, String sql, Object ...args) { // sql 中佔位符的個數與可變形參的長度相同
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			for(int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]);
			}
			return ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 4. 資源的關閉
			JDBCUtils.closeResource(null, ps);
		}
		return 0;
	}
	
	// 通用的查詢操作, 用於返回資料表中的一條紀錄(version 2.0, 考慮交易)
	public T getInstance(Connection conn, String sql, Object ...args){
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			for(int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]); 
			}
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			if(rs.next()) {
				T t = clazz.getDeclaredConstructor().newInstance();
				for (int i = 0; i < columnCount; i++) {
					Object columnValue = rs.getObject(i + 1);
					String columnLabel = rsmd.getColumnLabel(i + 1);
					Field field = clazz.getDeclaredField(columnLabel);
					field.setAccessible(true);
					field.set(t, columnValue);
				}
				return t;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(null, ps, rs);			
		}
		return null;
	}
	
	// 通用的查詢操作, 用於返回資料表中的多條紀錄構成的集合(version 2.0, 考慮交易)
	public List<T> getForList(Connection conn, String sql, Object ...args){
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			for(int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]); // 注意索引
			}
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			ArrayList<T> list = new ArrayList<T>();
			while(rs.next()) {
				T t = clazz.getDeclaredConstructor().newInstance();
				for (int i = 0; i < columnCount; i++) {
					Object columnValue = rs.getObject(i + 1);
					String columnLabel = rsmd.getColumnLabel(i + 1);
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
