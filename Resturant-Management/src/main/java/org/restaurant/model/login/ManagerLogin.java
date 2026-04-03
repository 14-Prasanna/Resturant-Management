package org.restaurant.model.login;

public class ManagerLogin {
    private String username;
    private String password;

    public ManagerLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}