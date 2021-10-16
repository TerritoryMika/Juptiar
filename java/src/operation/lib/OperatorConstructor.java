package operation.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import operation.Environment;
import operation.Expression;
import operation.Operator;

public class OperatorConstructor implements Library {
	
	public Environment envir;
	
	public OperatorConstructor(Environment e) {
		this.envir = e;
	}
	
	public void insertOperation(Operator operator) {
		insertCheck(operator);
	}
	
	public void constructOperation(String symbol, BiFunction<String, String, String> function) {
		insertCheck(
			new Operator() {
				String value = symbol; 
				@Override public boolean compare(String str) { return str.equals(value);}
				@Override public int length() { return value.length();}
				@Override public String getValue() { return value;}
				@Override
				public String operate(Expression expression) {
					String nodeL = (expression.hasNodeL())? envir.solveExpression(expression.getNodeL()) : null;
					String nodeR = (expression.hasNodeR())? envir.solveExpression(expression.getNodeR()) : null;
					return function.apply(nodeL, nodeR);
				}
			}
		);
	}
	
	public void constructOperation_HigherOrder(String symbol, BiFunction<Expression, Expression, String> function) {
		insertCheck(
			new Operator() {
				String value = symbol; 
				@Override public boolean compare(String str) { return str.equals(value);}
				@Override public int length() { return value.length();}
				@Override public String getValue() { return value;}
				@Override
				public String operate(Expression expression) {
					return function.apply(expression.getNodeL(), expression.getNodeR());
				}
			}
		);
	}
	
	private void insertCheck(Operator operator) {
		boolean containSubset = false;
		int place = 0;
		checking :
		for(String subset : powerSet(operator.getValue())) {
			place = 0;
			for(Operator checks : envir.operatorList) {
				place++;
				if(checks.getValue().equals(subset)) {
					containSubset = true;
					break checking;
				}
			}
		}
		if(!containSubset) {
			envir.operatorList.add(operator);
		}else{
			envir.operatorList.add(place - 1, operator);
		}
	}
	private List<String> powerSet(String s) {
		int l = s.length();
		List<String> output = new ArrayList<String>();
	    for(int i = 1; i <= l; i++) {
	    	for(int j = 0; j + i <= l; j++) {
	    		output.add(s.substring(j, (j + i)));
	    	}
	    }
	    return output;
	}

	public static boolean isNull(String str) { return str == null;}
	public static boolean isNumeric(String str) {
		if(isNull(str)) return false;
		return str.matches("-?\\d+(\\.\\d+)?");
	}
	
	@Override public void loadLibrary() { }
	@Override public String getName() { return null;}
	
}

