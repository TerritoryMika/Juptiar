package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Main {

	public static void main(String[] args) {
		
		System.out.println(" - Juptiar Starting up...");
		
		Expression.operatorListInit_prelude();
		
		System.out.println("### Juptiar Interpreter ###");
		
		try {
			if(args.length == 1 && args[0].endsWith(".jur")) read(args[0]);
			if(args.length == 2 && args[0].endsWith(".jur")) {
				for(int i = 0; i < Integer.parseInt(args[1]); i++) 
					read(args[0]);
			}
			if(args.length == 3 && args[0].endsWith(".jur")) {
				for(int i = 0; i < Integer.parseInt(args[1]); i++) 
					read(args[0]);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		Scanner scan = new Scanner(System.in);

		System.out.print("> ");
		while(scan.hasNextLine()) {
			String input = scan.nextLine();
			if(input.equals("/")) break;
			System.out.println(Expression.toExpression(input).solve());
			System.out.print("> ");
		}
		System.out.println("##### Juptiar Halted ######");
		scan.close();
	}
	
	public static void read(String fileName) throws FileNotFoundException {
		Scanner scan = new Scanner(new File(fileName));
		boolean mode_command = false;
		boolean mode_text = false;
		while(scan.hasNextLine()) {

			String line = scan.nextLine();
			if(line.equals("{")) mode_command = true;
			if(line.equals("}")) mode_command = false;
			if(line.equals("\"")) {
				mode_text = (mode_text)? false : true;
				continue;
			}
			
			if(mode_command) {
				Expression.toExpression(line).execute();
			}
			
			if(mode_text) {
				String temp = line;
				while(temp.contains("{")) {
					String original = temp.substring(temp.indexOf("{"), temp.indexOf("}") + 1);
					temp = temp.replace(original, Expression.toExpression(original.substring(1, original.length() - 1)).solve());
				}
				System.out.println(temp);
			}
			
		}
		scan.close();
		System.out.println();
	}
	
	
	public static List<String> output = new ArrayList<String>();
}

class Expression {
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
	
	public static Expression toExpression(String expression) {
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
	
	public void execute() {
		solveExpression(this);
	}
	
	public String solve() {
		return solveExpression(this);
	}
	
	public String express() {
		if(noNode()) return getValue();
		return String.format("( %s %s %s )", 	hasNodeL()? getNodeL().express() : "",
												getValue(),
												hasNodeR()? getNodeR().express() : "");
	} 
	
	public static String solveExpression(Expression expression) {
		if(expression == null) return null;
		
		String value = expression.getValue();
		
		if(expression.noNode()) {
			String matched = null;
			if(unstableMap.containsKey(value)) matched = solveExpression(unstableMap.get(expression.getValue()));
			if(variableMap.containsKey(value)) matched = variableMap.get(value);
			if(matched != null) return matched;
		}
		
		
		for(Operator operator : operatorList)
			if(operator.getValue().equals(value)) return operator.operate(expression);
		return value;
	}
	
	private static boolean isNull(String str) { return str == null;}
	private static boolean isNumeric(String str) {
		if(isNull(str)) return false;
		return str.matches("-?\\d+(\\.\\d+)?");
	}
	
	public static void operatorListInit_prelude() {
		operatorList.add(constructOperation_HigherOrder("?", (L, R) -> {
			Expression ex1; String ex2;
			if((ex1 = unstableMap.get(R.getValue())) != null) return ex1.express();
			if((ex2 = variableMap.get(R.getValue())) != null) return ex2;
			return R.express();
		}));
		operatorList.add(constructOperation_HigherOrder("->", (L, R) -> {
			if(L != null && R != null) unstableMap.put(L.getValue(), R);
			return solveExpression(R);
		}));
		operatorList.add(constructOperation("@", (L, R) -> {
			if(R.equals("o")) operatorList.forEach((value) -> { System.out.println(" - " + value.getValue());});
			if(R.equals("v")) variableMap.forEach((key, value) -> { System.out.println(" - " + key);});
			if(R.equals("p")) unstableMap.forEach((key, value) -> { System.out.println(" - " + key);});
			return null;
		}));
		operatorList.add(constructOperation("=", (L, R) -> {
			if(!(isNull(L) || isNull(R))) variableMap.put(L, R);
			return R;
		}));
		operatorList.add(constructOperation("|>", (L, R) -> { return R;}));
		operatorList.add(constructOperation("<|", (L, R) -> { return L;}));
		operatorList.add(constructOperation("|", (L, R) -> { return (Math.random() > 0.5)? L : R;}));
		System.out.println(" - loaded juptiar::prelude");
		operatorListInit_math();
	}
	
	public static void operatorListInit_math() {
		operatorList.add(constructOperation("~", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((int) (Math.random() * (Integer.parseInt(R) - Integer.parseInt(L) + 0.99) + Integer.parseInt(L)));
			if(isNull(L) || isNull(R)) return null;
			return L + "~" + R;
		}));
		operatorList.add(constructOperation("f+", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((Double.parseDouble(L) + Double.parseDouble(R)));
			if(isNull(L) || isNull(R)) return null;
			return L + "f+" + R;
		}));
		operatorList.add(constructOperation("+", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((Integer.parseInt(L) + Integer.parseInt(R)));
			if(isNull(L) || isNull(R)) return null;
			return L + "+" + R;
		}));
		operatorList.add(constructOperation("f-", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((Double.parseDouble(L) - Double.parseDouble(R)));
			if(isNull(L) && isNumeric(R)) return String.valueOf(-Double.parseDouble(R));
			if(isNull(L) || isNull(R)) return null;
			return L + "f-" + R;
		}));
		operatorList.add(constructOperation("-", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((Integer.parseInt(L) - Integer.parseInt(R)));
			if(isNull(L) && isNumeric(R)) return String.valueOf(-Integer.parseInt(R));
			if(isNull(R)) return null;
			return L + "-" + R;
		}));
		operatorList.add(constructOperation("f*", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((Double.parseDouble(L) * Double.parseDouble(R)));
			if(isNull(L) || isNull(R)) return null;
			return L + "f*" + R;
		}));
		operatorList.add(constructOperation("*", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((Integer.parseInt(L) * Integer.parseInt(R)));
			if(isNull(L) || isNull(R)) return null;
			return L + "*" + R;
		}));
		operatorList.add(constructOperation("f/", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((Double.parseDouble(L) / Double.parseDouble(R)));
			if(isNull(L) || isNull(R)) return null;
			return L + "f/" + R;
		}));
		operatorList.add(constructOperation("/", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((Integer.parseInt(L) / Integer.parseInt(R)));
			if(isNull(L) || isNull(R)) return null;
			return L + "/" + R;
		}));
		operatorList.add(constructOperation("%", (L, R) -> {
			if(isNumeric(L) && isNumeric(R)) return String.valueOf((Integer.parseInt(L) % Integer.parseInt(R)));
			if(isNull(L) || isNull(R)) return null;
			return L + "%" + R;
		}));
		System.out.println(" - loaded juptiar::math");
	}
	
	public static Operator constructOperation(String symbol, BiFunction<String, String, String> function) {
		return new Operator() {
			String value = symbol; 
			@Override public boolean compare(String str) { return str.equals(value);}
			@Override public int length() { return value.length();}
			@Override public String getValue() { return value;}
			@Override
			public String operate(Expression expression) {
				String nodeL = (expression.hasNodeL())? solveExpression(expression.getNodeL()) : null;
				String nodeR = (expression.hasNodeR())? solveExpression(expression.getNodeR()) : null;
				return function.apply(nodeL, nodeR);
			}
		};
	}
	
	public static Operator constructOperation_HigherOrder(String symbol, BiFunction<Expression, Expression, String> function) {
		return new Operator() {
			String value = symbol; 
			@Override public boolean compare(String str) { return str.equals(value);}
			@Override public int length() { return value.length();}
			@Override public String getValue() { return value;}
			@Override
			public String operate(Expression expression) {
				return function.apply(expression.getNodeL(), expression.getNodeR());
			}
		};
	}
	
	public static List<Operator> operatorList = new ArrayList<Operator>();

	public static Map<String, String> variableMap = new HashMap<String, String>();
	public static Map<String, Expression> unstableMap = new HashMap<String, Expression>();
}

interface Operator {
	public boolean compare(String str);
	public int length();
	public String getValue();
	public String operate(Expression expression);
}