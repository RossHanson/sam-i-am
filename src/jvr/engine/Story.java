package jvr.engine;

import java.util.*;

/** 
 * Stores any necessary information about the Story. This includes main
 * characters, Story arc, general emotional analysis.
 * @author vesha
 *
 */
/**
* Please note the meanings for the different part-of-speech tags:
* CC Coordinating conjunction
* CD Cardinal number
* DT Determiner
* EX Existential there
* FW Foreign word
* IN Preposition or subordinating conjunction
* JJ Adjective
* JJR Adjective, comparative
* JJS Adjective, superlative
* LS List item marker
* MD Modal
* NN Noun, singular or mass
* NNS Noun, plural
* NNP Proper noun, singular
* NNPS Proper noun, plural
* PDT Predeterminer
* POS Possessive ending
* PRP Personal pronoun
* PRP$ Possessive pronoun
* RB Adverb
* RBR Adverb, comparative
* RBS Adverb, superlative
* RP Particle
* SYM Symbol
* TO to
* UH Interjection
* VB Verb, base form
* VBD Verb, past tense
* VBG Verb, gerund or present participle
* VBN Verb, past participle
* VBP Verb, non third person singular present
* VBZ Verb, 3rd person singular present
* WDT Whdeterminer
* WP Whpronoun
* WP$ Possessive whpronoun
* WRB Whadverb
*/
public class Story {
	/**
	 *  A note on types: 
	 *  
	 *  The type of the Story before processing:
	 *  
	 *  String
	 *  
	 *  where the entire Story is contained in that string.
	 *  
	 *  The type of the Story after being separated into sentences:
	 *  
	 *  SortedMap<Integer, String>
	 *  
	 *  where the Integer key represents the placement of the sentence in the
	 *  Story. The value (of type String) holds all of the words in the original sentence
	 *  
	 *  The type of Story after parsing with POS tagger:
	 *  
	 *  SortedMap<Integer, SortedMap<String, Integer>>
	 *  
	 *  where the Integer key represents the placement of the sentence in the
	 *  Story. The value (of type SortedMap<String, Integer>) stores the words contained
	 *  in the sentence that are considered important along with the number of times each
	 *  of those important words appear in the sentence.
	 *  
	 *  The type of a Story after an emotional analysis has been performed:
	 *  
	 *  SortedMap<Integer, Double>
	 *  
	 *  where the Integer key represents the placement of the sentence in the
	 *  Story. The value (of type Double) represents the overall emotional score
	 *  of the sentence in that location of the original Story
	 */

	//You'll have to change the path to the tagger to wherever you store english-left3words-distsim.tagger once you download the Stanford pos tagger.
	//public static final MaxentTagger tagger = new MaxentTagger("/Users/vesha/Desktop/SamIAm/stanford-postagger-2013-11-12/models/english-left3words-distsim.tagger");
	//perhaps also have a HashMap that maps keys (POS tag) to weights (each of these constants)
	public static final double VERB_WEIGHT = 0;
	public static final double NOUN_WEIGHT= 0;
	public static final double PROPERNOUN_WEIGHT= 0;
	public static final double ADJECTIVE_WEIGHT= 0;
	public static final double MODIFIER_WEIGHT = 0;
	public static SortedMap<String, Double> POSweights = new TreeMap<String, Double>();
	//Represents the parts of speech that we actually consider to be important in our analysis.
	public static SortedSet<String> importantPOS = new TreeSet<String>(); 
	// Represents the average length of all stories (unit of measure for
	// length is a sentence). In analysis of emotion, must interpolate or 
	// reduce data to fit this value.
	public static Integer normalizedLength = 100; 
	// Values used in the calculation of the average (normalized) length
	// of a Story
	public static Integer numStoriesProcessed = 0;
	public static Integer totalSumLengths = 0;
	public static SortedMap<Integer, SortedMap<String, Integer>> int2sentences;
	
	public static SortedMap<Integer, Double> avgStoryStats;
	public static SortedMap<String, Integer> characters = new TreeMap<String, Integer>();
	public static SortedMap<Integer, SortedSet<String>> occ2characters = new TreeMap<Integer, SortedSet<String>>();


	//////////////////////////   HELPER FUNCTIONS    //////////////////////////  

	public static void main(String[] arg) {
		
		String str = ("JJ JJR JJS NN NNS NNP NNPS RB RBR RBS VB VBD VBG VBN VBP VBP VBZ");
		List<String> list = Arrays.asList(str.split(" "));
		importantPOS.addAll(list);
		System.out.println("importantPOS : "+importantPOS.toString());
		String story = "Once upon a time there were four little rabbits, and their"+
		" names were Flopsy, Mopsy, Cotton-tail and Peter. They lived with their"+
				"mother in a sand-bank underneath the root of a very big fir tree."+
		"\"Now, my dears\", said old Mrs. Rabbit one morning, \"You may go into the"+
				" fields or down the land, but don't go into Mr. McGregor's garden."+
		" your father had an accident there; and he was put in a pie by Mrs. McGregor. Accidental \"";

        SingleWordClassifier swc = SingleWordClassifier.getInstance();
        String reducedStory = story.replace(",","");
        String[] words = reducedStory.split(" ");
        for (String w: words){
            int rating = swc.checkWordStatus(w);
            System.out.println("Word: " + w + " | Rating: " + rating);
        }

		
	}

	/**
	 * Given a character's name, adds the character to the static SortedMap
	 * characters and updates the SortedMap accordingly.
	 * @param characterName
	 */
	//TODO: needs review
	public static void addCharacterToMap(String characterName){
		Integer prev_value = 1;
		if (!characters.containsKey(characterName)){ //check if the character has already been added to the map
			prev_value = characters.get(characterName) + 1;
		}
		characters.put(characterName, prev_value);
	}

	/**
	 * Given a the number of occurrences of a character's name, and the 
	 * character's name itself, this adds the Integer and character to 
	 * the static SortedMap occ2characters and updates it accordingly.
	 * @param occ
	 * @param characterName
	 */
	//TODO: needs review
	public static void addIntCharToMap(Integer occ, String characterName){
		SortedSet<String> prev_values = new TreeSet<String>();
		if (!occ2characters.containsKey(occ)){ //check if the character has already been added to the map
			prev_values = occ2characters.get(occ);
		}
		prev_values.add(characterName);
		occ2characters.put(occ, prev_values);
	}

	/** 
	 * Given a Story processed into Emotions, determines if information needs to
	 * be interpolated or reduced based on how its length compares to the class
	 * variable normalizedLength. Returns interpolated/reduced SortedMap.
	 * @param map
	 * @return
	 */
	//TODO: complete this
	public static SortedMap<Integer, Double> normalizeStory (SortedMap<Integer, Double> map){
		return null;
	}

	/**
	 * Given a Story processed into Emotions whose length (measured in number
	 * of sentences) is greater than the class variable normalizedLength, 
	 * makes necessary updates (i.e. averages) of the data in order to return
	 * a normalized Story with length normalizedLength.
	 * @param story
	 * @return
	 */
	//TODO: complete this
	public static SortedMap<Integer, Double> reduceStory (SortedMap<Integer, Double> story){
		return null;
	}

	/**
	 * Given a Story processed into Emotions whose length (measured in number
	 * of sentences) is less than the class variable normalizedLength, makes 
	 * necessary updates (i.e. interpolation) of the data in order to return
	 * a normalized Story with length normalizedLength.
	 * @param story
	 * @return
	 */
	//TODO: complete this
	public static SortedMap<Integer, Double> interpolateStory (SortedMap<Integer, Double> story){
		return null;
	}

	/** 
	 * Given a Story processed into Emotions, returns the separation of the
	 * Story into a model that more closely mimics the Story's plot
	 * progression (arc).
	 * @param story
	 * @return
	 */
	//TODO: complete this
	public static SortedMap<String, Integer[]> storyArc (SortedMap<Integer, Double> story){
		return null;
	}

	////////////////////////  END OF HELPER FUNCTIONS ////////////////////////// 


	///////////////////////////     STORY PARSER     ///////////////////////////

	//______________________________   WORDS   _______________________________

	/**
	 * Given a String representation of a word, returns the POS tag of the 
	 * Stanford parser for that word. This method also checks if the word
	 * passed in is a proper noun (a character). If it is a proper noun
	 * (its part-of-speech tag is "NNP" or "NNPS"), that word is added
	 * to the static SortedMap characters.
	 * @param word
	 * @return
	 */
	//TODO: needs review
	public static String getPOS (String word){

		String pos = null;
		if (pos.equals("NNP") || pos.equals("NNPS")){
			addCharacterToMap(word);
		}
		int index = pos.indexOf('_');
		return pos.substring(index+1);
	}

	/**
	 * Given a String representation of a word, returns a value of type Double 
	 * representing the emotional connotation of a word.
	 * @param word
	 * @return
	 */
	//TODO: complete this
	public static Double wordSentiment (String word){
		return null;
	}

	/**
	 * Given a String representation of a word, returns a value of type Double
	 * which represents the emotional connotation of a word, weighted according
	 * to it's importance determined in the class constants above.
	 * @param word
	 * @return weighted
	 */
	//TODO: needs review
	public static Double weightedWordSentiment (String word){
		String pos = getPOS(word);
		Double weighted = wordSentiment(word) * POSweights.get(pos);
		return weighted;
	}

	//___________________________   END OF WORDS   ___________________________


	//_____________________________   SENTENCES   ____________________________
	/**
	 * Given a SortedSet<Integer> called ends which represents the location
	 * of different punctuation marks in a string, returns the lowest index that
	 * is greater than or equal to 0.
	 * @param ends
	 * @return
	 */
	public static Integer findThis_end (SortedSet<Integer> ends){
		for (Integer i : ends){
			if (i>-1){
				return i;
			}
		}
		return -1;

	}

	/**
	 * Given a string representing a Story, returns a SortedMap<Integer, String>
	 * where the Integer key represents the order of the sentence in the Story
	 * (i.e. the first sentence is mapped to 1 and the fifteenth sentence in 
	 * the Story is mapped to 15). Sentences are delimited by periods (.).
	 */
	public static SortedMap<Integer, String> storyToSentences (String story){
		SortedMap<Integer, String> storySentences = new TreeMap<Integer, String>();
		SortedSet<Integer> ends = new TreeSet<Integer>();
		Integer index = 0;
		char period = '.';
		char exclam = '!';
		char question = '?';
		int prev_end = 0;
		int period_end = story.indexOf(period);
		int exclam_end = story.indexOf(exclam);
		int question_end = story.indexOf(question);
		ends.add(period_end);
		ends.add(exclam_end);
		ends.add(question_end);
		int this_end = findThis_end(ends);
		String sentence;
		while (this_end > -1 && prev_end > -1){
			sentence = story.substring(prev_end, this_end);
			storySentences.put(index, sentence);
			story = story.substring(this_end+1);
			period_end = story.indexOf(period);
			exclam_end = story.indexOf(exclam);
			question_end = story.indexOf(question);
			ends.clear();
			ends.add(period_end);
			ends.add(exclam_end);
			ends.add(question_end);
			this_end =findThis_end(ends);
			index++;
		}
		return storySentences;
	}


	/**
	 * Given a parsed string representing a processed sentence, this method
	 * adds all of the words belonging to one of the parts of speech of interest
	 * to us (all of these are stored in class variable SortedSet<String> 
	 * importantPOS) to the static SortedMap posToWords.
	 * @param sentence
	 * @param pos
	 */
	//TODO: NEED TO EVALUATE THE REST OF THE CODE AND SEE IF THIS METHOD IS REALLY NECESSARY.
	public static void findWordsFromPos (String sentence, String pos){
		StringTokenizer st = new StringTokenizer(sentence);//Separates a string into words
		int lengthPos = pos.length();
		int lengthWord;
		int index;
		String word;
		SortedSet<String> wordsMatchingPos = new TreeSet<String>();
		while (st.hasMoreTokens()){
			word = st.nextToken();
			lengthWord = word.length();
			index = lengthWord-lengthPos;
			if (pos.equals(word.substring(index))){
				wordsMatchingPos.add(word.substring(0, index));
			}
		}
		//		posToWords.put(pos, wordsMatchingPos);
	}

	public static void createSentenceMaps(){
		
	}
	
	/**
	 * Given a Story in representation number 2 (discussed on lines xxxxxxx)
	 * Returns a  SortedMap<Integer, SortedMap<String, Integer>> such that 
	 * the Integer represents the placement of the sentence which is represented
	 * by its corresponding SortedMap<String, Integer> in the Story.
	 * @param story
	 * @return
	 */
	public static SortedMap<Integer, SortedMap<String, Integer>> sentences2words2Integers(SortedMap<Integer, String> story){
		StringTokenizer st;
		String sentence;
		String important_words;
		SortedMap<Integer, SortedMap<String, Integer>> sortedMap = new TreeMap<Integer, SortedMap<String, Integer>>();
		SortedSet<String> sentenceSet = new TreeSet<String>(); //Stores all of the unique words in the sentence.
		for (Integer i : story.keySet()){
			sentence = story.get(i);
			important_words = sentenceToImportantWords(sentence);
			System.out.println("Story.get("+i+") is "+important_words);
			st = new StringTokenizer(important_words);
			while (st.hasMoreTokens()){
				sentenceSet.add(st.nextToken());
			}
			SortedMap<String, Integer> sm = new TreeMap<String, Integer>();
			for (String word : sentenceSet){
				Integer times = timesWordOccursInSentence(sentence, word);
				sm.put(word, times);
			}
			sortedMap.put(i, sm);
			sentenceSet.clear();
		}
		return sortedMap;
	}

	/**
	 * Given two inputs, String sentence and string word, this method returns 
	 * an Integer representation of the number of times that the word appears 
	 * in the sentence. 
	 * @param sentence
	 * @param word
	 * @return occurrences
	 */
	public static Integer timesWordOccursInSentence (String sentence, String word){
		StringTokenizer st = new StringTokenizer(sentence); 
		Integer occurrences = 0;
		while (st.hasMoreTokens()){
			if (st.nextToken().toLowerCase().equals(word.toLowerCase())){
				occurrences = occurrences + 1;
			}
		}
		return occurrences;
	}
	
	/**
	 * Returns a String where the String represents the sentence that was passed 
	 * in with all words with a POS tag that is not in the list of important POS
	 * tags (class variable importantPOS) removed.
	 * @param sentence
	 * @return newSentence
	 */
	//TODO: needs review
	public static String sentenceToImportantWords(String sentence){
		String new_sentence = "";
		String word;
		StringTokenizer st = new StringTokenizer(sentence);
		while (st.hasMoreTokens()){
			word = st.nextToken();
			System.out.println("getPOS(word) : "+getPOS(word));
			if (importantPOS.contains(getPOS(word))){// So this belongs to a part of speech that interests us.
				System.out.println("HEREEEEE");
				new_sentence = new_sentence + word;
			}
		}
		return new_sentence;
	}

	/**
	 * Given a SortedMap<String, Integer> that represents a sentence containing 
	 * only words with important POS tags and their corresponding values (the
	 * number of times each word occurs in that sentence), this method returns
	 * a value of type double representing the overall sentiment of the sentence.
	 * The overall sentiment is determined by multiplying the sentiment of each
	 * word by the weight given to it by the class constants above.
	 * @param sentence
	 * @return allSentiments
	 */
	//TODO: needs review
	public static Double sentenceSentiment(SortedMap<String, Integer> sentence){
		Double sentiment;
		Double allSentiments = 0.0;
		for (String word : sentence.keySet()){
			sentiment = weightedWordSentiment(word)*sentence.get(word); //accounts for the number of times this word occurs in the sentence.
			allSentiments = allSentiments+ sentiment;
		}
		return allSentiments;
	}

	//_________________________  END OF SENTENCES   _________________________


	//______________________________  STORY   _______________________________
	/**
	 * Given a Story, updates the class variable normalizedLength to include
	 * the length of this new Story.
	 * @param story
	 */
	//TODO: needs review
	public void updateAverageLength(SortedMap<Integer, String> story){
		Integer lenStory = story.size();
		numStoriesProcessed++;
		totalSumLengths = totalSumLengths + lenStory;
		normalizedLength = numStoriesProcessed/totalSumLengths;
	}

	/** 
	 * Given a Story (SortedMap that maps sentence number to the emotion
	 * attributed to that sentence), returns bounds corresponding to the 
	 * different parts of the Story's arc.
	 * @param story
	 * @return
	 */
	//TODO: complete this
	public static Double storySentiment(SortedMap<Integer, String> story){
		return 0.0;
	}

	/**
	 * Given a Story, updates the class variable avgStoryStats with the
	 * values of the sentiments of the newest Story.
	 * @param story
	 */
	//TODO: Complete this
	public void updateAverageSentiment (SortedMap<Integer, Double> story){

	}
	//___________________________  END OF STORY   ___________________________

	/////////////////////////   END OF STORY PARSER   //////////////////////////


	///////////////////////////  CHARACTER ANALYSIS  ///////////////////////////

	/** 
	 * Converts the SortedMap<String, Integer> characters to a map of type
	 * SortedMap<Integer, SortedSet<String> called occ2characters which maps
	 * the number of times a character appears in the text to a set of 
	 * characters with that number of occurrences.
	 */
	//TODO: needs review
	public static void char2occMap(){
		Integer occurrences;
		for (String character : characters.keySet()){
			occurrences = characters.get(character);
			addIntCharToMap(occurrences, character);
		}
	}

	/**
	 * Finds the main characters of a Story by the frequency of their appearance
	 * in the Story. Want at least the top two characters.
	 */
	//TODO: needs review
	public static SortedSet<String> findMainCharacters(){
		if (!occ2characters.isEmpty()){
			SortedSet<String> mainCharacters = new TreeSet<String>();
			// The last key represents the highest Integer value in the keyset.
			// The main characters are the characters who appear most frequently. 
			mainCharacters = occ2characters.get(occ2characters.lastKey()); 
			Set<Integer> otherOccs = occ2characters.keySet();
			otherOccs.remove(occ2characters.lastKey());
			return mainCharacters;
		}
		return null;
	}

	/** 
	 * Given a data structure that stores the main characters of the Story,
	 * finds the protagonists of the Story based on which of the most
	 * frequently occurring characters has the most positive treatment in the Story.
	 */
	//TODO: complete this
	public static void findProtagonists(){

	}

	/** 
	 * Given a data structure that stores the main characters of the Story,
	 * finds the antagonists of the Story based on which of the most
	 * frequently occurring characters has the most negative treatment in the Story.
	 */
	//TODO: complete this
	public static void findAntagonists(){

	}

	///////////////////////////END OF CHARACTER ANALYSIS///////////////////////////

}
