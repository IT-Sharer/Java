package data;

import core.SQLHelper;

/**
 * @author benfenghua
 *产生十折交叉验证需要的十份数据，保证正常样本和非正常样本的比例一致。
 *视图命名规则：KDDCup99_(数据折数编号0-9）_train 或者Test
 */
public class TenFoldGenerate {
	SQLHelper sqlHelper=new SQLHelper();
	public void Generate(){
		int numFold=10;
		int start=1,normalMax= 812814,unNormalMin=normalMax+1,unNormalMax=1073856;
		int normalfold=81281,unnmf=26104;
		String viewNameString="KDDCup99_";
		String sqlString="create view ";
		String  cond=" as select * from KDDcup99_no3_3 where ";
		for(int i=1;i<numFold;i++){
			//产生训练集集
			viewNameString="KDDCup99_"+i+"_train";
			int min=i*normalfold,max=(i+1)*normalfold;
			int min2=unNormalMin+i*unnmf,max2=min2+unnmf;
			String sql=sqlString+viewNameString+cond+" no <'"+min+"' or (no>='"+max+"' and no<'"+min2+"') or no>='"+max2+"'";
			sqlHelper.executeSQL(sql);
			//产生测试集
			viewNameString="KDDCup99_"+i+"_test";
			sql=sqlString+viewNameString+cond+" (no >='"+min+"' and no<'"+max+"') or( no>='"+min2+"' and no<'"+max2+"' )";
			sqlHelper.executeSQL(sql);
		}
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TenFoldGenerate tenFoldGenerate=new TenFoldGenerate();
		tenFoldGenerate.Generate();
	}

}
