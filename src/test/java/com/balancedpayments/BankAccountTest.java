package com.balancedpayments;

import static org.junit.Assert.assertEquals;

import com.balancedpayments.errors.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class BankAccountTest  extends BaseTest {

    protected BankAccount ba;

    @Rule
    public ExpectedException apiError = ExpectedException.none();

    @Override
    @Before
    public void setUp() throws NoResultsFound, MultipleResultsFound, HTTPError {
        super.setUp();
        Customer customer = createPersonCustomer();
        ba = createBankAccount();
        ba.associateToCustomer(customer);
    }

    @Test
    public void testVerify() throws HTTPError {
        BankAccountVerification bav = ba.verify();
        ba.reload();

        assertEquals(ba.verification.id, bav.id);
        bav.confirm(1, 1);
        assertEquals(bav.attempts.intValue(), 1);
        assertEquals(bav.attempts_remaining.intValue(), 2);
        assertEquals(bav.deposit_status, "succeeded");
        assertEquals(bav.verification_status, "succeeded");
    }

    @Test(expected=BankAccountVerificationFailure.class)
    public void testDoubleVerify() throws HTTPError {
        ba.verify();
        ba.verify();
    }

    @Test
    public void testDeleteBankAccount() throws HTTPError, NotCreated {
    	BankAccount bankAccount = new BankAccount();
    	bankAccount.name = "Harry Fakester";
    	bankAccount.routing_number = "121042882";
    	bankAccount.account_number = "112233a";
    	bankAccount.account_type = "checking";
    	bankAccount.save();
    	assertNotNull(bankAccount.href);
        String href = bankAccount.href;
        bankAccount.unstore();
        // TODO: make sure this was deleted
    }
}
