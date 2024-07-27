package phonebook;

public class Contact {
    private int id;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;

    public Contact(int id, String name, String phoneNumber, String email, String address) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }

    @Override
    public String toString() {
        return String.format("ID: %d, Name: %s, Phone: %s, Email: %s, Address: %s", id, name, phoneNumber, email, address);
    }
}