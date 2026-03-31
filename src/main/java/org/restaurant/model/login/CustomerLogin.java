package org.restaurant.model.login;

import java.util.ArrayList;
import java.util.List;

public class CustomerLogin {
    private String username;
    private String password;
    private String email;
    private String phone;
    private List<String> pastOrders;
    private List<String> reports;
    private List<String> deliveries;

    // 2-param constructor (kept for backward compatibility)
    public CustomerLogin(String username, String password) {
        this(username, password, null, null);
    }

    // 4-param constructor (used by CustomerLoginRepo when reading from DB)
    public CustomerLogin(String username, String password, String email, String phone) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.pastOrders = new ArrayList<>();
        this.reports = new ArrayList<>();
        this.deliveries = new ArrayList<>();
    }

    public String getUsername()        { return username; }
    public String getPassword()        { return password; }
    public String getEmail()           { return email; }
    public String getPhone()           { return phone; }
    public List<String> getPastOrders(){ return pastOrders; }
    public List<String> getReports()   { return reports; }
    public List<String> getDeliveries(){ return deliveries; }

    public void addOrder(String order)      { pastOrders.add(order); }
    public void addReport(String report)    { reports.add(report); }
    public void addDelivery(String delivery){ deliveries.add(delivery); }
}