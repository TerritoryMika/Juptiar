package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import operation.Environment;
import operation.lib.standard.JupLogic;
import operation.lib.standard.JupMath;
import operation.lib.standard.JupPrelude;
import operation.lib.standard.JupStatement;

public class Main {

	public static void main(String[] args) {		
		System.out.println(" - Juptiar Starting up...");
		
		loadLibrary();
		
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
			System.out.println(envir.solveExpression(envir.toExpression(input)));
			System.out.print("> ");
		}
		System.out.println("\n##### Juptiar Halted ######");
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
				envir.executeExpression(envir.toExpression(line));
			}
			
			if(mode_text) {
				String temp = line;
				while(temp.contains("{")) {
					String original = temp.substring(temp.indexOf("{"), temp.indexOf("}") + 1);
					temp = temp.replace(original, envir.solveExpression(envir.toExpression(original.substring(1, original.length() - 1))));
				}
				System.out.println(temp);
			}
			
		}
		scan.close();
		System.out.println();
	}
	
	private static void loadLibrary() {
		envir.loadLibrary(new JupPrelude(envir));
		envir.loadLibrary(new JupMath(envir));
		envir.loadLibrary(new JupLogic(envir));
		envir.loadLibrary(new JupStatement(envir));
	}
		
	public static List<String> output = new ArrayList<String>();
	public static Environment envir =new Environment();
}
