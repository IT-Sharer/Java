package data;

import java.io.IOException;
import java.util.HashMap;

import core.FileReadAndWriter;

/**
 * @author benfenghua
 *管理文件的静态方法类
 *获取指定标签下的文件
 */
public class DataFileManager {
	public static HashMap<String, String[]> dataFiles=new HashMap<>();
	
	public static String path="E:/dataProcess/logFile.txt";
	/**
	 * 写入文件信息
	 * 参数：（###号隔开）  文件名称（路径，文件夹路径和当前文件路径一致），
	 * 时间日期,描述，列数，列分隔符，
	 * 离散型属性的序号（逗号隔开）,文件分类
	 * @param strings
	 * @throws IOException 
	 */
	public static void Write(String [] strings){
		String lineString="";
		for(int i=0;i<strings.length-1;i++){
			lineString+=strings[i]+"###";
		}
		lineString+=strings[strings.length-1];
		if(!IsExist(strings[0])){
			FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(path, false,true);
			fileReadAndWriter.WriteLine(lineString);
			fileReadAndWriter.EndWrite();
			dataFiles.put(strings[0], strings);			
		}
		else {
			try {
				throw new IOException("文件已存在不能写入！请验证后在写入");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private static void Load(){
		FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(path, true);
		while(fileReadAndWriter.EndRead()){			
			String [] strings=fileReadAndWriter.ReadLine().split("###");
			dataFiles.put(strings[0], strings);
		}
	}
	/**检测是否存在文件记录
	 * @param filename
	 * @return
	 */
	private static boolean IsExist(String filename) {
		if(dataFiles.size()<1)Load();
		return dataFiles.containsKey(filename);
	}
}
