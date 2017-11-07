package bgu.spl.a2.sim.tasks;



import bgu.spl.a2.Deferred;
import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.tools.Tool;

/**
 * 
 * Class Worker that represents a Task that holds a Long value.
 * A worker task uses the tools on the products 
 * The Long value held is the result of the tool being used on a product
 */
public class Worker extends Task<Long> {

	private Product product;
	private String toolName;
	private Warehouse warehouse;
	/**
	 * Constructor
	 * @param part
	 * @param toolName
	 * @param warehouse
	 */
	public Worker(Product part, String toolName, Warehouse warehouse){
		this.product = part;
		this.toolName = toolName;
		this.warehouse = warehouse;
	}
	
	@Override
	/**
	 * Start method of the worker task
	 * This method will use a tool on a product
	 */
	protected void start() {
		Deferred<Tool> deferredTool = warehouse.acquireTool(toolName);
		deferredTool.whenResolved(new Runnable() {	
			@Override
			public synchronized void run() {
				Tool tool = deferredTool.get();
				Long result = new Long(tool.useOn(product));
				warehouse.releaseTool(tool);
				complete(result);
			}
		});
	}
	
}
