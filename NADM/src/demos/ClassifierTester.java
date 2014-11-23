package demos;

import knn.DistanceMethod;
import knn.DistanceMethod1;
import knn.GetKNNMethod;
import knn.GetKNNMethod1;
import knn.GetKNNSameOrDiff;
import knn.KNN;
import knn.KNNDecisionMethod;
import knn.KNNDecisionMethod1;
import knn.TCM_KNN;
import knn.MyKNN;
import core.InstanceProcessMethod1;
import core.Instances;
import core.KNNClassifier;
import core.LoadCSVData1;
import core.Save2CSV1;

/**
 * @author benfenghua
 *分类器构造及测试
 *1.实例化一个具体的分类器
 *2.设置相关参数和属性
 *3.训练分类器
 *4.测试分类器，记录准确率，误差等效果评估指标
 *5.输出结果
 */
public class ClassifierTester {
	private Instances trainInstances,testInstances;
	DistanceMethod distanceMethod;
	GetKNNMethod getKNNMethod;
	KNNDecisionMethod knnDecisionMethod;
	int k;
	KNNClassifier knnClassifier;
	public ClassifierTester(int k,String param) {
		// TODO Auto-generated constructor stub
		this.k=k;
		Initialize();
		if(param.equals("KNN")){
			getKNNMethod=new GetKNNMethod1(this.trainInstances, distanceMethod, k);
			
			knnClassifier=new KNN(k,distanceMethod,getKNNMethod,knnDecisionMethod
					,trainInstances,testInstances,param);			
		}
		else if(param.equals("TCM_KNN")) {
			getKNNMethod=new GetKNNSameOrDiff(distanceMethod, trainInstances);
			knnClassifier=new TCM_KNN(k,distanceMethod,getKNNMethod,knnDecisionMethod
					,trainInstances,testInstances,param);
		}
		else if (param.equals("MyKNN")) {
			getKNNMethod=new GetKNNMethod1(this.trainInstances, distanceMethod, k);
			knnClassifier=new MyKNN(k,distanceMethod,getKNNMethod,knnDecisionMethod
					,trainInstances,testInstances,param);
		}
	}
	/**
	 * 加载数据,初始化
	 */
	public void Initialize(){
		Save2CSV1 save2csv1=new Save2CSV1();
		LoadCSVData1 loadCSVData1=new LoadCSVData1(new int []{0,1,2,3,4,5,6,7,8}, "E:/dataProcess/sample1.csv");
		trainInstances=loadCSVData1.loadInstances("sample1");
		trainInstances=new InstanceProcessMethod1("离散转连续属性").processInstances(trainInstances);
//		trainInstances.SaveInstances("E:/dataProcess/sample1_lisan2lianxu2.csv", save2csv1);
		trainInstances=trainInstances.Standalise();
//		trainInstances.SaveInstances("E:/dataProcess/sample1_lisan2lianxu_biaozhunhua2.csv", save2csv1);
		loadCSVData1=new LoadCSVData1(new int []{0,1,2,3,4,5,6,7,8}, "E:/dataProcess/TestSample1.csv");
		testInstances=loadCSVData1.loadInstances("TestSample1");
		testInstances=new InstanceProcessMethod1("离散转连续属性").processInstances(testInstances);
//		 testInstances.SaveInstances("E:/dataProcess/TestSample1_lisan2lianxu.csv", save2csv1);
		 testInstances=testInstances.Standalise();
//		 testInstances.SaveInstances("E:/dataProcess/TestSample1_lisan2lianxu_biaozhunhua.csv", save2csv1);
		distanceMethod=new DistanceMethod1();
		knnDecisionMethod=new KNNDecisionMethod1();
//		System.exit(0);
	}
	
	/**
	 * 测试分类器分类结果
	 */
	public void Result(){
		knnClassifier.Classify();
		int count=0;
		for(int i=0;i<knnClassifier.getResultStrings().length;i++){
			if (knnClassifier.getResultStrings()[i].split(",")[0].trim().equals(testInstances.getInstance(i).getLabel().trim())) {
//				System.out.println("分类正确！");
				count++;
			}
			else {
				System.out.println(testInstances.getInstance(i).getLabel().trim()+","+knnClassifier.getResultStrings()[i]+","+
						knnClassifier.getResultStrings()[i].split(",")[0].trim());
			}
		}
		System.out.println("正确率是："+count*1.0/testInstances.getCount());
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int k=30;//KNN貌似30最好
		String param="MyKNN";
		ClassifierTester classifierTester=
				new ClassifierTester(k, param);
		classifierTester.Result();
	}

}
