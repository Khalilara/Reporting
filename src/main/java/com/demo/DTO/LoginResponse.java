package com.demo.dto;

public class LoginResponse {
    private String token;
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String fonction;
    
    public LoginResponse() {}
    
    public LoginResponse(String token, Long id, String username, String email, String firstName,
                        String lastName, String phoneNumber, String fonction) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.fonction = fonction;
    }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getFonction() { return fonction; }
    public void setFonction(String fonction) { this.fonction = fonction; }
}
