package logic.TD;

public class runTraining {

	public static void main(String[] args) {
		TrainTD td = new TrainTD();
		for(int i=0;i<10000;i++)
		td.train(100);
	}
}
