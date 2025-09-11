import java.lang.*;
import java.util.*;

public class Shuffler {
	
	Shuffler(){
		setSize(136);	
	}

	Shuffler(int N){
		setSize(N);	
	}
	
	public int[] index;
	public int count = 0;
	private int left = 14;


	
	public void setSize(int N){					
		if (index == null || N != index.length){
			index = new int[N];
			initializeIndex();
			permuteIndex();
		}
	}
	
	public void initializeIndex(){				
		for(int i=0;i<index.length;i++)
			index[i] = i;
	}

	public void permuteIndex(){					
		java.util.Random rnd = new java.util.Random();
		for(int i=index.length-1;i>=0;i--){
			int j = rnd.nextInt(i+1);
			int tmp = index[j];
			index[j] = index[i];
			index[i] = tmp;
		}
		left = 14;
		count = 0;
	}

	public void ackKong(){
		left++;
	}

	public Tile getNext(){		
		Tile res;				
		int next = index[count++];
		if (count > index.length - left){
			permuteIndex();
			return null;
		}
	
		res = new Tile(next/4);
		
		return res;
	}
}

