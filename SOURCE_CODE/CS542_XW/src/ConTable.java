
class ConTable {
	private TopologyMatrix mTopologyMatrix;
	private int mRouter;
	
	private void print(String content) 
	{
		System.out.print(content);
	}
	
	private void initDijkstra() 
	{
		mTopologyMatrix.dijkstraInit(mRouter);
	}
	
	public boolean removeNode(int router) 
	{
		if(mTopologyMatrix.getMatrixSize() >= router && router > 0 && mTopologyMatrix.checkStatus(router)) {
			mTopologyMatrix.removeNode(router);
			if(chooseRouter(mRouter, mTopologyMatrix)) {
				showConTable();
			}
			return true;
		}
		return false;
	}
	
	public boolean chooseRouter(int router, TopologyMatrix matrix) 
	{
		mTopologyMatrix = matrix;
		if(mTopologyMatrix.getMatrixSize() >= router && router > 0 && mTopologyMatrix.checkStatus(router)) {
			mRouter = router;
			initDijkstra();
			return true;
		}
		return false;
	}
		
	public void showConTable() 
	{
		print("\nRouter " + mRouter + " Connection Table\n");
		print("Destination	Interface\n");
		print("=========================");
		mTopologyMatrix.printInterfaceInfo();
	}
	
	public void showPath(int desNode) 
	{
		if(mTopologyMatrix.checkStatus(desNode)) {
			int cos =  mTopologyMatrix.getShorestCost(desNode);
			String path = mTopologyMatrix.getPathInfo(desNode);
			print("The shortest path from router "+ mRouter +" to router " + desNode +" is "+path+", the total cost is "+cos);
		}
		else {
			print("\ninvalide router selected");
		}
	}
}
