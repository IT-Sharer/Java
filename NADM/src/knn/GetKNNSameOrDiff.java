/**
 * 
 */
package knn;

import core.Instance;
import core.Instances;

/**
 * @author benfenghua
 *获取与样本相同类型的或者不同类型的K个近邻方法
 */
public class GetKNNSameOrDiff extends GetKNNMethod {

	public GetKNNSameOrDiff(DistanceMethod distanceMethod, Instances instances,boolean IsGetSame) {
		super(distanceMethod, instances);
		this.setK(k);
		this.setIsGetSame(IsGetSame);
		kNNInstances=new Instance[2*k];
		// TODO Auto-generated constructor stub
	}
	public GetKNNSameOrDiff(DistanceMethod distanceMethod, Instances instances) {
		super(distanceMethod, instances);
		this.setK(k);
		this.setBothSameAndDiff(true);
		kNNInstances=new Instance[2*k];
		// TODO Auto-generated constructor stub
	}
	private Instance [] kNNInstances = null;
	private int k;
	private boolean IsGetSame=true;//默认获取相同类型的K近邻
	private boolean bothSameAndDiff=false;//默认值获取一种，若为True则是两种
	public boolean isIsGetSame() {
		return IsGetSame;
	}
	public void setIsGetSame(boolean isGetSame) {
		IsGetSame = isGetSame;
	}
	/* (non-Javadoc)
	 * @see knn.GetKNNMethod#getKNN(core.Instance, double[])
	 */
	@Override
	public Instance[] getKNN(Instance instance, double[] KNNDistances) {
		// TODO Auto-generated method stub
		if(bothSameAndDiff)IsGetSame=true;
		int point=0,point2=k;//目前栈中的数量
		for(int i=0;i<this.instances.getCount();i++){
			if(!instance.SameAs(instances.getInstance(i))){
				if(IsGetSame==(instance.getLabel().equals(this.instances.getInstance(i).getLabel()))){
					//计算近邻
					double distance=instances.getInstance(i).Distance(distanceMethod, instance);
					if(point==0){
						KNNDistances[point]=distance;
//						kNNInstances[point]=instances.getInstance(i);
						point++;
					}else{
						int j=point;
						while(KNNDistances[j]>distance&&j>0){
							KNNDistances[j]=KNNDistances[j-1];
//							kNNInstances[j]=kNNInstances[j-1];
							j--;
						}
						KNNDistances[j]=distance;
//						kNNInstances[j]=instances.getInstance(i);	
						if(point<k-1)point++;
					}				
				}
				else if(bothSameAndDiff) {
					double distance=instances.getInstance(i).Distance(distanceMethod, instance);
					if(point2==k){
						KNNDistances[point2]=distance;
//						kNNInstances[point2]=instances.getInstance(i);
						point2++;
					}else{
						int j=point2;
						while(KNNDistances[j]>distance&&j>k){
							KNNDistances[j]=KNNDistances[j-1];
//							kNNInstances[j]=kNNInstances[j-1];
							j--;
						}
						KNNDistances[j]=distance;
//						kNNInstances[j]=instances.getInstance(i);	
						if(point2<2*k-1)point2++;
					}			
				}
			}
		}
		return kNNInstances;
	}
	public int getK() {
		return k;
	}
	public void setK(int k) {
		this.k = k;
	}
	public Instance [] getkNNInstances() {
		return kNNInstances;
	}
	public void setkNNInstances(Instance [] kNNInstances) {
		this.kNNInstances = kNNInstances;
	}
	public boolean isBothSameAndDiff() {
		return bothSameAndDiff;
	}
	public void setBothSameAndDiff(boolean bothSameAndDiff) {
		this.bothSameAndDiff = bothSameAndDiff;
	}

}
