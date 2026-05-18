package com.example.webflow.service;

import com.example.webflow.model.LoginCredentials;
import org.springframework.stereotype.Service;

/**
 * Service xác thực người dùng.
 * 
 * Trong demo này, chúng ta sử dụng hardcoded credentials.
 * Trong ứng dụng thực tế, bạn sẽ kết nối với database
 * hoặc sử dụng Spring Security để xác thực.
 * 
 * Service này được gọi từ flow definition (XML) thông qua
 * expression language: #{authenticationService.authenticate(credentials)}
 */
@Service("authenticationService")
public class AuthenticationService {

    // === Thông tin đăng nhập demo (hardcoded) ===
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "123456";

    /**
     * Xác thực thông tin đăng nhập.
     *
     * @param credentials đối tượng chứa username và password
     * @return true nếu đăng nhập thành công, false nếu thất bại
     */
    public boolean authenticate(LoginCredentials credentials) {
        if (credentials == null) {
            return false;
        }

        String username = credentials.getUsername();
        String password = credentials.getPassword();

        // Kiểm tra xem username và password có khớp không
        boolean isValid = VALID_USERNAME.equals(username)
                && VALID_PASSWORD.equals(password);

        return isValid;
    }

    /**
     * Kiểm tra xem credentials có rỗng không.
     * Action-state se dua vao gia tri "valid" / "invalid" de quyet dinh transition.
     *
     * @param credentials thông tin đăng nhập
     * @return "valid" nếu hợp lệ, "invalid" nếu rỗng
     */
    public String validateCredentials(LoginCredentials credentials) {
        if (credentials == null
                || credentials.getUsername() == null
                || credentials.getUsername().trim().isEmpty()
                || credentials.getPassword() == null
                || credentials.getPassword().trim().isEmpty()) {
            return "invalid";
        }
        return "valid";
    }
}
