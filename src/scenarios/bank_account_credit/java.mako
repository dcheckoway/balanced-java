% if mode == 'definition':
BankAccount().credit(Map<String, Object> payload)

% else:
Balanced.configure("ak-test-22IOkhevjZlmRP2do6CZixkkDshTiOjTV");

BankAccount bankAccount = new BankAccount("/bank_accounts/BA3Y63fK5STwlhKNMkE3Utmd");

HashMap<String, Object> payload = new HashMap<String, Object>();
payload.put("amount", 5000);

try {
    Credit credit = bankAccount.credit(payload);
}
catch (HTTPError e) {}

% endif

