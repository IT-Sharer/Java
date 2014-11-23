/**
 * 
 */
package data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;

import knn.DistanceMethod;
import knn.DistanceMethod1;
import core.Instance;
import core.InstanceProcessMethod1;
import core.InstanceProcessMethod2;
import core.Instances;
import core.LoadSqlData;
import core.SQLHelper;
import core.Save2CSV1;

/**
 * @author benfenghua
 *产生聚集
 */
public class GenerateCluster {
	int k;//num of clusters
	//ArrayList<Integer> [] clustersArrayLists=new ArrayList<Integer>[k];//instance ids in clusters;
	StringBuffer [] clusterStrings=new StringBuffer[k];
	Instances instances1;//instances to be clustered;
	Instance [] kClusterCenter=new Instance[k];//the center of K clusters;
	HashMap<String, Double []> tailDoubleHashMap=new HashMap<>();
	SQLHelper sqlHelper=new SQLHelper();
	InstanceProcessMethod2 instanceProcessMethod2=new InstanceProcessMethod2();
	InstanceProcessMethod1 instanceProcessMethod1=new InstanceProcessMethod1("method1");
	DistanceMethod distanceMethod=new DistanceMethod1();
	LoadSqlData loadSqlData;
	String tablenameString="Kddcup99_no1";
	boolean finished=false;
	Save2CSV1 save2csv1=new Save2CSV1();
	/**
	 * initial K Center Instances,Random method
	 */
	public void InitKCenter(){
		//加载每个属性的最值
		String sqlString="SELECT [attributeName],min(rate) as minvalue ,max(rate) as maxvalue  FROM [NTDM].[dbo].[AttributeDistribute]"
				+ "group by attributeName  union  select attributeName,amin as minvalue,amax as maxvalue"
				+ "  from [dbo].[AttributeContinuous]";
		ResultSet resultSet=sqlHelper.executeSql(sqlString);
		try {
			while(resultSet.next()){
				String attributenameString=resultSet.getString("attributeName").trim();
				double minvalue=resultSet.getDouble("minvalue");
				double maxvalue=resultSet.getDouble("maxvalue");
//				if(attributenameString.equals("[protocol_type]")){
//					System.out.println(minvalue+","+maxvalue);
//				}
				tailDoubleHashMap.put(attributenameString, new Double[]{minvalue,maxvalue});
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(tailDoubleHashMap.size());
//		for(String att:tailDoubleHashMap.keySet()){
//			System.out.println(att);
//		}
		//初始化出K个最初的聚集中心；用随机函数
		Instance instance=instances1.getInstance(0);
		String [] attributeStrings=instance.getAttrNames();
		for(int i=0;i<k;i++){
			Instance instance2=instance.clone();
			double []cont=instance2.getContinuousAttributes();
			for(int j=0;j<attributeStrings.length;j++){
				Double[] minmax=tailDoubleHashMap.get(attributeStrings[j]);
//				System.out.println(attributeStrings[j]);
				double min=minmax[0];
				double max=minmax[1];
				double random=Math.random()*(max-min)+min;
				cont[j]=random;	
				}
			instance2.setContinuousAttributes(cont);
			instanceProcessMethod2.processInstance(instance2);
			kClusterCenter[i]=instance2;
			System.out.println("正在产生随机实例》》》");
		}
	}
	public void InitKCenter2(){
		int [] nos=new int[k];
		StringBuffer condt=new StringBuffer("no in( ");
		Random rdRandom=new Random();
		for(int i=0;i<k-1;i++){			
			nos[i]=rdRandom.nextInt(KDDCUP99Process.allnum);
			condt.append(nos[i]+",");
		}
		nos[k-1]=(int)Math.random()*KDDCUP99Process.allnum;
		condt.append(nos[k-1]+" )");
		System.out.println(condt.toString());
		loadSqlData=new LoadSqlData(tablenameString, condt.toString());
		Instances KC=loadSqlData.loadInstances("Kcenter");
		KC=instanceProcessMethod1.processInstances(KC);
		KC=instanceProcessMethod2.processInstances(KC);
		save2csv1.SaveInstances(KC, "E:/dataProcess/KCenterInit0805.txt");
		for(int i=0;i<k&&i<KC.getCount();i++){
			kClusterCenter[i]=KC.getInstance(i);
		}	
		k=KC.getCount();
	}
	/**
	 * @param k
	 */
	public GenerateCluster(int k) {
		String conditionString=" no < '100' ";
		loadSqlData=new LoadSqlData(tablenameString, conditionString);
		this.instances1=loadSqlData.loadInstances("base");
		instances1=instanceProcessMethod1.processInstances(instances1);
		instances1=instanceProcessMethod2.processInstances(instances1);
		this.k=k;
		kClusterCenter=new Instance[k];
		clusterStrings=new StringBuffer[k];
		for(int i=0;i<k;i++){			
			clusterStrings[i]=new StringBuffer(" ");
		}
		// TODO Auto-generated constructor stub
	}
	/**
	 * 根据聚集重新设置聚集中心
	 */
	public void ResetKcenters(){
		double distanceAll=0.0;
		for(int i=0;i<k;i++){			
			String nostring=clusterStrings[i].toString();	
			if(nostring.length()<=2){
				System.out.println(i);
				continue;
			}
			nostring=nostring.substring(0, nostring.length()-2);//要判断是否为空？？？
			String [] noStrings=nostring.split(",");
			int numofClusters=noStrings.length;//聚集中元素个数，
			int numOnetime=100000;//每次处理的最大数量
			int cursor=0;
			//聚类中心求均值
			Instance instance=kClusterCenter[i].clone();
			double [] cont=new double[instance.getContinuousAttributes().length];
			if(numofClusters<=numOnetime){
				//一次处理			
				loadSqlData=new LoadSqlData(tablenameString, "no in("+nostring+")");
				Instances instances=loadSqlData.loadInstances("cluster"+i);
				instances=instanceProcessMethod1.processInstances(instances);
				instances=instanceProcessMethod2.processInstances(instances);
				int num=instances.getCount();
				for(int j=0;j<num;j++){
					Instance instance2=instances.getInstance(j);
					double [] cont2=instance2.getContinuousAttributes();
					for(int m=0;m<cont.length;m++){
						cont[m]+=cont2[m];
					}
				}
			}else{//多次处理
				int start=0,end=0;
				end=nostring.indexOf(","+noStrings[cursor+numOnetime]+",");
				while(cursor+numOnetime<numofClusters){
					String condt=nostring.substring(start, end);
					loadSqlData=new LoadSqlData(tablenameString, "no in("+condt+")");
					Instances instances=loadSqlData.loadInstances("cluster"+i);
					if(instances==null)continue;
					instances=instanceProcessMethod1.processInstances(instances);
					instances=instanceProcessMethod2.processInstances(instances);
					int num=instances.getCount();
					for(int j=0;j<num;j++){
						Instance instance2=instances.getInstance(j);
						double [] cont2=instance2.getContinuousAttributes();
						for(int m=0;m<cont.length;m++){
							cont[m]+=cont2[m];
						}
					}
					start=end;
					cursor+=numOnetime;
					if(cursor+numOnetime<numofClusters){
						end=nostring.indexOf(","+noStrings[cursor+numOnetime]+",");
					}
					else end=nostring.length();
				}
				String condt=nostring.substring(start, end);
				loadSqlData=new LoadSqlData(tablenameString, "no in("+condt+")");
				Instances instances=loadSqlData.loadInstances("cluster"+i);
				if(instances!=null){
				instances=instanceProcessMethod1.processInstances(instances);
				instances=instanceProcessMethod2.processInstances(instances);
				int num=instances.getCount();
				for(int j=0;j<num;j++){
					Instance instance2=instances.getInstance(j);
					double [] cont2=instance2.getContinuousAttributes();
					for(int m=0;m<cont.length;m++){
						cont[m]+=cont2[m];
					}
				}
				}
			}
			for(int m=0;m<cont.length;m++){
				cont[m]/=numofClusters;
			}
			instance.setContinuousAttributes(cont);
			distanceAll+=instance.Distance(distanceMethod, kClusterCenter[i]);
			kClusterCenter[i]=instance;
		}
		System.out.println(distanceAll);
		if(distanceAll<0.0001)finished=true;
	}
	/**
	 * 根据聚集中心重新生成聚集
	 */
	public void ResetClusters(){
		if(kClusterCenter==null)return;
		for(int i=0;i<k;i++){
			int len=clusterStrings[i].length();
			clusterStrings[i].delete(1, len);
		}
		int numOnetime=30000;//每次处理的数量
		int allnum=KDDCUP99Process.allnum;//总数据量
		int cursor=0;
		for(;cursor<allnum;cursor+=numOnetime){
			String conditString=" no >'"+cursor+"' and no<'"+(cursor+numOnetime)+"' ";
			loadSqlData=new LoadSqlData(tablenameString, conditString);
			Instances instances=loadSqlData.loadInstances(" "+cursor);
			int count=instances.getCount();
			for(int i=0;i<count;i++){
				Instance instance=instances.getInstance(i);
				instance=instanceProcessMethod1.processInstance(instance);
				instance=instanceProcessMethod2.processInstance(instance);
				int min=0;
				double mindis=Double.MAX_VALUE;
				for(int j=0;j<k;j++){
					double dis=instance.Distance(distanceMethod, kClusterCenter[j]);
					if(dis<mindis){
						min=j;
						mindis=dis;
					}
				}
				clusterStrings[min].append(instance.getInstanceTagString()+",");				
			}
			System.out.println("生成聚集中。。。");
		}
		System.out.println("聚集产生完毕！");
		for(int i=0;i<k;i++){
			if (clusterStrings[i].length()<=2) {
				System.out.println((i));
			}
		}
		
	}
	public void Cluster(){
		InitKCenter2();
		int i=1;
		while(!finished){
			//根据聚集中心生成聚集
			System.out.println("第"+i+++"次聚类迭代！");
			ResetClusters();
			//产生新的聚集中心,并且评估
			ResetKcenters();	
		}
		//输出最终的聚类结果
		System.out.println();
	}
	/**evaluate clusters
	 * @return
	 */
	public boolean EvaluateClusters(){
		
		return false;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GenerateCluster generateCluster=new GenerateCluster(600);
		generateCluster.Cluster();
	}

}
