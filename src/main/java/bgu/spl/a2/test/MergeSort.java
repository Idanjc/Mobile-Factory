/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.test;

import bgu.spl.a2.Task;
import bgu.spl.a2.WorkStealingThreadPool;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class MergeSort extends Task<int[]> {

    private final int[] res;

    /**
     * Constructor
     * @param array
     */
    public MergeSort(int[] array) {
        this.res = array;
    }

    @Override
    /**
     * This method starts the MergeSort Task
     */
    protected void start() {
    	if (res.length<=1){
    		complete(res);
    	}
    	else{ // Spawn 2 sub-tasks, one for the first half of array, the other for the second half
    		int middle = res.length/2;
    		int[] halfOfArray1 = new int[middle];
	        int[] halfOfArray2 = new int[middle + res.length % 2];
    		divideArray(res, halfOfArray1, halfOfArray2, middle);
    		Task<?>[] twoHalfsTask = {new MergeSort(halfOfArray1), new MergeSort(halfOfArray2)};
    		spawn (twoHalfsTask);
    		Runnable merge = () ->
    			{complete(mergeArrays((int[])twoHalfsTask[0].getResult().get(), (int[])twoHalfsTask[1].getResult().get()));};
    		whenResolved(Arrays.asList(twoHalfsTask), merge);
    	}
    }
    
    /**
     * This method divides a given array into two arrays, representing the first half and second half of the given array
     */
    private static void divideArray(int[] array, int[] halfOfArray1, int[] halfOfArray2, int middle){
    	for (int i=0; i<middle; i++){
			halfOfArray1[i] = array[i];
			halfOfArray2[i] = array[i+middle];
		}
		if (array.length%2!=0)
			halfOfArray2[middle] = array[array.length-1];
    }
    /**
     * This method merges two arrays of type int[]
     * @param left the first array to be merged
     * @param right the second array to be merged
     * @return a new int[] that holds the values of both given arrays
     */
    private static int[] mergeArrays(int[] left, int[] right){
    	int[] res = new int[left.length+right.length];
    	int lPos=0,rPos=0, aPos=0;
    	while(lPos<left.length && rPos<right.length){
    		if (left[lPos]<=right[rPos]){
    			res[aPos] = left[lPos];
    			lPos++;
    		}
    		else{
    			res[aPos] = right[rPos];
    			rPos++;
    		}
    		aPos++;
    	}
    	while(rPos<right.length){
    		res[aPos] = right[rPos];
    		aPos++; rPos++;
    	}
    	while(lPos<left.length){
    		res[aPos] = left[lPos];
    		aPos++; lPos++;
    	}
    	return res;
    }
    	
    

    public static void main(String[] args) throws InterruptedException {
        WorkStealingThreadPool pool = new WorkStealingThreadPool(4);
        int n = 1000000; //you may check on different number of elements if you like
        int[] array = new Random().ints(n).toArray();
        MergeSort task = new MergeSort(array);
        CountDownLatch l = new CountDownLatch(1);
        pool.start();
        pool.submit(task);
        task.getResult().whenResolved(() -> {
            //warning - a large print!! - you can remove this line if you wish
            //System.out.println(Arrays.toString(array));
            l.countDown();
        });
        l.await();
        pool.shutdown();
    }
}
