package com.balancedpayments;

import com.balancedpayments.core.ResourceQuery;
import com.balancedpayments.errors.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DebitTest extends BaseTest {

    @Test
    public void testDebitCreate() throws HTTPError {
        Customer customer = createPersonCustomer();
        Card card = createCard();
        card.associateToCustomer(customer);

        HashMap<String, Object> meta = new HashMap<String, Object>();
        meta.put("invoice_id", "12141");

        HashMap<String, Object> payload = new HashMap<String, Object>();
        payload.put("amount", 10000);
        payload.put("description", "A simple debit");
        payload.put("meta", meta);

        Debit debit = card.debit(payload);
        assertNotNull(debit.href);
        assertEquals(10000, debit.amount.intValue());
        assertEquals("A simple debit", debit.description);
    }

    @Test
    public void testDebitCreateNoCustomer() throws HTTPError {
        Card card = createCard();

        HashMap<String, Object> meta = new HashMap<String, Object>();
        meta.put("invoice_id", "12141");

        HashMap<String, Object> payload = new HashMap<String, Object>();
        payload.put("amount", 10000);
        payload.put("description", "A simple debit");
        payload.put("meta", meta);

        Debit debit = card.debit(payload);
        assertNotNull(debit.href);
        assertEquals(10000, debit.amount.intValue());
        assertEquals("A simple debit", debit.description);
    }

    @Test
    public void testDebitRetrieve() throws HTTPError {
        Card card = createCard();

        HashMap<String, Object> meta = new HashMap<String, Object>();
        meta.put("invoice_id", "12141");

        HashMap<String, Object> payload = new HashMap<String, Object>();
        payload.put("amount", 10000);
        payload.put("description", "A simple debit");
        payload.put("meta", meta);

        Debit debit = card.debit(payload);
        assertNotNull(debit.href);
        assertEquals(10000, debit.amount.intValue());
        assertEquals("A simple debit", debit.description);

        Debit theDebit = new Debit(debit.href);
        assertNotNull(theDebit);
        assertNotNull(theDebit.href);
        assertEquals(debit.href, theDebit.href);
    }

    @Test
    public void testRefund() throws CannotCreate, HTTPError, NoResultsFound, MultipleResultsFound {
        Card card = createCard();

        HashMap<String, Object> meta = new HashMap<String, Object>();
        meta.put("invoice_id", "12141");

        HashMap<String, Object> payload = new HashMap<String, Object>();
        payload.put("amount", 10000);
        payload.put("description", "A simple debit");
        payload.put("meta", meta);

        Debit debit = card.debit(payload);
        assertNotNull(debit.href);
        assertEquals(10000, debit.amount.intValue());
        assertEquals("A simple debit", debit.description);

        Refund refund = debit.refund();

        assertEquals(debit.amount, refund.amount);
    }

    @Test
    public void testRefundsCollection() throws CannotCreate, HTTPError, NoResultsFound, MultipleResultsFound {

        Card card = createCard();

        HashMap<String, Object> meta = new HashMap<String, Object>();
        meta.put("invoice_id", "12141");

        HashMap<String, Object> payload = new HashMap<String, Object>();
        payload.put("amount", 10000);
        payload.put("description", "A simple debit");
        payload.put("meta", meta);

        Debit debit = card.debit(payload);
        assertNotNull(debit.href);
        assertEquals(10000, debit.amount.intValue());
        assertEquals("A simple debit", debit.description);

        Refund refund = debit.refund();

        debit.reload();
        Refund.Collection refunds = debit.refunds;
        assertTrue(refunds.total() == 1);
    }

    @Test
    public void testDebitBankAccountVerified() throws HTTPError {
        Customer customer = new Customer();
        customer.save();

        BankAccount ba = createBankAccount();

        BankAccountVerification bankAccountVerification = ba.verify();
        bankAccountVerification.confirm(1, 1);
        bankAccountVerification.reload();

        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("amount", 100000);

        Debit debit = ba.debit(payload);

        assertTrue(debit.status.equals("succeeded"));
        assertEquals(100000, (int)debit.amount);
    }

    @Test
    public void testDebitBankAccountUnverifiedNoCustomer() throws HTTPError {
        Customer customer = new Customer();
        customer.save();

        BankAccount ba = createBankAccount();
        BankAccountVerification verification = ba.verify();
        verification.confirm(1, 1);

        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("amount", 100000);

        Debit debit = ba.debit(payload);
    }

    @Test
    public void testDebitFilter() throws CannotCreate, HTTPError, NoResultsFound, MultipleResultsFound {
        Debit[] debits = new Debit[3];
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("amount", 100000);
        Map<String, Object> payload2 = new HashMap<String, Object>();
        payload2.put("amount", 777);
        Map<String, Object> payload3 = new HashMap<String, Object>();
        payload3.put("amount", 555);

        Card card = createCard();
        debits[0] = card.debit(payload);
        debits[1] = card.debit(payload2);
        debits[2] = card.debit(payload3);

        ResourceQuery<Debit> query = card.debits.query().filter("amount", "=", 777);
        assertEquals(1, query.total());
        assertEquals(debits[1].id, query.first().id);

        query = card.debits.query().filter("amount", 777);
        assertEquals(1, query.total());
        assertEquals(debits[1].id, query.first().id);

        query = (
            card
            .debits
            .query()
            .filter("amount", "<", 800)
            .order_by("created_at", ResourceQuery.SortOrder.ASCENDING)
        );
        assertEquals(2, query.total());

        ArrayList<Debit> all_debits = query.all();
        assertEquals(debits[1].id, all_debits.get(0).id);
        assertEquals(debits[2].id, all_debits.get(1).id);

        query = (
                card
                .debits
                .query()
                .filter("amount", ">", 600)
                .filter("amount", "<", 800)
                .order_by("amount", ResourceQuery.SortOrder.DESCENDING)
            );
        assertEquals(1, query.total());

        all_debits = query.all();
        assertEquals(debits[1].id, all_debits.get(0).id);
    }
}
