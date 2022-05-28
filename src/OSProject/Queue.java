package OSProject;

public class Queue {
	private int maxsize;
	private int front;
	private int rear;
	private int nItems;
	private Program [] elements;

	public Queue(int maxSize) 
	{
		this.maxsize = maxSize;
		front = 0;
		rear = -1;
		nItems = 0;
		elements = new Program[maxsize];
	}

	public void enqueue(Program x)
	{
		if(rear == maxsize - 1)
			rear = -1;

		elements[++rear] = x;
		nItems++;
	}

	public Program dequeue()
	{
		Program result = elements[front];
		front++;

		if(front == maxsize)
			front = 0;

		nItems--;
		return result;
	}

	public Program peek()
	{
		return elements[front];
	}

	public boolean isEmpty()
	{
		return (nItems == 0);
	}

	public boolean isFull()
	{
		return (nItems == maxsize);
	}

	public int size()
	{
		return nItems;
	}

	public void printQueue() {
		if(nItems == 0){
			System.out.println("The queue is empty!");
			return;
		}
		for (int i = 0;i < nItems;i++) {
			System.out.print(elements[(front + i)%maxsize].programId + "  ");
		}
		System.out.println();
	}
}
