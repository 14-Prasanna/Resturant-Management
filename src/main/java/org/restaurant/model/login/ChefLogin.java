package org.restaurant.model.login;

public class ChefLogin {
    private String username;
    private String password;
    
    public ChefLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
