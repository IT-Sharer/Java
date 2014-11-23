package core;

public abstract class InstanceProcess {
	protected String methodNameString;
	public Instances processInstances(Instances instances){
		Instances instances2=new Instances(instances.getDataSetNameString()+methodNameString);
		for(int i=0;i<instances.getCount();i++){
			instances2.AddInstance(processInstance(instances.getInstance(i)));
		}
		return instances2;
	}
	public abstract Instance processInstance(Instance instance);
}
