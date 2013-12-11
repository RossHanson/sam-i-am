package jvr.parser;

public class BasicParserFunctionality {

	public static void main(String[] arg){
		String story = "((Once) (ROOT upon (a time there were four little rabbits, and( their"+
				" names )were Flopsy, Mopsy,) Cotton-tail and Peter.) They) lived with their"+
						"mother in a sand-bank underneath the root of a very big fir tree."+
				"\"Now, my dears\", said old Mrs. Rabbit one morning, \"You may go into the"+
						" fields or down the land, but don't go into Mr. McGregor's garden."+
				" your father had an accident there; and he was put in a pie by Mrs. McGregor.\"";
		int loc = closingParenthesis(story,story.indexOf("(ROOT"));
		System.out.println("The substring is : "+story.substring(1,loc));
		System.out.println("The location of (ROOT is: "+story.indexOf("(ROOT"));
	}
	/**
	 * Given a string representing a sentence or a story, returns
	 * the index of the closing parenthesis that matches the
	 * parenthesis located at index firstParens of the string.
	 * @param story
	 * @return
	 */
	public static int closingParenthesis(String story, int firstParens){
		int lenStory = story.length();
		int numParens = 1;
		int openIndex = story.indexOf("(",firstParens+1);
		int closeIndex = story.indexOf(")", firstParens+1);
		int index=firstParens;
		boolean found = false;
		int iterations = 0;
		while (!found && index<lenStory && index<lenStory && (openIndex>=0 || closeIndex>=0)){
			openIndex = story.indexOf("(", index+1);
			closeIndex = story.indexOf(")", index+1);
			iterations = iterations + 1;
//			System.out.println("openIndex: "+openIndex+"\ncloseIndex: "+closeIndex);
//			System.out.println("numParens : "+numParens+"\n");
			if (numParens > 0 ){
				if (openIndex>=0 && (openIndex<closeIndex || closeIndex<0)){
//					System.out.println("In open");
					numParens = numParens + 1;
					index = openIndex;
				}
				if (closeIndex>=0 && (closeIndex<openIndex || openIndex<0)){
//					System.out.println("In close");
					numParens = numParens - 1;
					index = closeIndex;
				}
			}else{
				found = true;
			}
		}
		return index;
	}
	
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
}
