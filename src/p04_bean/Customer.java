package p04_bean;

import java.sql.Date;

/**
 * ClassName: Customer.java
 * PackageName: com.atsimoncc3.bean
 * Description: 
 */
/*
 * ORM(object relational mapping) 程式設計思想: 
 * 一個資料表對應一個 java 類
 * 表中的一條紀錄對應 java 類的一個對象
 * 表中的一個欄位對應 java 類的一個屬性
 */
public class Customer {
	
	private int id;
	
	private String name;
	
	private String email;
	
	private Date birth;
	
	public Customer() {
		super();
	}
	
	public Customer(int id, String name, String email, Date birth) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.birth = birth;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public Date getBirth() {
		return birth;
	}
	
	public void setBirth(Date birth) {
		this.birth = birth;
	}
	
	@Override
	public String toString() {
		return "Customer [id=" + id + ", name=" + name + ", email=" + email + ", birth=" + birth + "]";
	}
}
