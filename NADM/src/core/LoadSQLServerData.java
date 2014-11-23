package core;

import java.sql.Connection;

public class LoadSQLServerData extends LoadData {
	protected Connection connection;
	protected String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";   //加载JDBC驱动  
	protected String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=NTDM";   //连接服务器和数据库sample  
	protected String userName = "ntdmreader";   //默认用户名  
	protected String userPwd = "ntdmntdm";   //密码  
	
	@Override
	public Instances loadInstances(String string) {
		// TODO Auto-generated method stub
		return null;
	}

}
