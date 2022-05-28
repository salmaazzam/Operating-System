package OSProject;

public class Mutex {
	boolean available;
	int programId;
	public Mutex(boolean t,int x) {
		available=t;
		programId=x;
		
	}
}
