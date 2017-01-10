package userInterface;

public class Main {
	public static final char[] allowableChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'e', '-', '+', '/', '*', '^', '(', ')', ':', 'p', 'i', getE(), getPi()};
	public static final String[] allowableStrings = {"pi", "ans", "sqrt", "rt"};
	
	public static void main(String[] args){
		new Window();		
	}
	
	/**
	 * @return the unicode character for the natural number e.
	 */
	public static char getE(){
		return '\u212f';
	}
	/**
	 * @return the unicode character for pi.
	 */
	public static char getPi(){
		return '\u03C0';
	}
	
	/**
	 * @return the unicode character for - as string.
	 */
	public static char getHypen(){
		return '\u2013';
	}
}
