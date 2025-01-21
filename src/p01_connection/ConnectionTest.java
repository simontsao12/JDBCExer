package p01_connection;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Properties;

import org.junit.jupiter.api.Test;

/**
 * ClassName: ConnectionTest.java
 * PackageName: p01_connection
 * Description: 
 */
public class ConnectionTest {
	// 方式一
	@Test
	public void testConnection1() throws Exception {
		// 獲取 Driver 的實現類對象
		// 要注意加載 com.mysql.jdbc.Driver 已經過時，建議改加載 com.mysql.cj.jdbc.Driver
		Driver driver = new com.mysql.jdbc.Driver(); // 以 MySQL 為例，左邊是介面，右邊想要少使用 MySQL 的實現類所以需要加載 MySQL 驅動
		
		// jdbc:mysql: 協議
		// localhost: ip 地址
		// 3306: 預設 MySQL port
		// test: test 資料庫
		String url = "jdbc:mysql://localhost:3306/test";
		
		// 將用帳號和密碼封裝在 Properties 中
		Properties info = new Properties();
		info.setProperty("user", "");
		info.setProperty("password", "");
		
		Connection conn = driver.connect(url, info);
	
		System.out.println(conn);
	}
	
	// 方式二: 對方式一的迭代
	// 介面導向，希望有更好的移植性，所以盡可能不出現任何第三方的 API
	@Test
	public void testConnection2() throws Exception {
		// 1. 使用反射取得 Driver 的實現類對象
		// 要注意加載 com.mysql.jdbc.Driver 已經過時，建議改加載 com.mysql.cj.jdbc.Driver
		Class clazz = Class.forName("com.mysql.jdbc.Driver");
	
		Driver driver = (Driver)clazz.newInstance(); // 注意 newInstance() 已經 deprecated
	
		// 2. 提供連接的資料庫
		String url = "jdbc:mysql://localhost:3306/test";
	
		// 3. 提供連接需要的帳號和密碼
		Properties info = new Properties();
		info.setProperty("user", "");
		info.setProperty("password", "");
		
		// 4. 取得連接
		Connection conn = driver.connect(url, info);
	
		System.out.println(conn);
	}
	
	// 方式三: 使用 DriverManager 替換 Driver
	@Test
	public void testConnection3() throws Exception {
		// 1. 取得 Driver 的實現類對象
		Class clazz = Class.forName("com.mysql.jdbc.Driver");
		
		Driver driver = (Driver)clazz.newInstance();
		
		// 2. 提供三個連接的基本訊息
		String url = "jdbc:mysql://localhost:3306/test";
		String user = "";
		String password = "";
		
		// 註冊驅動
		DriverManager.registerDriver(driver);
		
		// 取得連接
		Connection conn = DriverManager.getConnection(url, user, password);
		System.out.println(conn);
	}
	
	// 方式四: 基於方式三進行優化，可以只是加載驅動，不用特別去註冊驅動
	@Test
	public void testConnection4() throws Exception {
		// 1. 提供三個連接的基本訊息
		String url = "jdbc:mysql://localhost:3306/test";
		String user = "";
		String password = "";
		
		// 2. 加載 Driver
		// 要注意加載 com.mysql.jdbc.Driver 已經過時，建議改加載 com.mysql.cj.jdbc.Driver
		// 加載 com.mysql.jdbc.Driver 因為繼承自 com.mysql.cj.jdbc.Driver 中有靜態程式區塊會執行註冊
		Class.forName("com.mysql.jdbc.Driver"); // 這行其實也可以省，但是只有 MySQL 可以省，因為在 /META-INF/services/java.sql.Driver 有相關資訊，加載時就會處理
		/* 相較於方式三可以省略以下的操作
		 * Driver driver = (Driver)clazz.newInstance();
		 * 註冊驅動
		 * DriverManager.registerDriver(driver);
		 * 為何可以省略?
		 * 在 MySQL 的 Driver 實現類中，宣告了以下操作
		 * static {
	     *   try {
	     *       java.sql.DriverManager.registerDriver(new Driver());
	     *   } catch (SQLException E) {
	     *       throw new RuntimeException("Can't register driver!");
	     *   }
    	 * }
		 */
		
		// 3. 取得連接
		Connection conn = DriverManager.getConnection(url, user, password);
		System.out.println(conn);
	}
	
	// 方式五(最終版): 將資料庫連接需要的 4 個訊息宣告在設定檔中，通過讀取設定檔的方式取得連接
	// 此種方式的好處: 
	// 1. 實現了資料與程式碼的分離，實現了解耦
	// 2. 如果需要修改設定檔，可以避免重新打包
	@Test
	public void testConnection5() throws Exception {
		// 1. 讀取設定檔中的 4 個基本訊息
		InputStream is = ConnectionTest.class.getClassLoader().getResourceAsStream("jdbc.properties"); // 獲取系統類加載器再去讀取設定檔
		Properties pros = new Properties();
		pros.load(is);
		String user = pros.getProperty("user");
		String password = pros.getProperty("password");
		String url = pros.getProperty("url");
		String driverClass = pros.getProperty("driverClass");
		
		// 2. 加載驅動
		Class.forName(driverClass);
		
		// 3. 取得連接
		Connection conn = DriverManager.getConnection(url, user, password);
		System.out.println(conn);
	}
}
