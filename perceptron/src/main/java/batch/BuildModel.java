package batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.mahout.classifier.mlp.TrainMultilayerPerceptron;;

public class BuildModel {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		try {
			String[] a = new String[]{
				"-i", args[0],
				"-labels"
			};
			String[] b = readFile(args[1]);
			File file = new File(args[2]);
			if(file.exists()){
				file.delete();
			}
			String[] c = new String[]{
				"-mo", args[2],
				"-ls", String.valueOf(readFile(args[0]).length-1), "8", String.valueOf(b.length),
				"-l", "0.2",
				"-m", "0.35",
				"-r", "0.0001"
			};
			String[] tmpArgs = (String[]) ArrayUtils.addAll(a,b);
			String[] allArgs = (String[]) ArrayUtils.addAll(tmpArgs,c);
			System.out.println(Arrays.toString(allArgs));
			TrainMultilayerPerceptron.main(allArgs);
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
