package tcmKNN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Map.Entry;

import knn.DistanceMethod;
import knn.DistanceMethod1;
import psoKmeans.Clusters;
import core.Instance;
import core.Instances;
import core.LoadSqlData;
import debug.DebugInfo;

public class TCMKNN4BigSet {
	protected DistanceMethod distanceMethod=new DistanceMethod1();
	protected Clusters clusters;//聚簇集合
	//保存各类别对应的奇异值序列
	public HashMap<String,TreeSet<Double>> strangeness=new HashMap<>();
	String dirString;//聚簇保存路径
	int k;
	String normalString="normal";//保存和加载的时候一定要trim一下
	String unNormalString="unnormal";
	LoadSqlData loadSqlData=new LoadSqlData();
	public TCMKNN4BigSet(String dirString) {
		// TODO Auto-generated constructor stub
		//初始化Clusters
		this.dirString=dirString;
		
	}
	public void loadClusters(){
		clusters=new Clusters();
		DebugInfo.printMsg("加载聚集中！");
		clusters.loadFromCSV(dirString);
	}
	public void init(){
		loadClusters();
		CalAllStrangeness();
		SaveStrageness(dirString);
	}
	/**
	 * 计算样本奇异值
	 */
	public double CalStrangeness(Instance instance){
		Instances difInstances=clusters.getNNInstances(instance, -1, k);//需要修改，一次计算返回两个实例集
		Instances sameInstances=clusters.getNNInstances(instance, 1, k);
		double difDists=0,sameDists=0;
		for(int i=0;i<difInstances.getCount();i++){
			difDists+=difInstances.getInstance(i).Distance(distanceMethod, instance);
		}for(int i=0;i<sameInstances.getCount();i++){
			sameDists+=sameInstances.getInstance(i).Distance(distanceMethod, instance);
		}
		if(difDists>0.0000001)return sameDists/difDists;
		else return Double.MAX_VALUE;
	}
	/**
	 * 计算所有样本的奇异值
	 */
	public void CalAllStrangeness(){
		ArrayList<Instances> iniArrayList=clusters.getcArrayList();
		for(int i=0;i<iniArrayList.size();i++){
			Instances instances=iniArrayList.get(i);
			for(int j=0;j<instances.getCount();j++){
				Instance instance=instances.getInstance(j);
				if(strangeness.containsKey(instance.getLabel().trim())){
					TreeSet<Double> tmpDoubles=strangeness.get(instance.getLabel().trim());
					tmpDoubles.add(CalStrangeness(instance));
				}
				else {
					TreeSet<Double> tmpDoubles=new TreeSet<>();
					tmpDoubles.add(CalStrangeness(instance));
					strangeness.put(instance.getLabel().trim(), tmpDoubles);
				}
			}			
		}
	}
	/**折半查找元素的排位
	 * @return
	 */
	public int halfSearch(String label,double d){
		TreeSet<Double> tmp=strangeness.get(label);
		int loc=tmp.headSet(d).size();
		return tmp.size()-loc;
	}
	/**根据奇异值序列计算概率值，有两种方法，采用论文方法
	 * @param label
	 * @param d
	 * @return
	 */
	public double calP(String label,double d){
		int c=halfSearch(label,d );
		return c*1.0/strangeness.get(label).size();	
		//方法2，计算在一定区间内的个数，查询分布的密度求概率？？？？
		
	}
	
	/**dir 要/结尾
	 * @param dir
	 */
	public void SaveStrageness(String dir){
		int i=0;
		for(Entry<String, TreeSet<Double>> entry:strangeness.entrySet()){
			try {
				FileWriter fileWriter=new FileWriter(dir+"_strangeness_"+i+".txt");
				fileWriter.write(entry.getKey()+"/r/n");
				Iterator<Double> iterator=entry.getValue().iterator();
				while(iterator.hasNext()){
					fileWriter.write(iterator.next()+"");
					if(iterator.hasNext())fileWriter.write(",");
				}
				fileWriter.write("/r/n");
				fileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void LoadStrageness(String dir){
		File dirPath=new File(dir);
		if(!dirPath.isDirectory()){
			DebugInfo.printMsg("dir 不是一个路径");
			return;
		}
		File[] files=dirPath.listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].getAbsolutePath().contains("_strangeness_")){
				//load strangeness first line is the label name,second line is the strangeness values
				try {
					BufferedReader bufferedReader=new BufferedReader(new FileReader(files[i]));
					String labelname=bufferedReader.readLine();
					String[]values=bufferedReader.readLine().split(",");
//					if (strangeness.containsKey(labelname)) {
//						strangeness.get(labelname).clear();
//					}else{
						TreeSet<Double> tmp=new TreeSet<Double>();
						for(int i1=0;i1<values.length;i1++)tmp.add(Double.parseDouble(values[i1]));
						strangeness.put(labelname, tmp);
//					}
						bufferedReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
	}
	class ClassifyResult{
		String result;//结果类别
		public String getResult() {
			return result;
		}
		public void setResult(String result) {
			this.result = result;
		}
		public double getPos() {
			return pos;
		}
		public void setPos(double pos) {
			this.pos = pos;
		}
		double pos;//置信度
	}
	/**分类
	 * @param instance
	 * @return
	 */
	public ClassifyResult classify(Instance instance){
		Instance instance2=instance.clone();
		String [] labelStrings={normalString,unNormalString};
		double [] P=new double[labelStrings.length];
		for(int i=0;i<labelStrings.length;i++){
			instance2.setLabel(labelStrings[i]);
			double strange=CalStrangeness(instance2);
			P[i]=calP(labelStrings[i], strange);					
		}
		//返回结果和置信度。???
		ClassifyResult result=new ClassifyResult();
		if(P.length<2){ 
			result.setPos(P[0]);
			result.setResult(labelStrings[0]);
		}else {
			double max=0,sec=0;
			int loc=0,loc2=0;
			for(int i=0;i<P.length;i++){
				if(P[i]>sec){
					sec=P[i];
					loc2=i;
				}
				if(sec>max){
					loc=loc2;
					double tmp=sec;
					sec=max;
					max=tmp;
				}
			}
			result.setPos(max-sec);
			result.setResult(labelStrings[loc]);			
		}
		return result;
	
	}
	public void test(){
			
		System.out.println("加载测试数据集：");
		String testviewString="KDDCup99_0_test";
		Instances testInstances=loadSqlData.loadInstances3("select * from "+testviewString);
		System.out.println("加载完毕，进行分类：");
		k=340;
		loadClusters();	
		clusters.deleteClusters(k);
		CalAllStrangeness();
		SaveStrageness(dirString);
		int count=0;
		int wubao=0;
		int loubao=0;System.out.println("现在时间："+new Date().toLocaleString());	
		System.out.println("K的值："+k);
		for (int i = 0; i < testInstances.getCount(); i++) {
			String result=classify(testInstances.getInstance(i)).getResult();
			if(result.equals(testInstances.getInstance(i).getLabel()))count++;
			else if(result.contains("unnormal"))wubao++;
			else if(result.contains("normal"))loubao++;			
		}
		System.out.println("分类的正确率是："+count*100.0/testInstances.getCount()+"%,分类正确总数："+count+";漏报 个数："+loubao+"; 误报个数："+wubao);
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("现在时间："+new Date().toLocaleString());
		String pathString="E:/dataProcess/newAlgorithm/1413349489521/";
		TCMKNN4BigSet tcmknn4BigSet=new TCMKNN4BigSet(pathString);
		tcmknn4BigSet.test();
		DebugInfo.printMsg("现在时间："+new Date().toLocaleString());
	}

}
