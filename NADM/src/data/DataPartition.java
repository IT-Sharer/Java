package data;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import knn.DistanceMethod;
import knn.DistanceMethod1;
import core.FileReadAndWriter;
import core.Instance;
import core.Instances;
import core.LoadCSVData1;
import core.LoadSqlData;
import core.Save2CSV1;

/**
 * @author benfenghua
 *采用混合的数据划分算法。对数据进行划分。
 */
public class DataPartition {
	String msgFromMain="";//从主线程传入的消息，用于开辟另外线程运行程序的情况。
	String logId="";
	boolean debug=true;
	LoadSqlData loadSqlData=new LoadSqlData();
	LoadCSVData1 loadCSVData1;
	DistanceMethod distanceMethod=new DistanceMethod1();
	Instances instances;
	String viewName="KDDCup99_0_train";
	Instances clusterCenters=new Instances("DataPartition");//聚集中心
	ArrayList<Instances> clusterMembers=new ArrayList<>();//对应每一个聚集中心的样本集合
	ArrayList<Double> maxR4Clusters=new ArrayList<>();//对应每个聚集中心最大的半径。
	int Vmin,Vmax,initN;
	String pathString="E:/dataProcess/newAlgorithm/";
	boolean dataOK=false;
	public DataPartition(int N,int min,int max) {
		// TODO Auto-generated constructor stub
		initN=N;
		Vmin=min;
		Vmax=max;
	}
	public DataPartition(int N,int min,int max,String path) {
		// TODO Auto-generated constructor stub
		initN=N;
		Vmin=min;
		Vmax=max;
		pathString=path;
		File file=new File(path+viewName+"_centers.csv");
		if (file.exists()) {
			loadCSVData1=new LoadCSVData1();
			clusterCenters=loadCSVData1.loadInstances1(path+viewName+"_centers.csv");
			for(int i=0;i<clusterCenters.getCount();i++){
				Instances inst=loadCSVData1.loadInstances1(path+viewName+"_"+i+".csv");
				clusterMembers.add(inst);
				double maxr=inst.getMaxDis(clusterCenters.getInstance(i), distanceMethod);
				maxR4Clusters.add(maxr);
			}
			dataOK=true;
		}
		else {
			Cluster();
		}
		System.out.println("load data complete!");
		logId=System.currentTimeMillis()+"";
	}
	
	public void recieveMsg(String msg){
		msgFromMain=msg;
	}
	
	
	/**
	 * 1.产生符合条件的聚集。
	 * 2.保存聚集到文件。
	 * 3.记录产生聚集所费时间，尽量改进算法使得时间最短。
	 * 先试试，内存完全够用的情况。
	 * 注意清理垃圾池
	 * @param initN 初始聚集个数
	 * @param Vmin 聚集最小容量
	 * @param Vmax 聚集最大容量
	 */
 	public void partition(){		
		PrintDebug(""+System.currentTimeMillis());
		//初始化聚集中心,选择其中的若干个点
		PrintDebug("初始化聚集中心！");
		String sqlString="select * from (select top "+initN/2+" * from "+viewName+" union select top "+initN/2+" * from "+viewName+" order by no desc) as b";
		PrintDebug(sqlString);
		clusterCenters=loadSqlData.loadInstances3(sqlString);		
		//初始化clusterMembers和maxR4Clusters
		PrintDebug("初始化聚集！");
		instances=loadSqlData.loadInstances3("select * from "+viewName);
		for(int i=0;i<clusterCenters.getCount();i++){
			Instances instances2=new Instances(""+i);
			clusterMembers.add(instances2);
			maxR4Clusters.add(0.0);
		}
		PrintDebug("加载数据完成！");
		PrintDebug(""+instances.getCount());
		for(int i=0;i<instances.getCount();i++){
			Instance instance=instances.getInstance(i);
			int loc=0;
			double dis=Double.MAX_VALUE;
			for(int j=0;j<clusterCenters.getCount();j++){//找出最近的聚集中心
				double tmp=instance.Distance(distanceMethod, clusterCenters.getInstance(j));
				if(tmp<=dis){
					loc=j;
					dis=tmp;
				}
			}
			clusterMembers.get(loc).AddInstance(instance);
			if(maxR4Clusters.get(loc)<dis){
				maxR4Clusters.remove(loc);
				maxR4Clusters.add(loc, dis);
			}			
		}
		PrintDebug("聚集初始化完成！");
		for(int i=maxR4Clusters.size()-1;i>=0;i--)
		PrintDebug(clusterMembers.get(i).getCount()+","+maxR4Clusters.get(i));
		//根据聚集中心进行聚集
		boolean stop=false;//停止条件
		while(!stop){
			PrintDebug("重新计算聚集中心");
			for(int i=0;i<clusterMembers.size();i++){
				Instances instances2=clusterMembers.get(i);
				Instance instance=instances2.CalCenter();
				clusterCenters.RemoveInstance(i);
				clusterCenters.AddInstance(i, instance);
				maxR4Clusters.remove(i);
				maxR4Clusters.add(i,instances2.getMaxDis(instance, distanceMethod));
			}
			//根据聚集中心，重新生成新的聚集？？？？
			
			
			//遍历每个聚集中心,不符合要求中心进行处理，并且设置stop=false,当遍历完成没有需要进行处理时,stop=TRUE
			stop=true;
			for (int i = 0; i < clusterMembers.size();i++) {
				Instances instances2=clusterMembers.get(i);
				//????这样还是不行，容易产生死循环，最近加入的方法要改变，如果样本过量，则踢出的加入，加入新的聚集，或者产生新的聚集。？？？？
				if(instances2.getCount()<Vmin){//样本数不足
					PrintDebug("样本数量不足！");
					stop=false;
//					clusterMembers.remove(i);
//					clusterCenters.RemoveInstance(i);
//					//选择最近的中心加入之
//					for(int j=0;j<instances2.getCount();j++){
//						Instance instance=instances2.getInstance(j);
//						int newloc=clusterCenters.getNearestInstance(instance, distanceMethod);
//						clusterMembers.get(newloc).AddInstance(instance);
//						//要不要再次计算加入的聚集的聚集中心？？？
//					}
//					i--;
				}
				else if(instances2.getCount()>Vmax){//样本数量过量。选择vmax/count * maxR4Clusters 
					PrintDebug("样本过量！");
					stop=false;
//					double fil=maxR4Clusters.get(i)*Vmax/instances2.getCount();
//					while(instances2.getCount()<Vmax){
//						for(int j=0;j<instances2.getCount();){
//							Instance instance=instances2.getInstance(j);
//							double tmp=instance.Distance(distanceMethod, clusterCenters.getInstance(i));
//							if(tmp>fil){
//								instances2.RemoveInstance(j);
//								//选择最近的一个聚集，加入之，如果还是原聚集，则另起一个聚集中心。最近聚集加入策略要改变，直到另起炉灶
//								int loc=clusterCenters.getNearestInstance(instance, distanceMethod);
//								if(loc==i){
//									clusterCenters.AddInstance(instance);
//									Instances instances3=new Instances("");
//									instances3.AddInstance(instance);
//									clusterMembers.add(instances3);
//									maxR4Clusters.add(0.0);
//								}
//								else {
//									clusterMembers.get(loc).AddInstance(instance);
//									//要不要再次计算加入的聚集的聚集中心？？？
//								}
//							}
//							else {
//								j++;
//							}
//						}
//						fil*=0.9;
//					}
				}
				else {//数量合格，但是要调整聚集，对聚集中的元素重新选择最近的聚集。
					PrintDebug("调整聚集中心！");
					for(int j=0;j<instances2.getCount();){
						Instance instance=instances2.getInstance(j);
						//选择最近的一个聚集，加入之，如果还是原聚集，则另起一个聚集中心。
						int loc=clusterCenters.getNearestInstance(instance, distanceMethod);
						if(loc!=i){
							stop=false;
							clusterMembers.get(loc).AddInstance(instance);
							instances2.RemoveInstance(j);
						}	
						else j++;
					}
				}
			}
			
		}
		PrintDebug(""+System.currentTimeMillis());
		//保存聚集中心，对应样本等相关信息
		Save2CSV1 save2csv1=new Save2CSV1();
		//保存聚集中心
		clusterCenters.SaveInstances(pathString+viewName+"_centers.csv", save2csv1);
		for(int i=0;i<clusterMembers.size();i++){
			Instances insm=clusterMembers.get(i);
			insm.SaveInstances(pathString+viewName+"_"+i+".csv", save2csv1);
		}		
	}
	
	public void Cluster(){		
		PrintDebug(""+System.currentTimeMillis());
		//初始化聚集中心,选择其中的若干个点
		PrintDebug("初始化聚集中心！");
		String sqlString="select * from (select top "+initN/2+" * from "+viewName+" union select top "+initN/2+" * from "+viewName+" order by no desc) as b";
		PrintDebug(sqlString);
		clusterCenters=loadSqlData.loadInstances3(sqlString);		
		//初始化clusterMembers和maxR4Clusters
		PrintDebug("初始化聚集！");
		instances=loadSqlData.loadInstances3("select * from "+viewName);
		for(int i=0;i<clusterCenters.getCount();i++){
			Instances instances2=new Instances(""+i);
			clusterMembers.add(instances2);
			maxR4Clusters.add(0.0);
		}
		PrintDebug("加载数据完成！");
		PrintDebug(""+instances.getCount());
		for(int i=0;i<instances.getCount();i++){
			Instance instance=instances.getInstance(i);
			int loc=0;
			double dis=Double.MAX_VALUE;
			for(int j=0;j<clusterCenters.getCount();j++){//找出最近的聚集中心
				double tmp=instance.Distance(distanceMethod, clusterCenters.getInstance(j));
				if(tmp<=dis){
					loc=j;
					dis=tmp;
				}
			}
			clusterMembers.get(loc).AddInstance(instance);
			if(maxR4Clusters.get(loc)<dis){
				maxR4Clusters.remove(loc);
				maxR4Clusters.add(loc, dis);
			}			
		}
		PrintDebug("聚集初始化完成！");
		for(int i=maxR4Clusters.size()-1;i>=0;i--)
		PrintDebug(clusterMembers.get(i).getCount()+","+maxR4Clusters.get(i));
		//根据聚集中心进行聚集
		boolean stop=false;//停止条件
		int num=0;
		while(!stop){
			PrintDebug("重新计算聚集中心");
			for(int i=0;i<clusterMembers.size();i++){
				Instances instances2=clusterMembers.get(i);
				Instance instance=instances2.CalCenter();
				clusterCenters.RemoveInstance(i);
				clusterCenters.AddInstance(i, instance);
				maxR4Clusters.remove(i);
				maxR4Clusters.add(i,instances2.getMaxDis(instance, distanceMethod));
			}
			//遍历每个聚集中心,不符合要求中心进行处理，并且设置stop=false,当遍历完成没有需要进行处理时,stop=TRUE
			
			stop=true;
			for (int i = 0; i < clusterMembers.size();i++) {
				Instances instances2=clusterMembers.get(i);
				for(int j=0;j<instances2.getCount();){
					Instance instance=instances2.getInstance(j);
					//选择最近的一个聚集，加入之，如果还是原聚集，则另起一个聚集中心。
					int loc=clusterCenters.getNearestInstance(instance, distanceMethod);
					if(loc!=i){
						stop=false;
						clusterMembers.get(loc).AddInstance(instance);
						instances2.RemoveInstance(j);
					}	
					else j++;
				}				
			}
			if(num%100==0)for(int i=maxR4Clusters.size()-1;i>=0;i--)
				PrintDebug(clusterMembers.get(i).getCount()+","+maxR4Clusters.get(i));
			num++;
		}
		PrintDebug(""+System.currentTimeMillis());
		//保存聚集中心，对应样本等相关信息
		Save2CSV1 save2csv1=new Save2CSV1();
		//保存聚集中心
		clusterCenters.SaveInstances(pathString+viewName+"_centers.csv", save2csv1);
		for(int i=0;i<clusterMembers.size();i++){
			Instances insm=clusterMembers.get(i);
			insm.SaveInstances(pathString+viewName+"_"+i+".csv", save2csv1);
		}		
	}
	public void saveData(){
		Save2CSV1 save2csv1=new Save2CSV1();
		long c=System.currentTimeMillis();
		File file=new File(pathString+c+"/");
		if(!file.exists())
			file.mkdir();
		clusterCenters.SaveInstances(pathString+c+"/"+viewName+"_centers.csv", save2csv1);
		for(int i=0;i<clusterMembers.size();i++){
			Instances insm=clusterMembers.get(i);
			insm.SaveInstances(pathString+c+"/"+viewName+"_"+i+".csv", save2csv1);
		}
		msgFromMain="saved";
	}
	public void print(){
		int count=clusterCenters.getCount();
		System.out.println("聚集数量："+count);
		int all=0;
		for(int i=0;i<count;i++){
			all+=clusterMembers.get(i).getCount();
			System.out.println(i+":"+clusterMembers.get(i).getCount());
		}
		System.out.println("样本总数："+all);
		msgFromMain="printed";
	}
	/**
	 * 多次迭代，使聚集达到平衡
	 */
 	public void makeBalance(){
		boolean stop=false;//停止条件
		int num=0;
		while(!msgFromMain.equals("stop")&&!stop){
			while(!stop&&!stop()){
				PrintDebug("重新计算聚集中心");
				maxR4Clusters.clear();
				for(int i=0;i<clusterMembers.size()&&!stop();i++){
					Instances instances2=clusterMembers.get(i);
					Instance instance=instances2.CalCenter();
					clusterCenters.RemoveInstance(i);
					clusterCenters.AddInstance(i, instance);
					maxR4Clusters.add(instances2.getMaxDis(instance, distanceMethod));
				}
				stop=true;
				for (int i = 0; i < clusterMembers.size()&&!stop();i++) {
					Instances instances2=clusterMembers.get(i);
					//对于少于Vmin/15的聚集 ，删除之，然后加入到最近的聚集中。？？？？
//					if(instances2.getCount()<=Vmin/15){
//						clusterCenters.RemoveInstance(i);
//						clusterMembers.remove(i);
//						maxR4Clusters.remove(i);
//					}
					for(int j=0;j<instances2.getCount();){
						Instance instance=instances2.getInstance(j);
						int loc=clusterCenters.getNearestInstance(instance, distanceMethod);
						if(loc!=i){
							stop=false;
							clusterMembers.get(loc).AddInstance(instance);
							instances2.RemoveInstance(j);
						}
						else j++;
					}				
				}
				if(num%100==0)for(int i=clusterMembers.size()-1;i>=0;i--)
					PrintDebug(clusterMembers.get(i).getCount()+","+maxR4Clusters.get(i));
				num++;
			}
			PrintDebug("聚集收敛！");
			if (msgFromMain.equals("save")||msgFromMain.equals("stop")) {
				PrintDebug("保存数据中。。。");
				saveData();
				stop=false;
				msgFromMain="";
			}
			if (msgFromMain.equals("print")) {
				print();
				stop=false;
				msgFromMain="";
			}
			
		}		
	}
	
	/**
	 * 对聚集进行划分,返回新的聚集中心和聚集
	 * @param center聚集中心 
	 * @param samples 样本
	 * @param newCenters 新的若干个聚集中心
	 * @param newSamples 新的聚集样本
	 */
	public boolean PartitionCluster(Instance center,Instances samples,Instances newCenters,ArrayList<Instances> newSamples){
		boolean re=true;
		int farIndex=samples.getFarestInstance(center, distanceMethod);
		Instance inst=samples.getInstance(farIndex);//最远的样本
		Instance midlleInstance=center.getMiddleInstance(inst);//介于最远样本和中心样本之间的样本
		midlleInstance=samples.getInstance(samples.getNearestInstance(midlleInstance,distanceMethod));
		Instance otherSide=samples.getInstance(samples.getNearestInstance(center,distanceMethod));//.getOthersideInstance(midlleInstance);//middle 样本 关于中心样本对称的样本
		int count=samples.getCount();
		//划分样本
		Instances instances1=new Instances("1");
		Instances instances2=new Instances("2");		
		for(int i=0;i<count;i++){
			Instance tmpInstance=samples.getInstance(i);
			double dis1=tmpInstance.Distance(distanceMethod, midlleInstance);
			double dis2=tmpInstance.Distance(distanceMethod, otherSide);
			if(dis1<dis2)instances1.AddInstance(tmpInstance);
			else {
				instances2.AddInstance(tmpInstance);
			}
		}
		boolean stop=false;
		while(!stop&&!msgFromMain.equals("stop")&&!msgFromMain.equals("save")&&!msgFromMain.equals("print")){
			stop=true;
			//重新计算中心直到稳定
			midlleInstance=instances1.CalCenter();
			otherSide=instances2.CalCenter();
			int num2=instances2.getCount();
			int num1=instances1.getCount();
			for(int i=0;i<num1;){
				Instance tmpInstance=instances1.getInstance(i);
				double dis1=tmpInstance.Distance(distanceMethod, midlleInstance);
				double dis2=tmpInstance.Distance(distanceMethod, otherSide);
				if(dis1>dis2){
					stop=false;
					instances1.RemoveInstance(i);
					instances2.AddInstance(tmpInstance);
					num1--;
				}
				else i++;
				if (stop()) {
					return false;
				}
			}
			for(int i=0;i<num2;){
				Instance tmpInstance=instances2.getInstance(i);
				double dis1=tmpInstance.Distance(distanceMethod, midlleInstance);
				double dis2=tmpInstance.Distance(distanceMethod, otherSide);
				if(dis1<dis2){
					stop=false;
					instances2.RemoveInstance(i);
					instances1.AddInstance(tmpInstance);
					num2--;
				}
				else i++;
				if (stop()) {
					return false;
				}
			}
		}
		//达到平衡
		if(instances1.getCount()<=Vmax&&instances1.getCount()>0){
			newCenters.AddInstance(midlleInstance);
			newSamples.add(instances1);
		}else {
			Instances newCenters2=new Instances("");
			ArrayList<Instances> newArrayList=new ArrayList<>();
			if(PartitionCluster(midlleInstance, instances1, newCenters2, newArrayList)){
				for (int i = 0; i < newCenters2.getCount(); i++) {
					newCenters.AddInstance(newCenters2.getInstance(i));
					newSamples.add(newArrayList.get(i));
				}
			}
			else return false;
		}
		if(instances2.getCount()<=Vmax&&instances2.getCount()>0){
			newCenters.AddInstance(otherSide);
			newSamples.add(instances2);
		}else {
			Instances newCenters2=new Instances("");
			ArrayList<Instances> newArrayList=new ArrayList<>();
			if(PartitionCluster(otherSide, instances2, newCenters2, newArrayList)){
				for (int i = 0; i < newCenters2.getCount(); i++) {
					newCenters.AddInstance(newCenters2.getInstance(i));
					newSamples.add(newArrayList.get(i));
				}
			}
			else return false;
		}
		return re;
	}
	private boolean stop(){
		return msgFromMain.equals("stop")||msgFromMain.equals("save")||msgFromMain.equals("print");
	}
	/**
	 * 限制聚集的数量，最后再达到平衡
	 */
	public void limitVol(){
		PrintDebug("限制聚集数量：");
		if(!dataOK){
			System.out.println("the data is not OK!");
			Cluster();
		}
		boolean stop=false;
		while (!msgFromMain.equals("stop")&&!stop) {
			while(!stop&&!stop()){
				makeBalance();//检查聚集是否达到平衡状态
				stop=true;
				for(int i=0;i<clusterCenters.getCount()&&!stop();){
					Instances tmpInstances=clusterMembers.get(i);
					if(tmpInstances.getCount()>Vmax){
						System.out.println(i+","+tmpInstances.getCount());
						stop=false;
						Instance centerInstance=clusterCenters.getInstance(i);
						Instances newCenters=new Instances("");
						ArrayList<Instances> newSamples=new  ArrayList<>();
						if(PartitionCluster(centerInstance, tmpInstances, newCenters, newSamples)){
							for (int j = 0; j < newCenters.getCount(); j++) {
								clusterCenters.AddInstance(newCenters.getInstance(j));
								clusterMembers.add(newSamples.get(j));
							}
							clusterCenters.RemoveInstance(i);
							clusterMembers.remove(i);
						}
					}
					else i++;					
				}
			}
			if(stop&&!stop()){
				saveData();
				msgFromMain="stop";
				stop=false;
			}
			if (msgFromMain.equals("save")||msgFromMain.equals("stop")) {
				saveData();
				stop=false;
			}
			if (msgFromMain.equals("print")) {
				print();
				stop=false;
			}
		}
		
	}
	public void limitVol(int i){
		
	}
 	public void PrintDebug(String msg){
		FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(pathString+viewName+logId+"_log.txt", false,true);
		if(debug){
//			System.out.println(msg);
			fileReadAndWriter.WriteLine(msg);
			fileReadAndWriter.Flush();
		}
		fileReadAndWriter.EndWrite();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path="E:/dataProcess/newAlgorithm/";
		//用newThread线程运行计算任务
		System.out.println(new Date().toLocaleString());
		DataPartition dataPartition=new DataPartition(90, 5000, 10000,path);
		NewThread newThread=new NewThread(dataPartition);
		Thread thread=new Thread(newThread);
		thread.start();
		UIOperation uiOperation=new UIOperation(dataPartition);
		uiOperation.Interaction();
//		dataPartition.debug=false;
//		dataPartition.Cluster();
		
//		dataPartition.limitVol();
		System.out.println(new Date().toLocaleString());
	}

}
