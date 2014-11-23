package knn;

import java.util.ArrayList;

import core.Instance;

public class KNNDecisionMethod1 extends KNNDecisionMethod{
	public KNNDecisionMethod1() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public String getDecision(Instance[] instances, double[] distances) {
		// TODO Auto-generated method stub
		ArrayList<String> labelsArrayList=new ArrayList<>();
		int [] count=new int[instances.length];
		for(int i=0;i<instances.length;i++){
			if(labelsArrayList.contains(instances[i].getLabel())){
				count[labelsArrayList.indexOf(instances[i].getLabel())]++;				
			}else{
				labelsArrayList.add(instances[i].getLabel());
				count[labelsArrayList.indexOf(instances[i].getLabel())]++;
			}
		}
		int max=0;
		for(int i=0;i<labelsArrayList.size();i++){
			if(count[max]<=count[i])max=i;
		}		
		return labelsArrayList.get(max);
	}

}
