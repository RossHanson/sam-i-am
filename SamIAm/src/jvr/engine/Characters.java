/**
 * 
 */
package jvr.engine;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author vesha
 *
 */
public class Characters {
	public static SortedMap<String, Character> characters= new TreeMap<String, Character>();
	
	public static void main(String[] arg){
		String test = "(ROOT (S (S (NP (DT The) (NN wizard)) (VP (VBD walked) (SBAR (S (SBAR (ADVP (RB outside) (. .)) (S (NP (PRP He)) (VP (VBD liked) (NP (DT the) (NN snow))) (. .))) (NP (DT The) (NN wizard)) (VP (VBD lied) (PP (TO to) (NP (DT the) (JJ little) (NN girl))))))) (. .)) (NP (DT The) (NN wolf)) (VP (VBD ate) (NP (DT the) (JJ fat) (NN girl))) (. .)))";
		Characters chars = new Characters();
		chars.addCharacterToMap(test);
		System.out.println("Characters.characters: \n"+Characters.characters.toString());
	}

	public Characters(){
//		characters = new TreeMap<String, Character>(); //Character's name, all of its qualities are stored in the Character object which is the value.
	}
	
	/**
	 * Either adds the character to the map charactersAndSentiments if it is not
	 * already in that map, or updates the character with newly found information
	 * if it is already in the map.
	 * @param nounClause
	 */
	public void addCharacterToMap (String nounClause){
		String characterName = Character.findName(nounClause);
		if (characters.keySet().contains(characterName)){ //Have already recorded this character
			characters.get(characterName).updateCharacter(nounClause);
		}else{
			Character personaje = new Character(nounClause, true);
			characters.put(personaje.getName(), personaje);
		}
	}
	
	public void dealWithIt (){
		
	}
}
