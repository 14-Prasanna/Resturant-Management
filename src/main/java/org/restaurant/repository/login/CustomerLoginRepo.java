package org.restaurant.repository.login;

import org.restaurant.config.CleverCloudDB;
import org.restaurant.model.login.CustomerLogin;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.*;

public class CustomerLoginRepo {
    private static CustomerLoginRepo instance;
    private Map<String, CustomerLogin> customerMap = new HashMap<>();

    private CustomerLoginRepo() {}

    public static CustomerLoginRepo getInstance() {
        if (instance == null) {
            instance = new CustomerLoginRepo();
        }
        return instance;
    }

    public boolean register(String username, String password) {
        if (customerMap.containsKey(username)) {
            System.out.println("Username already taken. Try another.");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public CustomerLogin findByUsername(String username) {
        String sql = "SELECT * FROM customer_login WHERE username = ?";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new CustomerLogin(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("phone")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Collection<CustomerLogin> getAllCustomers() {
        List<CustomerLogin> list = new ArrayList<>();
        String sql = "SELECT * FROM customer_login";
        try (Connection con = CleverCloudDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new CustomerLogin(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("phone")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}