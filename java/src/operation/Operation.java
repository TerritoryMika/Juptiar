package operation;

public interface Operation {
	public boolean compare(String str);
	public int length();
	public String getValue();
	public int getOrder();
	public String operate(Expression expression);
}
