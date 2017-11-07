package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

public class NextPrimeHammer implements Tool{


	@Override
	/**
	 * Return the type of the tool
	 * @return String representing the tool type
	 */
	public String getType() {
		return "np-hammer";
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
	 * Give a value for a specific part
	 * @param id - the final id of the part
	 * @return long representing the use of the tool on the part
	 */
	  private long func(long id) {
	    	
	        long v =id + 1;
	        while (!isPrime(v)) {
	            v++;
	        }

	        return v;
	  }
	  
	  /**
	   * Check whether a number is a prime number
	   * @param value - the number to check
	   * @return false if value is not prime, true otherwise
	   */
	  private boolean isPrime(long value) {
		  if(value < 2) return false;
		  if(value == 2) return true;
		  long sq = (long) Math.sqrt(value);
		  for (long i = 2; i <= sq; i++) {
			  if (value % i == 0) {
				  return false;
			  }
		  }

        return true;
	  }

}
