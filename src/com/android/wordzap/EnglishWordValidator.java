/**
 * @author Kowshik Prakasam
 * 
 * Validates english words in Word Zap
 * 
 */
package com.android.wordzap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

public class EnglishWordValidator implements WordValidator {

	// Stores all word lists - grouped by word length (key)
	private Map<Integer, Set<String>> wordListsHash;

	/*
	 * Constructs the validator from a list of words. Restricts caching of words
	 * to those that can be formed from chars specified in charSet. All other
	 * words in the word list stream is ignored.
	 * 
	 * Parameter 1 : Path to file containing list of words
	 * 
	 * Parameter 2 : Array of characters, which are the superset of characters
	 * in each word
	 * 
	 * Throws IOException : If I/O errors happen when reading the list of words
	 */
	public EnglishWordValidator(String wordListFile, final char[] charSet)
			throws IOException {
		this(new FileReader(wordListFile), charSet);
	}

	/*
	 * Constructs the validator from a list of words. Restricts caching of words
	 * to those that can be formed from chars specified in charSet. All other
	 * words in the word list stream is ignored.
	 * 
	 * Parameter 1 : Handle to file containing list of words
	 * 
	 * Parameter 2 : Array of characters, which are the superset of characters
	 * in each word
	 * 
	 * Throws IOException : If I/O errors happen when reading the list of words
	 */
	public EnglishWordValidator(final InputStream wordListHandle,
			final char[] charSet) throws IOException {
		this(new InputStreamReader(wordListHandle), charSet);
	}

	/*
	 * Constructs the validator from a list of words. Restricts caching of words
	 * to those that can be formed from chars specified in charSet. All other
	 * words in the word list stream is ignored.
	 * 
	 * Parameter 1 : Handle to file containing list of words
	 * 
	 * Parameter 2 : Array of characters, which are the superset of characters
	 * in each word
	 * 
	 * Throws IOException : If I/O errors happen when reading the list of words
	 */
	public EnglishWordValidator(final Reader wordListHandle,
			final char[] charSet) throws IOException {
		List<Character> charSetCollection = new Vector<Character>();
		for (char alphabet : charSet) {
			charSetCollection.add(Character.toUpperCase(alphabet));
		}
		this.wordListsHash = this.cacheWords(wordListHandle, charSetCollection);

	}

	/*
	 * Returns a hash with keys as word lengths and values as sorted lists of
	 * words of a particular length. Words are hashed only if characters in a
	 * word form a subset of characters in charSet
	 * 
	 * Parameter 1 : Handle to file containing list of words
	 * 
	 * Parameter 2 : List of characters, which are the superset of characters in
	 * each word
	 * 
	 * Throws IOException : If I/O errors happen when reading the list of words
	 */
	private Map<Integer, Set<String>> cacheWords(final Reader wordListHandle,
			final List<Character> charSet) throws IOException {
		BufferedReader buffRdr = new BufferedReader(wordListHandle);
		Map<Integer, Set<String>> wordListsHash = new HashMap<Integer, Set<String>>();

		String word = null;

		while ((word = buffRdr.readLine()) != null) {
			word = word.toUpperCase();
			if (this.isWordSubset(charSet, word)) {
				int wordLength = word.length();
				if (wordLength != 0) {
					Set<String> wordListSet = wordListsHash.get(wordLength);
					if (wordListSet == null) {
						wordListSet = new TreeSet<String>();
						wordListsHash.put(wordLength, wordListSet);
					}
					wordListSet.add(word);
				}
			}
		}
		//System.out.println(wordListsHash);
		return wordListsHash;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.wordzap.WordValidator#isWordValid(java.lang.String)
	 * 
	 * Returns true if word is valid, false otherwise
	 */
	public boolean isWordValid(String word) {
		word = word.toUpperCase();
		int wordLength = word.length();
		Set<String> wordListSet = wordListsHash.get(wordLength);
		if (wordListSet != null && wordListSet.contains(word)) {
			return true;
		}
		return false;
	}

	/*
	 * Returns true of characters in the word are a subset of characters in
	 * charSet. Returns false otherwise.
	 * 
	 * Parameter 1 : List of superset characters
	 * 
	 * Parameter 2 : Word that needs to be checked
	 */
	private boolean isWordSubset(final List<Character> charSet, String word) {
		int index = 0;
		List<Character> charSetTmp = new LinkedList<Character>(charSet);
		//System.out.println("isWordSubset : charSet - " + charSet + ", word - "+ word);
				
		for (char alphabet : word.toCharArray()) {
			if (charSet.isEmpty() || !charSet.contains(alphabet)) {
				//System.out.println("false");
				return false;
			}
			charSetTmp.remove(new Character(alphabet));
			word = word.substring(1, word.length());
			index++;
		}
		//System.out.println("true");
		return true;
	}

//	public static void main(String[] args) throws IOException {
//		StringReader rdr = new StringReader("test\na\nb\nc\ndef");
//		char[] charSet = { 't', 'e', 's', 't', 'a' };
//		WordValidator aValidator = new EnglishWordValidator(rdr, charSet);
//		System.out.println(aValidator.isWordValid("aa"));
//	}
}
