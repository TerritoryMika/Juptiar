package operation.lib.standard;

import operation.Environment;
import operation.lib.OperatorConstructor;

public class JupStatement extends OperatorConstructor {

	public JupStatement(Environment envir) { super(envir);}
	
	public String getName() {
		return "JupStatement";
	}
	
	public void loadLibrary() {
		constructOperation_HigherOrder("=>", (L, R) -> {
			if(L == null) return null;
			if(!envir.solveExpression(L).equals("0")) return envir.solveExpression(R.getNodeL());
			return envir.solveExpression(R.getNodeR());
		});
		constructOperation_HigherOrder("[^]", (L, R) -> {
			if(L == null) return envir.solveExpression(R);
			String l = envir.solveExpression(L);
			String acc = envir.solveExpression(R.getNodeL());
			if(isNumeric(l))
				for(int i = 0; i < Integer.parseInt(l); i++)
					acc = envir.solveExpression(envir.toExpression(acc + R.getValue() + envir.solveExpression(R.getNodeR())));
			return acc;
		});
		constructOperation(":", (L, R) -> {
			return L + ":" + R;
		});
	}
}
