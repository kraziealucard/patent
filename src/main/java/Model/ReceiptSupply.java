package Model;

import java.time.LocalDate;
import java.util.ArrayList;

public class ReceiptSupply extends Receipt {
    Supplier supplier;

    public ReceiptSupply(long ID, LocalDate date, User performer, String invoiceNumberField, Supplier supplier,boolean isProduct) {
        super(ID, date, performer, invoiceNumberField,isProduct);
        this.supplier = supplier;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
