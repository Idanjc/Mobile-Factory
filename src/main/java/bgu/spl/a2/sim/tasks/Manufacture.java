package bgu.spl.a2.sim.tasks;

import java.util.Arrays;

import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.conf.ManufactoringPlan;

/**
 * 
 * Class Manufacture is a Task that holds the value Product
 * Manufacture will produce the product, and store it as value.
 * In the process, Manufacture will create all the parts needed for the product to be produced
 *
 */
public class Manufacture extends Task<Product> {

	private Product finalProduct;
	private ManufactoringPlan plan;
	private long startId;
	private Warehouse warehouse;
	
	/**
	 * Constructor
	 * @param plan - the plan to manufacature
	 * @param startId - the start id of the product to build
	 * @param warehouse - an initialized warehouse
	 */
	public Manufacture(ManufactoringPlan plan, long startId, Warehouse warehouse) {
		finalProduct = new Product(startId, plan.getProductName());
		this.startId = startId;
		this.plan = plan;
		this.warehouse = warehouse;
	}
	
	@Override
	/**
	 * Start the Task, creating a product to hold as value
	 */
	protected void start() {
		String[] parts = plan.getParts();
		Manufacture[] manufactureTasks = new Manufacture[parts.length];
		String[] toolList = plan.getTools();
		for (int i = 0; i < parts.length; i++) { // creates an array of manufacture tasks - one for every part
			manufactureTasks[i]= new Manufacture(warehouse.getPlan(parts[i]), startId+1, warehouse);
			spawn (manufactureTasks[i]);
		}
		/*
		 * A Runnable callback to acquire all the tools in order to finish a product.
		 * It is called once all the manufacturing tasks are resolved
		 */
		Runnable acquireTools = new Runnable() { 
			public void run() {
				for(int i=0; i<manufactureTasks.length; i++){// Adding all the parts to the product's parts list
					finalProduct.addPart(manufactureTasks[i].getResult().get());
				}
				Worker[] workers = new Worker[toolList.length];
				for (int j = 0; j < toolList.length; j++) { // Create workers that attempt to acquire the tools and use them on the product
					workers[j] = new Worker(finalProduct, toolList[j], warehouse);
					spawn (workers[j]);
				}
				/*
				 *  A Runnable callback to finish the product. The callback calculates the products final id and creates it
				 *  It is called once all workers tasks are resolved
				 */
				Runnable createFinalId = new Runnable() { 
					public void run() {
						long finalId = startId;
						for(int j=0; j<toolList.length; j++){
							finalId += (workers[j].getResult().get());
						}
						finalProduct.setId(finalId);
						complete(finalProduct);
					}
				};
				
				if (workers.length==0)
					createFinalId.run();
				else
					whenResolved(Arrays.asList(workers), createFinalId);
			}
		};
		
		if (manufactureTasks.length==0)
			acquireTools.run();
		else
			whenResolved(Arrays.asList(manufactureTasks), acquireTools);
	}


}
