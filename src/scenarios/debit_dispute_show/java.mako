% if mode == 'definition':
Debit().dispute

% else:
Balanced.configure("ak-test-22IOkhevjZlmRP2do6CZixkkDshTiOjTV");

Debit debit = new Debit("/debits/WD4YCKAyFrQBFYuFCUCRynOx");
Dispute dispute = debit.dispute;

% endif

