package bgu.spl.a2;

/**
 * Describes a monitor that supports the concept of versioning - its idea is
 * simple, the monitor has a version number which you can receive via the method
 * {@link #getVersion()} once you have a version number, you can call
 * {@link #await(int)} with this version number in order to wait until this
 * version number changes.
 *
 * you can also increment the version number by one using the {@link #inc()}
 * method.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class VersionMonitor {

	private int version;
	
	public VersionMonitor() {
		version = 0;
	}
	/**
	 * 
	 * @return the current version of the monitor
	 */
    public int getVersion() {
        return version;
    }
    /**
     * increase the version of the monitor
     */
    synchronized public void inc() {
        version++;
        this.notifyAll();
    }
    /**
     * check whether the version has changed. If not, the thread will wait until it's changed
     * @param version - the monitor version that a thread received when it started
     * @throws InterruptedException notify that the thread is no longer waiting because the version of the monitor changed
     */
    synchronized public void await(int version) throws InterruptedException {
        while (version == this.version){
    		try{this.wait();}
    		catch(InterruptedException e){throw e;}
        }
    }
}