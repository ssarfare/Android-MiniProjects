package com.example.inclass09;

public class UserSignUpRequest {


        public String email,password,fname,lname;
        public boolean isValid=true;
    public UserSignUpRequest(String email, String password, String fname, String lname) {
        this.email = email;
        this.password = password;
        this.fname = fname;
        this.lname = lname;
    }
}
