package core;

import java.io.BufferedReader;
import java.io.IOException;

public class LoadCSVData1 extends LoadData {
	private int [] discreteAttributesIndex;
	private String path;
	/**
	 * @param discreteAttributesIndex 离散属性的序号，从0开始
	 * @param pathString 文件的路径
	 */
	public LoadCSVData1(int [] discreteAttributesIndex,String pathString) {
		// TODO Auto-generated constructor stub
		this.discreteAttributesIndex=discreteAttributesIndex;
		this.path=pathString;
	}
	public LoadCSVData1() {
		// TODO Auto-generated constructor stub
		
	}
	/* (non-Javadoc)
	 * @see core.LoadData#loadInstances(java.lang.String)
	 * @param string 构造实例的名称
	 */
	@Override
	public Instances loadInstances(String string) {
		// TODO Auto-generated method stub
		Instances instances=new Instances(string);
		FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(path, true);
		BufferedReader bufferedReader=fileReadAndWriter.getReader();
		String line="";
		try {
			String[] attributeStrings;
			if((line=bufferedReader.readLine())!=null){
				String []tempStrings=line.split(",");
				attributeStrings=new String[tempStrings.length-2];
				for(int i=0;i<attributeStrings.length;i++){
					attributeStrings[i]=tempStrings[i+1];
				}
			}
			else {
				System.out.println("文件为空！");
				return null;
			}
			while((line=bufferedReader.readLine())!=null){
				String [] attriStrings=line.split(",");
				String [] dA=new String[discreteAttributesIndex.length];
				double [] cA=new double[attriStrings.length-dA.length-1];
				int i=0,j=0;
				String name="";
				for(int k=0;k<attriStrings.length-1;k++){
					if(isDiscrete(k)){
						dA[i]=attriStrings[k].trim();
						name+=attriStrings[k];
						i++;
					}else {
						cA[j]=Double.parseDouble(attriStrings[k]);
						name+=attriStrings[k];
						j++;
					}
				}
				Instance instance=new Instance(name);
				instance.setContinuousAttributes(cA);
				instance.setDisperseAttributes(dA);
				instance.setLabel(attriStrings[attriStrings.length-1]);
				instance.setAttrNames(attributeStrings);				
				instances.AddInstance(instance);
			}
			return instances;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private boolean isDiscrete(int k){
		for(int i=0;i<discreteAttributesIndex.length;i++){
			if(discreteAttributesIndex[i]==k) return true;
		}
		return false;
	}
	public Instances loadInstances1(String pathString){
		Instances instances=new Instances("");
		FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(pathString, true);
		BufferedReader bufferedReader=fileReadAndWriter.getReader();
		String line="";
		try {
			while((line=bufferedReader.readLine())!=null){
				String [] attriStrings=line.split(",");
				double [] cA=new double[attriStrings.length-2];
				for(int k=1;k<attriStrings.length-1;k++){
						cA[k-1]=Double.parseDouble(attriStrings[k]);
				}
				Instance instance=new Instance(attriStrings[0]);
				instance.setContinuousAttributes(cA);
				instance.setLabel(attriStrings[attriStrings.length-1].trim());			
				instances.AddInstance(instance);
			}
			return instances;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
