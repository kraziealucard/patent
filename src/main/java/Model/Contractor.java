package Model;

public class Contractor {
    private long ID;
    private String name;
    private String address;
    private String information;
    private String passport;
    private String phone;
    private String bankRequisites;
    private String IIN;
    private String KPP;
    private boolean isActive;
    private boolean isCustomer;
    private boolean isSupplier;


    public Contractor(long ID, String name) {
        this.ID = ID;
        this.name = name;
        this.isActive = true;
        this.isCustomer = false;
        this.isSupplier = false;
    }

    public long getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBankRequisites() {
        return bankRequisites;
    }

    public void setBankRequisites(String bankRequisites) {
        this.bankRequisites = bankRequisites;
    }

    public String getIIN() {
        return IIN;
    }

    public void setIIN(String IIN) {
        this.IIN = IIN;
    }

    public String getKPP() {
        return KPP;
    }

    public void setKPP(String KPP) {
        this.KPP = KPP;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isCustomer() {
        return isCustomer;
    }

    public void setCustomer(boolean customer) {
        isCustomer = customer;
    }

    public boolean isSupplier() {
        return isSupplier;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public void setSupplier(boolean supplier) {
        isSupplier = supplier;
    }
}
