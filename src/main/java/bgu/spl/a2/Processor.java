package bgu.spl.a2;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * this class represents a single work stealing processor, it is
 * {@link Runnable} so it is suitable to be executed by threads.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 *
 */
public class Processor implements Runnable {

    private final WorkStealingThreadPool pool;
    private final int id;
    
    private ConcurrentLinkedDeque<Task<?>> taskQueue;


    /**
     * constructor for this class
     *
     * IMPORTANT:
     * 1) this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     *
     * 2) you may not add other constructors to this class
     * nor you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param id - the processor id (every processor need to have its own unique
     * id inside its thread pool)
     * @param pool - the thread pool which owns this processor
     */
    /*package*/ Processor(int id, WorkStealingThreadPool pool) {
        this.id = id;
        this.pool = pool;
        taskQueue = new ConcurrentLinkedDeque<Task<?>>();
        pool.bootupSignal.countDown();
    }

    @Override
    /**
     * The processor would start running until interrupted
     */
    public void run() {
    	notInterrupted:
    	while(!(Thread.currentThread().isInterrupted())){
			while (!taskQueue.isEmpty()){
				Task<?> nextTask = taskQueue.pollFirst();
				if (nextTask!=null)
					nextTask.handle(this);  // keep doing tasks until you're out of tasks or interrupted
				if (Thread.currentThread().isInterrupted()){
					break notInterrupted;	
				}
			}
			try {
				stealTask();
			} catch (InterruptedException e) {Thread.currentThread().interrupt();}
    	}
    
    	pool.shutdownSignal.countDown();
    }
    /**
     * Add a task to the processor
     * @param task - the task to add
     */
    void addTask(Task<?> task){
    	taskQueue.add(task);
    	pool.versionMonitor.inc();
    }
    /**
     * removes and returns the last task from the queue
     * 
     */
     /*package*/ Task<?> giveTask(){	
    	 return taskQueue.pollLast();
     }
    
    /**
     * Steal tasks from other processors
     * @throws InterruptedException 
     * 
     */
    private void stealTask() throws InterruptedException {
    	int version = pool.versionMonitor.getVersion();
    	int numOfThreads = pool.getNumOfThreads();
	    for (int i=1; i<numOfThreads; i++){ // iterates over the processors, starting from the next one
	   		stealFrom(pool.getProcessor((i+id)%numOfThreads));
	   	}
	    if (taskQueue.isEmpty()){
			try {
				pool.versionMonitor.await(version);
			} catch (InterruptedException e) {throw e;}
	    }
   }
    
    /**
     * Attempt to steal half the tasks of a different processor
     * @param curr - the processor to steal from
     */
    private void stealFrom (Processor curr){
    	Task<?> newTask;
		int currSize = curr.taskQueue.size();
		for (int j=0; j<currSize/2; j++){
			newTask = curr.giveTask();
			if (newTask==null)
				break;
    		addTask(newTask);
		}
    }

}