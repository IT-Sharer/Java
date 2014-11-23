package data;

import java.util.Date;

public class NewThread implements Runnable {
	private DataPartition dataPartition=null;
	public void SendMSG2Cal(String msg){
		dataPartition.recieveMsg(msg);
	}
	public NewThread(DataPartition dataPartition) {
		// TODO Auto-generated constructor stub
		this.dataPartition=dataPartition;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		dataPartition.limitVol();
		System.out.println(new Date().toLocaleString());
	}

}
