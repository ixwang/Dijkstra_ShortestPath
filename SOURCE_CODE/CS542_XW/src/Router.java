import java.util.HashMap;
import java.util.Map;

class Router 
{
	private Map<Router,Integer> mConnRouter = new HashMap<Router,Integer>();
	private int mId;
	
	Router(int id)
	{
		mId = id;
	}
	
	public Map<Router,Integer> getConnRouter() 
	{
		return mConnRouter;
	}
	
	public void setConnRouter(Router router, int distance) 
	{
		mConnRouter.put(router, distance);
	}
	
	public void removeConnRoter(Router router) 
	{
		mConnRouter.remove(router);
	}
	
	public int getId() 
	{
		return mId;
	}
}	
