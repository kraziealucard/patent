package Model;

import java.time.LocalDate;

public class ReceiptDispatch extends Receipt {
    Contractor customer;

    public ReceiptDispatch(long ID, LocalDate date, User performer, String invoiceNumberField, Contractor customer, boolean isProduct) {
        super(ID, date, performer, invoiceNumberField, isProduct);
        this.customer = customer;
    }

    public Contractor getCustomer() {
        return customer;
    }

    public void setCustomer(Contractor customer) {
        this.customer = customer;
    }
}
