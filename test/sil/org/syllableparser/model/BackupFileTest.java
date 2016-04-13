/**
 * 
 */
package sil.org.syllableparser.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andy Black
 *
 */
public class BackupFileTest {

	BackupFile bup;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		bup = new BackupFile("DurangoSanPedroNahuat20160411-121941.ashebackup", "description");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertEquals("2016.04.11 12:19:41", bup.getDate());
		assertEquals("DurangoSanPedroNahuat", bup.getFileName());
	}

}
