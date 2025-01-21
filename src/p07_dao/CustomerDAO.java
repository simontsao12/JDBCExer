package p07_dao;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

import p04_bean.Customer;

/**
 * ClassName: CustomerDAO.java
 * PackageName: p07_dao
 * Description: 此介面用於規範 customers 表的常用操作
 */
public interface CustomerDAO {
	/**
	 * 將 cust 對象添加到資料庫中
	 */
	void insert(Connection conn, Customer cust);
	
	/**
	 * 針對指定的 id, 刪除表中的一條紀錄.
	 */
	void deleteById(Connection conn, int id);
	
	/**
	 * 針對記憶體中的 cust 對象, 去修改資料表中指定的紀錄.
	 */
	void update(Connection conn, Customer cust);
	/**
	 * 針對指定的 id 查詢得到對樣的 Customer 對象
	 */
	Customer getCustomerById(Connection conn, int id);
	
	/**
	 * 查詢表中的所有紀錄構成的集合
	 */
	List<Customer> getAll(Connection conn);
	
	/**
	 * 返回資料表中的資料的條數
	 */
	Long getCount(Connection conn);
	
	/**
	 * 返回資料表中最大的生日
	 */
	Date getMaxBirth(Connection conn);
	
}
