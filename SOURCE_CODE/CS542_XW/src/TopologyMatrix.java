import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class TopologyMatrix 
{
	private BufferedReader mBufferedReader;
	private int mTopoMarix[][];													//array to store matrix
	private boolean mRtrStatus[];												//statu of a router
	private ArrayList<Router> mRouterNode = new ArrayList<Router>();
	
	private Map<Integer,Integer> mPathMap = new HashMap<Integer,Integer>(); 	//distance info <des id, dis>  
    private Map<Integer,String> mInterfaceMap = new HashMap<Integer,String>(); 	//node info	<des id, interface info>
    private Map<Integer,String> mPathInfoMap = new HashMap<Integer,String>(); 	//node info	<des id, path info>
    
    Set<Router> S = new HashSet<Router>();  
    Set<Router> U = new HashSet<Router>(); 
	
	private void print(String content) 
	{
		System.out.print(content);
	}
	
	//--------------------test func, print distance from node to node---------------------
	
//	private void printNode() 
//	{
//		for(int i = 0, n = mRouterNode.size(); i < n; i++) {
//			Map<Router,Integer> temp = mRouterNode.get(i).getConnRouter();
//			for(int j = 0, m = mRouterNode.size(); j < m; j++) {
//				int tempDis = temp.get(mRouterNode.get(j));
//				int ori = i + 1;
//				int dst = j + 1;
//				print("\nNode: "+ ori +" to Node: "+ dst +"  distance = " + tempDis);
//			}
//		}
//	}
	
	//----init router info: their connected router and distance,  add them in map of router--
	
	private void initTopologyNode()
	{
		int size = getMatrixSize();
		for(int i = 1; i <= size; i++) {
			mRouterNode.add(new Router(i));
		}	
		for(int row = 0; row < size; row++) {
			for(int col = 0; col < size; col++) {
				mRouterNode.get(row).setConnRouter(mRouterNode.get(col), mTopoMarix[row][col]);
			}
		}
	//		printNode();
	}
	
	//----------------------transfer string in file into int array-----------------------
	
	private void transferMatrix(ArrayList<String> line) 
	{
		int len = line.size();
		mTopoMarix = new int[len][len];
		mRtrStatus = new boolean[len];
		for(int i = 0; i < len; i++) {	
			String[] temp = line.get(i).split("\\s+");
			for(int j = 0, n = temp.length; j < n; j++) { 
				try { mTopoMarix[i][j] = Integer.valueOf(temp[j]); } 
				catch (NumberFormatException e) { print("number format exception."); }
			}
			mRtrStatus[i] = true;
		}
		initTopologyNode();
	}
	
	//------------------- return shortest node in path dis info--------------------
	
	private Router getShortestPath(Router router) 
	{
		Router re = null;
		int minDistance = Integer.MAX_VALUE;
		for(Integer child:mPathMap.keySet()) {  
			Router temp = mRouterNode.get(child-1);
            if(U.contains(temp)) {  
                int distance = mPathMap.get(child);  
                if(distance < minDistance && distance > 0) {  
                	minDistance = distance;  
                    re = temp;  
                }  
            }  
        }
		
//		Map<Router,Integer> connRouter = router.getConnRouter();
//		for(Router child:connRouter.keySet()) {  
//            if(U.contains(child)) {  
//                int distance = connRouter.get(child);  
//                if(distance < minDistance && distance > 0) {  
//                	minDistance = distance;  
//                    re = child;  
//                }  
//            }  
//        }
		return re;
	}
	
	//--------------------------------read string in file--------------------------------
	
	public void readFile(String filename) 
	{	
		ArrayList<String> line =new ArrayList<String>();
		try {  
		    String read = null;
			mBufferedReader = new BufferedReader( new FileReader(filename) );
		    while((read = mBufferedReader.readLine()) != null) {
		    	line.add(read);
		    }  
		}
		catch(Exception e) {  
			print("invalide file name.");
		    return; 
		} 
		try {  mBufferedReader.close(); }
		catch(IOException e) { print("file close io error."); }	
		transferMatrix(line);
	}
	
	//--------------------------------first step of dijkstra algorithm--------------------------------
	
	public void dijkstraInit(int node)
	{
		int n = getMatrixSize();
		for(int i = 0; i < n; i++) {
			if(!mRtrStatus[i])										//check node sill active
				continue;
			int tempNode = i + 1;
			
			mPathMap.put(tempNode, mTopoMarix[node-1][i]);			//init path info and distance info
			if(i == node - 1 || mTopoMarix[node-1][i] == -1) {
				mInterfaceMap.put(tempNode, "		-");
				mPathInfoMap.put(tempNode, node + "->");
			}	
			else {
				mInterfaceMap.put(tempNode, "		"+tempNode);
				mPathInfoMap.put(tempNode, node + "->"+tempNode);
			}
																	//init node map info
			if(i == node - 1)
				S.add(mRouterNode.get(i));
			else 
				U.add(mRouterNode.get(i));
		}	
		dijkstraCompute(mRouterNode.get(node-1));
	}
	
	//--------- put the nearest node into set S, and remove form the U, and re-cal the distance----------
	
	public void dijkstraCompute(Router node) 
	{
		Router nearest = getShortestPath(node);
		if(nearest == null)
			return;
		S.add(nearest);
		U.remove(nearest);
		Map<Router,Integer> connRouter = nearest.getConnRouter();
		for(Router child:connRouter.keySet()) {  
            if(U.contains(child) && connRouter.get(child) > 0) { 
            	Integer newDis = mPathMap.get(nearest.getId()) + connRouter.get(child);  		
                if(mPathMap.get(child.getId()) > newDis || mPathMap.get(child.getId()) < 0) {
                	mPathMap.put(child.getId(), newDis);  													
                	mInterfaceMap.put(child.getId(), mInterfaceMap.get(nearest.getId()));      
                	mPathInfoMap.put(child.getId(), mPathInfoMap.get(nearest.getId())+"->"+child.getId());
                }	  
            }  
        }
		dijkstraCompute(node);
	}
	
	public boolean removeNode(int node) 
	{
		int size = getMatrixSize();
		if(size >= node && node > 0 && checkStatus(node)) {
			for(int i = 1; i <= size; i++) {
				if(i == node) {
					mRouterNode.get(i-1).removeConnRoter(mRouterNode.get(node-1));			//remove form the map of router
					mRtrStatus[i-1] = false;												//set router status
				}		
			}
			return true;
		}
		return false;
	}
	
	public void printMarix() 
	{	
		print("\nReview original topology matrix:");
		int len = mTopoMarix[0].length;
		for(int i = 0; i < len; i++) {
			for(int j = 0; j < len; j++) {
				if(j % len == 0) 
					print("\n");
				print(mTopoMarix[i][j] + "  ");
			}
		}
		print("\n");
	}
	
	public void printInterfaceInfo() 
	{
		for(int i = 1, n = getMatrixSize(); i <= n; i++) {
			if(!mRtrStatus[i-1])
				continue;
			print("\n"+ i +mInterfaceMap.get(i));
		}		
	}
	
	public String getPathInfo(int desNode) 
	{
		return mPathInfoMap.get(desNode);
	}
	
	public int getShorestCost(int desNode)
	{
		return mPathMap.get(desNode);
	}
	
	public void printDisInfo() {
		for(int i = 1, n = getMatrixSize(); i <= n; i++) {
			if(!mRtrStatus[i-1])
				continue;
			print("\ndistance to "+ i +" : "+mPathMap.get(i));
		}
	}
	
	public int getMatrixSize() 
	{
		return mTopoMarix.length;
	}
	
	public int getNodeSize()
	{
		return mRouterNode.size();
	}
	
	public boolean checkStatus(int node)
	{
		return mRtrStatus[node-1];
	}
}
