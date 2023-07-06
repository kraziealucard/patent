package Model;

import java.time.LocalDate;
import java.util.ArrayList;

public class ReceiptSupply extends Receipt {
    Contractor supplier;

    public ReceiptSupply(long ID, LocalDate date, User performer, String invoiceNumberField, Contractor supplier, boolean isProduct) {
        super(ID, date, performer, invoiceNumberField, isProduct);
        this.supplier = supplier;
    }

    public Contractor getSupplier() {
        return supplier;
    }

    public void setSupplier(Contractor supplier) {
        this.supplier = supplier;
    }
}
