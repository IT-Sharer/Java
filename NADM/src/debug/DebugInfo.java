package debug;

import core.FileReadAndWriter;

public class DebugInfo {
	public static boolean debug=true;
	public static boolean print=false;
	public static boolean log=false;
//	public static FileReadAndWriter filerwers=new FileReadAndWriter();
	public static void printMsg(String msg){
		if(debug){
			if(print)System.out.println(msg);			
		}
	}
}
