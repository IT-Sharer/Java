/**
 * 
 */
package psoKmeans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import knn.DistanceMethod;
import knn.DistanceMethod1;
import core.FileReadAndWriter;
import core.Instance;
import core.Instances;
import core.LoadCSVData1;
import core.Save2CSV1;
import core.SaveData;
import debug.DebugInfo;

/**
 * @author benfenghua
 *聚簇集合类
 *聚簇加载和保存
 *近邻搜索方法
 */
public class Clusters {
	protected ArrayList<Instances> cArrayList;//聚簇
	public ArrayList<Instances> getcArrayList() {
		return cArrayList;
	}
	protected Instances cCenters;//聚簇中心，与聚簇序号对应
	protected ArrayList<Double> maxDist;//聚簇的最大半径
//	protected ArrayList<Double> minDist;//聚簇的最小半径
//	protected ArrayList<Double> avgDist;//聚簇的平均半径
	double [][] centersdist;//聚集中心间的距离，序号对应
	String dirString;//聚簇保存的路径
	String viewname;//实验数据视图
	
	
	boolean dataOK=false;
	LoadCSVData1 loadCSVData1=new LoadCSVData1();;
	DistanceMethod distanceMethod=new DistanceMethod1();
	
	public Instances getcCenters() {
		return cCenters;
	}
	public void setcCenters(Instances cCenters) {
		this.cCenters = cCenters;
	}
	public String getDirString() {
		return dirString;
	}
	public void setDirString(String dirString) {
		this.dirString = dirString;
	}
	public String getViewname() {
		return viewname;
	}
	public void setViewname(String viewname) {
		this.viewname = viewname;
	}
	public boolean isDataOK() {
		return dataOK;
	}
	public void setcArrayList(ArrayList<Instances> cArrayList) {
//		if(cArrayList.get(0).getClass().isInstance(PSOInstances));
		this.cArrayList = cArrayList;
	}
	/**
	 * 重新计算聚集间距离和最大半径
	 */
	public void ReCal(){
		for(int i=0;i<cArrayList.size();i++){
			Instances instances=cArrayList.get(i);
			Instance instance=instances.CalCenter();
			//验证聚集中心
			if(!cCenters.getInstance(i).equal2(instance)){
				DebugInfo.printMsg("聚集中心有误差，需要调整！");
				cCenters.RemoveInstance(i);
				cCenters.AddInstance(i, instance);
			}
			//计算最大半径
			double tmp=instances.getMaxDis(instance, distanceMethod);
			maxDist.set(i, tmp);
		}
		//计算聚集间距离
		centersdist=new double[cArrayList.size()][cArrayList.size()];
		for(int i=0;i<cCenters.getCount();i++)
			for(int j=i+1;j<cCenters.getCount();j++){
				centersdist[i][j]=cCenters.getInstance(i).Distance(
						distanceMethod, cCenters.getInstance(j));
			}
	}
	/**从文件夹加载聚集
	 * @param dir
	 */
	public void loadFromCSV(String dir) {
		File dirPath=new File(dir);
		if(!dirPath.isDirectory()){
			DebugInfo.printMsg("dir 不是一个路径");
			return;
		}
		File[] files=dirPath.listFiles();
		String centerString="";
		for(int i=0;i<files.length;i++){
			if(files[i].getAbsolutePath().contains("_centers.csv"))centerString=files[i].getAbsolutePath();
		}
		if(centerString.equals("")){
			DebugInfo.printMsg("没有质心文件！");
			return;
		}
		cCenters=loadCSVData1.loadInstances1(centerString);
		for(int i=0;i<cCenters.getCount();i++){
			Instances inst=loadCSVData1.loadInstances1(centerString.replace("centers", ""+i));
			cArrayList.add(inst);
//			double maxr=inst.getMaxDis(cCenters.getInstance(i), distanceMethod);
//			maxDist.add(maxr);
		}
		//加载最大间距信息
		FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(centerString.replace("centers", "maxRadio"), true, false);
		BufferedReader bufferedReader=fileReadAndWriter.getReader();
		try {
			String[] maxes=bufferedReader.readLine().split(",");
			for(int i=0;i<maxes.length;i++){
				double tmp=Double.parseDouble(maxes[i]);
				maxDist.add(tmp);
			}
			bufferedReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fileReadAndWriter.EndRead();
		//加载聚集中心之间的距离
		fileReadAndWriter=new FileReadAndWriter(centerString.replace("centers", "cenDists"), true, true);
		bufferedReader=fileReadAndWriter.getReader();
		String line="";
		int m=0,n=0;
		double tmp=0;
		try {
			while((line=bufferedReader.readLine())!=null){
				String[] strings=line.split(",");
				m=Integer.parseInt(strings[0]);
				n=Integer.parseInt(strings[1]);
				tmp=Double.parseDouble(strings[2]);
				centersdist[m][n]=tmp;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataOK=true;	
	}
	/**保存到文件夹中.
	 * @param dir 要以/结束
	 */
	public void save2CSV(String dir){
		if(!dataOK)return;
		File dirPath=new File(dir);
		if(!dirPath.exists()){
			dirPath.mkdir();
		}
		if(!dirPath.isDirectory()){
			DebugInfo.printMsg("dir 不是一个路径");
			return;
		}
		SaveData saveData=new Save2CSV1();
		cCenters.SaveInstances(dir+viewname+"_centers.csv", saveData);
		//保存最大间距信息和聚集
		try {
			FileWriter fileWriter=new FileWriter(dir+viewname+"_maxRadio.csv");
			int i=0;
			for(i=0;i<cArrayList.size()-1;i++){
				cArrayList.get(i).SaveInstances(dir+viewname+"_"+i+".csv", saveData);
				fileWriter.write(maxDist+",");
			}			
			cArrayList.get(i).SaveInstances(dir+viewname+"_"+i+".csv", saveData);
			fileWriter.write(maxDist+"/r/n");
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//保存聚集中心之间的距离
		try {
			FileWriter fileWriter=new FileWriter(dir+viewname+"_cenDists.csv");
			for(int i=0;i<cCenters.getCount();i++)
				for(int j=i+1;j<cCenters.getCount();j++){
					fileWriter.write(i+","+j+","+centersdist[i][j]+"/r/n");
				}
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**从聚簇中获取近邻
	 * p:-1 不同类别近邻；1 相同类别近邻；0混合近邻;2 相同和不同都要返回。
	 * K：近邻数量；
	 * @return
	 */
	public Instances getNNInstances(final Instance inst,int p,int k){
		ArrayList<Integer> canSet=new ArrayList<>();//候选聚簇序号
		ArrayList<Double> dist=new ArrayList<>();//与每个聚簇中心的距离，如果候选后设置成double的最大值。
		Instances instances=new Instances("result");//最终的结果
		//选择候选数据集
		double min=Double.MAX_VALUE;
		int mloc=0;
		for(int i=0;i<cCenters.getCount();i++){
			double distance=cCenters.getInstance(i).Distance(distanceMethod, inst);
			if (distance<min) {
				mloc=i;
				min=distance;
			}
			dist.add(i, distance);
		}
		//最近的 一个
		dist.set(mloc, Double.MAX_VALUE);
		canSet.add(mloc);
		for(int i=0;i<dist.size();i++){
			if(dist.get(i)<=maxDist.get(i)||
					dist.get(i)<=0.5*(mloc<=i?centersdist[mloc][i]:centersdist[i][mloc]))	{
				dist.set(i, Double.MAX_VALUE);
				canSet.add(i);
			}else if (dist.get(i)-maxDist.get(i)>=0&&dist.get(i)-maxDist.get(i)<=min-maxDist.get(mloc)) {
				dist.set(i, Double.MAX_VALUE);
				canSet.add(i);
			}			
		}
		//从候选聚簇中选择对应的前K个近邻，及距离
		TreeSet<Instance> kNNInstances=new TreeSet<>(new Comparator<Instance>() {			
			@Override
			public int compare(Instance arg0, Instance arg1) {
				// TODO Auto-generated method stub
				if (arg0.Distance(distanceMethod, inst)<arg1.Distance(distanceMethod, inst)) {
					return -1;
				}
				if (arg0.Distance(distanceMethod, inst)>arg1.Distance(distanceMethod, inst)) {
					return 1;
				}
				return 0;
			}
		});
		for (int i = 0; i < canSet.size(); i++) {
			Instances insts=cArrayList.get(canSet.get(i));
			for(int j=0;j<insts.getCount();j++){
				Instance instance=insts.getInstance(j);
				if((p==1&&instance.getLabel().equals(inst.getLabel()))||
						(p==-1&&!instance.getLabel().equals(inst.getLabel()))
						||p==0){
					kNNInstances.add(instance);
					if (kNNInstances.size()>k) {
						kNNInstances.pollLast();
					}
				}
			}
		}
		//判断是否是K个近邻，如果不是则要添加，如果到最后还少于K个，则不添加，设置为空值。
		int loc=0;
		while (kNNInstances.size()<k&&loc>=0) {
			double mindist=Double.MAX_VALUE/2;
			loc=-1;
			for(int i=0;i<dist.size();i++){
				if (dist.get(i)<mindist) {
					mindist=dist.get(i);
					loc=i;
					dist.set(i, Double.MAX_VALUE);
				}
			}
			Instances insts=cArrayList.get(loc);
			for(int j=0;j<insts.getCount();j++){
				Instance instance=insts.getInstance(j);
				if((p==1&&instance.getLabel().equals(inst.getLabel()))||
						(p==-1&&!instance.getLabel().equals(inst.getLabel()))
						||p==0){
					kNNInstances.add(instance);
					if (kNNInstances.size()>k) {
						kNNInstances.pollLast();
					}
				}
			}
		}	
		Iterator< Instance>iterator=kNNInstances.iterator();
		while(iterator.hasNext()){
			instances.AddInstance(iterator.next());
		}
		return instances;
	}	
}
