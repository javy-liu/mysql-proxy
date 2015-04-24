package com.mysql.proxy;

import java.net.BindException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * The class <code>EngineTest</code> contains tests for the class <code>{@link Engine}</code>.
 *
 * @generatedBy CodePro at 13. 10. 24 오후 1:06
 * @author n2501
 * @version $Revision: 1.0 $
 */
public class EngineTest
{
	/**
	 * Run the Engine(int,String) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 13. 10. 24 오후 1:06
	 */
	@Test
	public void testEngine_1()
		throws Exception
	{
		int listenPort = 33006;
		String plugins = "";

		Engine result = new Engine(listenPort, plugins);

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 13. 10. 24 오후 1:06
	 */
	@Before
	public void setUp()
		throws Exception
	{
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 13. 10. 24 오후 1:06
	 */
	@After
	public void tearDown()
		throws Exception
	{
		// Add additional tear down code here
	}

	/**
	 * Launch the test.
	 *
	 * @param args the command line arguments
	 *
	 * @generatedBy CodePro at 13. 10. 24 오후 1:06
	 */
	public static void main(String[] args)
	{
		new org.junit.runner.JUnitCore().run(EngineTest.class);
	}
}