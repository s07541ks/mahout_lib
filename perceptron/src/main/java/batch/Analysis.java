package batch;

import java.io.File;

import org.apache.mahout.classifier.mlp.RunMultilayerPerceptron;;

public class Analysis {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		try {
			File file = new File(args[2]);
			if(file.exists()){
				file.delete();
			}
			RunMultilayerPerceptron.main(new String[]{
					"-i", args[0],
					"--columnRange", "0", "3",
					"-mo", args[1],
					"-o", args[2]
				});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
