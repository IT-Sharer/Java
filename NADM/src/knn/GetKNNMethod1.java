/**
 * 
 */
package knn;

import core.Instance;
import core.Instances;

/**
 * @author benfenghua
 *
 */
public class GetKNNMethod1 extends GetKNNMethod {
	public GetKNNMethod1(Instances instances,DistanceMethod distanceMethod,int k) {
		// TODO Auto-generated constructor stub
		super(distanceMethod, instances);
		this.setK(k);
		kNNInstances=new Instance[k];
	}
	private Instance [] kNNInstances = null;
	private int k;
	
	/* (non-Javadoc)
	 * @see knn.GetKNNMethod#getKNN(core.Instance)
	 */
	@Override
	public Instance[] getKNN(Instance instance,double [] KNNDistances) {
		// TODO Auto-generated method stub		
		int point=0;
		for(int i=0;i<instances.getCount();i++){
			if(!instance.SameAs(instances.getInstance(i))){
				double distance=instances.getInstance(i).Distance(distanceMethod, instance);
				if(point==0){
					KNNDistances[point]=distance;
					kNNInstances[point]=instances.getInstance(i);
					point++;
				}else{
					int j=point;
					while(KNNDistances[j]>distance&&j>0){
						KNNDistances[j]=KNNDistances[j-1];
						kNNInstances[j]=kNNInstances[j-1];
						j--;
					}
					KNNDistances[j]=distance;
					kNNInstances[j]=instances.getInstance(i);	
					if(point<k-1)point++;
				}				
			}
		}
		return kNNInstances;
	}

	
	public Instance [] getkNNInstances() {
		return kNNInstances;
	}

	public void setkNNInstances(Instance [] kNNInstances) {
		this.kNNInstances = kNNInstances;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

}
