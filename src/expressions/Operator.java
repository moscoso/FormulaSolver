package expressions;

public enum Operator implements Expression{

	SUM (" + "),
	DIFF (" - "),
	PROD ("*"),
	QUOT ("/"),
	POWER ("^");
	
	public final String op;
	
	private Operator(String s){
		op = s;
	}

	public boolean equals(Expression E){
		if(E == this ){
			return true;
		}
		return false;
	}
	
	public Expression simplify(){
		return this;
	}
	
	public int getVal(){
		if(this == Operator.POWER){
			return 1;
		}
		if(this == Operator.QUOT){
			return 2;
		}
		if(this == Operator.PROD){
			return 2;
		}
		if(this == Operator.DIFF){
			return 3;
		}
		else/*(this == Operator.SUM)*/{
			return 3;
		}
	}
	 
	public String toString(){
		return op;
	}
	
}