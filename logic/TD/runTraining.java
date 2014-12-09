package logic.TD;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class runTraining {

	public static void main(String[] args) {
		int count = 0;
		TrainTD td = new TrainTD();
		while(true) {	// run training continuously with periodic output
			td.train(100);
			
			BufferedReader reader = null;
			try {
				
				reader = new BufferedReader(new FileReader("count.txt"));
				String scount = reader.readLine();
				count = Integer.parseInt(scount);
				count += 100;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			try {
				reader.close();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter("count.txt"));
				writer.write(Integer.toString(count));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				writer.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	}
}
