/**
 * 
 */
package knn;

import core.Instance;
import core.Instances;

/**
 * @author benfenghua
 *KNN»ñÈ¡²ßÂÔ
 */
public abstract class GetKNNMethod {
	protected DistanceMethod distanceMethod;
	protected Instances instances;
	public GetKNNMethod(DistanceMethod distanceMethod,Instances instances) {
		// TODO Auto-generated constructor stub
		this.distanceMethod=distanceMethod;
		this.instances=instances;
	}
	public abstract Instance [] getKNN(Instance instance,double [] KNNDistances);
	public Instances getInstances() {
		return instances;
	}
	public void setInstances(Instances instances) {
		this.instances = instances;
	}
}
