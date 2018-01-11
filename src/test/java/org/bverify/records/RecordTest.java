package org.bverify.records;

import org.apache.commons.lang3.SerializationUtils;
import org.bverify.accounts.Account;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

import org.junit.Assert;


public class RecordTest extends TestCase {
	
	private static Account alice = new Account("Alice", 1);
	private static Account bob = new Account("Bob", 2);
	private static Account charlie = new Account("Charlie", 3);
	
	private static String goodCorn = "CORN";
	private static String goodWheat = "WHEAT";

	/**
	 * Test suite 
	 * @param testName
	 */
    public RecordTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( RecordTest.class );
    }
	
	
	@SuppressWarnings("unlikely-arg-type")
	public void testDepositEquality(){

		Deposit dep1 = new Deposit(goodCorn, 100, alice, bob);
		
		Deposit dep2 = new Deposit(goodCorn, 100, alice, bob);
		Deposit dep3 = new Deposit(goodWheat, 100, alice, bob);
		Deposit dep4 = new Deposit(goodCorn, 100, bob, alice);
		Deposit dep5 = new Deposit(goodCorn, 99, alice, bob);
		Withdrawal dep6 = new Withdrawal(goodCorn, 100, alice, bob);
		
		Assert.assertTrue(dep1.equals(dep2));
		Assert.assertFalse(dep1.equals(dep3));
		Assert.assertFalse(dep1.equals(dep4));
		Assert.assertFalse(dep1.equals(dep5));
		Assert.assertFalse(dep1.equals(dep6));
	}
	
	public void testSerilaizationDeposit() {
		Deposit dep = new Deposit(goodCorn, 100, alice, bob);
		dep.signEmployee();
		dep.signRecipient();
		byte[] depbytes = SerializationUtils.serialize(dep);
		Record rec = SerializationUtils.deserialize(depbytes);
		Assert.assertTrue(rec instanceof Deposit);
		Assert.assertEquals(rec, dep);
	}
	
	public void testSerializationWithdrawal() {
		Withdrawal wd = new Withdrawal(goodCorn, 100, alice, charlie);
		wd.signEmployee();
		wd.signRecipient();
		byte[] depbytes = SerializationUtils.serialize(wd);
		Record rec = SerializationUtils.deserialize(depbytes);
		Assert.assertTrue(rec instanceof Withdrawal);
		Assert.assertEquals(rec, wd);
	}
	
	public void testSerilizationTransfer() {
		Transfer tf = new Transfer(goodCorn, 10, alice, charlie);
		tf.signRecipient();
		byte[] depbytes = SerializationUtils.serialize(tf);
		Record rec = SerializationUtils.deserialize(depbytes);
		Assert.assertTrue(rec instanceof Transfer);
		Assert.assertEquals(rec, tf);	
	}
	
}