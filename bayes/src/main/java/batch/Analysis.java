package batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Reader;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.mahout.classifier.naivebayes.NaiveBayesModel;
import org.apache.mahout.classifier.naivebayes.StandardNaiveBayesClassifier;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileIterator;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;

public class Analysis {

	public static void main(String[] args) {
		String hdfs_rootPath = "hdfs://localhost:9000";
		String work_rootPath = hdfs_rootPath + args[0];
		String res_modelPath = work_rootPath + "/model";
		String work_vecPath = work_rootPath + "/wakati_vec";
		String res_dicPath = work_vecPath + "/dictionary.file-0";
		String work_liPath = work_rootPath + "/labelindex";

		Configuration conf = new Configuration();
		conf.set( "fs.defaultFS", hdfs_rootPath );
		conf.set( "fs.default.name", hdfs_rootPath );
		conf.set( "hadoop.tmp.dir", "tmp/" );

		ArrayList<String> targetDocWordList = readFile( args[1] );
		try {
			NaiveBayesModel model = NaiveBayesModel.materialize( new Path( res_modelPath ), conf );
			StandardNaiveBayesClassifier classifier = new StandardNaiveBayesClassifier(model);

			@SuppressWarnings("resource")
			SequenceFileIterator<Writable, Writable> dicIterator = new SequenceFileIterator<Writable, Writable>( new Path( res_dicPath ), true, conf );
			HashMap<String, Integer> wordIDMap = new HashMap<String, Integer>();
			while (dicIterator.hasNext()) {
			    Pair<?, ?> record = dicIterator.next();
			    String word = record.getFirst().toString();
			    Integer wordID = Integer.valueOf(record.getSecond().toString());
			    wordIDMap.put(word, wordID);
			}
			int wordNum = wordIDMap.size();
			Vector vector = new RandomAccessSparseVector(wordNum);
			
			for(String word : targetDocWordList){
			    if(wordIDMap.get(word) == null){
			        continue;
			    }
			    int wordID = wordIDMap.get(word);
			    vector.setQuick(wordID, vector.get(wordID) + 1);
			}
			
			HashMap<String, String> labelindex = labelIndex(hdfs_rootPath, conf, work_liPath);
			
		    Vector result = classifier.classifyFull(vector);
		    double maxProb = 0;
		    int labelId = 0;
		    for (int i = 0; i < result.size(); i++){
		    	if(i == 0 || (maxProb < result.get(i))){
		    		maxProb = result.get(i);
		    		labelId = i;		    			
		    	}
		    }
	        System.out.print(labelindex.get(String.valueOf(labelId)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static ArrayList<String> readFile( String filename ) {
		ArrayList<String> result = new ArrayList<String>();
		try{
			  File file = new File( filename );
			  BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			  String str;
			  while((str = br.readLine()) != null){
				  String[] arr = str.split(" ");
				  for( String tmp : arr ){
					  result.add( tmp );
				  }
			  }
			  br.close();
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
		return result;
	}
	
	private static HashMap<String, String> labelIndex(String hdfs_rootPath, Configuration conf, String work_liPath){
		HashMap<String, String> labelindex = new HashMap<String, String>();
		try {
			Path path = new Path(work_liPath);
			FileSystem fs = new Path( hdfs_rootPath ).getFileSystem(conf);
			SequenceFile.Reader reader = new Reader(fs, path, conf);
			WritableComparable key = (WritableComparable) reader.getKeyClass()
			        .newInstance();
			Writable value = (Writable) reader.getValueClass().newInstance();
			while (reader.next(key, value)) {
				labelindex.put(value.toString(), key.toString());
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return labelindex;
	}
}
