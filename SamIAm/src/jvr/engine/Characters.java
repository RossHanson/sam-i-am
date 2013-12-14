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
	public static SortedMap<String, Character> characters;

	public Characters(){
		characters = new TreeMap<String, Character>(); //Character's name, all of its qualities are stored in the Character object which is the value.
	}
	/**
	 * Either adds the character to the map charactersAndSentiments if it is not
	 * already in that map, or updates the character with newly found information
	 * if it is already in the map.
	 * @param nounClause
	 */
	public static void addCharacterToMap (String nounClause){
		String characterName = Character.findName(nounClause);
		if (characters.keySet().contains(characterName)){ //Have already recorded this character
			characters.get(characterName).updateCharacter(nounClause);
		}else{
			Character personaje = new Character(nounClause);
			characters.put(personaje.getName(), personaje);
		}
	}
}
