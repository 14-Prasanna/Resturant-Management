package org.restaurant.model.login;

import java.util.ArrayList;
import java.util.List;

public class CustomerLogin {
    private String username;
    private String password;
    private List<String> pastOrders;
    private List<String> reports;
    private List<String> deliveries;

    public CustomerLogin(String username, String password) {
        this.username = username;
        this.password = password;
        this.pastOrders = new ArrayList<>();
        this.reports = new ArrayList<>();
        this.deliveries = new ArrayList<>();
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public List<String> getPastOrders() { return pastOrders; }
    public List<String> getReports() { return reports; }
    public List<String> getDeliveries() { return deliveries; }

    public void addOrder(String order) { pastOrders.add(order); }
    public void addReport(String report) { reports.add(report); }
    public void addDelivery(String delivery) { deliveries.add(delivery); }
}