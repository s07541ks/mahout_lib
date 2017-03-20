package batch;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.classifier.naivebayes.test.TestNaiveBayesDriver;
import org.apache.mahout.classifier.naivebayes.training.TrainNaiveBayesJob;
import org.apache.mahout.text.SequenceFilesFromDirectory;
import org.apache.mahout.utils.SplitInput;
import org.apache.mahout.vectorizer.SparseVectorsFromSequenceFiles;

public class BuildModel {

	public static void main(String[] args) {
		Path localPath = new Path( args[0] );

		String hdfs_rootPath = "hdfs://localhost:9000";
		String hdfs_workPath = args[1];
		String work_rootPath = hdfs_rootPath + hdfs_workPath;
		String work_tmpPath = work_rootPath + "/tmp";
		String work_txtPath = work_rootPath + "/" + args[2];
		String work_seqPath = work_rootPath + "/wakati_seq";
		String work_vecPath = work_rootPath + "/wakati_vec";
		String work_tfPath = work_vecPath + "/tfidf-vectors";
		String work_trPath = work_rootPath + "/wakati_vec_tr";
		String work_tsPath = work_rootPath + "/wakati_vec_ts";
		String work_liPath = work_rootPath + "/labelindex";
		String res_modelPath = work_rootPath + "/model";
		String res_tsPath = work_rootPath + "/test";
		
		Configuration conf = new Configuration();
		conf.set( "fs.defaultFS", hdfs_rootPath );
		conf.set( "fs.default.name", hdfs_rootPath );
		conf.set( "fs.permissons", "false" );
		conf.set( "hadoop.tmp.dir", "tmp/" );
		try {
			// hadoop fs -mkdir & -put
			System.out.println( "start: hadoop fs -mkdir & -put" );
			FileSystem fs = new Path( hdfs_rootPath ).getFileSystem(conf);
			fs.delete( new Path( work_rootPath ), true );
			fs.mkdirs( new Path( work_rootPath ) );
			System.out.println( hdfs_workPath );
			fs.copyFromLocalFile(false, false, localPath, new Path( hdfs_workPath ) );
			System.out.println( "end: hadoop fs -mkdir & -put" );

			// mahout seqdirectory
			System.out.println( "start: mahout seqdirectory" );
			ToolRunner.run( conf, new SequenceFilesFromDirectory(), new String[]{
					"-i", work_txtPath, 
					"-o", work_seqPath,
					"--tempDir", work_tmpPath
				} );
			System.out.println( "end: mahout seqdirectory" );
			
			// seq2parse
			System.out.println( "start: mahout seq2parse" );
			ToolRunner.run( conf, new SparseVectorsFromSequenceFiles(), new String[]{
					"-i", work_seqPath, 
					"-o", work_vecPath,
					"-a", "org.apache.lucene.analysis.core.WhitespaceAnalyzer",
					"-ow"
				} );
			System.out.println( "end: mahout seq2parse" );

			// split
			System.out.println( "start: mahout split" );
			ToolRunner.run( conf, new SplitInput(), new String[]{
				"-i", work_tfPath,
				"-tr", work_trPath,
				"-te", work_tsPath,
				"--randomSelectionPct", "20",
				"--randomSelectionPct",
				"--method", "sequential", 
				"-ow",
				"-seq"
			} );

			// train
			System.out.println( "mahout train" );
			ToolRunner.run( conf, new TrainNaiveBayesJob(), new String[]{
				"-i", work_trPath, 
				"-o", res_modelPath, 
				"-li", work_liPath,
				"-el", "-ow",
			} );
			
			// test
			System.out.println( "mahout test" );
			ToolRunner.run( conf, new TestNaiveBayesDriver(), new String[]{
				"-i", work_tsPath,
				"-m", res_modelPath,
				"-l", work_liPath,
				"-o", res_tsPath,
				"-ow"
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
