package bgu.spl.a2;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bgu.spl.a2.Deferred;

public class DeferredTest {
	
	Deferred<Integer> d1;
	Runnable run1;
	Runnable run2;
	
	@Before
	public void setUp() throws Exception {
		d1 = new Deferred<Integer>();
		run1 = new Runnable() {
			@Override
			public void run() {System.out.println("I'm running!");} // we might not need those
			};
		run2 = new Runnable() {
			@Override
			public void run() {System.out.println("Me too!");} // we might not need those
			};
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	/**
	 * Test the get() method in Deferred.
	 * tests:
	 * attempt to get when deferred object is null
	 * assign resolved values to a deferred object and make sure get() returns them
	 */
	public void testGet() {
		try{
			d1.get();
			fail("IllegalStateException was expected");
		}
		catch(IllegalStateException e){}
		d1.resolve(4);
		Integer i = 4;
		assertEquals(i, d1.get());
		d1.resolve(54);
		i = 54;
		assertEquals(i, d1.get());
	}

	@Test
	/**
	 * Test the isResolved() in Deferred
	 * test:
	 * check that returns false for an empty deferred object
	 * check that returns true for non-empty deferred object
	 */
	public void testIsResolved() {
		assertEquals(false, d1.isResolved());
		d1.resolve(5);
		assertEquals(true, d1.isResolved());
	}


	@Test
	/**
	 * Test the whenResolved() in Deferred
	 * tests:
	 * check that the number of callbacks in in empty deferred object is 0
	 * add callbacks to the deferred object and check that the number of callbacks has increased accordingly
	 */
	public void testWhenResolved() {
		assertEquals(0, d1.numOfCallbacks());
		d1.whenResolved(run1);
		assertEquals(1, d1.numOfCallbacks());
		d1.whenResolved(run2);
		assertEquals(2, d1.numOfCallbacks());
	}
	
	@Test
	/**
	 * Test the resolve() in Deferred
	 * tests:
	 * assign new callbacks to the deferred object
	 * resolve the callbacks with a given value, making sure we get the correct result back (with get()) 
	 * and that the number of callbacks is 0
	 */
	public void testResolve() {
		d1.whenResolved(run1);
		d1.whenResolved(run2);
		d1.resolve(5);
		Integer i = 5;
		assertEquals(i, d1.get());
		assertEquals(0, d1.numOfCallbacks());
	}

}