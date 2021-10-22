using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using System.IO;

public class Program
{
	public static void Main(String[] args)
	{
		JupEnvironment envirMain = new JupEnvironment();

		envirMain.loadLibrary(new JupPrelude(envirMain));

		// simulationMode
		
		if (args.Length >= 1)
		{
			int arg2 = (args.Length >= 2)? Int32.Parse(args[1]) : 1;

			// outputMode
			String arg3 = (args.Length >= 3)? args[2] : "";
			bool outputMode = !arg3.Equals("");
			int arg3count = 0;

			if (outputMode)
			{
				Console.WriteLine("Enter Output Mode");
			}
			else 
			{
				Console.WriteLine("Enter Simulation Mode");
			}

			for (int i = 0; i < arg2; i++)
			{
				String[] lines = System.IO.File.ReadAllLines(args[0]);

				String[] output = (outputMode)? new String[lines.Length] : null;
				StreamWriter sw = (outputMode)? new StreamWriter(arg3 + arg3count + ".txt") : null;

				bool mode_command = false;
				bool mode_text = false;

				Console.WriteLine(arg3count + " : (");

				foreach (String line in lines)
				{
					if (line.Equals("{")) mode_command = true;
					if (line.Equals("}")) mode_command = false;
					if (line.Equals("\""))
					{
						mode_text = (mode_text) ? false : true;
						continue;
					}

					if (mode_command)
					{
						envirMain.execute(line);
					}

					if (mode_text)
					{
						String temp = line;
						while (temp.Contains("{"))
						{
							String original = temp.Substring(temp.IndexOf("{"), temp.IndexOf("}") + 1 - temp.IndexOf("{"));
							temp = temp.Replace(original, envirMain.solveExpression(envirMain.toExpression(original.Substring(1, original.Length - 2))));
						}
						Console.WriteLine(temp);
						if (outputMode)
							sw.WriteLine(temp);
						
					}
				}
				arg3count++;
				Console.WriteLine(")");

				// re-new all variable
				envirMain.execute("!clear*");
				envirMain.run("name");
			}
		}


		// interpretMode
		Console.WriteLine("Enter Interpret Mode");

		while (true)
		{
			Console.Write("> ");
			String command = Console.ReadLine();
			if (command.Equals("/")) {
				break;
			}
			Console.WriteLine(envirMain.solveExpression(envirMain.toExpression(command)));
		}
		
		Console.WriteLine("- Halted -");
	}

	interface Operation
	{
		String getValue();
		String operate(Expression expression);
	}

	class Operator : Operation
	{
		public String value;
		public Func<Expression, String> operation;

		public String getValue() { return value; }
		public String operate(Expression expression) { return operation(expression); }
	}

	class Expression
	{

		private String value;
		private Expression L;
		private Expression R;

		public Expression() { }
		public Expression(String value) { setValue(value); }
		public Expression(String value,
						  Expression left,
						  Expression right)
		{
			setValue(value);
			setNodeL(left);
			setNodeR(right);
		}

		public String getValue() { return value; }
		public Expression getNodeL() { return L; }
		public Expression getNodeR() { return R; }
		public void setValue(String value) { this.value = value; }
		public void setNodeL(Expression nodeL) { this.L = nodeL; }
		public void setNodeR(Expression nodeR) { this.R = nodeR; }

		public bool hasNodeL() { return getNodeL() != null; }
		public bool hasNodeR() { return getNodeR() != null; }

		public bool fullNode() { return hasNodeL() && hasNodeR(); }
		public bool hasNode() { return hasNodeL() || hasNodeR(); }
		public bool noNode() { return !hasNode(); }

		public int getHeight()
		{
			if (noNode()) return 0;
			int l, r;
			return ((l = getNodeL().getHeight()) > (r = getNodeR().getHeight())) ? l + 1 : r + 1;
		}


		public void forEach(Action<String> action)
		{
			action(getValue());
			if (hasNodeL()) getNodeL().forEach(action);
			if (hasNodeR()) getNodeR().forEach(action);
		}

		public String express()
		{
			if (noNode()) return getValue();
			return String.Format("( {0} {1} {2} )", (hasNodeL() ? getNodeL().express() : "")
												  , getValue()
												  , (hasNodeR() ? getNodeR().express() : ""));
		}
	}

	interface Library
	{
		void loadLibrary();
		String getName();
	}

	class OperatorConstructor : Library
	{

		public JupEnvironment envir;

		public OperatorConstructor(JupEnvironment e)
		{
			this.envir = e;
		}

		public void insertOperation(Operator oper)
		{
			insertCheck(oper);
		}

		public void constructOperation(String symbol, Func<String, String, String> function)
		{
			insertCheck
			(
				new Operator() 
				{
					value = symbol,
					operation = (expression) =>
					{
						String nodeL = (expression.hasNodeL()) ? envir.solveExpression(expression.getNodeL()) : null;
						String nodeR = (expression.hasNodeR()) ? envir.solveExpression(expression.getNodeR()) : null;
						return function(nodeL, nodeR);
					}
				}
			);
		}

		public void constructOperation_HigherOrder(String symbol, Func<Expression, Expression, String> function)
		{
			insertCheck
			(
				new Operator()
				{
					value = symbol,
					operation = (expression) =>
					{
						Expression nodeL = (expression.hasNodeL()) ? expression.getNodeL() : null;
						Expression nodeR = (expression.hasNodeR()) ? expression.getNodeR() : null;
						return function(nodeL, nodeR);
					}
				}
			);
		}
	
		private void insertCheck(Operator oper)
		{
				envir.operList.Add(oper);
		}

		private List<String> powerSet(String s)
		{
			int l = s.Length;
			List<String> output = new List<String>();
			for (int i = 1; i <= l; i++)
			{
				for (int j = 0; j + i <= l; j++)
				{
					output.Add(s.Substring(j, i));
				}
			}
			return output;
		}

		public static bool isNull(String str) 
		{
			return str == null;
		}

		public static Regex regexN = new Regex(@"^[0-9]+$");
		public static bool isNumeric(String str)
		{
			if (isNull(str)) return false;

			return regexN.Match(str).Success;
		}
		public static Regex regexF = new Regex(@"^[0-9][0-9,\.]+$");
		public static bool isNumericFloat(String str)
		{
			if (isNull(str)) return false;
			if (isNumeric(str)) return true;
			return regexF.Match(str).Success;
		}

		virtual public void loadLibrary() 
		{
		}

		virtual public String getName()	
		{
			return null;
		}
	}

	class JupEnvironment
	{

		public void loadLibrary(Library library)
		{
			try
			{
				library.loadLibrary();
				Console.WriteLine(" - Finish loading Juptiar Library - " + library.getName());
			}
			catch (Exception e) { Console.WriteLine(e); }
		}

		public Expression toExpression(String expression)
		{
			if (expression == null || expression.Equals("")) return null; // null check
			String exp = trim(expression).Replace(" ", "").Replace("\t", "");
			int floor;
			
			foreach (Operator oper in operList)
			{
				String value = oper.getValue();
				if (!exp.Contains(value)) continue;
				floor = 0;
				int range = exp.Length - (value.Length - 1);
				
				for (int i = 0; i < range; i++)
				{
					if (exp[i] == ')') floor--;
					if (floor != 0) continue;
					if (exp[i] == '(') floor++;
					
					if (exp.Substring(i, value.Length).Equals(value))
						return new Expression(value,
											  toExpression(exp.Substring(0, i)),
											  toExpression(exp.Substring(i + value.Length))
											  );
				}
			}
			return new Expression(exp);
		}

		public void execute(String expression)
		{
			executeExpression(toExpression(expression));
		}

		public void run(String expression)
		{
			Console.WriteLine(solveExpression(toExpression(expression)));
		}

		public void executeExpression(Expression expression)
		{
			solveExpression(expression, false);
		}


		public String solveExpression(Expression expression)
		{
			return solveExpression(expression, false);
		}

		public String solveExpression(Expression expression, bool skipOverride)
		{
			if (expression == null) return null;

			String value = expression.getValue();

			if (!skipOverride && expression.noNode())
			{
				String matched = null;
				if (unstableMap.ContainsKey(value)) matched = solveExpression(unstableMap[expression.getValue()]);
				if (variableMap.ContainsKey(value)) matched = variableMap[value];
				if (matched != null) return matched;
			}

			foreach (Operator oper in operList)
			{
				if (oper.getValue().Equals(value)) return oper.operate(expression);
			}
			
			return value;
		}

		public Dictionary<String, String> variableMap = new Dictionary<String, String>();
		public Dictionary<String, Expression> unstableMap = new Dictionary<String, Expression>();

		public List<Operator> operList = new List<Operator>();

		public static String trim(String input)
		{
			int layer = 0;
			bool fallback = false; bool uphill = true;
			int submin = 0;
			for (int i = 0; i < input.Length; i++) {
				if (input[i] == '(') layer++;
				if (input[i] == ')') {
					if (!fallback)
					{
						fallback = true;
						submin = layer;
					}
					layer--;
				}
				if (uphill && !input.Substring(i).Contains("(")) uphill = false;
				if (fallback && uphill) submin = (submin < layer) ? submin : layer;
			}
			return trimIterate(input, submin);
		}

		public static String trimIterate(String input, int g)
		{
			if (g == 0) return input;  // iterate trim
			return (input.StartsWith("(") && input.EndsWith(")"))?
				trimIterate(input.Substring(1, input.Length - 2), g - 1) : input;
		}

		public static bool isEnclosed(String expression)
		{
			int layer = 0;
			for (int i = 0; i < expression.Length; i++)
			{
				if (expression[i] == '(') layer++;
				if (expression[i] == ')') layer--;
				if (layer < 0) return false;
			}
			return layer == 0;
		}

        public override string ToString()
        {
            return base.ToString();
        }

        public override bool Equals(object obj)
        {
            return base.Equals(obj);
        }

        public override int GetHashCode()
        {
            return base.GetHashCode();
        }
    }

	class JupPrelude : OperatorConstructor
	{

		public JupPrelude(JupEnvironment envir) : base(envir) 
		{

		}

		override public String getName()
		{
			return "JupPrelude";
		}

		override public void loadLibrary()
		{
			constructOperation_HigherOrder("?", (L, R) =>
			{
				if (R == null) return null;

				if(envir.unstableMap.ContainsKey(R.getValue()))
				{
					return envir.unstableMap[R.getValue()].express();
				}

				if (envir.variableMap.ContainsKey(R.getValue()))
				{
					return envir.variableMap[R.getValue()];
				}
				
				return R.express();
			});
			constructOperation_HigherOrder("->", (L, R) =>
			{
				if (L != null && R != null)
					if (envir.unstableMap.ContainsKey(L.getValue()))
					{
						envir.unstableMap[L.getValue()] = R;
					}
					else
					{
						envir.unstableMap.Add(L.getValue(), R);
					}
				return envir.solveExpression(L);
			});
			constructOperation_HigherOrder("!clear*", (L, R) =>
			{
				envir.variableMap = new Dictionary<String, String>();
				envir.unstableMap = new Dictionary<String, Expression>();
				return null;
			});
			constructOperation_HigherOrder("!clear", (L, R) =>
			{
				String key = envir.solveExpression(R, true);
				envir.variableMap.Remove(key);
				envir.unstableMap.Remove(key);
				return null;
			});
			constructOperation("|>", (L, R) =>
			{
				return R; 
			});
			constructOperation("<|", (L, R) => 
			{
				return L; 
			});
			constructOperation("|", (L, R) => 
			{
				return ((rand.Next() % 10) > 5) ? L : R;
			});
			constructOperation("~", (L, R) => 
			{
				if (isNumeric(L) && isNumeric(R))
					return ((int)((rand.NextDouble() % 1) * (Int32.Parse(R) - Int32.Parse(L) + 0.99) + Int32.Parse(L))).ToString();

				return L + "~" + R;
			});
			constructOperation("f+", (L, R) => 
			{
				if (isNumericFloat(L) && isNumericFloat(R)) return (double.Parse(L) + double.Parse(R)).ToString();
				return L + "f+" + R;
			});
			constructOperation("+", (L, R) => 
			{
				if (isNumeric(L) && isNumeric(R)) return (Int32.Parse(L) + Int32.Parse(R)).ToString();
				return L + "+" + R;
			});
			constructOperation("f-", (L, R) => 
			{
				if (isNumericFloat(L) && isNumericFloat(R)) return (double.Parse(L) - double.Parse(R)).ToString();
				if (isNull(L) && isNumericFloat(R)) return (-double.Parse(R)).ToString();
				return L + "f-" + R;
			});
			constructOperation("-", (L, R) => 
			{
				if (isNumeric(L) && isNumeric(R)) return (Int32.Parse(L) - Int32.Parse(R)).ToString();
				if (isNull(L) && isNumeric(R)) return (-Int32.Parse(R)).ToString();
				return L + "-" + R;
			});
			constructOperation("f*", (L, R) => 
			{
				if (isNumericFloat(L) && isNumericFloat(R)) return (double.Parse(L) * double.Parse(R)).ToString();
				return L + "f*" + R;
			});
			constructOperation("*", (L, R) => 
			{
				if (isNumeric(L) && isNumeric(R)) return (Int32.Parse(L) * Int32.Parse(R)).ToString();
				return L + "*" + R;
			});
			constructOperation("f/", (L, R) =>
			{
				if (isNumericFloat(L) && isNumericFloat(R)) return (double.Parse(L) / double.Parse(R)).ToString();
				return L + "f/" + R;
			});
			constructOperation("/", (L, R) => 
			{
				if (isNumeric(L) && isNumeric(R)) return (Int32.Parse(L) / Int32.Parse(R)).ToString();
				return L + "/" + R;
			});
			constructOperation("%", (L, R) => 
			{
				if (isNumeric(L) && isNumeric(R)) return (Int32.Parse(L) % Int32.Parse(R)).ToString();
				return L + "%" + R;
			});
			constructOperation("==", (L, R) => 
			{
				if (L == R || L.Equals(R)) return logic(true);
				return logic(false);
			});
			constructOperation("!=", (L, R) => 
			{
				if (L == null && R == null) return logic(false);
				if (L == null || R == null) return logic(true);
				if (!L.Equals(R)) return logic(true);
				return logic(false);
			});
			constructOperation("<", (L, R) => 
			{
				if (isNumeric(L) && isNumeric(R)) return Int32.Parse(L) < Int32.Parse(R) ? logic(true) : logic(false);
				return L + "<" + R;
			});
			constructOperation(">=", (L, R) => 
			{
				if (isNumeric(L) && isNumeric(R)) return Int32.Parse(L) >= Int32.Parse(R) ? logic(true) : logic(false);
				return L + ">=" + R;
			});
			constructOperation("<=", (L, R) => 
			{
				if (isNumeric(L) && isNumeric(R)) return Int32.Parse(L) <= Int32.Parse(R) ? logic(true) : logic(false);
				return L + "<=" + R;
			});
			constructOperation_HigherOrder("=>", (L, R) => 
			{
				if (L == null) return null;
				if (!envir.solveExpression(L).Equals("0")) return envir.solveExpression(R.getNodeL());
				return envir.solveExpression(R.getNodeR());
			});
			constructOperation(">", (L, R) =>
			{
				if (isNumeric(L) && isNumeric(R)) return Int32.Parse(L) > Int32.Parse(R) ? logic(true) : logic(false);
				return L + ">" + R;
			});
			constructOperation_HigherOrder("=", (L, R) =>
			{
				if (L != null && R != null)
					if (envir.variableMap.ContainsKey(envir.solveExpression(L, true)))
					{
						envir.variableMap[envir.solveExpression(L, true)] = envir.solveExpression(R);
					}
					else 
					{
						envir.variableMap.Add(envir.solveExpression(L, true), envir.solveExpression(R));
					}
					
				return envir.solveExpression(L);
			});
			constructOperation_HigherOrder("[^]", (L, R) => 
			{
				if (L == null) return envir.solveExpression(R);
				String l = envir.solveExpression(L);
				String acc = envir.solveExpression(R.getNodeL());
				if (isNumeric(l))
					for (int i = 0; i < Int32.Parse(l); i++)
						acc = envir.solveExpression(envir.toExpression(acc + R.getValue() + envir.solveExpression(R.getNodeR())));
				return acc;
			});
			constructOperation(":", (L, R) => 
			{
				return L + ":" + R;
			});
		}

		Random rand = new Random();

		private String logic(bool b) {
			return (b)? "1" : "0";
		}
	}

}
