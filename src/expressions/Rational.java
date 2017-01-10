package expressions;

public class Rational implements Expression{

	public final int val;
	
	public Rational(int val){
		//2^30 = 1073741824
		if(val > 1073741824)
			throw new IllegalArgumentException("Program cannot handle numbers larger than 1073741824");
		this.val = val;
	}
	
	public boolean equals(Expression E){
		if(!(E instanceof Rational) || ((Rational)E).val != this.val){
			return false;
		}
		return true;
	}
	
	public Expression simplify(){
		return this;
	}
	
	public String toString(){
		return ""+val;
	}
	
}
