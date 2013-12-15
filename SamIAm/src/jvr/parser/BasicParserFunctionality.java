package jvr.parser;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class BasicParserFunctionality {

	public static void main(String[] arg){
		String story = "((Once) (ROOT upon (a time there were four little rabbits, and( their"+
				" names )were Flopsy, Mopsy,) Cotton-tail and Peter.) They) lived with their"+
				"mother in a sand-bank underneath the root of a very big fir tree."+
				"\"Now, my dears\", said old Mrs. Rabbit one morning, \"You may go into the"+
				" fields or down the land, but don't go into Mr. McGregor's garden."+
				" your father had an accident there; and he was put in a pie by Mrs. McGregor.\"";
		int loc = closingParenthesis(story,story.indexOf("(ROOT"));
		String phrase = "(ROOT (S (S (NP (DT The) (NN wizard)) (VP (VBD walked) (SBAR (S (SBAR (ADVP (RB outside) (. .)) (S (NP (PRP He)) (VP (VBD liked) (NP (DT the) (NN snow))) (. .))) (NP (DT The) (NN wizard)) (VP (VBD lied) (PP (TO to) (NP (DT the) (JJ little) (NN girl))))))) (. .)) (NP (DT The) (NN wolf)) (VP (VBD ate) (NP (DT the) (JJ fat) (NN girl))) (. .)))";
		System.out.println(phrase);
		SortedSet<String> nps = extractNounPhrases(phrase);
		System.out.println(nps.toString());
		SortedSet<String> vps = extractVerbPhrases(phrase);
		System.out.println(vps.toString());
		//		System.out.println("The substring is : "+story.substring(1,loc));
		//		System.out.println("The location of (ROOT is: "+story.indexOf("(ROOT"));
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
		return min+offset;
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

	public static SortedSet<String> extractPhrases(String phrase, String[] p){
		SortedSet<String> returnSet = new TreeSet<String>();
		int index = chooseNextIndex(p, phrase, 0);
		int prev = closingParenthesis(phrase, index);
		while (index > -1 && prev > -1 && index < prev){
//			System.out.println("index: "+index+" prev: "+prev+".\n");
//			System.out.println(phrase.substring(index, prev));
			returnSet.add(phrase.substring(index, prev-1));

			index = chooseNextIndex(p, phrase, prev);
			prev = closingParenthesis(phrase, index);
		}
		return returnSet;
	}

}
