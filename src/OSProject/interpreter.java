package OSProject;
import java.io.*;
import java.util.*;
import OSProject.Process;


public class interpreter {
	Queue ready= new Queue(3);
	Queue generalblockedQueue= new Queue(3);
	Queue blockedInput= new Queue(3);
	Queue blockedOutput= new Queue(3);
	Queue blockedfileAccess= new Queue(3);
	Program[] programs=new Program[3];
	Mutex fileAccess;
	Mutex userInput;
	Mutex output;
	boolean isAssign=false;
	
	public interpreter(Program [] p) {
		programs=p;
		fileAccess=new Mutex(true,-1);
		userInput=new Mutex(true,-1);
		output=new Mutex(true,-1);
		
	}
	
	public void runAll() throws IOException {
		for(int i=0;i<programs.length;i++) {
			programs[i].filereader();
		}
		
		scheduler();
	}
	public boolean checkInstruction(Program p) {
		Process currentInstruction=null;
		for(int i=0;i<p.noofInstructions;i++){
			if(p.pc==p.instructions[i].processId){
				currentInstruction=p.instructions[i];
				break;
			}
			
		}
		String[] terms=currentInstruction.process.trim().split("\\s+");
		if(terms[0]=="assign") {
			return true;
		}
		else {
			return false;
		}

	}
	
	
	
	
	
	//pc starts at 1
	public void runProcess(Program p) throws IOException {
		//get the process i stopped at in the program w
		Process currentInstruction=null;
		for(int i=0;i<p.noofInstructions;i++){
			if(p.pc==p.instructions[i].processId){
				currentInstruction=p.instructions[i];
				break;
			}
			
		}
		
		System.out.println("current instruction running:"+currentInstruction.process);
		
		String[] terms=currentInstruction.process.trim().split("\\s+");
		int z=0;
		switch(terms[0]) {
		case "print":
			while(p.pairs[z]!=null) {
				if(p.pairs[z].var.equals(terms[1])){
					System.out.println(p.pairs[z].value);
					break;
				}
				z++;
			}
			break;
		case "assign":
				//assign b readFile a
				isAssign=true;
				if(terms[2]=="readFile") {
					String path=null;
					for(int i=0;i<p.pairs.length;i++){
						if(p.pairs[i].var.equals(terms[3])) {
							path=p.pairs[i].value;
						}

					}
					
					FileReader fr1 =new FileReader(path);
					BufferedReader br=new BufferedReader(fr1);
					StringBuffer sb=new StringBuffer();
					String result="";
					String line;
					while((line=br.readLine())!=null) {
						sb.append(line);
						sb.append("\n");
						result=result+line;
					}
					br.close();
					//assign to the location b the string in result find b first
					for(int i=0;i<p.pairs.length;i++) {
						if(p.pairs[i].var.equals(terms[1])) {
							p.pairs[i].value=result;
						}
					}
					break;	
				}
				else {
					Scanner sc= new Scanner(System.in); 
					System.out.print("Please enter a value");
					String value= sc.next();
					Pair pair=new Pair(terms[1],value);
					for(int i=0;i<p.pairs.length;i++) {
						if(p.pairs[i]==null) {
							p.pairs[i]=pair;
							break;
						}
					}
					break;
				}
				
				
			case "writeFile":
				String filepath = null;
				String value = null;
				while(p.pairs[z]!=null) {
					if(p.pairs[z].var.equals(terms[1])) {
						filepath=p.pairs[z].value;
						break;
					}
					z++;
				}
				z=0;
				while(p.pairs[z]!=null) {
					if(p.pairs[z].var.equals(terms[2])) {
						value=p.pairs[z].value;
						break;
					}
					z++;
				}
				try {
				      FileWriter myWriter = new FileWriter(filepath);
				      myWriter.write(value);
				      myWriter.close();
				    } catch (IOException e) {
				      System.out.println("An error occurred.");
				    }
				
				
				break;
				
				
			case "readFile":
				while(p.pairs[z]!=null) {
					if(p.pairs[z].var.equals(terms[1])) {
						String path=p.pairs[z].value;
						FileReader fr = new FileReader(path);
						int s;
				        while ((s = fr.read()) != -1)
				            System.out.print((char)s);
				        break;
					}
					z++;
				}
				break;

				
			case "printFromTo":
				String start="";
				String end="";
				z=0;
				while(p.pairs[z]!=null) {
					if(p.pairs[z].var.equals(terms[1])) {
						start=p.pairs[z].value;
						break;
					}
					z++;
				}
				z=0;
				while(p.pairs[z]!=null) {
					if(p.pairs[z].var.equals(terms[2])) {
						end=p.pairs[z].value;
						break;
					}
					z++;
				}
				int s=Integer.parseInt(start);
				int e=Integer.parseInt(end);
				for(int i=s; i<=e;i++) {
					System.out.println(i);
				}
				break;
				
				
				
			case "semWait":
				if(terms[1]=="userInput") {
					if(userInput.available==true) {
						userInput.available=false;
						userInput.programId=p.programId;
					}
					else {
						blockedInput.enqueue(p);
						generalblockedQueue.equals(p);
						
					}
				}
				else if (terms[1]=="file"){
					if(fileAccess.available==true) {
						fileAccess.available=false;
						fileAccess.programId=p.programId;
					}
					else {
						blockedfileAccess.enqueue(p);
						generalblockedQueue.equals(p);
					}
				}
				else {
					if(output.available==true) {
						output.available=false;
						output.programId=p.programId;
					}
					else {
						blockedOutput.enqueue(p);
						generalblockedQueue.equals(p);
					}
				}
			
				
				
				
			case "semSignal":
				if(terms[1]=="userInput") {
					if(userInput.programId==p.programId) {
						userInput.available=true;
						while(!blockedInput.isEmpty()) {
							Program remove=blockedInput.peek();
							ready.enqueue(remove);
							blockedInput.dequeue();
							for(int i=0;i<generalblockedQueue.size();i++) {
								if(generalblockedQueue.peek()==remove) {
									generalblockedQueue.dequeue();
								}
								else {
									generalblockedQueue.enqueue(generalblockedQueue.dequeue());
								}
							}
						}
						
						
						
						
					}
					else {
						System.out.println("not in control");
					}
				}
				else if (terms[1]=="file"){
					if(fileAccess.programId==p.programId) {
						fileAccess.available=true;
						while(!blockedfileAccess.isEmpty()) {
							Program remove=blockedfileAccess.peek();
							ready.enqueue(remove);
							blockedfileAccess.dequeue();
							for(int i=0;i<generalblockedQueue.size();i++) {
								if(generalblockedQueue.peek()==remove) {
									generalblockedQueue.dequeue();
								}
								else {
									generalblockedQueue.enqueue(generalblockedQueue.dequeue());
								}
							}
						}
						
						
					}
					else {
						System.out.println("not in control");
					}
				}
				else {
					if(output.programId==p.programId) {
						output.available=true;
						while(!blockedOutput.isEmpty()) {
							Program remove=blockedOutput.peek();
							ready.enqueue(remove);
							blockedOutput.dequeue();
							for(int i=0;i<generalblockedQueue.size();i++) {
								if(generalblockedQueue.peek()==remove) {
									generalblockedQueue.dequeue();
								}
								else {
									generalblockedQueue.enqueue(generalblockedQueue.dequeue());
								}
							}
						}
						
					}
					else {
						System.out.println("not in control");
					}
				}
			
			}
		
		
		p.pc++;
		if(p.pc==p.noofInstructions) {
			p.completed=true;
		}
		
		
		
		}
	
	

	
	public void scheduler() throws IOException {
		int arrivalProgram1=0;
		int arrivalProgram2=1;
		int arrivalProgram3=4;
		int timeSlice=2;
		Queue finished= new Queue(3);
		
		
		int time=0;
		if(time==arrivalProgram1) {
			ready.enqueue(programs[0]);
		}
		if(time==arrivalProgram2) {
			ready.enqueue(programs[1]);
		}
		
		if(time==arrivalProgram3) {
			ready.enqueue(programs[2]);
		}
		
		while(finished.size()!=3) {
			
			
			if(!ready.isEmpty()) {
				Program executing=ready.dequeue();
				
				
				System.out.println("executing program="+executing.programId);
				
				
				for(int i=0;i<timeSlice;i++) {
					runProcess(executing);
					
					if(isAssign==true){
					
					time++;
					if(time==arrivalProgram1) {
						ready.enqueue(programs[0]);
					}
					if(time==arrivalProgram2) {
						ready.enqueue(programs[1]);
					}
					
					if(time==arrivalProgram3) {
						ready.enqueue(programs[2]);
					}
					time++;
					i++;
					isAssign=false;
				}
					else{
				time++;}
					
					if(executing.completed==true) {
						finished.enqueue(executing);
						
						if(time==arrivalProgram1) {
							ready.enqueue(programs[0]);
						}
						if(time==arrivalProgram2) {
							ready.enqueue(programs[1]);
						}
						
						if(time==arrivalProgram3) {
							ready.enqueue(programs[2]);
						}
						
						time++;
						
						
						break;
					}
					
					if(time==arrivalProgram1) {
						ready.enqueue(programs[0]);
					}
					if(time==arrivalProgram2) {
						ready.enqueue(programs[1]);
					}
					
					if(time==arrivalProgram3) {
						ready.enqueue(programs[2]);
					}
					
					
					System.out.println("time = "+time);
					System.out.println("ready queue ");
					ready.printQueue();
					System.out.println("finished queue= ");
					finished.printQueue();
					System.out.println("blocked file access queue= ");
					blockedfileAccess.printQueue();
					System.out.println("blocked input queue= ");
					blockedInput.printQueue();
					System.out.println("blocked output= ");
					blockedOutput.printQueue();
				}
				
				
				
				
				if(executing.completed==false) {
					ready.enqueue(executing);
				}
			}
			
			else {
				time ++;
				if(time==arrivalProgram1) {
					ready.enqueue(programs[0]);
				}
				if(time==arrivalProgram2) {
					ready.enqueue(programs[1]);
				}
				
				if(time==arrivalProgram3) {
					ready.enqueue(programs[2]);
				}
				
				System.out.println("time = "+time);
				System.out.println("ready queue ");
				ready.printQueue();
				System.out.println("finished queue= ");
				finished.printQueue();
				System.out.println("blocked file access queue= ");
				blockedfileAccess.printQueue();
				System.out.println("blocked input queue= ");
				blockedInput.printQueue();
				System.out.println("blocked output= ");
				blockedOutput.printQueue();
			}
			
	
		}
		System.out.println(time);
	
	}
	public static void main(String[] args) throws IOException {

		Program program1=new Program("C:\\Users\\DELL\\Desktop\\OS_22_Project\\Program_1.txt");
		Program program2=new Program("C:\\Users\\DELL\\Desktop\\OS_22_Project\\Program_2.txt");
		Program program3=new Program("C:\\Users\\DELL\\Desktop\\OS_22_Project\\Program_3.txt");
		Program[] pArray=new Program[]{program1,program2, program3};
		interpreter inter=new interpreter(pArray);
		inter.runAll();
	}
	

 
}