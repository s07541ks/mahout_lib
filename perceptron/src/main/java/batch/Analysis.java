package batch;

import org.apache.mahout.classifier.mlp.RunMultilayerPerceptron;;

public class Analysis {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		try {
			System.out.println( "mahout train" );
			RunMultilayerPerceptron.main(new String[]{
					"-i", "/vagrant/iris.csv",
					"--skipHeader",
					"--columnRange", "0", "3",
					"-mo", "/vagrant/iris_model.model",
					"-o", "/vagrant/iris_result.txt"
				});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
