package tmp;

public class DecisionTree {
	private String string;
	public int i;
	public DecisionTree() {
		// TODO Auto-generated constructor stub
		
		
	}
	public static int count=0;
	public static void printNdig(int n,int [] a) {
		if (n==0) {
			boolean b=false;
			for(int i=a.length-1;i>=0;i--){
				if(a[i]!=0)b=true;
				if(b==true)System.out.print(a[i]);
			}
			if(b!=false){
				System.out.println("");
				count++;
			}
		}
		else {
			for(int i=0;i<10;i++){
				a[n-1]=i;
				printNdig(n-1, a);
			}
		}
	}
	public static void main(String[] args) {
		int n=5;
		int [] a=new int[n];
		printNdig(n, a);
		System.out.println(count);
	}
}
