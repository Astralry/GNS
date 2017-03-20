import java.util.Scanner;

public class question1
{
	public static void main(String[] args)
	{
		
		String input;
		Scanner in = new Scanner(System.in);

		System.out.println("Enter a binary number");
		
		input=in.next();
		 
		//The argument of this if statement is the method that checks if the input is a binary number
		//The method will return a boolean operator
		if(CheckInputCorrect(input)){
			BinaryToNumber(input);
			System.out.println("");
		}else {
			System.out.println("Stop trying to screw up my program");
			main(args);
		}
		
	}

	public static boolean CheckInputCorrect(String input)
	{ 
		boolean binary = true;
		//this for loop goes through all the characters in input and checks if they are not equal to 1 or 0
		//if not 1 or 0, binary is set to false/
		for(int i = 0; i < input.length(); i++){
			 if(input.charAt(i) != '1' && input.charAt(i) != '0'){
				 binary = false;
			 }
		 }
		return binary;
	}


	public static int BinaryToNumber(String input)
	{
		int length = input.length();
		int x = 1;
		// This for loop serves as the power operator
		for (int y = 0; y < length - 1; y++){
			x = 2*x;
		}
		int total = 0;
		//This for loop goes through each digit of the input and adds 2^(digit # to the total)
		for(int j = 0;j < length; j++){
			if(input.charAt(j) == '1'){
				
				total = total + x;
				
			}
			x = x/2;
		}
		
		System.out.println("Your binary number is " +total+ " in decimal");
		return total;
		
	}
}