package math;
import expressions.*;

public class Exponent 
{	
	
	public static Expression power(Expression E1, Expression E2)
	{
		if(E1 instanceof Irrational || E2 instanceof Irrational){
			if(E2 instanceof Rational){
				Rational r = (Rational)E2;
				if(r.val == 0)
					return new Rational(1);
				if(r.val == 1)
					return E1;
				if(r.val < 0)
					return new FullExpression(new Rational(1), power(E1, new Rational(-1*r.val)), Operator.QUOT);
			}
			return new FullExpression(E1, E2, Operator.POWER);
		}
		if(E2 instanceof Rational){
			if(E1 instanceof Rational){
				return power((Rational)E1, (Rational)E2);
			}
			else/*(E1 instanceof FullExpression)*/{
				return power((FullExpression)E1, (Rational)E2);
			}
		}
		else if(((FullExpression)E2).op == Operator.QUOT){
			if(E1 instanceof Rational){
				return power((Rational)E1, (FullExpression)E2);
			}
			else/*(E1 instanceof FullExpression)*/{
				return power((FullExpression)E1, (FullExpression)E2);
			}
		}
		return new FullExpression(E1, E2, Operator.POWER);
	}
	
	private static Expression power(Rational r1, Rational r2){
		
		if(r2.val == 1)
			return r1;		
		return reduceRoot(r1.val, r2.val, 1);
	}
	
	private static Expression power(Rational r1, FullExpression frac){
		if(r1.val == 0) return new Rational(0);
		if(r1.val == 1) return new Rational(1);
		if(frac.E1 instanceof Rational){
			if(frac.E2 instanceof Rational){
				return reduceRoot(r1.val, ((Rational)frac.E1).val, ((Rational)frac.E2).val);
			}
			return new FullExpression(new Rational(power(r1.val, ((Rational)frac.E1).val)), new FullExpression(new Rational(1), frac.E2, Operator.QUOT), Operator.POWER);
		}
		return new FullExpression(r1, frac, Operator.POWER);
	}
	
	private static Expression power(FullExpression base, Rational r){
		if(r.val < 1){
			return new FullExpression(new Rational(1), power(base, new Rational(-r.val)), Operator.QUOT);
		}
		Expression temp = new Rational(1);
		for(int i = 0; i < r.val; i++){
			temp = Multiplication.multiply(temp, base);
		}
		return temp;
	}
	
	private static Expression power(FullExpression base, FullExpression frac){
		if(!(frac.E1 instanceof Rational)) return new FullExpression(base, frac, Operator.POWER);
		Expression res = power(base, (Rational)frac.E1);
		return new FullExpression(res, new FullExpression(new Rational(1), frac.E2, Operator.QUOT), Operator.POWER);
	}
		
///////////////////////////////////////////HELPER METHODS/////////////////////////////////////////////////////////////////
		
	private static Expression reduceRoot(int base, int power, int root){
		if(power > 30){throw new IllegalArgumentException(" This program does not support numbers greater than 2^30");}
		if(power < 0){
			return Division.divide(new Rational(1), reduceRoot(base, -power, root));
		}
		base = power(base, power);
		if(root == 1)
			return new Rational(base);
		return root(base, root);
	}
	
	private static int power(int base, int power){
		
		int k = 1;
		for(int i = 0; i < power; i++){
			k *= base;
		}
		return k;
	}
	
	public static Expression root(int base, int root){
		if(base == 0 || base == 1 || (base == -1 && root%2 == 1)) return new Rational(base);		
		if(base < 0){
			if(root % 2 == 1){
				return Multiplication.multiply(new Rational(-1), root(-base, root));
			}
			throw new IllegalArgumentException("Can't take even root of negative number");
		}
		for(int i = base / 2; i > 1; i--){
			int k = 1;
			for(int j = 0; j < root; j++){
				k *= i;
			}
			if(base % k == 0){
				if(base == k){
					return new Rational(i);
				}
				return new FullExpression(new Rational(i), new FullExpression(new Rational(base / k), new FullExpression(new Rational(1), new Rational(root), Operator.QUOT), Operator.POWER), Operator.PROD);
			}
		}
		return new FullExpression(new Rational(base), new FullExpression(new Rational(1), new Rational(root), Operator.QUOT), Operator.POWER);
	}
}