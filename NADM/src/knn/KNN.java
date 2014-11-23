package knn;
import core.*;
public class KNN extends KNNClassifier{
	
	
	/**
	 * @param k
	 * @param distanceMethod
	 * @param getKNNMethod
	 * @param knnDecision
	 * @param trainInstances
	 * @param testInstances
	 * @param classifierName
	 */
	public KNN(int k, DistanceMethod distanceMethod,
			GetKNNMethod getKNNMethod, KNNDecisionMethod knnDecision,Instances trainInstances, Instances testInstances,
			String classifierName) {
		// TODO Auto-generated constructor stub
		super(k, distanceMethod, getKNNMethod, knnDecision,trainInstances,testInstances,classifierName);
	}

	@Override
	public String Classify(Instance instance) {
		double [] KNNDistances=new double[k];
		Instance [] kNNInstances=getKNNMethod.getKNN(instance, KNNDistances);
		return knnDecision.getDecision(kNNInstances, KNNDistances);		
	}

	
	
}
