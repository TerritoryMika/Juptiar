package operation.lib.standard;

import operation.Environment;
import operation.lib.OperatorConstructor;

public class JupLogic extends OperatorConstructor {

	public JupLogic(Environment envir) { super(envir);}
	
	public String getName() {
		return "JupLogic";
	}
	
	public void loadLibrary() {
		constructOperation("==", (L, R) -> {
			if(L == R || L.equals(R)) return logic(true);
			return logic(false);
		});
		constructOperation("!=", (L, R) -> {
			if(L == null && R == null) return logic(false);
			if(L == null || R == null) return logic(true);
			if(!L.equals(R)) return logic(true);
			return logic(false);
		});
		constructOperation(">", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return Integer.parseInt(L) > Integer.parseInt(R)? logic(true) : logic(false);
			return L + ">" + R;
		});
		constructOperation("<", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return Integer.parseInt(L) < Integer.parseInt(R)? logic(true) : logic(false);
			return L + "<" + R;
		});
		constructOperation(">=", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return Integer.parseInt(L) >= Integer.parseInt(R)? logic(true) : logic(false);
			return L + ">=" + R;
		});
		constructOperation("<=", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return Integer.parseInt(L) <= Integer.parseInt(R)? logic(true) : logic(false);
			return L + "<=" + R;
		});
	}
	
	public String logic(boolean bool) {
		return bool? "1" : "0";
	}
}
