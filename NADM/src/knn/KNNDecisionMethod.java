/**
 * 
 */
package knn;

import core.Instance;

/**
 * @author benfenghua
 *根据K个近邻做出决策的策略
 */
public abstract class KNNDecisionMethod {
	public abstract String getDecision(Instance [] instances,double []distances);
}
