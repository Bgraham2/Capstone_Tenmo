package com.techelevator.tenmo.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
@Data
public class RegisterUserDTO {

    @NotEmpty
    private String username;
    @NotEmpty
    private String password;

    RegisterUserDTO(){
        super();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
