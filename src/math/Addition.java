package math;
import expressions.*;
import solve.*;
import java.util.*;

public class Addition 
{
	
	public static Expression add(Expression E1, Expression E2)
	{
		//rational + rational
		if(E1 instanceof Rational && E2 instanceof Rational)
		{
			Rational r1 = (Rational)E1;
			Rational r2 = (Rational)E2;
			return add(r1, r2);
		}	
		//irrational + irrational
		else if(E1 instanceof Irrational && E2 instanceof Irrational)
		{
			Irrational i1 = (Irrational)E1;
			Irrational i2 = (Irrational)E2;
			return add(i1, i2);
		}
		//irrational + rational
		else if(E1 instanceof Irrational && E2 instanceof Rational)
		{
			Irrational i = (Irrational)E1;
			Rational r = (Rational)E2;
			return add(i, r);
		}
		//rational + irrational
		else if(E1 instanceof Rational && E2 instanceof Irrational)
		{
			Irrational i = (Irrational) E2;
			Rational r = (Rational) E1;
			return add(i, r);
		}
		//rational + full expression
		else if(E1 instanceof Rational && E2 instanceof FullExpression)
		{
			Rational r = (Rational)E1;
			FullExpression f = (FullExpression)E2;
			return add(r, f);
		}
		//full expression + rational
		else if(E1 instanceof FullExpression && E2 instanceof Rational)
		{
			Rational r = (Rational)E2;
			FullExpression f = (FullExpression)E1;
			return add(r, f);
		}
		//full expression + full expression
		else if(E1 instanceof FullExpression && E2 instanceof FullExpression)
		{
			FullExpression f1 = (FullExpression)E1; 
			FullExpression f2 = (FullExpression)E2;
			return add(f1, f2);
		}
		//irrational + full expression
		else if(E1 instanceof Irrational && E2 instanceof FullExpression)
		{
			Irrational i = (Irrational)E1;
			FullExpression f = (FullExpression)E2;
			return add(i, f);
		}
		//full expression + irrational
		else if(E2 instanceof Irrational && E1 instanceof FullExpression)
		{
			Irrational i = (Irrational)E2;
			FullExpression f = (FullExpression)E1;
			return add(i, f);
		}
		else
		{
			throw new IllegalArgumentException("Addition Error: " + E1.toString() + Operator.SUM + E2.toString());
		}
	}
	
	public static Expression add(Rational r1, Rational r2)
	{
		return new Rational(r1.val + r2.val);
	}
	
	public static Expression add(Irrational i1, Irrational i2)
	{
		if(i1.equals(i2))
		{
			return new FullExpression(new Rational(2), i1, Operator.PROD);
		}
		return new FullExpression(i1, i2, Operator.SUM);
	}
	
	public static Expression add(Irrational i, Rational r)
	{
		if(r.val == 0)
			return i;
		return new FullExpression(r, i, Operator.SUM);
	}
	
	public static Expression add(Rational r, FullExpression f)
	{
		if(r.val == 0)
			return f;
		if(f.op == Operator.SUM)
		{
			ArrayList<Expression> allF = FullExpression.getAllRelevant(f);
			int i;
			for(i = 0; i < allF.size(); i++){
				if(allF.get(i) instanceof Rational){
					allF.set(i, new Rational(r.val + ((Rational)allF.get(i)).val));
					break;
				}
			}
			ArrayList<Expression> res = new ArrayList<Expression>();
			if(i >= allF.size()){
				res.add(r);
				i = 0;
			}
			else{
				res.add(allF.get(0));
				i = 1;
			}
			for(; i < allF.size(); i++){
				res.add(allF.get(i));
				res.add(Operator.SUM);
			}
			return Solver.makeExpression(res);
		}
		if(f.op == Operator.PROD)
		{
			return new FullExpression(r, f, Operator.SUM);
		}
		if(f.op == Operator.QUOT){
			Expression numerator = add(Multiplication.multiply(r, f.E2), f.E1);
			if(!(numerator instanceof FullExpression) || ((FullExpression)numerator).op != Operator.SUM){
				return Division.divide(numerator, f.E2);
			}
			return new FullExpression(r, f, Operator.SUM);
		}
		return new FullExpression(r, f, Operator.SUM);
	}
	
	public static Expression add(Irrational i, FullExpression f)
	{
		if(f.op == Operator.SUM)
		{
			ArrayList<Expression> allF = FullExpression.getAllRelevant(f);
			int j;
			for(j = 0; j < allF.size(); j++){
				Expression res = add(allF.get(j), i);
				if(!(res instanceof FullExpression) || ((FullExpression)res).op != Operator.SUM){
					allF.set(j, res);
					break;
				}
			}
			ArrayList<Expression> res = new ArrayList<Expression>();
			if(j >= allF.size()){
				res.add(i);
				j = 0;
			}
			else{
				res.add(allF.get(0));
				j = 1;
			}
			for(; j < allF.size(); j++){
				res.add(allF.get(j));
				res.add(Operator.SUM);
			}
			return Solver.makeExpression(res);
		}
		if(f.op == Operator.PROD)
		{
			if(f.E2.equals(i) && f.E1 instanceof Rational){
				return Multiplication.multiply(new Rational(((Rational)f.E1).val + 1), i);
			}
			return new FullExpression(i, f, Operator.SUM);
		}
		if(f.op == Operator.QUOT){
			Expression numerator = add(Multiplication.multiply(i, f.E2), f.E1);
			if(!(numerator instanceof FullExpression) || ((FullExpression)numerator).op != Operator.SUM){
				return Division.divide(numerator, f.E2);
			}
			return new FullExpression(i, f, Operator.SUM);
		}
		return new FullExpression(i, f, Operator.SUM);
	}
	
	public static Expression add(FullExpression f1, FullExpression f2)
	{
		if(f1.equals(f2)){
			return Multiplication.multiply(new Rational(2), f1);
		}
		if(f2.op.equals(Operator.SUM)  && !f1.op.equals(Operator.SUM) || f2.op.equals(Operator.QUOT) && f1.op.equals(Operator.PROD))
		{
			return add(f2, f1);
		}
		if(f1.op == Operator.SUM)
		{
			f2 = new FullExpression(new Rational(0), f2, Operator.SUM);
			ArrayList<Expression> all1 = FullExpression.getAllRelevant(f1);
			ArrayList<Expression> all2 = FullExpression.getAllRelevant(f2);
			all2.remove(0);
			for(int i = 0; i < all1.size(); i++)
			{
				for(int j = 0; j < all2.size(); j++)
				{
					Expression res = add(all1.get(i), all2.get(j));
					if(!(res instanceof FullExpression) || ((FullExpression)res).op != Operator.SUM)
					{
						all1.set(i, res);
						all2.remove(j);
					}
				}
			}
			ArrayList<Expression> res = new ArrayList<Expression>();
			res.add(all1.get(0));
			for(int i = 1; i < all1.size(); i++){
				res.add(all1.get(i));
				res.add(Operator.SUM);
			}
			for(int i = 0; i < all2.size(); i++){
				res.add(all2.get(i));
				res.add(Operator.SUM);
			}
			return Solver.makeExpression(res);
		}
		if(f1.op.equals(Operator.PROD))
		{
			
			if(f1.E1 instanceof Rational)
			{
				if(f1.E2.equals(f2)){
					return Multiplication.multiply(new Rational(((Rational)f1.E1).val + 1), f1.E2);
				}
				if(f2.op == Operator.PROD && f2.E1 instanceof Rational && f2.E2.equals(f1.E2)){
					return Multiplication.multiply(new Rational(((Rational)f1.E1).val + ((Rational)f2.E1).val), f1.E2);
				}
			}
			return new FullExpression(f1, f2, Operator.SUM);
		}
		if(f1.op == Operator.QUOT)
		{
			Expression numerator = add(Multiplication.multiply(f2, f1.E2), f1.E1);
			if(!(numerator instanceof FullExpression) || !((FullExpression)numerator).op.equals(Operator.SUM))
			{
				return Division.divide(numerator, f1.E2);
			}
			return new FullExpression(f1, f2, Operator.SUM);
		}
		return new FullExpression(f1, f2, Operator.SUM);
	}
	
}
