package knn;

import java.util.ArrayList;

import core.Instance;
import core.Instances;
import core.KNNClassifier;

public class MyKNN extends KNNClassifier {
	
	public MyKNN(int k, DistanceMethod distanceMethod,
			GetKNNMethod getKNNMethod, KNNDecisionMethod knnDecision,
			Instances trainInstances, Instances testInstances,
			String classifierName) {
		super(k, distanceMethod, getKNNMethod, knnDecision, trainInstances,
				testInstances, classifierName);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see core.Classifier#Classify(core.Instance)
	 * 获取kNN之后，投票方法要改一下，针对每个近邻，判断其效度
	 */
	@Override
	public String Classify(Instance instance) {
		// TODO Auto-generated method stub
		double [] KNNDistances=new double[k];
		Instance [] kNNInstances=getKNNMethod.getKNN(instance, KNNDistances);
		ArrayList<String> labelsArrayList=new ArrayList<>();
		double []all=new double [k];
		for(int i=0;i<kNNInstances.length;i++){
			double [] KNNDistances2=new double[k];
			Instance []instances2=getKNNMethod.getKNN(kNNInstances[i], KNNDistances2);
			int count=0;
			for(int j=0;j<k;j++){
				if (instances2[j].SameAs(kNNInstances[i])) {
					count++;
				}
			}
			if (labelsArrayList.contains(kNNInstances[i])) {
				all[labelsArrayList.indexOf(kNNInstances[i].getLabel())]+=count*1.0/k;
			}
			else {
				labelsArrayList.add(kNNInstances[i].getLabel());
				all[labelsArrayList.indexOf(kNNInstances[i].getLabel())]+=count*1.0/k;
			}
		}
		int loc=0;
		for(int i=0;i<labelsArrayList.size();i++){
			if(all[i]>all[loc])loc=i;
		}
		return labelsArrayList.get(loc);
	}


}
