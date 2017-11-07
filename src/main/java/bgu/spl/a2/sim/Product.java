package bgu.spl.a2.sim;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * A class that represents a product produced during the simulation.
 */
public class Product implements Serializable{
	
	private long id, startId;
	private String name;
	private List<Product> parts = new LinkedList<Product>();
	
	/**
	* Constructor 
	* @param startId - Product start id
	* @param name - Product name
	*/
    public Product(long startId, String name){
    	this.startId = startId;
    	id = startId;
    	this.name = name;
    }

	/**
	* @return The product name as a string
	*/
    public String getName(){
    	return name;
    }

	/**
	* @return The product start ID as a long. start ID should never be changed.
	*/
    public long getStartId(){
    	return startId;
    }
    
	/**
	* @return The product final ID as a long. 
	* final ID is the ID the product received as the sum of all UseOn(); 
	*/
    public long getFinalId(){ 
    	return id;
    }

	/**
	* @return Returns all parts of this product as a List of Products
	*/
    public List<Product> getParts(){
    	return parts;
    }

	/**
	* Add a new part to the product
	* @param p - part to be added as a Product object
	*/
    public void addPart(Product p){
    	parts.add(p);
    }
    /**
     * Sets a new id to the product
     * @param id - the id to set
     */
    public void setId (long id){
    	this.id = id;
    }


}
