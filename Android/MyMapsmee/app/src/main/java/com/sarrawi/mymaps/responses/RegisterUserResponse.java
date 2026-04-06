package com.sarrawi.mymaps.responses;

import com.sarrawi.mymaps.entities.User;

public class RegisterUserResponse {
    User user;
    String jwt;
    Error error;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Error getError() {
        return error;
    }

    public void setError(User user) {
        this.error = error;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public class Error {
        String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
