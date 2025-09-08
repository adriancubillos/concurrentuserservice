package com.amex.assesment.concusers.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailUpdateRequest {

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email should be valid")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
