/**
 * 
 */
package vnsBFP;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author gurghet
 *
 */
public class StorageVNSTest {
	StorageVNS storageVNS1 = new StorageVNS(1);
	StorageVNS storageVNS4 = new StorageVNS(4);
	StorageVNS storageVNS16 = new StorageVNS(16);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link vnsBFP.StorageVNS#inizializzaCoiJob(java.util.ArrayList)}.
	 */
	@Test
	public void testinizializzaCoiJob() {
		// SET-UP
		ArrayList<Job> testJobs = new ArrayList<Job>();
		testJobs.add(new Job("job1", 0, 4, 17, 4));
		testJobs.add(new Job("job2", 0, 6, 49, 1));
		testJobs.add(new Job("job3", 0, 12, 32, 5));
		//	//	//
		
		storageVNS1.inizializzaCoiJob(testJobs);
		
		// TESTS
		assertEquals(storageVNS1.getNumberOfJobsOnMachine(0), 3);
		assertEquals(storageVNS1.calculateTwt(), 0.0, 1e-12);
	}

}
