package batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
					"--columnRange", "0", String.valueOf(readFile(args[0]).length-2),
					"-mo", args[1],
					"-o", args[2]
				});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String[] readFile(String filename) {
		ArrayList<String> result = new ArrayList<String>();
		try{
			  File file = new File( filename );
			  BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			  String[] arr = br.readLine().split(",");
			  for( String tmp : arr ){
				  result.add( tmp );
			  }
			  br.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		return result.toArray(new String[0]);
	}
}
