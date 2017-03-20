public class question2
{
	public static void main(String[] args) 
	{
	
		DrawAxis(0,0);
		DrawLine(0,0,1,3);
		DrawParabola(0,0,0.2,1,2);
	}


	public static String DrawAxis(int i, int j)
	{
		// H = 21 because if it were 20, the positive y axis would only have 9 units
				int W = 20;
				int H = 21;
				int center_x = W/2;
				int center_y = H/2;

				for (int y=0; y<H; y++){
					for (int x=0; x<W; x++){
						int x_ = x - center_x;
						int y_ = y - center_y;
						if (x_ == 0 && y_ == 0){
							
							System.out.print(".");
							
						}if (x_ == 0 && y_ == -10){
							
							System.out.print("^");
							
						}if (x_ == 9 && y_ == 0) {
							
							System.out.print(">");
							
						}if (x_ == 0 && y_ != 0 && y_ != -10){

							System.out.print("|");

						}if (y_ == 0 && x_ != 9){
							
							System.out.print("-");
							
						} else {

							System.out.print(" ");
						}
						
					}

					System.out.println();
				}
		return ""; 
	}

	public static String DrawLine(int i, int j, double a, double b)
	{
		int W = 20;
		int H = 20;
		
		for (int y=0; y<H; y++){
			for (int x=0; x<W; x++){
				
				if (a*x+b >= y && a*x+b <= y ){
					System.out.print("*");
				} else {
					System.out.print(" ");
				}
			}
			System.out.println();
		}
			
	return ""; 

	}
	public static String DrawParabola(int i, int j, double a, double b, double c)
	{
		int W = 20;
		int H = 20;
		int center_x = W/2;
		int center_y = H/2;
		
		for (int y=0; y<H; y++){
			for (int x=0; x<W; x++){
				int x_ = x - center_x -5;
				int y_ = y - center_y;

				if (a*x_*x_+b*x_+c >= y_-2 && a*x_*x_+b*x_+c <= y_+2){
					System.out.print("*");
				} else {
					
					System.out.print(" ");
				}
			}
			
			System.out.println();
		}
		return ""; 

	}
}