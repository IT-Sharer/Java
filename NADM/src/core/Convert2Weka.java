package core;

import java.io.File;
import java.io.IOException;

import weka.core.FastVector;
import weka.core.Instance;
import weka.core.converters.CSVSaver;
import weka.core.converters.Saver;
/**
 * @author benfenghua
 *将Instance和Instances转化到Weka中的实例与实例集输出
 */
public class Convert2Weka {

	public weka.core.Instance convert2Instance(core.Instance instance){
		weka.core.Instance instance2=new Instance(instance.getContinuousAttributes().length+1);
		int da=0;
		if(instance.getDisperseAttributes()!=null)
			da=instance.getDisperseAttributes().length;
		String [] attriN=instance.getAttrNames();
		int i=0;
		for(;i<instance.getContinuousAttributes().length-1;i++){
//			Attribute attribute=new Attribute(attriN[da+i]);
//			instance2.setValue(attribute, instance.getContinuousAttributes()[i]);
			instance2.setValue(i, instance.getContinuousAttributes()[i]);
		}		
		instance2.setValue(i, instance.getLabel());
		return instance2;
	}
	public weka.core.Instances convert2Instances(Instances instances){
		weka.core.Instance instance=convert2Instance(instances.getInstance(0));
		FastVector att=new FastVector(instance.numAttributes());
		for(int i=0;i<instance.numAttributes();i++)att.addElement(instance.attribute(i));
		
		weka.core.Instances instances2=new weka.core.Instances("", att, instances.getCount());
		instances2.add(instance);
		for(int i=1;i<instances.getCount();i++){
			instances2.add(convert2Instance(instances.getInstance(i)));
		}
		return null;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LoadCSVData1 loadCSVData1=new LoadCSVData1(new int []{0,1,2,3,4,5,6,7,8}, "E:/dataProcess/sample1.csv");
		Instances trainInstances=loadCSVData1.loadInstances("sample1");
		trainInstances=new InstanceProcessMethod1("离散转连续属性").processInstances(trainInstances);
		loadCSVData1=new LoadCSVData1(new int []{0,1,2,3,4,5,6,7,8}, "E:/dataProcess/TestSample1.csv");
		Instances testInstances=loadCSVData1.loadInstances("TestSample1");
		testInstances=new InstanceProcessMethod1("离散转连续属性").processInstances(testInstances);
		Convert2Weka convert2Weka=new Convert2Weka();
		weka.core.Instances instances=convert2Weka.convert2Instances(trainInstances);
		weka.core.Instances instances2=convert2Weka.convert2Instances(testInstances);
		Saver saver=new CSVSaver();
		try {
			saver.setFile(new File("E:/dataProcess/sample1_weka.csv"));
			saver.setInstances(instances);
			saver.writeBatch();
			saver.setFile(new File("E:/dataProcess/TestSample1_weka.csv"));
			saver.setInstances(instances2);
			saver.writeBatch();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
