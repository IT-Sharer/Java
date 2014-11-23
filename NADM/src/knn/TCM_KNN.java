package knn;

import java.util.ArrayList;

import core.Instance;
import core.Instances;
import core.KNNClassifier;

public class TCM_KNN extends KNNClassifier{
	private boolean isCal=false;//是否计算训练集中每个样本的 奇异值。
	private double [] singularValues;//每个样本的奇异值
	private ArrayList<String> classeStrings=new ArrayList<>();//样本类别集合。
	public TCM_KNN(int k, DistanceMethod distanceMethod,
			GetKNNMethod getKNNMethod, KNNDecisionMethod knnDecision,Instances trainInstances, Instances testInstances,
			String classifierName) {
		// TODO Auto-generated constructor stub
		super(k, distanceMethod, getKNNMethod, knnDecision,trainInstances,testInstances,classifierName);
		isCal=false;
		singularValues=new double[trainInstances.getCount()];
		
	}
	@Override
	public void setTrainInstances(Instances instances){
		TrainInstances=instances;
		setSingularValues(new double[TrainInstances.getCount()]);
	}
	/**
	 * 计算训练集样本的奇异值，类别统计
	 */
	private void CalDis(){
		if(isCal) return;
		double [] KSameAndDiff=new double[2*k];//2k个最近的相同的和不同的样本的距离，前K个为相同的，
		for(int i=0;i<TrainInstances.getCount();i++){
			if(!classeStrings.contains(TrainInstances.getInstance(i).getLabel()))
				classeStrings.add(TrainInstances.getInstance(i).getLabel());
			getKNNMethod.getKNN(TrainInstances.getInstance(i), KSameAndDiff);
			double Dy=0.0,Ddy=0.0;
			for(int j=0;j<k;j++){
				Dy+=KSameAndDiff[j];
				Ddy+=KSameAndDiff[j+k];
			}
			singularValues[i]=Dy/Ddy;					
		}
		isCal=true;
	}
	/**
	 * 统计K近邻，及其类别
	 * @param instance
	 * @return
	 */
	public String Classify2(Instance instance){
		CalDis();
		ArrayList<String> kNNStrings=new ArrayList<>();
		
		return "";
	}
	/* (non-Javadoc)
	 * @see core.Classifier#Classify(core.Instance)
	 * 输出的是分类的"类别,分类"的置信度。
	 * 
	 */
	@Override
	public String Classify(Instance instance) {
		// TODO Auto-generated method stub
		CalDis();//计算样本奇异值和类别统计
		//每一类别的Ｐ值,和奇异值
		double [] P=new double[classeStrings.size()],singularValue2=new double[classeStrings.size()];
		Instance instance2=instance.clone();
		for (int i=0;i<classeStrings.size();i++) {
			String label=classeStrings.get(i);
			instance2.setLabel(label);
			double [] KSameAndDiff=new double[2*k];
			getKNNMethod.getKNN(instance2, KSameAndDiff);
			double Dy=0.0,Ddy=0.0;
			for(int j=0;j<k;j++){
				Dy+=KSameAndDiff[j];
				Ddy+=KSameAndDiff[j+k];
			}
			singularValue2[i]=Dy/Ddy;			
		}
		//统计每一类别的数量，以及奇异值大于待检测样本奇异值的个数
		int [] countAll=new int[classeStrings.size()],count2=new int[classeStrings.size()];
		for(int i=0;i<TrainInstances.getCount();i++){
			int loc=classeStrings.indexOf(TrainInstances.getInstance(i).getLabel());//类别位置
			countAll[loc]++;
			if(singularValues[i]>singularValue2[loc])count2[loc]++;
		}	
		double max=0.0,secMax=0.0;
		int loc=0;//最大P对应的类别位置
		//计算类别的P值,求出最大的P值，划分为该类，置信度为第二大P值与之的差
		for(int i=0;i<classeStrings.size();i++){
			P[i]=count2[i]*1.0/(countAll[i]*1.0);
			if(max<P[i]){
				max=P[i];
				loc=i;
//				secMax=max;
			}else if(secMax<P[i]) secMax=P[i];			
		}
		return classeStrings.get(loc)+","+(max-secMax);
	}
	public double[] getSingularValues() {
		return singularValues;
	}
	public void setSingularValues(double[] singularValues) {
		this.singularValues = singularValues;
	}
	public ArrayList<String> getClasseStrings() {
		return classeStrings;
	}
	public void setClasseStrings(ArrayList<String> classeStrings) {
		this.classeStrings = classeStrings;
	}
}
