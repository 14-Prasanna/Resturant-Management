package org.restaurant.repository.login;

import org.restaurant.config.CleverCloudDB;
import org.restaurant.model.login.ChefLogin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ChefLoginRepository {

    public boolean addChef(String username, String password) {
        String sql = "INSERT INTO chef_login (username, password) VALUES (?, ?)";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("❌ Chef already exists or DB Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteChef(String username) {
        String sql = "DELETE FROM chef_login WHERE username = ?";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public List<ChefLogin> getAllChefs() {
        List<ChefLogin> chefs = new ArrayList<>();
        String sql = "SELECT * FROM chef_login";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                chefs.add(new ChefLogin(rs.getString("username"), rs.getString("password")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chefs;
    }

    public boolean validateLogin(String username, String password) {
        String sql = "SELECT * FROM chef_login WHERE username = ? AND password = ?";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            return false;
        }
    }
}
