package solve;
import expressions.*;
import math.Addition;
import math.Division;
import math.Exponent;
import math.Multiplication;
import math.Subtraction;
import java.util.ArrayList;

public class Solver {

	public static String solve(String eq){
		String[] tokenized = ShuntingYard.solve(eq.split(" "));
		ArrayList<Expression> exps = new ArrayList<Expression>();
		while(exps.size() < tokenized.length){
			exps.add(makeExpression(tokenized[exps.size()]));
		}
		return makeExpression(exps).simplify().toString();
	}
	
	public static Expression makeExpression(ArrayList<Expression> exps){
		while(exps.size() > 1){
			int firstOp = findFirstOp(exps);
			try{
				exps.set(firstOp, new FullExpression(exps.get(firstOp-2), exps.get(firstOp-1), (Operator) exps.get(firstOp)));
			}catch(ArrayIndexOutOfBoundsException e){
				throw new ArrayIndexOutOfBoundsException("Need proper expressions on either side of all operators.");
			}
			exps.remove(firstOp - 1);
			exps.remove(firstOp - 2);
		}
		return exps.get(0);
	}
	
	private static int findFirstOp(ArrayList<Expression> exps){
		for(int i = 0; i < exps.size(); i++){
			if(exps.get(i) instanceof Operator){
				return i;
			}
		}
		throw new IllegalArgumentException("Missing operator.");
	}
	
	private static Expression makeExpression(String s){
		if(s.equals("+")){return Operator.SUM;}
		if(s.equals("-")){return Operator.DIFF;}
		if(s.equals("*")){return Operator.PROD;}
		if(s.equals("/")){return Operator.QUOT;}
		if(s.equals("^")){return Operator.POWER;}
		if(s.equals("e") ||s.equals("+e")){return Irrational.e;}
		if(s.equals("-e")){return new FullExpression(new Rational(-1), Irrational.e, Operator.PROD);}
		if(s.equals("p") || s.equals("+p")){return Irrational.pi;}
		if(s.equals("-p")){return new FullExpression(new Rational(-1), Irrational.pi, Operator.PROD);}
		try{
			return new Rational(Integer.parseInt(s));
		}
		catch(NumberFormatException e){
			if(s.equals("(")){
				throw new IllegalArgumentException("Missing close parenthesis.");
			}
			throw new IllegalArgumentException("'" + s + "' is not a valid input.");
		}
	}
	
	public static Expression combine(Expression E1, Expression E2, Operator op)
	{
		if(op == Operator.SUM)
		{
			return Addition.add(E1, E2);
		}
		else if(op == Operator.DIFF)
		{
			return Subtraction.subtract(E1, E2);
		}
		else if(op == Operator.PROD)
		{
			return Multiplication.multiply(E1, E2);
		}
		else if(op == Operator.QUOT)
		{
			return Division.divide(E1, E2);
		}
		else if(op == Operator.POWER)
		{
			return Exponent.power(E1, E2);
		}	
		else
		{
			throw new IllegalArgumentException("ERROR: " + op + " is not a valid operation");
		}
	}

	public static float solveFloat(String eq){
		while(eq.contains("e")){
			eq = eq.replace("e", Math.E + "");
		}
		while(eq.contains("p")){
			eq = eq.replace("p", Math.PI + "");
		}
		String[] tokenized = ShuntingYard.solve(eq.split(" "));
		ArrayList<String> exps = new ArrayList<String>();
		while(exps.size() < tokenized.length){
			exps.add(tokenized[exps.size()]);
		}
		return makeFloat(exps);
	}
	
	private static float makeFloat(ArrayList<String> exps){
		while(exps.size() > 1){
			int firstOp = findFirstOpFloat(exps);
			try{
				exps.set(firstOp, combine(Float.parseFloat(exps.get(firstOp - 2)), Float.parseFloat(exps.get(firstOp - 1)), exps.get(firstOp)));
			}catch(ArrayIndexOutOfBoundsException e){
				throw new IllegalArgumentException("Can't make float from input");
			}
			exps.remove(firstOp - 1);
			exps.remove(firstOp - 2);
		}
		return Float.parseFloat(exps.get(0));
	}
	
	private static int findFirstOpFloat(ArrayList<String> exps){
		for(int i = 0; i < exps.size(); i++){
			if(exps.get(i).equals("+") || exps.get(i).equals("-") || exps.get(i).equals("*") || exps.get(i).equals("/") || exps.get(i).equals("^")){
				return i;
			}
		}
		return -1;
	}
	
	private static String combine(float a, float b, String op){
		if(op.equals("+")) {
			float res = a+b; 
		    return res + "";
		}
		if(op.equals("-")) return a-b + "";
		if(op.equals("*")) return a*b + "";
		if(op.equals("/")) return a/b + "";
		return (float)(Math.pow(a,b)) + "";
	}
	
}


