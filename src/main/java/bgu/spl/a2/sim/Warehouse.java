
package bgu.spl.a2.sim;

import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import bgu.spl.a2.sim.tools.Tool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.a2.Deferred;

/**
 * A class representing the warehouse in your simulation
 * 
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 */
public class Warehouse {


	private ConcurrentLinkedQueue<RandomSumPliers> plierQueue = new ConcurrentLinkedQueue<RandomSumPliers>();
	private ConcurrentLinkedQueue<NextPrimeHammer> hammerQueue = new ConcurrentLinkedQueue<NextPrimeHammer>();
	private ConcurrentLinkedQueue<GcdScrewDriver> driverQueue = new ConcurrentLinkedQueue<GcdScrewDriver>();
	
	private ConcurrentLinkedQueue<Deferred<Tool>> plierWaitingList = new ConcurrentLinkedQueue<Deferred<Tool>>();
	private ConcurrentLinkedQueue<Deferred<Tool>> hammerWaitingList = new ConcurrentLinkedQueue<Deferred<Tool>>();
	private ConcurrentLinkedQueue<Deferred<Tool>> driverWaitingList = new ConcurrentLinkedQueue<Deferred<Tool>>();
	
	private LinkedList<ManufactoringPlan> plans = new LinkedList<ManufactoringPlan>();
	
	/**
	* Tool acquisition procedure
	* Note that this procedure is non-blocking and should return immediatly
	* @param type - string describing the required tool
	* @return a deferred promise for the  requested tool
	*/
    public Deferred<Tool> acquireTool(String type){
    	Deferred<Tool> tool = new Deferred<Tool>();
    	switch (type){
    	case "rs-pliers":
    		checkIfAvailable(tool, plierQueue, plierWaitingList);
    		break;
    	case "np-hammer":
    		checkIfAvailable(tool, hammerQueue, hammerWaitingList);
    		break;
    	case "gs-driver":
    		checkIfAvailable(tool, driverQueue, driverWaitingList);
    		break;
    	default: throw new UnsupportedOperationException("No such type of tool");
    	}
    	return tool;
    }
    /**
     * Check if a tool is available to use. If not, add it to a waiting list
     * @param tool - the tool to check
     * @param toolQueue - the queue the tool is stored in
     * @param waitingList - the waiting list for the tool if he is unavailable
     */
    private synchronized void checkIfAvailable(Deferred<Tool> tool, ConcurrentLinkedQueue<?> toolQueue, ConcurrentLinkedQueue<Deferred<Tool>> waitingList){
    	if (!toolQueue.isEmpty()){
    		tool.resolve((Tool)toolQueue.poll());
    	}
    	else{
    		waitingList.add(tool);
    	}
    }

	/**
	* Tool return procedure - releases a tool which becomes available in the warehouse upon completion.
	* @param tool - The tool to be returned
	*/
    public void releaseTool(Tool tool){
    	addTool(tool, 1);
    }

	
	/**
	* Getter for ManufactoringPlans
	* @param product - a string with the product name for which a ManufactoringPlan is desired
	* @return A ManufactoringPlan for product
	*/
    public ManufactoringPlan getPlan(String product){
    	for (ManufactoringPlan manufactoringPlan : plans) {
    		if (manufactoringPlan.getProductName().equals(product))
    			return manufactoringPlan;				
		}
    	throw new UnsupportedOperationException("No such manufactoring plan exists");
    }
	
	/**
	* Store a ManufactoringPlan in the warehouse for later retrieval
	* @param plan - a ManufactoringPlan to be stored
	*/
    public synchronized void addPlan(ManufactoringPlan plan){
    	plans.add(plan);
    }
    
	/**
	* Store a qty Amount of tools of type tool in the warehouse for later retrieval
	* @param tool - type of tool to be stored
	* @param qty - amount of tools of type tool to be stored
	*/
    public void addTool(Tool tool, int qty){
    	String name = tool.getType();
    	switch (name){
    	case "rs-pliers":
    		addRandomSumPliers(qty);
    		releaseFromWaitingList(tool, plierWaitingList);
    		break;
    	case "np-hammer":
    		addNextPrimeHammer(qty);
    		releaseFromWaitingList(tool, hammerWaitingList);
    		break;
    	case "gs-driver":
    		addGcdScrewDriver(qty);
    		releaseFromWaitingList(tool, driverWaitingList);
    		break;
    	default: throw new UnsupportedOperationException("No such type of tool");
    	}
    }
    
    /**
     * Release a tool from it's waiting list
     * @param tool - the tool to release
     * @param waitingList - the waiting list to release the tool from
     */
    private synchronized void releaseFromWaitingList(Tool tool, ConcurrentLinkedQueue<Deferred<Tool>> waitingList){
    	if (!waitingList.isEmpty())
    		waitingList.poll().resolve(tool);
    }
    /*
     *  Next functions all do the same for the different tools queues:
     *  Add a tool to it's queue of available tools
     * @param qty - amount of tools to add
     */
    private void addRandomSumPliers(int qty){
    	for (int i=0;i<qty;i++)
    		plierQueue.add(new RandomSumPliers());
    }
    private void addNextPrimeHammer(int qty){
    	for (int i=0;i<qty;i++)
    		hammerQueue.add(new NextPrimeHammer());
    }
    private void addGcdScrewDriver(int qty){
    	for (int i=0;i<qty;i++)
    		driverQueue.add(new GcdScrewDriver());
    }
}
