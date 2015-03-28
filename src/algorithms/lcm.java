package algorithms;

import java.util.ArrayList;

public class lcm {

	public lcm (){
		// do nothing for now
	}
	
	private int getGCD(int a, int b){
	    while (b > 0)
	    {
	        int temp = b;
	        b = a % b; // % is remainder
	        a = temp;
	    }
	    return a;
	}

	public int getGCD(ArrayList<Integer> input){
	    int result = input.get(0);
	    for(int i = 1; i < input.size(); i++) result = getGCD(result, input.get(i));
	    return result;
	}
	
	private int getLCM (int a, int b)
	{
	    return a * (b / getGCD(a, b));
	}

	public int getLCM (ArrayList<Integer> input)
	{
	    int result = input.get(0);
	    for(int i = 1; i < input.size(); i++) result = getLCM(result, input.get(i));
	    return result;
	}
}
