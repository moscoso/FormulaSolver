package expressions;
import solve.Solver;
import java.util.ArrayList;

public class FullExpression implements Expression{

	public final Expression E1; //The expression on the left of an operator
	public final Expression E2; //The expression on the right of an operator
	public final Operator op;   //The operation of the FullExpression
	
	public FullExpression(Expression E1, Expression E2, Operator op){
		this.E1 = E1;
		this.E2 = E2;
		this.op = op;
	}

	public boolean equals(Expression E){
		if(!(E instanceof FullExpression)){
			return false;
		}
		if(((FullExpression)E).op != this.op){
			return false;
		}
		if(this.op == Operator.QUOT || this.op == Operator.POWER){
			if(((FullExpression)E).E1.equals(this.E1) && ((FullExpression)E).E2.equals(this.E2)){
				return true;
			}
			return false;
		}
		return checkAllPermutations(getAllRelevant(E), getAllRelevant(this));
	}
	
	public static ArrayList<Expression> getAllRelevant(Expression E){
		ArrayList<Expression> allRelevant = new ArrayList<Expression>();
		if(!(E instanceof FullExpression)){
			allRelevant.add(E);
			return allRelevant;
		}
		if(((FullExpression)E).E1 instanceof FullExpression && ((FullExpression)((FullExpression)E).E1).op == ((FullExpression)E).op){
			allRelevant.addAll(getAllRelevant(((FullExpression)E).E1));
		}
		else{
			allRelevant.add(((FullExpression)E).E1);
		}
		if(((FullExpression)E).E2 instanceof FullExpression && ((FullExpression)((FullExpression)E).E2).op == ((FullExpression)E).op){
			allRelevant.addAll(getAllRelevant(((FullExpression)E).E2));
		}
		else{
			allRelevant.add(((FullExpression)E).E2);
		}
		return allRelevant;
	}
	
	private static boolean checkAllPermutations(ArrayList<Expression> list1, ArrayList<Expression> list2){
		if(list1.size() != list2.size()){
			return false;
		}
		if(list1.size() == 1){
			return list1.get(0).equals(list2.get(0));
		}
		for(int i = 0; i < list1.size(); i++){
			if(list1.get(0).equals(list2.get(i))){
				list1.remove(0);
				list2.remove(i);
				return checkAllPermutations(list1, list2);
			}
		}
		return false;
	}
	
	public Expression simplify(){
		return Solver.combine(E1.simplify(), E2.simplify(), op);
	}
	
	public String toString(){
		String before = E1.toString();
		String after = E2.toString();
		String operator = op.toString();
		if(E1 instanceof FullExpression && ((FullExpression)E1).op.getVal() > this.op.getVal()){
			before = "(" + before + ")";
		}
		if(E2 instanceof FullExpression && ((FullExpression)E2).op.getVal() > this.op.getVal()){
			after = "(" + after + ")";
		}
		if(E1 instanceof Rational && op.equals(Operator.SUM))
		{
			Rational r = (Rational)E1;
			if(r.val < 0)
			{
				String temp = before.substring(1);
				operator = Operator.DIFF.toString();
				before = after;
				after = temp;
			}
		}
		if(E2 instanceof FullExpression && op.equals(Operator.SUM))
		{
			FullExpression f = (FullExpression)E2;
			if(f.E1 instanceof Rational)
			{
				Rational r = (Rational) f.E1;
				if(r.val < 0){
					operator = Operator.DIFF.toString();
					after = after.substring(1);
					}
			}
		}
		if(op.equals(Operator.PROD)){
			operator = "";
			if(E1 instanceof Rational){
				Rational r = (Rational)E1;
				if(r.val == -1)
					before = "-";
			}
			//fix
			/*
			if(E1 instanceof FullExpression){
				System.out.println(true);
				FullExpression f = (FullExpression)E1;
				if(f.op.equals(Operator.QUOT)){
					System.out.println(true);
					String temp = before;
					int index = before.indexOf("/");
					before = "(" + before.substring(0,index) + after +")";
					after = temp.substring(index);
				}
			}*/
		}
		if(op.equals(Operator.POWER) && E2 instanceof FullExpression)
		{
			FullExpression f = (FullExpression)E2;
			if(f.E1 instanceof Rational && f.E2 instanceof Rational)
			{
				Rational r1 = (Rational)f.E1;
				Rational r2 = (Rational)f.E2;
				if(r2.val == 2){
					if(r1.val == 1){
						after = "";
						operator = before + ")";
						before = "sqrt:(";
					}
					else{
						after = after.substring(1, after.length() - 3);
						before = "(sqrt:("+before +"))";
					}
				}
				else{
					if(r1.val == 1){
						after = "";
						operator = before + "))";
						before = "(" + r2 +"rt:(";
					}
					else{
						after = after.substring(1, after.length() - 3);
						before ="(" + r2 + "rt:("+before +"))";
					}
				}
			}
		}
		return before + operator + after;
	}
	
}