package OSProject;
import java.util.*;

import java.io.*;

public class Program {
	private static int ID=1;
	int programId=1;
	String path;
	Process[] instructions= new Process[100];
	int noofInstructions;
	int pc;
	Pair[] pairs=new Pair[15];
	boolean completed;
	
	public Program(String path) {
		pc=1;
		this.path=path;
		programId=ID;
		ID++;
		completed=false;
		noofInstructions=0;
		
	}
	public void filereader() throws IOException {
		int x=1;
		FileReader fr1 =new FileReader(path);
		BufferedReader br=new BufferedReader(fr1);
		StringBuffer sb=new StringBuffer();
		String line;
		while((line=br.readLine())!=null) {
			sb.append(line);
			sb.append("\n");
			Process pr=new Process(line,programId,x);
			instructions[x-1]=pr;
			x++;
		}
		br.close();
		fr1.close();
		noofInstructions=x-1;
		
		
	}
	public static void main(String[] args) throws IOException{
		Program program1=new Program("C:\\Users\\DELL\\Desktop\\OS_22_Project\\Program_1.txt");
		Program program2=new Program("C:\\Users\\DELL\\Desktop\\OS_22_Project\\Program_2.txt");
		Program program3=new Program("C:\\Users\\DELL\\Desktop\\OS_22_Project\\Program_3.txt");
		program1.filereader();
		program2.filereader();
		program3.filereader();
		
		Program[] pArray=new Program[]{program1, program2, program3};
		interpreter inter=new interpreter(pArray);
		for(int i=0;i<inter.programs.length;i++) {
			inter.programs[i].filereader();
		}
		//System.out.println(inter.output.available);
		//System.out.println(inter.output.programId);
		for(int i=0;i<inter.programs[0].noofInstructions;i++) {
			inter.runProcess(inter.programs[0]);
		}
		//inter.runProcess(inter.programs[1]);
		//inter.runProcess(inter.programs[1]);
		//System.out.println(program3.noofInstructions);
		//System.out.println(program3.programId);
		//System.out.println(program3.pc);
		//System.out.println(inter.programs[1].pairs[0].value);
		//System.out.println(inter.programs[1].pairs[0].var);
		//System.out.println(inter.programs[1].pairs[1].value);
		//System.out.println(inter.programs[1].pairs[1].var);
		
		//for(int i=0;i<program1.noofInstructions;i++) {
			//System.out.println("salma");
			//System.out.println(program1.instructions[i].process);
			//System.out.println(program1.instructions[i].processId);
			//System.out.println(program1.instructions[i].programId);
		//}
		
	}
	
	
	
	
}
