package jvr.parser;

import java.util.SortedSet;
import java.util.TreeSet;

import edu.stanford.nlp.trees.Tree;

public class BasicParserFunctionality {

	public static void main(String[] arg){//For testing only. You can run to test.
		String phrase = "(ROOT (S (S (NP (DT The) (NN wizard)) (VP (VBD walked) (SBAR (S (SBAR (ADVP (RB outside) (. .)) (S (NP (PRP He)) (VP (VBD liked) (NP (DT the) (NN snow))) (. .))) (NP (DT The) (NN wizard)) (VP (VBD lied) (PP (TO to) (NP (DT the) (JJ little) (NN girl))))))) (. .)) (NP (DT The) (NN wolf)) (VP (VBD ate) (NP (DT the) (JJ fat) (NN girl))) (. .)))";
		System.out.println("The original String is: "+phrase);
		SortedSet<String> nps = extractNounPhrases(phrase);
		System.out.println("The noun phrases are: "+nps.toString());
		SortedSet<String> vps = extractVerbPhrases(phrase);
		System.out.println("The verb phrases are: "+vps.toString());
	}

	/**
	 * Given a string representing a sentence or a story, returns
	 * the index of the closing parenthesis that matches the
	 * parenthesis located at index firstParens of the string.
	 * @param story
	 * @return
	 */
	public static int closingParenthesis(String story, int firstParens){
		int numParens = 1;
		int index = firstParens+1;
		while (numParens > 0 && index < story.length() && index > -1){
			if (story.charAt(index)=='('){
				numParens++;
			}
			if (story.charAt(index)==')'){
				numParens--;
			}
			index++;
		}
		return index;
	}

	/**
	 * Given a String[] of Strings representing the parts of speech we
	 * are interested in, returns the index of the part of speech that occurs
	 * the earliest in the string "clause."
	 * @param posArr
	 * @param clause
	 * @param startIndex
	 * @return
	 */
	public static int chooseNextIndex(String[] posArr, String clause, int startIndex){
		int min = Integer.MAX_VALUE;
		int offset = posArr[0].length();
		for (String s : posArr){
			int i = clause.indexOf(s, startIndex);
			if (i>=0 && i<min){
				min = i;
				offset = s.length();
			}
		}
		if (min == -1){
			return -1;
		}
		return min;
	}

	/** 
	 * Return a SortedSet of all of the noun phrases contained in the phrase,
	 * which is the string representation of a noun or verb phrase (NP or VP).
	 * @param phrase
	 * @return
	 */
	public static SortedSet<String> extractNounPhrases(String phrase){
		String[] np = {"NP"};
		return extractPhrases(phrase, np);
	}

	/** 
	 * Return a SortedSet of all of the verb phrases contained in the phrase,
	 * which is the string representation of a noun or verb phrase (NP or VP).
	 * @param phrase
	 * @return
	 */
	public static SortedSet<String> extractVerbPhrases(String phrase){
		String[] vp = {"VP"};
		return extractPhrases(phrase, vp);
	}

	/**
	 * helper method for extractNounPhrases and extractVerbPhrases. See their function 
	 * comments for more info.
	 * @param phrase
	 * @param p: String[] containing Strings that represent the types of phrases we care about. 
	 * @return
	 */
	public static SortedSet<String> extractPhrases(String phrase, String[] p){
		SortedSet<String> returnSet = new TreeSet<String>();
		int index = chooseNextIndex(p, phrase, 0);
		int prev = closingParenthesis(phrase, index);
		while (index > -1 && prev > -1 && index < prev){
			returnSet.add(phrase.substring(index, prev));
			index = chooseNextIndex(p, phrase, prev);
			prev = closingParenthesis(phrase, index);
		}
		return returnSet;
	}

	public static boolean hasNextPhrase(String phrase, String[] p){
		int index = chooseNextIndex(p, phrase, 0);
		int prev = closingParenthesis(phrase, index);
		if (index > -1 && prev > -1 && index < prev){
			return true;
		}
		return false;
	}

	public static String extractNextPhrase(String phrase, String[] p){
		int index = chooseNextIndex(p, phrase, 0);
		int prev = closingParenthesis(phrase, index);
		String newPhrase =  (phrase.substring(index, prev));
		return newPhrase;
	}
	
	/** 
	 * Returns true if the label of the Tree "tree" corresponds to one of the 
	 * labels in the String[] labelArray. 
	 * @param subtree
	 * @param labelArray
	 * @return
	 */
	public static boolean equalsLabel(Tree subtree,String[] labelArray){
		for (String label : labelArray){
			if (subtree.label().value().equals(label)){
				return true;
			}
		}
		return false;
	}

}
