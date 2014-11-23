package knn;

import core.Instance;

public class DistanceMethod1 extends DistanceMethod {

	@Override
	public double DistanceOfInstances(Instance instance1, Instance instance2) {
		// TODO Auto-generated method stub
		if(instance1==null||instance2==null)return Double.MAX_VALUE;
		double [] i1=instance1.getContinuousAttributes();
		double [] i2=instance2.getContinuousAttributes();
		double distance=0.0;
		for(int i=0;i<i1.length;i++){
			distance+=(i1[i]-i2[i])*(i1[i]-i2[i]);
		}
		distance=Math.sqrt(distance);
		return distance;
	}

}
