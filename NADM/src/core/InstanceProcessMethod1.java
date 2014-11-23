package core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author benfenghua
 *将离散属性转换为对应的占比重。
 *
 */
public class InstanceProcessMethod1 extends InstanceProcess {
	public static HashMap<String, Double> valueWeightHashMap=new HashMap<>();
	
	public InstanceProcessMethod1(String string) {
		// TODO Auto-generated constructor stub
		methodNameString=string;
		GenerateMap();
	}
	/**
	 * 从数据库中抽取SQL离散属性值比重的数据
	 */
	public void GenerateMap(){
		if (valueWeightHashMap.size()>0) {
			return;
		}
		SQLHelper sqlHelper=new SQLHelper();
		String sqlString="select * from AttributeDistribute";
		ResultSet resultSet=sqlHelper.executeSql(sqlString);
		try {
			while(resultSet.next()){
				String nameString=resultSet.getString(1);
				String valueString=resultSet.getString(2);
				double weight=resultSet.getDouble(4);
				valueWeightHashMap.put(nameString.trim()+valueString.trim(), weight);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(valueWeightHashMap.size());
	}
	@Override
	public Instance processInstance(Instance instance) {
		// TODO Auto-generated method stub
		Instance instance2=new Instance(instance.getInstanceTagString());
		double [] allattr=new double[instance.getContinuousAttributes().length+instance.getDisperseAttributes().length];
		int i=0;
		for(;i<instance.getDisperseAttributes().length;i++){
			try {
				allattr[i]=valueWeightHashMap.get(instance.getAttrNames()[i].trim()+instance.getDisperseAttributes()[i].trim());			
			} catch (Exception e) {
				// TODO: handle exception
				allattr[i]=0.0;
				System.out.println(i+instance.getAttrNames()[i].trim());
			}
			}
		for(;i<allattr.length;i++){
			allattr[i]=instance.getContinuousAttributes()[i-instance.getDisperseAttributes().length];
		}
		instance2.setContinuousAttributes(allattr);
		instance2.setLabel(instance.getLabel());
		instance2.setAttrNames(instance.getAttrNames());
		return instance2;
	}

}
