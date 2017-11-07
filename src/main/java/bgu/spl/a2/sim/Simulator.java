/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import bgu.spl.a2.WorkStealingThreadPool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tasks.BuildFactory;
import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;


/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {
	
	private static WorkStealingThreadPool threadPool;
	private static Warehouse warehouse;
	private static ConcurrentLinkedQueue<Product> result = new ConcurrentLinkedQueue<Product>();
	// Fields from the Json parsing
	private static int numOfThreads;
	private static ArrayList<?> tools;
	private static ArrayList<?> plans;
	private static ArrayList<?> waves;

		
	
	/**
	* Begin the simulation
	* Should not be called before attachWorkStealingThreadPool()
	*/
	
    public static ConcurrentLinkedQueue<Product> start(){
    	try{  		
    		threadPool.start();
        	for (int i=0;i<waves.size();i++){ // Produce all the waves
        		CountDownLatch cdl = new CountDownLatch(1);
        		BuildFactory currentWave = new BuildFactory((ArrayList<?>)waves.get(i), warehouse, cdl);// Build the current wave
        		threadPool.submit(currentWave);
        		cdl.await(); // Wait until the wave is completed fully
        		ConcurrentLinkedQueue<Product> waveProducts = currentWave.getResult().get();
        		for (Product product : waveProducts) 
					result.add(product);
				}
        	threadPool.shutdown();
        	}
    	catch(InterruptedException e){
    		System.out.println("Please stop, it hurts");
    	}
    	return result;    		
    }
	
	/**
	* attach a WorkStealingThreadPool to the Simulator, this WorkStealingThreadPool will be used to run the simulation
	* @param myWorkStealingThreadPool - the WorkStealingThreadPool which will be used by the simulator
	*/
	public static void attachWorkStealingThreadPool(WorkStealingThreadPool myWorkStealingThreadPool){
		threadPool = myWorkStealingThreadPool;
	}
	
	public static void main(String [] args){
		try (FileOutputStream fout = new FileOutputStream("result.ser"); ObjectOutputStream oos= new ObjectOutputStream(fout);){
			parseJsonFile(args[0]);
			WorkStealingThreadPool pool = new WorkStealingThreadPool(numOfThreads);
			attachWorkStealingThreadPool(pool);
			warehouse = buildWarehouse();
			ConcurrentLinkedQueue<Product> result = start();			
			oos.writeObject(result);
		} catch (FileNotFoundException e) {
			System.out.println("found not file!!! D:");
		}
		catch(IOException e){
			System.out.println("I don't know what we did, we should not have gotten this");
		}			
	}
	
	/**
	 * Parse a json file, and get the fields needed for the simulation
	 * @param json - the json file to read from
	 * @throws FileNotFoundException
	 */
	private static void parseJsonFile(String json) throws FileNotFoundException{
		Gson gson = new Gson();
		FileReader reader = new FileReader(json);
		HashMap<String, ?> simulatorHash = gson.fromJson(reader,HashMap.class);
		numOfThreads =  ((Double)simulatorHash.get("threads")).intValue();
		tools = (ArrayList<?>)simulatorHash.get("tools");
		plans = (ArrayList<?>) simulatorHash.get("plans");
		waves = (ArrayList<?>) simulatorHash.get("waves");
	}
	
	/**
	 * Build a warehouse.
	 * The warehouse receives the plans and tools from the json file parsed
	 * @return a new warehouse filled with plans and tools from the json file
	 */
	private static Warehouse buildWarehouse(){
		Warehouse warehouse = new Warehouse();
		addToolsToWarehouse(warehouse);
		addPlansToWarehouse(warehouse);
		return warehouse;
	}
	
	/**
	 * Add the tools from the json file to the warehouse
	 * @param warehouse - the warehouse to add tools to
	 */
	private static void addToolsToWarehouse(Warehouse warehouse){
		for (int i=0;i<tools.size();i++){
			LinkedTreeMap<?,?> tool = (LinkedTreeMap<?,?>) tools.get(i);
			String toolType = (String) tool.get("tool");
			if (toolType.equals("gs-driver")) // Check which tool we need to add, and add the qty amount of it to the warehouse
				warehouse.addTool(new GcdScrewDriver(), ((Double)tool.get("qty")).intValue());
			else if (toolType.equals("np-hammer"))
				warehouse.addTool(new NextPrimeHammer(), ((Double)tool.get("qty")).intValue());
			else if (toolType.equals("rs-pliers"))
				warehouse.addTool(new RandomSumPliers(), ((Double)tool.get("qty")).intValue());
			else throw new UnsupportedOperationException("No such tool exists");
		}
	}
	/**
	 * Add the plans from the json file to the warehouse
	 * @param warehouse - the warehouse to add tools to
	 */
	private static void addPlansToWarehouse(Warehouse warehouse){
		for (int i=0;i<plans.size();i++){
			ManufactoringPlan manufactoringPlan = createManufactoringPlan(i);//Create each plan
			warehouse.addPlan(manufactoringPlan);						
		}
	}
	/**
	 * Create a plan from the json's plans list
	 * @param i - the number of the plan in the list
	 * @return a new ManufactoringPlan
	 */
	private static ManufactoringPlan createManufactoringPlan(int i){
		LinkedTreeMap<?,?> plan = (LinkedTreeMap<?,?>) plans.get(i);
		String product = (String)plan.get("product"); // Get the product name
		String[] toolsList = createStringArray((ArrayList<?>)plan.get("tools"));//Get the tools
		String[] partsList = createStringArray((ArrayList<?>)plan.get("parts"));//Get the parts
		return new ManufactoringPlan(product, partsList, toolsList);
	}

	private static String[] createStringArray(ArrayList<?> arrayList){
		String[] stringArray = new String[arrayList.size()];
		for (int j=0;j<arrayList.size();j++)
			stringArray[j] = (String)arrayList.get(j);
		return stringArray;
	}
}
