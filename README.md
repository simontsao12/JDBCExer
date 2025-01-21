# JDBCExer

## 專案概述

JDBCExer 是一個基於 Java 的學習專案，旨在練習 JDBC（Java Database Connectivity）相關的概念與操作。專案按照不同的學習目標進行分包，每個套件包含相關的測試程式與實現程式碼。


## 專案結構

### 1. 資料庫連線

**套件：** `p01_connection`

- **目標：** 測試資料庫連線的基本操作。
- **主要檔案：**
  - `ConnectionTest.java`：測試 JDBC 連線的基本流程。

### 2. JDBC 工具類

**套件：** `p02_util`

- **目標：** 提供 JDBC 工具類，簡化資料庫操作。
- **主要檔案：**
  - `JDBCUtils.java`：封裝 JDBC 連線、關閉資源等實用方法。

### 3. 基本增刪改查操作

**套件：** `p03_statement.crud`

- **目標：** 使用 PreparedStatement 進行增刪改查操作。
- **主要檔案：**
  - `PreparedStatementTest.java`：展示增刪改查操作的範例。
  - `User.java`：用於存儲用戶資料的簡單 JavaBean。

### 4. JavaBean 定義

**套件：** `p04_bean`

- **目標：** 定義與資料庫表對應的 JavaBean。
- **主要檔案：**
  - `Customer.java`：與客戶表對應的實體類。
  - `Order.java`：與訂單表對應的實體類。

### 5. 複雜查詢與更新

**套件：** `p04_preparedstatement.crud`

- **目標：** 更深入地使用 PreparedStatement 進行複雜查詢與更新。
- **主要檔案：**
  - `CustomerForQuery.java`：針對客戶表的查詢操作。
  - `PreparedStatementQueryTest.java`：查詢範例。
  - `PreparedStatementUpdateTest.java`：更新範例。

### 6. 操作 BLOB 資料

**套件：** `p05_blob`

- **目標：** 使用 BLOB（二進制大對象）進行資料庫操作。
- **主要檔案：**
  - `BlobTest.java`：演示如何操作 BLOB 資料。
  - `InsertTest.java`：批量插入資料範例。

### 7. 交易處理

**套件：** `p06_transaction`

- **目標：** 理解並實現資料庫的交易處理。
- **主要檔案：**
  - `TransactionTest.java`：展示交易的基本應用。
  - `ConnectionTest.java`：測試支援交易的連線管理。

### 8. 資料訪問層（DAO）

**套件：** `p07_dao` 和 `p08_dao`

- **目標：** 實現資料訪問層（DAO）的設計模式。
- **主要檔案：**
  - `BaseDAO.java`：基礎資料訪問類。
  - `CustomerDAO.java` 和 `CustomerDAOImpl.java`：針對客戶表的資料訪問操作。
  - `CustomerDAOImplTest.java`：對 DAO 的測試。

### 9. 資料源連線池

**套件：** `p09_connection`

- **目標：** 測試不同的資料源連線池（如 C3P0、DBCP、Druid）。
- **主要檔案：**
  - `C3P0Test.java`：測試 C3P0 資料源的使用。
  - `DBCPTest.java`：測試 DBCP 資料源的使用。
  - `DruidTest.java`：測試 Druid 資料源的使用。

### 10. 使用 DbUtils 簡化操作

**套件：** `p10_dbutils`

- **目標：** 使用 Apache Commons DbUtils 簡化 JDBC 操作。
- **主要檔案：**
  - `QueryRunnerTest.java`：展示 DbUtils 的使用範例。

## 設定檔

- `c3p0-config.xml`：C3P0 資料源的設定檔。
- `dbcp.properties`：DBCP 資料源的設定檔。
- `druid.properties`：Druid 資料源的設定檔。
- `jdbc.properties`：通用 JDBC 配置。

## 使用方式

1. **安裝依賴：** 確保已安裝 JDK 和相關資料庫。
2. **配置資料庫：** 修改 `jdbc.properties` 等設定檔中的參數，以匹配您的資料庫設定。
3. **導入專案：** 使用 IDE（如 IntelliJ IDEA 或 Eclipse）導入專案。
4. **執行測試：** 選擇並運行對應的測試類進行學習。

## 注意事項

- 確保資料庫服務已啟動，並正確設定用戶名與密碼。
- 在執行涉及 BLOB 的操作時，請檢查相關文件路徑是否有效。

## 主要學習資源

- 【尚硅谷】【宋紅康】JDBC核心技術 2019最新版。

## 貢獻者

此專案僅為學習用途。
