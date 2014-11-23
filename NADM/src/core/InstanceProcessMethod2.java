package core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class InstanceProcessMethod2 extends InstanceProcess {
	public static HashMap<String, Double[]> meanStvHashMap=new HashMap<>();
	private void GenerateMap(){
		if(meanStvHashMap.size()>0)return;
		SQLHelper sqlHelper=new SQLHelper();
		String sqlString="select [attributeName] ,[meanValue] ,[astdev] from [AttributeContinuous]";
		ResultSet resultSet=sqlHelper.executeSql(sqlString);
		try {
			while(resultSet.next()){
				String nameString=resultSet.getString(1);
				double valueString=resultSet.getDouble(2);
				double weight=resultSet.getDouble(3);
				meanStvHashMap.put(nameString.trim(), new Double[]{valueString,weight});
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public InstanceProcessMethod2() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public Instance processInstance(Instance instance) {
		// TODO Auto-generated method stub
		GenerateMap();
		String [] attStrings=instance.getAttrNames();
		double [] conti=instance.getContinuousAttributes();
		for(int i=9;i<attStrings.length;i++){
//			System.out.println(attStrings[i]);
			double mean=meanStvHashMap.get(attStrings[i])[0];
			double std=meanStvHashMap.get(attStrings[i])[1];
			if (std<-0.0000001||std>0.0000001) conti[i]=(conti[i]-mean)/std;
		}
		return instance;
	}

}
