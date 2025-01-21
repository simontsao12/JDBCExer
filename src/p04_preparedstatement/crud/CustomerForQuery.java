package p04_preparedstatement.crud;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.junit.jupiter.api.Test;

import p02_util.JDBCUtils;
import p04_bean.Customer;

/**
 * ClassName: CustomerForQuery.java
 * PackageName: p04_preparedstatement.crud
 * Description: 針對於 Customers 表的查詢操作
 */
public class CustomerForQuery {
	
	@Test
	public void testQueryForCustomers() {
		String sql = "SELECT id, name, email, birth FROM customers WHERE id = ?";
		Customer customer = queryForCustomers(sql, 9);
		System.out.println(customer);

		sql = "SELECT name, email FROM customers WHERE name = ?";
		Customer customer1 = queryForCustomers(sql, "汪風8");
		System.out.println(customer1);	
	}
	
	// 針對 customers 表通用的查詢操作
	public Customer queryForCustomers(String sql, Object ...args) {
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
				// 建議先用無參建構子造物件, 再用setter.
				Customer cust = new Customer(); // 放這邊代表有查到結果才建物件
				// 處理結果集一條數據中的每一個column
				for (int i = 0; i < columnCount; i++) {
					// 獲取 column 值
					Object columnValue = rs.getObject(i + 1);
					
					// 獲取每個 column 的 column 名稱
					// String columnName = rsmd.getColumnName(i + 1); //不推薦使用
					String columnLabel = rsmd.getColumnLabel(i + 1);
					
					// 使用動態實現，因為不知道要查幾個，或者表的 column 如何命名，所以使用反射
					// 給 Customer 物件指定的 columnName 屬性賦值為 columnValue，通過反射
					// Field field = Customer.class.getDeclaredField(columnName); //不推薦使用
					Field field = Customer.class.getDeclaredField(columnLabel);
					field.setAccessible(true);
					field.set(cust, columnValue);
				}
				return cust;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 5. 資源的關閉
			JDBCUtils.closeResource(conn, ps, rs);			
		}
		return null;
	}
	
	@Test
	public void testQuery1() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			// 1. 獲取資料庫的連接
			conn = JDBCUtils.getConnection();
			// 2. 預編譯 sql 語句，返回 PreparedStatement 實例
			String sql = "SELECT id, name, email, birth FROM customers WHERE id = ?";
			ps = conn.prepareStatement(sql);
			// 3. 填充佔位符
			ps.setObject(1, 1); // 使用通用的操作
			
			// 4. 執行並返回結果集
			resultSet = ps.executeQuery();
			//5. 處理結果集
			// 集合中的hasNext() 只有判斷下一條有無資料並返回布林值，指針下移與返回是 靠next()
			// ResultSet的next() 除了判斷下一條有無資料並返回布林值外, 如果 true 指針就下移，false 指針就不下移
			if(resultSet.next()) { // next(): 判斷結果集的下一條是否有數據，如果有返回 true 並指針下移，如果返回 false 指針不會下移
				// 獲取當前這條資料各個欄位值
				int id = resultSet.getInt(1);
				String name = resultSet.getString(2);
				String email = resultSet.getString(3);
				Date birth = resultSet.getDate(4);
				
				// 方式一: 
				// System.out.println("id = " + id + " ,name = " + name + " ,email = " + email + " ,birth = " + birth);
				// 方式二:
				// Object[] data = new Object[]{id, name, email, birth};
				// 方式三: 將資料封裝為一個物件(推薦)
				Customer customer = new Customer(id, name, email, birth);
				System.out.println(customer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 5. 資源的關閉
			JDBCUtils.closeResource(conn, ps, resultSet);			
		}
	}
}
