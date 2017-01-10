package math;
import expressions.*;
import solve.*;

import java.util.*;

public class Multiplication 
{
	
	public static Expression multiply(Expression E1, Expression E2)
	{
		//rational + rational
		if(E1 instanceof Rational && E2 instanceof Rational)
		{
			Rational r1 = (Rational)E1;
			Rational r2 = (Rational)E2;
			return multiply(r1, r2);
		}	
		//irrational + irrational
		else if(E1 instanceof Irrational && E2 instanceof Irrational)
		{
			Irrational i1 = (Irrational)E1;
			Irrational i2 = (Irrational)E2;
			return multiply(i1, i2);
		}
		//irrational + rational
		else if(E1 instanceof Irrational && E2 instanceof Rational)
		{
			Irrational i = (Irrational)E1;
			Rational r = (Rational)E2;
			return multiply(i, r);
		}
		//rational + irrational
		else if(E1 instanceof Rational && E2 instanceof Irrational)
		{
			Irrational i = (Irrational) E2;
			Rational r = (Rational) E1;
			return multiply(i, r);
		}
		//rational + full expression
		else if(E1 instanceof Rational && E2 instanceof FullExpression)
		{
			Rational r = (Rational)E1;
			FullExpression f = (FullExpression)E2;
			return multiply(r, f);
		}
		//full expression + rational
		else if(E1 instanceof FullExpression && E2 instanceof Rational)
		{
			Rational r = (Rational)E2;
			FullExpression f = (FullExpression)E1;
			return multiply(r, f);
		}
		//full expression + full expression
		else if(E1 instanceof FullExpression && E2 instanceof FullExpression)
		{
			FullExpression f1 = (FullExpression)E1; 
			FullExpression f2 = (FullExpression)E2;
			return multiply(f1, f2);
		}
		//irrational + full expression
		else if(E1 instanceof Irrational && E2 instanceof FullExpression)
		{
			Irrational i = (Irrational)E1;
			FullExpression f = (FullExpression)E2;
			return multiply(i, f);
		}
		//full expression + irrational
		else if(E2 instanceof Irrational && E1 instanceof FullExpression)
		{
			Irrational i = (Irrational)E2;
			FullExpression f = (FullExpression)E1;
			return multiply(i, f);
		}
		else
		{
			throw new IllegalArgumentException("Addition Error: " + E1.toString() + Operator.SUM + E2.toString());
		}
	}
	
	public static Expression multiply(Rational r1, Rational r2)
	{
		return new Rational(r1.val * r2.val);
	}
	
	public static Expression multiply(Irrational i1, Irrational i2)
	{
		if(i1.equals(i2))
		{
			return new FullExpression(i1, new Rational(2), Operator.POWER);
		}
		return new FullExpression(i1, i2, Operator.PROD);
	}
	
	public static Expression multiply(Irrational i, Rational r)
	{
		if(r.val == 0) return new Rational(0);
		if(r.val == 1) return i;
		return new FullExpression(r, i, Operator.PROD);
	}
	
	public static Expression multiply(Rational r, FullExpression f)
	{
		if(r.val == 0){
			return new Rational(0);
		}
		if(r.val == 1){
			return f;
		}
		if(f.op == Operator.SUM)
		{
			return Addition.add(multiply(r, f.E1), multiply(r, f.E2));
		}
		if(f.op == Operator.PROD)
		{
			ArrayList<Expression> allF = FullExpression.getAllRelevant(f);
			int i;
			for(i = 0; i < allF.size(); i++){
				if(allF.get(i) instanceof Rational){
					allF.set(i, new Rational(r.val * ((Rational)allF.get(i)).val));
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
				res.add(Operator.PROD);
			}
			return Solver.makeExpression(res);
		}
		if(f.op == Operator.QUOT){
			return Division.divide(multiply(r, f.E1), f.E2);
		}
		if(f.op == Operator.POWER){
			if(f.E1 instanceof Rational && ((Rational)f.E1).equals(r) && f.E2 instanceof Rational){
				return new FullExpression(new Rational(r.val + ((Rational)f.E1).val), f.E2, Operator.POWER);
			}
			return new FullExpression(r, f, Operator.PROD);
		}
		return new FullExpression(r, f, Operator.PROD);
	}
	
	public static Expression multiply(Irrational i, FullExpression f)
	{
		if(f.op == Operator.SUM)
		{
			return Addition.add(multiply(i, f.E1), multiply(i, f.E2));
		}
		if(f.op == Operator.PROD)
		{
			ArrayList<Expression> allF = FullExpression.getAllRelevant(f);
			int j;
			for(j = 0; j < allF.size(); j++){
				Expression res = multiply(i, allF.get(j));
				if(!(res instanceof FullExpression) || ((FullExpression)res).op != Operator.PROD){
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
				res.add(Operator.PROD);
			}
			return Solver.makeExpression(res);
		}
		if(f.op == Operator.QUOT){
			return Division.divide(multiply(i, f.E1), f.E2);
		}
		if(f.op == Operator.POWER){
			if(f.E1.equals(i)){
				return new FullExpression(i, Addition.add(f.E2, new Rational(1)), Operator.POWER);
			}
			return new FullExpression(i, f, Operator.PROD);
		}
		return new FullExpression(i, f, Operator.PROD);
	}
	
	public static Expression multiply(FullExpression f1, FullExpression f2)
	{
		if(f2.op == Operator.QUOT && f1.op != Operator.QUOT){
			return multiply(f2, f1);
		}
		if(f1.op == Operator.QUOT){
			if(f2.equals(f1.E2)){
				return(f1.E1);
			}
			if(f2.E1.equals(f1.E2)){
				return multiply(f1.E1, f2.E2);
			}
			return Division.divide(multiply(f1.E1, f2), f1.E2);
		}
		if(f2.op == Operator.SUM && f1.op != Operator.SUM || f1.op == Operator.POWER && f2.op != Operator.POWER){
			return multiply(f2,f1);
		}
		if(f1.op == Operator.SUM)
		{
			return Addition.add(multiply(f1.E1, f2), multiply(f1.E2, f2));
		}
		if(f1.op == Operator.PROD)
		{
			f2 = new FullExpression(new Rational(0), f2, Operator.PROD);
			ArrayList<Expression> all1 = FullExpression.getAllRelevant(f1);
			ArrayList<Expression> all2 = FullExpression.getAllRelevant(f2);
			all2.remove(0);
			for(int i = 0; i < all1.size(); i++){
				for(int j = 0; j < all2.size(); j++){
					Expression res = multiply(all1.get(i), all2.get(j));
					if(!(res instanceof FullExpression) || ((FullExpression)res).op != Operator.PROD){
						all1.set(i, res);
						all2.remove(j);
					}
				}
			}
			ArrayList<Expression> res = new ArrayList<Expression>();
			res.add(all1.get(0));
			for(int i = 1; i < all1.size(); i++){
				res.add(all1.get(i));
				res.add(Operator.PROD);
			}
			for(int i = 0; i < all2.size(); i++){
				res.add(all2.get(i));
				res.add(Operator.PROD);
			}
			return Solver.makeExpression(res);
		}
		if(f1.op == Operator.POWER){
			if(f2.op == Operator.POWER){
				if(f1.E1.equals(f2.E1)){
					return Exponent.power(f1.E1, Addition.add(f1.E2, f2.E2));
				}
				if(f1.E2.equals(f2.E2)){
					return Exponent.power(multiply(f1.E1, f2.E1), f1.E2);
				}
				return new FullExpression(f1, f2, Operator.PROD);
			}
			return new FullExpression(f1, f2, Operator.PROD);
		}
		return new FullExpression(f1, f2, Operator.PROD);
	}
	
}
