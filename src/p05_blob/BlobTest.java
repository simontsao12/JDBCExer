package p05_blob;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.jupiter.api.Test;

import p02_util.JDBCUtils;
import p04_bean.Customer;

/**
 * ClassName: BlobTest.java
 * PackageName: p05_blob
 * Description: 測試使用 PreparedStatement 操作 Blob 類型的資料
 */
public class BlobTest {
	// 向資料表 customers 中插入 Blob 類型的資料
	@Test
	public void testInsert() throws Exception {
		Connection conn = JDBCUtils.getConnection();
		String sql = "INSERT INTO customers(name, email, birth, photo) VALUES(?, ?, ?, ?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		
		ps.setObject(1, "純純");
		ps.setObject(2, "tt@cc.com");
		ps.setObject(3, "1995-01-01");
		FileInputStream is = new FileInputStream(new File("girl.jpg"));
		ps.setBlob(4, is);
		ps.execute();
		JDBCUtils.closeResource(conn, ps);
	}
	// 向資料表 customers 中插入 Blob 類型的資料(自己改成 try-catch)
	@Test
	public void testInsert2() {
		Connection conn = null;
		PreparedStatement ps = null;
		FileInputStream is = null;
		try {
			conn = JDBCUtils.getConnection();
			String sql = "INSERT INTO customers(name, email, birth, photo) VALUES(?, ?, ?, ?)";
			ps = conn.prepareStatement(sql);
			ps.setObject(1, "純純2");
			ps.setObject(2, "tt2@cc.com");
			ps.setObject(3, "1995-01-01");
			is = new FileInputStream(new File("girl.jpg"));
			ps.setBlob(4, is);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {			
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			JDBCUtils.closeResource(conn, ps);
		}
	}
	
	// 查詢資料表 customers 中 Blob 類型的欄位
	@Test
	public void testQuery() {
		Connection conn = null;
		PreparedStatement ps = null;
		InputStream is = null;
		FileOutputStream fos = null;
		ResultSet rs = null;
		try {
			conn = JDBCUtils.getConnection();
			String sql = "SELECT id, name, email, birth, photo FROM customers WHERE id = ?";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, 12);
			rs = ps.executeQuery();
			if(rs.next()) {
				// 方式一: 
				// int id = rs.getInt(1);
				// String name = rs.getString(2);
				// String email = rs.getString(3);
				// Date birth = rs.getDate(4);
			
				// 方式二: 
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				Date birth = rs.getDate("birth");
				
				Customer cust = new Customer(id, name, email, birth);
				System.out.println(cust);
				
				// 將 Blob 類型的欄位下載下來，以檔案的方式保存在本地
				Blob photo = rs.getBlob("photo");
				is = photo.getBinaryStream();
				fos = new FileOutputStream(new File("純純.jpg"));
				
				byte[] buffer = new byte[1024];
				int len;
				while((len = is.read(buffer))!= -1) {
					fos.write(buffer, 0, len);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			JDBCUtils.closeResource(conn, ps, rs);
		}
	}
}
