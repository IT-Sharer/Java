package data;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import core.FileReadAndWriter;
import core.Instance;
import core.InstanceProcessMethod1;
import core.InstanceProcessMethod2;
import core.Instances;
import core.LoadSqlData;
import core.SQLHelper;

/**
 * @author benfenghua
 *数据处理内容：
 *1.统计所有离散属性的所有组合情况，683种，KDDcup99_Discrete
 *2.统计每个离散属性的情况及分布，AttributeDistribute 函数statistic1
 *3.统计连续属性的最大值，最小值，方差，标准差，statistic2  AttributeContinuous
 *4.根据组合及分布情况，按比例选择数据集，离散属性在前，连续属性在后，保存到CSV格式的文件中，原，标准化之后的 dataSelector1
 *5.根据CSV加载，数据，指定离散型和连续型数据的序号
 *6.根据instance对离散型的数据进行转换，生成指定的instance。
 *7.抽样获取测试实例集
 *
 *
 */
public class KDDCUP99Process {
	public static int allnum=1074992;//4898431;
	public static String [] discreteAttributes=("[protocol_type],"
			+ "[service],[flag] ,[land],[logged_in]"
			+ " ,[root_shell] ,[su_attempted],[is_hot_login],[is_guest_login]").split(",");//离散型属性
	public static String [] continuousAttributes=("[duration],[src_bytes],[dst_bytes] ,[wrong_fragment],[urgent],"
			+ "[hot],[num_failed_logins],[num_compromised] ,[num_root],[num_file_creations]"
			+ ",[num_shells],[num_access_files],[num_outbound_cmds] ,[countlink],[srv_count]"
			+ ",[serror_rate],[srv_serror_rate],[rerror_rate],[srv_rerror_rate] "
			+ ",[same_srv_rate],[diff_srv_rate],[srv_diff_host_rate],[dst_host_count]"
			+ ",[dst_host_srv_count],[dst_host_same_srv_rate],[dst_host_diff_srv_rate]"
			+ " ,[dst_host_same_src_port_rate],[dst_host_srv_diff_host_rate]"
			+ ",[dst_host_serror_rate],[dst_host_srv_serror_rate],[dst_host_rerror_rate]"
			+ ",[dst_host_srv_rerror_rate]").split(",");//连续型属性
	/**
	 * 对原始数据Kddcup99_no1进行处理，保存到表Kddcup99_no2
	 */
	public static void ProcessData(){
		int numOneTime=999;//每次处理的数据量
		int cursor=0;//游标
		InstanceProcessMethod1 instanceProcessMethod1=new InstanceProcessMethod1("1");
		InstanceProcessMethod2 instanceProcessMethod2=new InstanceProcessMethod2();
		SQLHelper sqlHelper=new SQLHelper();
		while(cursor<allnum){
			StringBuffer sqlBuffer=new StringBuffer();
			sqlBuffer.append("insert into Kddcup99_no2 (no,");
			
			LoadSqlData loadSqlData=new LoadSqlData("Kddcup99_no1", " no>'"+cursor+"' and no<='"+(cursor+numOneTime)+"' ");
			Instances instances=loadSqlData.loadInstances("temp");
			int count=instances.getCount();
			if(count>0){
				String [] attStrings=instances.getInstance(0).getAttrNames();
				for(int i=0;i<attStrings.length;i++){
					sqlBuffer.append(attStrings[i]+",");
				}
				sqlBuffer.append("classlabel) values ");
				instances=instanceProcessMethod1.processInstances(instances);
				instances=instanceProcessMethod2.processInstances(instances);
				for(int i=0;i<count-1;i++){
					sqlBuffer.append("(");
					Instance instance=instances.getInstance(i);
					sqlBuffer.append(instance.getInstanceTagString()+",");
					double [] con=instance.getContinuousAttributes();
					for(int j=0;j<con.length;j++){
						sqlBuffer.append(con[j]+",");
					}
					sqlBuffer.append("'"+instance.getLabel().trim()+"'),");					
				}
				sqlBuffer.append("(");
				Instance instance=instances.getInstance(count-1);
				sqlBuffer.append(instance.getInstanceTagString()+",");
				double [] con=instance.getContinuousAttributes();
				for(int j=0;j<con.length;j++){
					sqlBuffer.append(con[j]+",");
				}
				sqlBuffer.append("'"+instance.getLabel().trim()+"')");	
			}
			cursor+=numOneTime;
//			System.out.println(sqlBuffer.toString());
//			System.exit(0);
			sqlHelper.executeSQL(sqlBuffer.toString());
		}
	}
	public static SQLHelper sqlHelper=new SQLHelper();
	String sqlString="";
	public static void statistic1(){
		for (String  attributename : discreteAttributes ) {
			String sqlString="insert into AttributeDistribute "+
		" select '"+attributename+"' as attributeName,"+attributename+" as attributeValue,count(*) as sumcount,count(*)*1.0/"+allnum+" as rate from KDDcup99 "
				+ " group by "+attributename;
			if(sqlHelper.executeSQL(sqlString))System.out.println(attributename+" is processed successfully!");
		}
	}
	public static void statistic2(){
		for (String  attributename : continuousAttributes ) {
			String sqlString="insert into AttributeContinuous "+
		" select '"+attributename+"' as attributeName,AVG("+attributename+") "+",MAX("+attributename+") "+
					",MIN("+attributename+") "+",STDEV("+attributename+") "+",STDEVP("+attributename+") "+
		",VAR("+attributename+") "+",VARP("+attributename+") From KDDcup99";
			if(sqlHelper.executeSQL(sqlString))System.out.println(attributename+" is processed successfully!");
		}
	}
	/**
	 * 
	 * @param pathString
	 */
	public static void dataSelector1(String pathString){
		String sqlString="select * from KDDcup99_Discrete";
		ResultSet resultSet=sqlHelper.executeSql(sqlString);
		try {
			StringBuffer attributeBuffer=new StringBuffer();
			attributeBuffer.append("no,");
			for(int i=0;i<discreteAttributes.length;i++){
				attributeBuffer.append(discreteAttributes[i]+",");
			}
			for(int i=0;i<continuousAttributes.length;i++){			
				attributeBuffer.append(continuousAttributes[i]+",");				
			}
			attributeBuffer.append("classLabel ");
			//写入标签头
			FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(pathString, false,true);
			fileReadAndWriter.WriteLine(attributeBuffer.toString());
			ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
			while(resultSet.next()){
				String [] resultStrings=new String[10];
				StringBuffer conditionString=new StringBuffer();
				for(int i=1;i<10;i++){
					resultStrings[i]=resultSet.getString(i);
					conditionString.append(resultSetMetaData.getColumnName(i)+"='"+resultSet.getString(i)+"' and ");
				}
				conditionString.append(resultSetMetaData.getColumnName(10)+"='"+resultSet.getString(10)+"' ");
				int sumcount=resultSet.getInt(11);
				int numOfInstances=sumcount*9900/allnum;
				if (numOfInstances<5) {
					numOfInstances=4;
				}
				//按条件选取数据
				sqlString="select top "+numOfInstances+" ";
				
				sqlString=sqlString+attributeBuffer.toString()+" from  Kddcup99_no1 where ";
				sqlString=sqlString+conditionString.toString();
				ResultSet resultSet2=sqlHelper.executeSql(sqlString);
				String [] strings =new String[7];
				strings[0]=pathString;
				writeResultSet(resultSet2,pathString);				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void dataSelector2(String pathString){
		String sqlString="select * from KDDcup99_Discrete";
		ResultSet resultSet=sqlHelper.executeSql(sqlString);
		try {
			StringBuffer attributeBuffer=new StringBuffer();
			attributeBuffer.append("no,");
			for(int i=0;i<discreteAttributes.length;i++){
				attributeBuffer.append(discreteAttributes[i]+",");
			}
			for(int i=0;i<continuousAttributes.length;i++){			
				attributeBuffer.append(continuousAttributes[i]+",");				
			}
			attributeBuffer.append("classLabel ");
			//写入标签头
			FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(pathString, false,true);
			fileReadAndWriter.WriteLine(attributeBuffer.toString());			
			ResultSetMetaData resultSetMetaData=resultSet.getMetaData();			
			while(resultSet.next()){
				String [] resultStrings=new String[10];
				StringBuffer conditionString=new StringBuffer();
				for(int i=1;i<10;i++){
					resultStrings[i]=resultSet.getString(i);
					conditionString.append(resultSetMetaData.getColumnName(i)+"='"+resultSet.getString(i)+"' and ");
					}
				conditionString.append(resultSetMetaData.getColumnName(10)+"='"+resultSet.getString(10)+"' ");
				int sumcount=resultSet.getInt(11);
				int numOfInstances=sumcount*9900/allnum;
				if (numOfInstances<10) {
					numOfInstances=9;
					sqlString="select top "+numOfInstances+" ";
					sqlString+=attributeBuffer.toString()+" from  Kddcup99_no1 where ";
					sqlString+=conditionString;
					ResultSet resultSet2=sqlHelper.executeSql(sqlString);
					writeResultSet(resultSet2,pathString);		
				}else {
					String sqlString2="Select no=Identity(int,1,1),* Into #temptable From   Kddcup99_no1 where ";
					sqlString2=sqlString+conditionString;
					sqlHelper.executeSQL(sqlString2);
					sqlString="select  ";
					sqlString=sqlString+attributeBuffer.toString()+" from  #temptable where ";
					sqlString=sqlString+" no>='"+(numOfInstances-10)+
							"' and no<='"+(numOfInstances+20)+"' ";
					ResultSet resultSet2=sqlHelper.executeSql(sqlString);
					writeResultSet(resultSet2,pathString);
					sqlHelper.executeSQL("Drop Table #temptable");
				}
				
						
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 将数据转化成double型的数据保存到另一个表中
	 * 
	 */
	public static void dataTransform(){
		InstanceProcessMethod1 inProcessMethod1=new InstanceProcessMethod1("");
		inProcessMethod1.GenerateMap();
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		statistic1();
//		statistic2();
//		String pathString="E:/dataProcess/sample1.csv";
//		dataSelector1(pathString);
//		String pathString="E:/dataProcess/TestSample1.csv";
//		dataSelector2(pathString);
		ProcessData();
	}
	private static void writeResultSet(ResultSet resultSet,String pathString){
		if(resultSet==null) return;
		System.out.println("正在写入数据：----");
		System.out.println(pathString);
		try {
			ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
			int columnCount=resultSetMetaData.getColumnCount();
			int count=1,limit=100;
			FileReadAndWriter fileReadAndWriter;
			StringBuffer string=new StringBuffer("");
			while(resultSet.next()){
				if(count==limit){
					fileReadAndWriter=new FileReadAndWriter(pathString, false, true);	
					for(int i=1;i<columnCount;i++){
						string.append(resultSet.getString(i)+",");
					}
					string.append(resultSet.getString(columnCount)+"\r\n");
					fileReadAndWriter.Write(string.toString());
					fileReadAndWriter.EndWrite();
					string.delete(0, string.length());
					count=1;
				}
				else{
					for(int i=1;i<columnCount;i++){
						string.append(resultSet.getString(i)+",");
					}
					string.append(resultSet.getString(columnCount)+"\r\n");
					count++;
				}
			}
			if(string.length()>0){
				fileReadAndWriter=new FileReadAndWriter(pathString, false, true);	
				fileReadAndWriter.Write(string.toString());
				fileReadAndWriter.EndWrite();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("完成写入数据！");
		
	}
}
