package batch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		String name = args[0];
		String[] removedArgs = removeTargets(args);
		if(name.equals("build")){
			BuildModel.main(removedArgs);
		}else if(name.equals("analysis")){
			Analysis.main(removedArgs);
		}else{
			System.out.println("no class specified");
		}
	}

	private static String[] removeTargets(String[] args){
		List<String> argList = new ArrayList<String>();
		argList.addAll(Arrays.asList(args));
		argList.remove(0);
		return (String[]) argList.toArray(new String[0]);
	}
}
