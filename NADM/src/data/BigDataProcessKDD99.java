package data;

import java.text.DecimalFormat;

import knn.DistanceMethod;
import knn.DistanceMethod1;
import core.FileReadAndWriter;
import core.Instance;
import core.InstanceProcessMethod1;
import core.InstanceProcessMethod2;
import core.Instances;
import core.LoadSqlData;

public class BigDataProcessKDD99 {

	/**
	 * 计算所有样本的K近邻，保存到文件中
	 * <id   id2:距离,id3:距离:是否与ID1类别相同1和0表示,id4:距离>
	 */
	public static void CalAllKnn(String path){
		String  tableName="Kddcup99_no1";
		String conditionString="";
		LoadSqlData loadSqlData;
		InstanceProcessMethod1 instanceProcessMethod1=new InstanceProcessMethod1("");
		InstanceProcessMethod2 instanceProcessMethod2=new InstanceProcessMethod2();	
		DecimalFormat df=new DecimalFormat("#.00000"); 
		for(int i=1;i<=KDDCUP99Process.allnum;i++){
			FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(path, false,true);
			int numKnn=800;//每个样本的近邻计算量；
			int numPT=50000;//每次加载的数据量
			conditionString=" no='"+i+"' ";
			loadSqlData=new LoadSqlData(tableName, conditionString);
			Instance instance=loadSqlData.loadInstances("").getInstance(0);
			instance=instanceProcessMethod1.processInstance(instance);
			instanceProcessMethod2.processInstance(instance);
//			Save2CSV1 save2csv1=new Save2CSV1();
//			Instances instances=new Instances("spa");
//			instances.AddInstance(instance);
//			save2csv1.SaveInstances(instances, "E:/dataProcess/temp.csv");
//			System.exit(0);
			Instance [] knnInstances=new Instance[numKnn];
			double [] knnDistances=new double[numKnn];			
			int start=0,point=0;
			while(start<KDDCUP99Process.allnum){
				conditionString="no>='"+start+"' and no<'"+(start+numPT)+"' ";
				loadSqlData.setConditionString(conditionString);
				Instances instances=loadSqlData.loadInstances(""+start);
				instances=instanceProcessMethod1.processInstances(instances);
				instances=instanceProcessMethod2.processInstances(instances);
				//计算近邻
				DistanceMethod distanceMethod=new DistanceMethod1();
//				System.out.println(instances.getCount());
				for(int j=0;j<instances.getCount();j++){
					if(!instance.SameAs(instances.getInstance(j))){
						double distance=instance.Distance(distanceMethod, instances.getInstance(j));
						if(point==0){
							knnDistances[point]=distance;
							knnInstances[point]=instances.getInstance(j);
							point++;
						}else {
							int tem=point;
							while (tem>0&&knnDistances[tem-1]>distance) {
								knnDistances[tem]=knnDistances[tem-1];
								knnInstances[tem]=knnInstances[tem-1];
								tem--;								
							}
							if(tem>numKnn-1)tem=numKnn-1;
							knnDistances[tem]=distance;
							knnInstances[tem]=instances.getInstance(j);
							if(point<numKnn-1)point++;
						}
					}
				}	
				System.out.println(start+"处理完！");				
				start+=numPT;
			}
			//<id   id2:距离,id3:距离:是否与ID1类别相同1和0表示,id4:距离>
			StringBuffer line=new StringBuffer();
			line.append(i+"\t");
			int n=0;
			for(;n<knnDistances.length-1;n++){
//				System.out.println(n);
				line.append(knnInstances[n].getInstanceTagString()+":");
				line.append(df.format(knnDistances[n])+":");
				line.append(knnInstances[n].getLabel().equals(instance.getLabel())?1:0);
				line.append(",");
			}
			line.append(knnInstances[n].getInstanceTagString()+":");
			line.append(knnDistances[n]+":");
			line.append(knnInstances[n].getLabel().equals(instance.getLabel())?1:0);
			fileReadAndWriter.WriteLine(line.toString());
			fileReadAndWriter.EndWrite();
		}
	}
	
	/**
	 * 近邻抽样
	 * @param pathString
	 */
	public static void DataSelector(String pathString){
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CalAllKnn("E:/dataProcess/CalAllKnn1.txt");
	}

}
