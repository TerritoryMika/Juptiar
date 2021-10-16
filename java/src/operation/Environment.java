package operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import operation.lib.Library;

public class Environment {
	
	public void loadLibrary(Library library) {
		try {
			library.loadLibrary();
			System.out.println(" - Finish loading Juptiar Library - " + library.getName());
		} catch (Exception e) { e.printStackTrace();}
	}
	
	public Expression toExpression(String expression) {
		if(expression == null || expression.equals("")) return null; // null check
		final String exp = trim(expression).replaceAll("\\s+","");
		int floor;
		for(Operator operator : operatorList) {
			 
			String value = operator.getValue();
			
			if(!exp.contains(value)) continue;
			floor = 0;
			int range = exp.length() - (value.length() - 1);
			for(int i = 0; i < range; i++) {
				if(exp.charAt(i) == ')') floor--;
				if(floor != 0) continue;
				if(exp.charAt(i) == '(') floor++;
				if(exp.substring(i, i + value.length()).equals(value))
					return new Expression(	value,
											toExpression(exp.substring(0,i)),
											toExpression(exp.substring(i + value.length()))
											);
			}
		}
		return new Expression(exp);
	}
	
	public void executeExpression(Expression expression) {
		solveExpression(expression, false);
	}

	public String solveExpression(Expression expression) {
		return solveExpression(expression, false);
	}
	
	public String solveExpression(Expression expression, boolean skipOverride) {
		if(expression == null) return null;
		
		String value = expression.getValue();
		
		if(!skipOverride && expression.noNode()) {
			String matched = null;
			if(unstableMap.containsKey(value)) matched = solveExpression(unstableMap.get(expression.getValue()));
			if(variableMap.containsKey(value)) matched = variableMap.get(value);
			if(matched != null) return matched;
		}
		
		for(Operator operator : operatorList)
			if(operator.getValue().equals(value)) return operator.operate(expression);
		return value;
	}
	
	public Map<String, String> variableMap = new HashMap<String, String>();
	public Map<String, Expression> unstableMap = new HashMap<String, Expression>();
	
	public List<Operator> operatorList = new ArrayList<Operator>();

	public static String trim(String in) {
		int layer = 0;
		boolean fallback = false; boolean uphill = true;
		int submin = 0;
		for(int i = 0; i < in.length(); i++) {
			if(in.charAt(i) == '(') layer++;
			if(in.charAt(i) == ')') {
				if(!fallback) {
					fallback = true;
					submin = layer;
				}
				layer--;
			}
			if(uphill && !in.substring(i).contains("(")) uphill = false;
			if(fallback && uphill) submin = (submin < layer)? submin : layer;
		}
		return trimIterate(in,submin);
	}
	
	public static String trimIterate(String in, int g) {
		if(g == 0) return in;	// iterate trim
		return (in.startsWith("(") && in.endsWith(")"))?
				trimIterate(in.substring(1,in.length() - 1), g - 1) : in;
	}
	
	public static boolean isEnclosed(String expression) {
		int layer = 0;
		for(int i = 0; i < expression.length(); i++) {
			if(expression.charAt(i) == '(') layer++;
			if(expression.charAt(i) == ')') layer--;
			if(layer < 0) return false;
		}
		return layer == 0;
	}
	
}
