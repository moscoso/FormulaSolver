package solve;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
 
public class ShuntingYard {
    private static final int LEFT_ASSOC = 0;
    private static final int RIGHT_ASSOC = 1;
 
    private static final Map<String, int[]> OPERATORS = new HashMap<String, int[]>();
    static {
        OPERATORS.put("+", new int[] { 0, LEFT_ASSOC });
        OPERATORS.put("-", new int[] { 0, LEFT_ASSOC });
        OPERATORS.put("*", new int[] { 5, LEFT_ASSOC });
        OPERATORS.put("/", new int[] { 5, LEFT_ASSOC });
        OPERATORS.put("%", new int[] { 5, LEFT_ASSOC });
        OPERATORS.put("^", new int[] { 10, RIGHT_ASSOC });
    }
 
    private static boolean isOperator(String token) {
        return OPERATORS.containsKey(token);
    }
 
    private static boolean isAssociative(String token, int type) {
        if (!isOperator(token)) {
            throw new IllegalArgumentException("Invalid token: " + token);
        }
        if (OPERATORS.get(token)[1] == type) {
            return true;
        }
        return false;
    }
 
    private static final int comparePrecedence(String token1, String token2) {
        if (!isOperator(token1) || !isOperator(token2)) {
            throw new IllegalArgumentException("Invalied tokens: " + token1
                    + " " + token2);
        }
        return OPERATORS.get(token1)[0] - OPERATORS.get(token2)[0];
    }
 
    public static String[] convertToArray(String[] inputTokens) {
        ArrayList<String> out = new ArrayList<String>();
        Stack<String> stack = new Stack<String>();
        for (String token : inputTokens) {
            if (isOperator(token)) {
                while (!stack.empty() && isOperator(stack.peek())) {
                    if ((isAssociative(token, LEFT_ASSOC) && comparePrecedence(
                            token, stack.peek()) <= 0)
                            || (isAssociative(token, RIGHT_ASSOC) && comparePrecedence(
                                    token, stack.peek()) < 0)) {
                        out.add(stack.pop()); 
                        continue;
                    }
                    break;
                }
                try{
                    stack.push(token);
                }catch(Exception e){
                	throw new IllegalArgumentException("WTF");
                }
            } else if (token.equals("(")) {
                stack.push(token); 
            } else if (token.equals(")")) {
                while (!stack.empty() && !stack.peek().equals("(")) {
                    out.add(stack.pop());
                }
                try{
                	stack.pop();
                }
                catch(Exception e){
                	throw new IllegalArgumentException("Missing close parenthesis.");
                }
            }
             else {
                out.add(token);
            }
        }
        while (!stack.empty()) {
            out.add(stack.pop()); 
        }
        String[] output = new String[out.size()];
        return out.toArray(output);
    }
 
    public static String[] solve(String[] eq) {
        return convertToArray(eq);
    }
}