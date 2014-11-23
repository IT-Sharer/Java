package core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import knn.DistanceMethod;

public class Instances {
	public ArrayList<Instance> instances=null;
	private String dataSetNameString;
	public Instances(String name) {
		// TODO Auto-generated constructor stub
		dataSetNameString=name;
		instances=new ArrayList<>();
	}
	@Override
	public Instances clone(){
		Instances instances2=new Instances(this.dataSetNameString);
		instances2.setInstances((ArrayList<Instance>)this.instances.clone());
		return instances2;
	}
	/**
	 * 对连续型属性标准化处理。
	 * @return
	 */
	public Instances Standalise(){
		double [] mean=new double [instances.get(0).getContinuousAttributes().length];
		double []standard=new double[mean.length];
		for(int i=0;i<mean.length;i++){
			for(int j=0;j<instances.size();j++){
				mean[i]+=instances.get(j).getContinuousAttributes()[i];
			}
			mean[i]/=instances.size();
		}
		for(int i=0;i<mean.length;i++){
			for(int j=0;j<instances.size();j++){
				standard[i]+=(instances.get(j).getContinuousAttributes()[i]-mean[i])*(instances.get(j).getContinuousAttributes()[i]-mean[i]);
			}
			standard[i]=Math.sqrt(standard[i]/(instances.size()-1));
			
		}
		Instances instances2=new Instances(this.dataSetNameString);
		for(int j=0;j<instances.size();j++){
			Instance instance=instances.get(j).clone();
			double [] contemp=instance.getContinuousAttributes();
			for(int i=0;i<mean.length;i++){
				if (standard[i]>-0.00000001&&standard[i]<0.00000001) {
					contemp[i]=0;
				}else
				contemp[i]=(contemp[i]-mean[i])/standard[i];
			}
			instance.setContinuousAttributes(contemp);
			instances2.AddInstance(instance);
		}		
		return instances2;
	}
	public void SaveInstances(String path,SaveData saveData){
		saveData.SaveInstances(this, path);
	}
	/**
	 * @param index 从0开始
	 * @return
	 */
	public Instance getInstance(int index){
		return instances.get(index);
	}
	public int getCount(){
		return instances.size();
	}
	public void AddInstance(Instance instance){
		instances.add(instance);
	}
	public void AddInstance(Instance[] instances){
		for(Instance instance:instances){
			this.instances.add(instance);
		}
	}
	public void AddInstance(int i,Instance instance){
		instances.add(i, instance);
	}
	public void RemoveInstance(int i){
		if (i<getCount()) {
			instances.remove(i);			
		}
	}
	public Instance CalCenter(){
		if(getCount()>0){
			Instance instance=instances.get(0).clone();
			double [] con=new double[instance.getContinuousAttributes().length];
			for(int i=0;i<instances.size();i++){
				double [] tmp=instances.get(i).getContinuousAttributes();
				for(int j=0;j<con.length;j++){
					con[j]+=tmp[j];
				}
			}
			for(int j=0;j<con.length;j++){
				con[j]/=instances.size();
			}
			instance.setContinuousAttributes(con);
			return instance;
		}
		else {
			System.out.println("数量为0");
			return null;
		}
	}
	public ArrayList<Instance> getInstances() {
		return instances;
	}
	public int  getNearestInstance(Instance instance,DistanceMethod distanceMethod) {
		int loc=0;
		double dis=Double.MAX_VALUE;
		for(int i=0;i<getCount();i++){
			double tmp=instance.Distance(distanceMethod, instances.get(i));
			if(tmp<=dis){
				dis=tmp;
				loc=i;
			}
		}
		return loc;
	}
	/**取K近邻???
	 * @param instance
	 * @param distanceMethod
	 * @param k
	 * @return
	 */
	public Instances getNearestInstances(final Instance instance,final DistanceMethod distanceMethod ,int k){
		Instances instances=new Instances("");
		TreeSet<Instance> instances2=new TreeSet<>(new Comparator<Instance>() {

			@Override
			public int compare(Instance arg0, Instance arg1) {
				// TODO Auto-generated method stub
				double dis1=instance.Distance(distanceMethod, arg0);
				double dis2=instance.Distance(distanceMethod, arg1);
				if(dis1<dis2)return -1;
				if(dis1>dis2)return 1;
				return 0;
			}
		});
		for(int i=0;i<this.instances.size();i++){
			instances2.add(this.instances.get(i));
			if(instances2.size()>k)instances2.pollLast();
		}
		for(Instance instance2:instances2){
			instances.AddInstance(instance2);
		}
		return instances;
	}
	public int  getNearestInstance(Instance instance,DistanceMethod distanceMethod,int except) {
		int loc=0;
		double dis=Double.MAX_VALUE;
		for(int i=0;i!=except&&i<getCount();i++){
			double tmp=instance.Distance(distanceMethod, instances.get(i));
			if(tmp<=dis){
				dis=tmp;
				loc=i;
			}
		}
		return loc;
	}
	public int getFarestInstance(Instance instance,DistanceMethod distanceMethod){
		int loc=0;
		double dis=0.0;
		for(int i=0;i<getCount();i++){
			double tmp=instance.Distance(distanceMethod, instances.get(i));
			if(tmp>=dis){
				dis=tmp;
				loc=i;
			}
		}
		return loc;
	}
	public double getMaxDis(Instance instance,DistanceMethod distanceMethod){
		double dis=0.0;
		for(int i=0;i<getCount();i++){
			double tmp=instance.Distance(distanceMethod, instances.get(i));
			if(tmp>dis){
				dis=tmp;
			}
		}
		return dis;
	}
 	public void setInstances(ArrayList<Instance> instances) {
		this.instances = instances;
	}
	public String getDataSetNameString() {
		return dataSetNameString;
	}
	public void setDataSetNameString(String dataSetNameString) {
		this.dataSetNameString = dataSetNameString;
	}
	
	
}
