package batch;

import org.apache.mahout.classifier.mlp.TrainMultilayerPerceptron;;

public class BuildModel {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		try {
			System.out.println( "mahout train" );
			TrainMultilayerPerceptron.main(new String[]{
					"-i", "/vagrant/iris.csv",
					"-sh",
					"-labels", "setosa", "versicolor", "virginica",
					"-mo", "/vagrant/iris_model.model",
					"-ls", "4", "8", "3",
					"-l", "0.2",
					"-m", "0.35",
					"-r", "0.0001"
				});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
