package psoKmeans;

import java.util.ArrayList;

import core.Instance;
import core.Instances;

public class PSOInstances extends Instances {

	public PSOInstances(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	/**将原来的转换到粒子群中
	 * @param instances
	 */
	public PSOInstances(Instances instances) {
		super(instances.getDataSetNameString());
		super.instances=instances.getInstances();
		initPSO();
	}
	protected ArrayList<Instance> velocity=null;//表示粒子的速度向量
	protected ArrayList<Instance> xPosition=null;//表示粒子的位置向量
	/**
	 * 初始化PSO粒子群，将样本初始化生成一个粒子群
	 * 
	 */
	public void initPSO(){
		velocity=(ArrayList<Instance>) instances.clone();
		xPosition=(ArrayList<Instance>) instances.clone();
	}
}
