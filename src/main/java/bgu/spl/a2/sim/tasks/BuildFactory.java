package bgu.spl.a2.sim.tasks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import com.google.gson.internal.LinkedTreeMap;

import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;


/**
 * Class BuildFactory represents a Task that holds value ConcurrentLinkedQueue<Product>
 * Builds a factory that creates all the products in a wave of orders
 * The value ConcurrentLinkedQueue<Product> contains all products manufactured in this wave
 *
 */
public class BuildFactory extends Task<ConcurrentLinkedQueue<Product>> {

	private ArrayList<?> wave;
	private Warehouse warehouse;
	private ConcurrentLinkedQueue<Product> result= new ConcurrentLinkedQueue<Product>();
	private CountDownLatch cdl;
	
	/**
	 * Constructor
	 * @param currentWave - a wave of orders
	 * @param warehouse - an already built warehouse
	 * @param cdl - an already initialized CountDownLatch
	 */
	public BuildFactory(ArrayList<?> currentWave, Warehouse warehouse, CountDownLatch cdl){
		wave = currentWave;
		this.warehouse = warehouse;	
		this.cdl = cdl;
	}

	
	@Override
	/**
	 * Start to produce a wave of products
	 */
	protected void start() {
		LinkedList<Manufacture> manufacture = new LinkedList<Manufacture>();
		for (int i=0;i<wave.size();i++){
			//Create a task for a product in the wave, according to the required qty
			LinkedTreeMap<?,?> currentProduct = (LinkedTreeMap<?,?>)wave.get(i);
			String product = (String)currentProduct.get("product");
			long startId = ((Double)(currentProduct.get("startId"))).longValue();
			int qty = ((Double)(currentProduct.get("qty"))).intValue();
			for (int j=0;j<qty;j++){
				Manufacture temp = new Manufacture(warehouse.getPlan(product), startId+j, warehouse);
				manufacture.add(temp);
				spawn(temp);
			}
		}
		whenResolved(manufacture, new Runnable() { // Add the finished products to the result
			public void run() {
				for (Manufacture manufacture : manufacture) {
					result.add(manufacture.getResult().get());
				}
				complete(result);
				cdl.countDown();
			}
		});
	}
	
}
