
public class line {
	public static void main(String[] args) {
		int W = 20;
		int H = 20;
		int center_x = W/2;
		int center_y = H/2;
		
		double a = -0.5;
		int b = -1;
		int c = 1;
		
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
			
	}
}
		


