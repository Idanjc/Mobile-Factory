package bgu.spl.a2;

import java.util.concurrent.CountDownLatch;

/**
 * represents a work stealing thread pool - to understand what this class does
 * please refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class WorkStealingThreadPool {
	
	protected VersionMonitor versionMonitor = new VersionMonitor();
	private int numOfThreads;
	private Processor[] processorArray;
	private Thread[] threadArray;
	CountDownLatch bootupSignal;
	CountDownLatch shutdownSignal;

    /**
     * creates a {@link WorkStealingThreadPool} which has n threads
     * {@link Processor}s. Note, threads should not get started until calling to
     * the {@link #start()} method.
     *
     * Implementors note: you may not add other constructors to this class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param nthreads the number of threads that should be started by this
     * thread pool
     */
	public WorkStealingThreadPool(int nthreads) {
    	numOfThreads = nthreads;
    	bootupSignal = new CountDownLatch(numOfThreads); // a CDL for the start method
    	shutdownSignal = new CountDownLatch(numOfThreads); // a CDL for the shutdown method
    	processorArray = new Processor[nthreads];
    	threadArray = new Thread[nthreads];
    	for (int i=0; i<nthreads; i++){
    		processorArray[i] = new Processor(i, this);
    		threadArray[i] = new Thread(processorArray[i]); // the threads recieve this pool name
    	}
    }

    /**
     * submits a task to be executed by a processor belongs to this thread pool
     *
     * @param task the task to execute
     */
    public synchronized void submit(Task<?> task) {
    	int submitTo = (int) (Math.random()*numOfThreads);
        processorArray[submitTo].addTask(task);
    }

    /**
     * closes the thread pool - this method interrupts all the threads and wait
     * for them to stop - it is returns *only* when there are no live threads in
     * the queue.
     *
     * after calling this method - one should not use the queue anymore.
     *
     * @throws InterruptedException if the thread that shut down the threads is
     * interrupted
     * @throws UnsupportedOperationException if the thread that attempts to
     * shutdown the queue is itself a processor of this queue
     */
    public synchronized void shutdown() throws InterruptedException {
    	while(!Thread.currentThread().isInterrupted()){
    		for (Thread thread : threadArray)
				if (Thread.currentThread() == thread) // checks whether current thread is a processor of this queue
					throw new UnsupportedOperationException("the thread that attempts to shutdown the queue is itself a processor of this queue");
    		for (int i=0; i<numOfThreads; i++){
    			threadArray[i].interrupt();
    		}
    		shutdownSignal.await(); //wait for all to finish
    		return;
    	}
    }

    /**
     * start the threads belongs to this thread pool
     * @throws InterruptedException 
     */
    public synchronized void start() throws InterruptedException {  
        for (int i=0; i<numOfThreads; i++)
        	threadArray[i].start();
        bootupSignal.await();
    }
    
    /**
     * @return the number of threads handled by the threadpool
     */
    int getNumOfThreads() {
		return numOfThreads;
	}
    /**
     * 
     * @param id - the id of the processor to get
     * @return the processor
     */
    Processor getProcessor (int id){
    	return processorArray[id];
    }

}