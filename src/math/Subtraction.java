package math;
import expressions.*;

public class Subtraction 
{
	public static Expression subtract(Expression E1, Expression E2)
	{
		return Addition.add(E1, Multiplication.multiply(new Rational(-1), E2));
	}
}
