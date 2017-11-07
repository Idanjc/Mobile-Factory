package bgu.spl.a2.sim.tools;

import java.math.BigInteger;

import bgu.spl.a2.sim.Product;

public class GcdScrewDriver implements Tool {

	@Override
	/**
	 * Return the type of the tool
	 * @return String representing the tool type
	 */
	public String getType() {
		return ("gs-driver");
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
	  * Find the gcd of a part's id and the id's reverse 
	  * @param id - the part to find the gcd of
	  * @return long that represents the gcd
	  */
	  private long func(long id){
	    	BigInteger b1 = BigInteger.valueOf(id);
	        BigInteger b2 = BigInteger.valueOf(reverse(id));
	        long value= (b1.gcd(b2)).longValue();
	        return value;
	  }
	  /**
	   * Reverse a number
	   * @param n - the number to reverse
	   * @return long that represents the reversed number
	   */
	  private long reverse(long n){
	    long reverse=0;
	    while( n != 0 ){
	        reverse = reverse * 10;
	        reverse = reverse + n%10;
	        n = n/10;
	    }
	    return reverse;
	  }
}
