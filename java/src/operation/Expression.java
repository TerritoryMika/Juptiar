package operation;

import java.util.function.Consumer;

public class Expression {
	
	private String value;
	private Expression L;
	private Expression R;
	
	public Expression() {}
	public Expression(String value) { setValue(value);}
	public Expression(	String value,
						Expression left,
						Expression right) {
		setValue(value);
		setNodeL(left);
		setNodeR(right);
	}
	
	public String getValue() { return value;}
	public Expression getNodeL() { return L;}
	public Expression getNodeR() { return R;}
	public void setValue(String value) { this.value = value;}
	public void setNodeL(Expression nodeL) { this.L = nodeL;}
	public void setNodeR(Expression nodeR) { this.R = nodeR;}
	

	public boolean hasNodeL() { return getNodeL() != null;}
	public boolean hasNodeR() { return getNodeR() != null;}
	
	public boolean fullNode() { return hasNodeL() && hasNodeR();}
	public boolean hasNode() { return hasNodeL() || hasNodeR();}
	public boolean noNode() { return !hasNode();}
	
	public int getHeight() {
		if(noNode()) return 0;
		int l, r;
		return ((l = getNodeL().getHeight() ) > (r = getNodeR().getHeight()))? l + 1 : r + 1;
	}
	
	
	public void forEach(Consumer<String> action) {
		action.accept(getValue());
		if(hasNodeL()) getNodeL().forEach(action);
		if(hasNodeR()) getNodeR().forEach(action);
	}

	public String express() {
		if(noNode()) return getValue();
		return String.format("( %s %s %s )", 	hasNodeL()? getNodeL().express() : "",
												getValue(),
												hasNodeR()? getNodeR().express() : "");
	} 
	
}
