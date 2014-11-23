package knn;

import core.Instance;
//
/**
 * @author benfenghua
 *距离的计算方法（策略）
 */
public abstract class DistanceMethod {
	public abstract double DistanceOfInstances(Instance instance1,Instance instance2);
}
