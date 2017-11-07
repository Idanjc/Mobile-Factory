package bgu.spl.a2;


import static org.junit.Assert.*;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VersionMonitorTest {

	VersionMonitor vm;
	
	@Before
	public void setUp() throws Exception {
		vm = new VersionMonitor();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	/**
	 * Test the getVersion() method in VersionMonitor
	 * test that the version of initialized VersionMonitor is 0
	 */
    public void getVersionTest() {
        assertEquals(0, vm.getVersion());
    }

	@Test
	/**
	 * Test the inc() method in VersionMonitor
	 * test:
	 * make sure the version of the monitor is 0 (just initialized)
	 * increase version by one, and test that the version is indeed 1
	 * increase it again, twice, and make sure the version is now 3
	 */
    public void incTest() {
		assertEquals(0, vm.getVersion());
		vm.inc();
		assertEquals(1, vm.getVersion());
		vm.inc();
		vm.inc();
		assertEquals(3, vm.getVersion());
    }

	@Test
	/**
	 * Test await() method in VersionMonitor
	 * create a new runnable object that waits until the version of the monitor changes.
	 * using a boolean variable to determine when the thread that runs the new runnable starts waiting (assigned true) and wakes up (assigned false)
	 */
    public void awaitTest() {
		Runnable r1 = new Runnable() {
			
			@Override
			public void run() {
				int prevVersion = vm.getVersion();
				try{
					vm.await(prevVersion);
				}
				catch (InterruptedException e){}
				assertEquals(true, prevVersion != vm.getVersion());
			}
		};
		Thread testThread = new Thread(r1);
		testThread.start();
		vm.inc();
		
    }


}