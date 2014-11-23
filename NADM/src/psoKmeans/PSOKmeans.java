package psoKmeans;

import java.util.ArrayList;

import knn.DistanceMethod;
import knn.DistanceMethod1;
import core.Instances;
import core.LoadCSVData1;
import core.LoadSqlData;

public class PSOKmeans {
	//参考DataPartition，双线程实现
	int k;
	String logId="";
	boolean debug=true;
	LoadSqlData loadSqlData=new LoadSqlData();
	LoadCSVData1 loadCSVData1;
	DistanceMethod distanceMethod=new DistanceMethod1();
	Instances instances;//初始数据集
	String viewName="KDDCup99_0_train";
	Instances clusterCenters=new Instances("DataPartition");//聚集中心
	ArrayList<Instances> clusterMembers=new ArrayList<>();//对应每一个聚集中心的样本集合
	ArrayList<Double> maxR4Clusters=new ArrayList<>();//对应每个聚集中心最大的半径。
	int Vmin,Vmax,initN;
	
	String pathString="E:/dataProcess/newAlgorithm/";
	boolean dataOK=false;
	protected Clusters clusters;//聚簇类
	
	/**
	 * PSO-Kmeans的数据划分算法
	 */
	public void PSOKPartition(){
		//
	}
	
	
	
}
