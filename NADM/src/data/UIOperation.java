/**
 * 
 */
package data;

import java.util.Scanner;

/**
 * 与用户交互的执行
 * @author benfenghua
 *
 */
public class UIOperation {
	private DataPartition dataPartition=null;
	public UIOperation(DataPartition dataPartition){
		this.dataPartition=dataPartition;
	}
	public void Interaction() {
		while(true){
			System.out.println("please Input print stop save:");
			Scanner input=new Scanner(System.in);
			String string=input.next();
			dataPartition.recieveMsg(string);
			if(string.equals("stop"))break;
			input.close();
		}
	}
}
