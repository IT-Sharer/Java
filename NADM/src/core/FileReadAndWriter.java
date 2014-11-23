package core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileReadAndWriter {
	private FileReader fileReader;
	private FileWriter fileWriter;
	private int start=0;
	private int length=100;
	private boolean allRead=false;
	/**
	 * @param path
	 * @param bl 为TRUE表示读数据，FALSE表示写数据
	 * @param append 为TRUE表示从后添加
	 */
	public FileReadAndWriter(String path,boolean bl,boolean append){
		if(bl)
			try {
				fileReader=new FileReader(path);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else {
			try {
				fileWriter=new FileWriter(path,append);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/** 
	 * @param path
	 * @param b
	 */
	public FileReadAndWriter(String path, boolean b) {
		// TODO Auto-generated constructor stub
		try {
			if(b)
			fileReader=new FileReader(path);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void WriteLine(String string){
		try {
			fileWriter.write(string+"\r\n");
			fileWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public BufferedReader getReader(){
		return new BufferedReader(fileReader);
	}
	public String ReadLine() {
		char [] temp = new char[length];
		try {
			if(fileReader.read(temp,start,length)==-1){
				allRead=true;				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public boolean EndRead(){
		return allRead;
	}
	
	public void Write(String string){
		try {
			fileWriter.write(string);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void EndWrite(){
		try {
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void Flush(){
		try {
			fileWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
