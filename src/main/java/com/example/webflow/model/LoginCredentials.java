package com.example.webflow.model;

import java.io.Serializable;

/**
 * Model class chứa thông tin đăng nhập.
 * 
 * Implements Serializable vì Spring Web Flow lưu trữ
 * các đối tượng trong flow scope (có thể serialize để
 * lưu vào session hoặc database).
 * 
 * Lớp này được sử dụng trong flow như một form-backing object,
 * nghĩa là dữ liệu từ form sẽ được bind vào các thuộc tính
 * của đối tượng này.
 */
public class LoginCredentials implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Tên đăng nhập */
    private String username;

    /** Mật khẩu */
    private String password;

    // === Constructor mặc định (bắt buộc cho databinding) ===
    public LoginCredentials() {
    }

    // === Getter và Setter ===

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

    @Override
    public String toString() {
        return "LoginCredentials{username='" + username + "'}";
    }
}
