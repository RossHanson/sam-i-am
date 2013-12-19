package jvr.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.StringTokenizer;

public class WordTrainingSet {
	public static HashSet<String> positiveWords = new HashSet<String>();
	public static HashSet<String> negativeWords = new HashSet<String>();
	public static String pos_words = "data/positive-words.txt";
	public static String neg_words = "data/negative-words.txt";
	
	/**
	 * For training. Just run this and follow the instructions to update our
	 * .txt files containing positive and negative words.
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		importWordSet(true);
	}

    public static void importWordSet(boolean train){
		try {
			addWordsToMap(pos_words, neg_words);
			if (train) trainingSet();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void trainingSet () throws IOException{
		Scanner in = new Scanner(System.in);
		System.out.println("Please enter the story you want to have analyzed.");
		String story = in.nextLine();
		StringTokenizer st = new StringTokenizer(story);
		String pos_neg = "0";
		String word;
		FileWriter posWriter = new FileWriter(pos_words,true);
		FileWriter negWriter = new FileWriter(neg_words,true);
		while(!story.toLowerCase().equals("exit")){
			while(st.hasMoreTokens()){
				word = removePeriods(st.nextToken().toLowerCase());
				if (!positiveWords.contains(word)&&!negativeWords.contains(word)){
					System.out.println("Please enter 1 if the word \""+word+"\" is positive, 0 if it is a neutral word, and -1 otherwise");
					pos_neg = in.nextLine();
                    while (!(pos_neg.equals("-1") || pos_neg.equals("0") || pos_neg.equals("1"))){
                        System.out.println("This is not a valid input. Please enter 1, 0 or -1!");
                        pos_neg = in.nextLine();
                    }
                    if (pos_neg.equals("1")){
						positiveWords.add(word);
						posWriter.write("\n"+word);
					}
					if (pos_neg.equals("-1")){
						negativeWords.add(word);
						negWriter.write("\n"+word);
					}

				}
			}
			System.out.println("If you want to continue training, enter the next story. Otherwise type \"exit\" and all changes will be saved.");
			story = in.nextLine();
			st = new StringTokenizer(story);
		}
		in.close();
		posWriter.close();
		negWriter.close();
	}
	
	private static String removePeriods(String story){
		String removed = "";
		int current_index = 0;
		int index = story.indexOf(".");
		while (current_index < story.length() && index > -1){
			removed = removed+story.substring(current_index, index);
			index = story.indexOf(".");
			current_index = index+1;
		}
		removed = removed+story.substring(current_index);
		return removed;
	}

	public static void addWordsToMap (String posDir, String negDir) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(posDir));
		String line = null;
		while ((line = reader.readLine()) != null) {
			positiveWords.add(line.toLowerCase());
		}
		reader.close();
		reader = new BufferedReader(new FileReader(negDir));
		line = null;
		while ((line = reader.readLine()) != null) {
			negativeWords.add(line.toLowerCase());
		}
		reader.close();
	}
	public static void printwords(String text_dir) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(text_dir));
		String line = null;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		reader.close();
	}
}
