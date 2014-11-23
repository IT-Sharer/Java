package core;

public class Save2CSV1 extends SaveData {

	@Override
	public void SaveInstances(Instances instances,String path) {
		// TODO Auto-generated method stub
		System.out.println("向\""+path+"\"写入数据！");
		FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(path,false, true);
		if(instances.getCount()==0){
			System.out.println("数据集为空！");
			return;
		}
		if(instances.getInstance(0).getAttrNames()!=null){
			String stringsString="no,";
			for(int i=0;i<instances.getInstance(0).getAttrNames().length;i++){
				stringsString +=instances.getInstance(0).getAttrNames()[i]+",";
			}
			stringsString+="classLabel ";
			fileReadAndWriter.WriteLine(stringsString);
		}		
		for(int i=0;i<instances.getCount();i++){
			StringBuffer string=new StringBuffer();
			Instance instance=instances.getInstance(i);
			string.append(instance.getInstanceTagString()+",");
			if(instance.getDisperseAttributes()!=null)
			for(int j=0;j<instance.getDisperseAttributes().length;j++){
				string.append(instance.getDisperseAttributes()[j]+",");
			}
			if(instance.getContinuousAttributes()!=null)
			for(int j=0;j<instance.getContinuousAttributes().length;j++){
				string.append(instance.getContinuousAttributes()[j]+",");
			}
			string.append(instance.getLabel());
			fileReadAndWriter.WriteLine(string.toString());
		}
		
	}

}
