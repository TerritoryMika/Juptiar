package operation.lib.standard;

import operation.Environment;
import operation.Expression;
import operation.lib.OperatorConstructor;

public class JupPrelude extends OperatorConstructor{

	public JupPrelude(Environment envir) { super(envir);}
	
	public String getName() {
		return "JupPrelude";
	}
	
	public void loadLibrary() {
		constructOperation_HigherOrder("?", (L, R) -> {
			if(R == null) return null;
			Expression ex1; String ex2;
			if((ex1 = envir.unstableMap.get(R.getValue())) != null) return ex1.express();
			if((ex2 = envir.variableMap.get(R.getValue())) != null) return ex2;
			return R.express();
		});
		constructOperation_HigherOrder("->", (L, R) -> {
			if(L != null && R != null) envir.unstableMap.put(L.getValue(), R);
			return envir.solveExpression(L);
		});
		constructOperation_HigherOrder("=", (L, R) -> {
			if(L != null && R != null) envir.variableMap.put(envir.solveExpression(L, true), envir.solveExpression(R));
			return envir.solveExpression(L);
		});
		constructOperation("!list", (L, R) -> {
			if(R.equals("o") || R.equals("operator")) envir.operatorList.forEach((value) -> { System.out.println(" - " + value.getValue());});
			if(R.equals("v") || R.equals("variable")) envir.variableMap.forEach((key, value) -> { System.out.println(" - " + key + " = " + value);});
			if(R.equals("u") || R.equals("unstable")) envir.unstableMap.forEach((key, value) -> { System.out.println(" - " + key + " = " + value.express());});
			return null;
		});
		constructOperation_HigherOrder("!clear", (L, R) -> {
			String key = envir.solveExpression(R, true);
			envir.variableMap.remove(key);
			envir.unstableMap.remove(key);
			return null;
		});
		constructOperation("|>", (L, R) -> { return R;});
		constructOperation("<|", (L, R) -> { return L;});
		constructOperation("|", (L, R) -> { return (Math.random() > 0.5)? L : R;});
	}
}
