package math;
import java.util.ArrayList;

import solve.Solver;

import expressions.*;

public class Division 
{
	//RATIONAL / RATIONAL
	public static Expression divide(Rational r1, Rational r2)
	{
		//Divide by zero
		if(r2.val == 0)
			throw new IllegalArgumentException("Divide by zero error: " + r1 + "/" + r2);
		//Quotient is integer
		else if(r1.val % r2.val == 0)
		{
			int quotient = r1.val / r2.val;
			return new Rational(quotient);
		}
		//Quotient is fraction
		else
			return reduceFraction(r1.val, r2.val);
	}
	
	//IRRATIONAL / IRRATIONAL
	public static Expression divide(Irrational i1, Irrational i2)
	{
		if(i1.equals(i2))
			return new Rational(1);
		else
			return new FullExpression(i1, i2, Operator.QUOT);
	}
	
	//IRRATIONAL / RATIONAL
	public static Expression divide(Irrational i, Rational r)
	{
		//Divide by zero
		if(r.val == 0)
			throw new IllegalArgumentException("Divide by zero error: " + i + "/" + r);
		//Divide by 1
		else if(r.val == 1)
			return i;
		//Divide by -1
		else if(r.val == -1)
			return Multiplication.multiply(i, new Rational(-1));
		else
			return new FullExpression(i, r, Operator.QUOT);
	}
	
	//RATIONAL / IRRATIONAL
	public static Expression divide(Rational r, Irrational i)
	{
		//Numerator is zero
		if(r.val == 0)
			return r;
		else
			return new FullExpression(r, i, Operator.QUOT);
	}
	
	//FULLEXPRESSION / RATIONAL
	public static Expression divide(FullExpression f, Rational r)
	{
		//Divide by zero
		if(r.val == 0)
			throw new IllegalArgumentException("Divide by zero error: " + f + "/" + r);
		//Divide by 1
		else if(r.val == 1)
			return f;
		//Divide by -1
		else if(r.val == -1)
			return Multiplication.multiply(f, new Rational(-1));
		//Full Expression: Addition
		else if(f.op.equals(Operator.SUM))
		{
			return Addition.add(divide(f.E1, r), divide(f.E2, r));
		}
		//Full Expression: Subtraction
		else if(f.op.equals(Operator.DIFF))
		{
			return Subtraction.subtract(divide(f.E1, r), divide(f.E2, r));
		}
		//Full Expression: Division
		else if(f.op.equals(Operator.QUOT))
		{
			if(divide(f.E1, r) instanceof Rational || divide(f.E1, r) instanceof Irrational)
			{
				return divide(divide(f.E1, r), f.E2);
			}
			else if(f.E1 instanceof Rational)
			{
				FullExpression exp = (FullExpression)divide(f.E1, r);
				return new FullExpression(exp.E1, Multiplication.multiply(exp.E2, f.E2), Operator.QUOT);
			}
			else
				return new FullExpression(f.E1, Multiplication.multiply(f.E2, r),Operator.QUOT);
		}
		//Full Expression: Multiplication
		else if(f.op.equals(Operator.PROD))
		{
			if(divide(f.E1, r) instanceof Rational)
			{
				return Multiplication.multiply(divide(f.E1, r), f.E2);
			}
			else if(f.E1 instanceof Rational)
			{
				FullExpression exp =(FullExpression)divide(f.E1,r);
				return new FullExpression(Multiplication.multiply(exp.E1, f.E2), exp.E2, Operator.QUOT);
			}
			else
				return new FullExpression(f, r, Operator.QUOT);
		}
		//Full Expression: Power
		else if(f.op.equals(Operator.POWER))
		{
			if(f.E1.equals(r))
				return Exponent.power(f.E1, Subtraction.subtract(f.E2, new Rational(1)));
			else
				return new FullExpression(f, r, Operator.QUOT);
		}
		else
			throw new IllegalArgumentException("FullExpression/Rational error " + f + "/" + r);
	}
	
	//FULLEXPRESSION / IRRATIONAL
	public static Expression divide(FullExpression f, Irrational i)
	{
		//Full Expression: Addition
		if(f.op.equals(Operator.SUM))
		{
			return Addition.add(divide(f.E1, i), divide(f.E2, i));
		}
		//Full Expression: Subtraction
		else if(f.op.equals(Operator.DIFF))
		{
			return Subtraction.subtract(divide(f.E1, i), divide(f.E2, i));
		}
		//Full Expression: Division
		else if(f.op.equals(Operator.QUOT))
		{
			if(f.E1.equals(i))
				return divide(new Rational(1), f.E2);
			else
				return new FullExpression(f.E1, Multiplication.multiply(f.E2, i), Operator.QUOT);
		}
		//Full Expression: Multiplication
		else if(f.op.equals(Operator.PROD))
		{
			if(f.E1.equals(i) )
				return f.E2;
			else if(f.E2.equals(i))
				return f.E1;
			else
				return new FullExpression(f, i, Operator.QUOT);
		}
		//Full Expression: Power
		else if(f.op.equals(Operator.POWER))
		{
			if(f.E1.equals(i))
				return Exponent.power(f.E1, Subtraction.subtract(f.E2, new Rational(1)));
			else
				return new FullExpression(f, i, Operator.QUOT);
		}
		else
			throw new IllegalArgumentException("FullExpression/Irrational error " + f + "/" + i);
	}
	
	//RATIONAL / FULLEXPRESSION
	public static Expression divide(Rational r, FullExpression f)
	{
		//Full Expression: Addition or Subtraction
		if(f.op.equals(Operator.SUM) || f.op.equals(Operator.DIFF))
		{
			return new FullExpression(r, f, Operator.QUOT);					
		}		
		//Full Expression: Division
		else if(f.op.equals(Operator.QUOT))
		{
			return divide(Multiplication.multiply(r, f.E2), f.E1);
		}
		//Full Expression: Multiplication
		else if(f.op.equals(Operator.PROD))
		{
			if(f.E1 instanceof Rational)
			{
				if(r.equals(f.E1))
					return new FullExpression(new Rational(1), f.E2, Operator.QUOT);
				else
					return divide(divide(r, f.E1), f.E2);
			}
			else
				return new FullExpression(r, f, Operator.QUOT);
		}
		//Full Expression: Power
		else if(f.op.equals(Operator.POWER))
		{
			if(f.E1.equals(r))
				return Exponent.power(f.E1, Multiplication.multiply(Subtraction.subtract(f.E2, new Rational(1)), new Rational(-1)));
			else
				return new FullExpression(r, f, Operator.QUOT);
		}
		else
			throw new IllegalArgumentException("Rational / FullExpression error " + r + "/" + f);
	}
	
	//IRRATIONAL / FULLEXPRESSION
	public static Expression divide(Irrational i, FullExpression f)
	{
		//Full Expression: Addition or Subtraction
		if(f.op.equals(Operator.SUM) || f.op.equals(Operator.DIFF))
		{
			return new FullExpression(i, f, Operator.QUOT);					
		}		
		//Full Expression: Division
		else if(f.op.equals(Operator.QUOT))
		{
			return divide(Multiplication.multiply(i, f.E2), f.E1);
		}
		//Full Expression: Multiplication
		else if(f.op.equals(Operator.PROD))
		{
			if(i.equals(f.E1))
				return new FullExpression(new Rational(1), f.E2, Operator.QUOT);
			else if(i.equals(f.E2))
				return new FullExpression(new Rational(1), f.E1, Operator.QUOT);
			else
				return new FullExpression(i, f, Operator.QUOT);
		}
		//Full Expression: Power
		else if(f.op.equals(Operator.POWER))
		{
			if(f.E1.equals(i))
				return Exponent.power(f.E1, Multiplication.multiply(Subtraction.subtract(f.E2, new Rational(1)), new Rational(-1)));
			else
				return new FullExpression(i, f, Operator.QUOT);
		}
		else
			throw new IllegalArgumentException("Irrational / FullExpression error " + i + "/" + f);
	}

	//FULLEXPRESSION / FULLEXPRESSION
	public static Expression divide(FullExpression numerator, FullExpression denominator)
	{
		//Numerator = Denominator
		if(numerator.equals(denominator))
			return new Rational(1);
		//Numerator = -Denominator
		if(numerator.equals(Multiplication.multiply(new Rational(-1), denominator)))
			{System.out.println(true);
			return new Rational(-1);}		
		//Numerator: Addition
		if(numerator.op.equals(Operator.SUM))
		{						
			return new FullExpression(divide(numerator.E1, denominator), divide(numerator.E2, denominator), Operator.SUM);
		}
		//Numerator: Multiplication
		else if(numerator.op.equals(Operator.PROD))
		{
			//Denominator: Multiplication
			if(denominator.op.equals(Operator.PROD))
			{
				ArrayList<Expression> numeratorAll = FullExpression.getAllRelevant(numerator);
				ArrayList<Expression> denominatorAll = FullExpression.getAllRelevant(denominator);
				for(int i = 0; i < numeratorAll.size(); i++)
				{
					for(int j = 0; j < denominatorAll.size(); j++)
					{
						if(numeratorAll.get(i).equals(denominatorAll.get(j)))
						{
							System.out.println(true);
							System.out.println(numeratorAll.get(i) + ""+ denominatorAll.get(j));
							numeratorAll.remove(i);
							denominatorAll.remove(j);
						}
					}
				}
				ArrayList<Expression> resultNumerator = new ArrayList<Expression>();
				ArrayList<Expression> resultDenominator = new ArrayList<Expression>();
				resultNumerator.add(numeratorAll.get(0));
				resultDenominator.add(denominatorAll.get(0));
				for(int i = 1; i < numeratorAll.size(); i++)
				{
					resultNumerator.add(Operator.PROD);
					resultNumerator.add(numeratorAll.get(i));					
				}
				for(int i = 1; i < denominatorAll.size(); i++)
				{					
					resultDenominator.add(Operator.PROD);
					resultDenominator.add(denominatorAll.get(i));
				}
				return divide(Solver.makeExpression(resultNumerator), Solver.makeExpression(resultDenominator));
			}
			//Denominator: Division
			else if(denominator.op.equals(Operator.QUOT))
			{
				return divide(Multiplication.multiply(numerator, denominator.E2), denominator.E1);
			}
			//Denominator: Power
			else if(denominator.op.equals(Operator.POWER))
			{
				if(numerator.equals(denominator.E1))
					return divide(new Rational(1), Exponent.power(denominator.E1, Subtraction.subtract(denominator.E2, new Rational(1))));
				else if(numerator.E1.equals(denominator.E1))
				{
					return divide(numerator.E2, Exponent.power(denominator.E1, Subtraction.subtract(denominator.E2, new Rational(1))));
				}
				else if(numerator.E2.equals(denominator.E1))
				{
					return divide(numerator.E1, Exponent.power(denominator.E1, Subtraction.subtract(denominator.E2, new Rational(1))));
				}
				else
					return new FullExpression(numerator, denominator, Operator.QUOT);
			}
			else
				return new FullExpression(numerator, denominator, Operator.QUOT);
		}
		//Numerator: Division
		else if(numerator.op.equals(Operator.QUOT))
		{
			//Denominator: Addition
			if(denominator.op.equals(Operator.SUM))
			{
				return divide(numerator.E1, Multiplication.multiply(numerator.E2, denominator));
			}
			//Denominator: Multiplication
			else if(denominator.op.equals(Operator.PROD))
				return divide(numerator.E1, Multiplication.multiply(numerator.E2, denominator));
			//Denominator: Division
			else if(denominator.op.equals(Operator.QUOT))
				return divide(Multiplication.multiply(numerator.E1, denominator.E2), Multiplication.multiply(numerator.E2, denominator.E1));
			//Denominator: Power
			else if(denominator.op.equals(Operator.POWER))
			{
				if(numerator.E1.equals(denominator.E1))
					return new FullExpression(new Rational(1), Exponent.power(denominator.E1, Subtraction.subtract(denominator.E2, new Rational(1))), Operator.QUOT);
				else 
					return new FullExpression(numerator.E1, Multiplication.multiply(numerator.E2, denominator), Operator.QUOT);
			}
			else
				throw new IllegalArgumentException("This case should never happen");
		}
		//Numerator: Power
		else if(numerator.op.equals(Operator.POWER))
		{
			//Denominator: Multiply
			if(denominator.op.equals(Operator.PROD))
			{
				if(numerator.E1.equals(denominator.E1))
					return new FullExpression(numerator.E2, Exponent.power(denominator.E1, Subtraction.subtract(denominator.E2, new Rational(1))),Operator.QUOT);
				else if(numerator.E2.equals(denominator.E1))
					return new FullExpression(numerator.E1, Exponent.power(denominator.E1, Subtraction.subtract(denominator.E2, new Rational(1))),Operator.QUOT);
				else
					return new FullExpression(numerator, denominator, Operator.QUOT);
			}
			//Denominator: Divide
			else if(denominator.op.equals(Operator.QUOT))
				return divide(Multiplication.multiply(numerator, denominator.E1), denominator.E2);
			//Denominator: Power
			else if(denominator.op.equals(Operator.POWER))
			{
				if(numerator.E1.equals(denominator.E1))
					return Exponent.power(numerator.E1, Subtraction.subtract(numerator.E2, denominator.E2));
				else
					return new FullExpression(numerator, denominator, Operator.QUOT);
			}
			else
				return new FullExpression(numerator, denominator, Operator.QUOT);
		}
		//Denominator: Addition
		if(denominator.op.equals(Operator.SUM))
		{
			return new FullExpression(numerator, denominator, Operator.QUOT);
		}
		else
		{
			throw new IllegalArgumentException("This case should not happen");
		}
	}
	
	//EXPRESSION / EXPRESSION
	public static Expression divide(Expression E1, Expression E2)
	{
		//rational / rational
		if(E1 instanceof Rational && E2 instanceof Rational)
		{
			Rational r1 = (Rational)E1;
			Rational r2 = (Rational)E2;
			return divide(r1, r2);
		}
		//irrational / irrational
		else if(E1 instanceof Irrational && E2 instanceof Irrational)
		{
			Irrational i1 = (Irrational) E1;
			Irrational i2 = (Irrational) E2;
			return divide(i1, i2);
		}
		//irrational / rational
		else if(E1 instanceof Irrational && E2 instanceof Rational)
		{
			Irrational i = (Irrational)E1;
			Rational r = (Rational)E2;
			return divide(i, r);
		}
		//rational / irrational
		else if(E1 instanceof Rational && E2 instanceof Irrational)
		{
			Rational r = (Rational)E1;
			Irrational i = (Irrational)E2;
			return divide(r, i);
		}
		//full expression / rational
		else if(E1 instanceof FullExpression && E2 instanceof Rational)
		{
			FullExpression f = (FullExpression)E1;
			Rational r = (Rational)E2;
			return divide(f, r);
		}
		//full expression / irrational
		else if(E1 instanceof FullExpression && E2 instanceof Irrational)
		{
			FullExpression f = (FullExpression)E1;
			Irrational i = (Irrational)E2;
			return divide(f, i);
		}
		//rational / full expression
		else if(E2 instanceof FullExpression && E1 instanceof Rational)
		{
			Rational r = (Rational)E1;
			FullExpression f = (FullExpression)E2;
			return divide(r, f);
		}
		//irrational /  full expression
		else if(E2 instanceof FullExpression && E1 instanceof Irrational)
		{
			FullExpression f = (FullExpression)E2;
			Irrational i = (Irrational)E1;
			return divide(i, f);
		}
		//full expression / full expression
		else if(E1 instanceof FullExpression && E2 instanceof FullExpression)
		{
			FullExpression f1 = (FullExpression)E1;
			FullExpression f2 = (FullExpression)E2;
			return divide(f1, f2);
		}
		else
		{
			throw new IllegalArgumentException("Division Error:" + E1.toString() + Operator.QUOT + E2.toString());
		}
	}
	
/////////////////////////////////////////////HELPER METHODS///////////////////////////////////////////////////////////////
	
	//Reduces rational / rational
	private static Expression reduceFraction(int numerator, int denominator)
	{
		if(denominator == 0)
		{
			throw new IllegalArgumentException("Divide by zero error: " + numerator + "/" + denominator);
		}
		int gcd;
		if(numerator >= denominator)
		{
			gcd = gcd(numerator, denominator);
		}
		else
		{
			gcd = gcd(denominator, numerator);
		}
		numerator /= gcd;
		denominator /= gcd;
		if(denominator == 1)
		{
			//Quotient is integer
			return new Rational(numerator);
		}		
		//Negative denominator
		if(denominator < 0)			
		{
			denominator *= -1;
			numerator *= -1;
		}
		//Quotient is fraction
		return new FullExpression(new Rational(numerator), new Rational(denominator), Operator.QUOT);
	}
	
	//Finds greatest common divisor
	private static int gcd(int a, int b)
	{
		int remainder = a % b;
		if(remainder == 0)
		{
			return b;
		}
		return gcd(b, remainder);
	}
}
