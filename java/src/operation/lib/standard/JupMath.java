package operation.lib.standard;

import operation.Environment;
import operation.lib.OperatorConstructor;

public class JupMath extends OperatorConstructor {

	public JupMath(Environment e) { super(e);}
	
	public String getName() {
		return "JupMath";
	}
	
	public void loadLibrary() {
		constructOperation("~", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((int) (Math.random() * (Integer.parseInt(R) - Integer.parseInt(L) + 0.99) + Integer.parseInt(L)));
			return L + "~" + R;
		});
		constructOperation("f+", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((Double.parseDouble(L) + Double.parseDouble(R)));
			return L + "f+" + R;
		});
		constructOperation("+", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((Integer.parseInt(L) + Integer.parseInt(R)));
			return L + "+" + R;
		});
		constructOperation("f-", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((Double.parseDouble(L) - Double.parseDouble(R)));
			if(isNull(L) && isNumeric(R)) return String.valueOf(-Double.parseDouble(R));
			return L + "f-" + R;
		});
		constructOperation("-", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((Integer.parseInt(L) - Integer.parseInt(R)));
			if(isNull(L) && isNumeric(R)) return String.valueOf(-Integer.parseInt(R));
			return L + "-" + R;
		});
		constructOperation("f*", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((Double.parseDouble(L) * Double.parseDouble(R)));
			return L + "f*" + R;
		});
		constructOperation("*", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((Integer.parseInt(L) * Integer.parseInt(R)));
			return L + "*" + R;
		});
		constructOperation("f/", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((Double.parseDouble(L) / Double.parseDouble(R)));
			return L + "f/" + R;
		});
		constructOperation("/", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((Integer.parseInt(L) / Integer.parseInt(R)));
			return L + "/" + R;
		});
		constructOperation("%", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((Integer.parseInt(L) % Integer.parseInt(R)));
			return L + "%" + R;
		});
	}
	
}
