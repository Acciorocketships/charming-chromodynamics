import java.util.Iterator;
import java.io.IOException;


public class TokenScanner implements Iterator<String> {

	
	java.io.Reader file;
	char nextchar;
	
	
  public TokenScanner(java.io.Reader in) throws IOException {
	  if (in == null) {
		  throw new IllegalArgumentException();
	  }
	  file = in;
	  nextchar = (char) file.read();
  }

  
  public static boolean isWordCharacter(int c) {
	  return Character.isLetterOrDigit(c);
  }

  
  public static boolean isWord(String s) {
	  if (s == null || s.equals("")) {
		  return false;
	  }
	  char[] chars = s.toCharArray();
	  for (int i = 0; i < chars.length; i++) {
		  if (!isWordCharacter(chars[i])) {
			  return false;
		  }
	  }
	  return true;
  }

  
  public boolean hasNext() {
	  return nextchar != -1 && nextchar != 65535;
  }


  public String next() {
	  String word = "";
	  boolean isWord = true;
	  while (true) {
		  boolean isWordChar = isWordCharacter(nextchar);
		  if (word.isEmpty()) {
			  isWord = isWordChar;
		  }
		  if (isWordChar && isWord) {
			  word += nextchar;
		  }
		  else if (!isWordChar && !isWord) {
			  if (!hasNext()){
				  if (word.isEmpty()) {
					  throw new java.util.NoSuchElementException();
				  }
				  return word;
			  }
			  word += nextchar;
		  }
		  else {
			  return word;
		  }
		  try {
			  nextchar = (char) file.read();
		  } catch (IOException e) {
			  throw new java.util.NoSuchElementException();
		  }
	  }
  }


  public void remove() {
    throw new UnsupportedOperationException();
  }
}
