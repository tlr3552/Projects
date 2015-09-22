import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;

/*
 * @author Tyler Russell
 * Classifier.java is the java implementation of a NaiveBayes classifier
 * using the Weka libraries for it.
 * 
 * The dataset, in ".arff" format, is taken from the command line argument.
 */

public class NaiveBayesClassifier {

	public static void main(String[] args) {

		try {
			
			//Read in .arff file
			File dataset = new File(args[0]);
			BufferedReader br = new BufferedReader(new FileReader(dataset));
			Instances trainingset = new Instances(br);
			
			//Tell the instances object which attributes are being used.
			//The last one isnt included because its the target
			trainingset.setClassIndex(trainingset.numAttributes() - 1);
	
			//Classifier object
			NaiveBayes bayes = new NaiveBayes();
			
			//Build the classifier with the dataset
			bayes.buildClassifier(trainingset);
			
			//Close data stream
			br.close();	
			
			//Actually get the results
			Evaluation eval = new Evaluation(trainingset);
			eval.evaluateModel(bayes, trainingset);
			
			//10 fold cross validation
			eval.crossValidateModel(bayes, trainingset, 10, new Random(1));
			
			//Display results
			System.out.print(eval.toSummaryString("Naive Bayes Classification Results: \n\nNumber of folds for validation: \t10", false));
			System.out.println("Area under ROC:\t\t\t" + eval.areaUnderROC(1));
			System.out.println(eval.toMatrixString("\n---- Confusion Matrix ----"));
			System.out.println("\nBayesian classification successful.");
		} 
		//Catch errors
		catch (IOException e) {
			System.err.println("Couldn't read in ARFF file. Exiting...");
			System.exit(0);
		}
		catch(Exception e) {
			System.err.println("Error building the classifier. Trace:\n");
			e.printStackTrace();
			System.exit(0);
		}
	}
}
