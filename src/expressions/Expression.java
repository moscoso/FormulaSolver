package expressions;

public interface Expression {
	
	abstract boolean equals(Expression E);
	abstract Expression simplify();
	abstract String toString();
	
}
