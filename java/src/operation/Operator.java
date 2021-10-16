package operation;

public class Operator implements Operation {
	@Override public boolean compare(String str) { return false;}
	@Override public int length() { return -1;}
	@Override public String getValue() { return null;}
	@Override public int getOrder() { return -1;}
	@Override public String operate(Expression expression) { return null;}	
}
