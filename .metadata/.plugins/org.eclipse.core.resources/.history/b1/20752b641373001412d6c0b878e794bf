package core;

import knn.DistanceMethod;

/**
 * @author benfenghua
 *一个实例
 */
public class Instance {
	private String instanceTagString;
	private String [] DisperseAttributes;
	private double [] ContinuousAttributes;
	private String [] attrNames;
	private String label;// 分类标签
	public Instance(String name) {
		// TODO Auto-generated constructor stub
		this.instanceTagString=name;
	}
	public double Distance(DistanceMethod distance,Instance instance) {
		return distance.DistanceOfInstances(this, instance);
	}
	@Override
	public Instance clone(){
		Instance instance=new Instance(instanceTagString);
		instance.instanceTagString=this.instanceTagString;
		instance.label=this.label;
		instance.attrNames=this.attrNames;
		instance.ContinuousAttributes=this.ContinuousAttributes.clone();
		if(this.DisperseAttributes!=null)instance.DisperseAttributes=this.DisperseAttributes.clone();
		return instance;
	}
	public boolean SameAs(Instance instance){
		return instance.getInstanceTagString().equals(this.instanceTagString);
	}
	public String getInstanceTagString() {
		return instanceTagString;
	}
	public Instance getMiddleInstance(Instance instance){
		Instance instance2=clone();
		double [] cont=instance2.getContinuousAttributes();
		double [] cont2=instance.getContinuousAttributes();
		for(int i=0;i<cont.length;i++){
			cont[i]=(cont[i]+cont2[i])/2;
		}
		instance2.setContinuousAttributes(cont);		
		return instance2;
	}
	public Instance getOthersideInstance(Instance instance){
		Instance instance2=clone();
		double [] cont=instance2.getContinuousAttributes();
		double [] cont2=instance.getContinuousAttributes();
		for(int i=0;i<cont.length;i++){
			cont[i]=2*cont[i]-cont2[i];
		}
		instance2.setContinuousAttributes(cont);		
		return instance2;
	}
	public void setInstanceTagString(String instanceTagString) {
		this.instanceTagString = instanceTagString;
	}

	public String[] getDisperseAttributes() {
		return DisperseAttributes;
	}

	public void setDisperseAttributes(String[] disperseAttributes) {
		DisperseAttributes = disperseAttributes;
	}

	public double[] getContinuousAttributes() {
		return ContinuousAttributes;
	}

	public void setContinuousAttributes(double[] continuousAttributes) {
		ContinuousAttributes = continuousAttributes;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String [] getAttrNames() {
		return attrNames;
	}
	public void setAttrNames(String [] attrNames) {
		this.attrNames = attrNames;
	}
}
