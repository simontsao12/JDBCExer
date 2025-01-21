package p06_transaction;

import java.sql.Connection;

import org.junit.jupiter.api.Test;

import p02_util.JDBCUtils;

/**
 * ClassName: ConnectionTest.java
 * PackageName: p06_transaction
 * Description: 
 */
public class ConnectionTest {
	@Test
	public void testGetConnection() throws Exception {
		Connection conn = JDBCUtils.getConnection();
		System.out.println(conn);
	}
}
