package Model;

import java.time.LocalDate;

public class ReceiptDispatch extends Receipt {
    Customer customer;

    public ReceiptDispatch(long ID, LocalDate date, User performer, String invoiceNumberField, Customer customer,boolean isProduct) {
        super(ID, date, performer, invoiceNumberField,isProduct);
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
