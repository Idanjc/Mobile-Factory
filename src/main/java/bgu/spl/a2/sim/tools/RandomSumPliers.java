package bgu.spl.a2.sim.tools;

import java.util.Random;

import bgu.spl.a2.sim.Product;

public class RandomSumPliers implements Tool {
	/**
	 * Return the type of the tool
	 * @return String representing the tool type
	 */
	@Override
	public String getType() {
		return "rs-pliers";
	}

	/**
	 * Use the tool on a given product's parts 
	 * @param p - the product to use the tool on it's parts
	 * @return long with the value of the use of the tool on product p
	 */
	public long useOn(Product p){
    	long value=0;
    	for(Product part : p.getParts()){
    		value+=Math.abs(func(part.getFinalId()));
    		
    	}
      return value;
    }
	/**
	 * Give a random value to add
	 * @param id - the final id of the part
	 * @return long representing the random number
	 */
	private long func(long id){
    	Random r = new Random(id);
        long  sum = 0;
        for (long i = 0; i < id % 10000; i++) {
            sum += r.nextInt();
        }

        return sum;
    
	}
}
