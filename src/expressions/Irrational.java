package expressions;

public enum Irrational implements Expression
{
	e ('e'),
	pi ('p');
	
	public final char val;
	
	private Irrational(char val)
	{
		this.val = val;
	}
	
	public boolean equals(Expression E)
	{
		if(E instanceof Irrational && E == this){
			return true;
		}
		return false;
	}
	
	public Expression simplify(){
		return this;
	}
	
	public String toString(){
		return ""+val;
	}
	
}
