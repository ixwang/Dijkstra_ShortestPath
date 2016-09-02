import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

class Driver 
{
	private Scanner mScanner;
	private BufferedReader mBufferedReader; 
	private TopologyMatrix mTopoMarix = null;
	private ConTable mConTable = null;
	private ConTable[] mConTableArray = null;
	Driver()
	{
		mBufferedReader = new BufferedReader(new InputStreamReader(System.in));
		mScanner = new Scanner(System.in);
	}
	
	private void print(String content) 
	{
		System.out.print(content);
	}
	
	//  Menu
	public void menu()
	{
		// Print menu
		print("\n\nCS542 Link State Routing Simulator \n\n");
		print("(1) Create a Network Topology \n");
		print("(2) Build a Connection Table \n");
		print("(3) Shortest Path to Destination Router \n");
		print("(4) Modify a topology \n");
		print("(5) Exit \n");
		print("-------------Extra func:-------------\n");
		print("(6)show connection table for each node\n");
		print("(7)choose source node\n\n");
		print("Command:\n");
		// Receive command input
		int command = mScanner.nextInt();
		switch(command) {
			case 1: command1(); break;
			case 2: command2(); break;
			case 3: command3(); break;
			case 4: command4(); break;
			case 5: command5(); break;
			case 6: command6(); break;
			case 7: command7(); break;
			default: print("Invalid input."); menu(); break;
		}
	}	
	
	//command 1 action
	public void command1()
	{
		print("Input original network topology matix data file: \n");
		String filename = null;
		try { filename = mBufferedReader.readLine(); }
		catch(IOException e) { print("I/O error.\n"); } 
		mTopoMarix = new TopologyMatrix();
		mTopoMarix.readFile(filename);
		mTopoMarix.printMarix();
		
		//----------------------record default connect tion table--------
		int n = mTopoMarix.getMatrixSize();
		mConTableArray = new ConTable[n];
		for(int i = 0; i < n; i++) {
			mConTableArray[i] = null;
		}
		menu();
	}
	
	//command 2 action
	public void command2()
	{
		if(mTopoMarix == null){
			print("please create a topology first (command 1)\n");
		}
		else {
			print("Select a source router: \n");
			int router = mScanner.nextInt();
			mConTable = new ConTable();
			if(mConTable.chooseRouter(router, mTopoMarix)) {
				mConTable.showConTable();
			}
			else {
				mConTable = null;
				print("invalide router selected \n");
			}
		}	
		menu();
	}
	
	//command 3 action
	public void command3()
	{
		if(mTopoMarix == null){
			print("please create a topology first (command 1)\n");
		}
		else {
			if(mConTable == null) {
				print("please build a connection table first (command 2)\n");
			}
			else {
				print("Select the destination router: \n");
				int router = mScanner.nextInt();
				mConTable.showPath(router);
			}
		}
		menu();
	}
	
	//command 4 action
	public void command4()
	{	
		if(mTopoMarix == null){
			print("please create a topology first (command 1)\n");
		}
		else {
			print("Select a router to be removed: \n");
			int router = mScanner.nextInt();
			if(mConTable == null) {
				if(!mTopoMarix.removeNode(router)) {
					print("invalide router selected \n");
				}
			}
			else {
				if(!mConTable.removeNode(router)) {
					print("invalide router selected \n");
				}
			}
		}
		menu();
	}
	
	//command 5 action
	public void command5()
	{
		print("Exit CS542 project.\n");
	}
	
	//extra fucntion Create a connection table of each node as default and display all
	public void command6()
	{
		if(mTopoMarix == null){
			print("please create a topology first (command 1)\n");
		}
		else {
			int n = mTopoMarix.getMatrixSize();
			for(int i = 0; i < n; i++) {
				mConTableArray[i] = new ConTable();
				if(mConTableArray[i].chooseRouter(i+1, mTopoMarix)) {
					mConTableArray[i].showConTable();
				}
				else {
					mConTableArray[i] = null;
					print("invalide router selected \n");
				}
			}
		}	
		menu();
	}
	
	public void command7()
	{	
		if(mTopoMarix == null){
			print("please create a topology first (command 1)\n");
		}
		else {
			print("Select a source router: \n");
			int router = mScanner.nextInt();		
			if(router > 0 && router <= mConTableArray.length) {
				if(mConTableArray[router-1] != null) {
					mConTable = mConTableArray[router-1];
					if(mConTable.chooseRouter(router, mTopoMarix)) {
						mConTable.showConTable();
					}
					else {
						mConTable = null;
						print("invalide router selected \n");
					}
				}
				else {
					print("please choose command 6 first to init all connection table \n");
				}
			}
			else {
				print("invalide router selected \n");
			}
		}
		menu();
	}
}

