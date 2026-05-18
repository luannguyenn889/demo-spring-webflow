# Demo Spring Web Flow với Spring Boot

Demo này minh họa một `login flow` đơn giản bằng Spring Web Flow.

## 1. Spring Web Flow là gì?

Spring Web Flow là framework giúp mô hình hóa một quy trình nhiều bước bằng các `state` và `transition`.

- `state`: màn hình hoặc bước xử lý hiện tại
- `transition`: đường đi từ state này sang state khác khi có event
- `flow execution`: một phiên chạy cụ thể của flow, được giữ qua nhiều request

Nên dùng khi ứng dụng có quy trình nhiều bước cần giữ trạng thái:

- đăng ký nhiều bước
- checkout / thanh toán
- quên mật khẩu + OTP
- wizard cấu hình

Không nhất thiết phải dùng với các màn hình CRUD đơn giản một bước.

## 2. Flow đăng nhập trong demo

### Sơ đồ ASCII

```text
[loginForm] --submit--> [validateInput] --valid--> [authenticateUser] --success--> [loginSuccess] --finish--> [flowEnd]
     ^                         |                         |
     |                         | invalid                 | failure
     |                         v                         v
     +------------------ [validationError] <---- [loginFailed]
```

### Ý nghĩa các state

- `loginForm`:
  Hiển thị form nhập username/password.
- `validateInput`:
  Kiểm tra người dùng có nhập đủ dữ liệu hay không.
- `validationError`:
  Đặt thông báo lỗi rồi quay lại form.
- `authenticateUser`:
  Gọi service để kiểm tra tài khoản.
- `loginFailed`:
  Sai tài khoản hoặc mật khẩu, quay lại form với lỗi.
- `loginSuccess`:
  Hiển thị trang đăng nhập thành công.
- `flowEnd`:
  Kết thúc flow và redirect về trang chủ.

## 3. Cấu trúc code

### Maven dependencies

Trong [pom.xml](/D:/demospringwebflow/pom.xml), các dependency chính là:

- `spring-boot-starter-web`: Spring MVC + Tomcat embedded
- `spring-boot-starter-thymeleaf`: render view HTML
- `spring-webflow`: thư viện chính của Spring Web Flow

## 4. Giải thích từng phần

### 4.1. Cấu hình Web Flow

File: [WebFlowConfig.java](/D:/demospringwebflow/src/main/java/com/example/webflow/config/WebFlowConfig.java)

Phần quan trọng:

- `flowRegistry()`:
  đăng ký file XML flow trong `src/main/resources/flows`
- `flowExecutor()`:
  chịu trách nhiệm start hoặc resume flow execution
- `flowHandlerMapping()`:
  map URL `/login` tới flow id `login`
- `flowHandlerAdapter()`:
  kết nối `DispatcherServlet` của Spring MVC với Web Flow
- `flowBuilderServices(...)`:
  nối Web Flow với Thymeleaf resolver để view `login/loginForm` và `login/loginSuccess` render đúng

### 4.2. Không cần Controller riêng cho `/login`

File: [WebMvcConfig.java](/D:/demospringwebflow/src/main/java/com/example/webflow/config/WebMvcConfig.java)

- Trang chủ `/` dùng `ViewController`
- Route `/login` do Spring Web Flow xử lý trực tiếp qua `FlowHandlerMapping`

### 4.3. Model form

File: [LoginCredentials.java](/D:/demospringwebflow/src/main/java/com/example/webflow/model/LoginCredentials.java)

Model này chứa:

- `username`
- `password`

Object này được giữ trong `flowScope` với tên `credentials`.

### 4.4. Service xác thực

File: [AuthenticationService.java](/D:/demospringwebflow/src/main/java/com/example/webflow/service/AuthenticationService.java)

Service demo dùng tài khoản cứng:

- username: `admin`
- password: `123456`

Hai method chính:

- `validateCredentials(...)`: trả về `valid` hoặc `invalid`
- `authenticate(...)`: trả về `true` hoặc `false`

### 4.5. Flow definition XML

File: [login-flow.xml](/D:/demospringwebflow/src/main/resources/flows/login/login-flow.xml)

#### `var`

```xml
<var name="credentials" class="com.example.webflow.model.LoginCredentials"/>
```

- Tạo biến `credentials`
- Biến này nằm trong `flowScope`
- Tồn tại trong suốt một flow execution

#### `view-state`

```xml
<view-state id="loginForm" view="login/loginForm" model="credentials">
    <binder>
        <binding property="username"/>
        <binding property="password"/>
    </binder>
    <transition on="submit" to="validateInput"/>
</view-state>
```

- Hiển thị form login
- `model="credentials"` để bind dữ liệu form vào object
- Event `submit` sẽ chuyển sang `validateInput`

#### `action-state`

```xml
<action-state id="validateInput">
    <evaluate expression="authenticationService.validateCredentials(credentials)"
              result="flowScope.validationResult"/>
    <transition to="checkValidationResult"/>
</action-state>
```

- `action-state` dùng để chạy logic Java
- `evaluate` gọi method trong Spring bean
- kết quả được lưu vào `flowScope.validationResult`

Một `action-state` khác là:

```xml
<action-state id="authenticateUser">
    <evaluate expression="authenticationService.authenticate(credentials)"
              result="flowScope.loginSuccess"/>
    <transition to="checkLoginResult"/>
</action-state>
```

- Lưu `true/false` vào `flowScope.loginSuccess`

#### `decision-state`

Demo này dùng thêm `decision-state` để dễ đọc:

```xml
<decision-state id="checkLoginResult">
    <if test="flowScope.loginSuccess"
        then="loginSuccess"
        else="loginFailed"/>
</decision-state>
```

- Nếu đúng thì sang `loginSuccess`
- Nếu sai thì sang `loginFailed`

#### `end-state`

```xml
<end-state id="flowEnd" view="externalRedirect:/"/>
```

- Kết thúc flow execution
- Redirect ra ngoài flow về `/`

### 4.6. View Thymeleaf

Các file:

- [home.html](/D:/demospringwebflow/src/main/resources/templates/home.html)
- [loginForm.html](/D:/demospringwebflow/src/main/resources/templates/login/loginForm.html)
- [loginSuccess.html](/D:/demospringwebflow/src/main/resources/templates/login/loginSuccess.html)

Điểm cần chú ý trong form:

```html
<form th:action="${flowExecutionUrl}" th:object="${credentials}" method="post">
    <button type="submit" name="_eventId_submit">Đăng nhập</button>
</form>
```

- `flowExecutionUrl` là URL resume flow hiện tại
- `_eventId_submit` là event mà Web Flow dùng để tìm transition `on="submit"`

## 5. Flow execution hoạt động như thế nào?

Khi bạn mở `/login`:

1. Spring Web Flow tạo một flow execution mới.
2. Flow execution được gán một key, ví dụ `e1s1`.
3. Trình duyệt được redirect tới URL dạng `/login?execution=e1s1`.
4. Khi submit form, request gửi lại đúng `execution` này.
5. Web Flow resume flow execution, bind dữ liệu form, chạy state tiếp theo và quyết định transition.

Nói ngắn gọn: `execution` chính là khóa để Web Flow biết người dùng đang ở bước nào của flow.

## 6. Cách chạy project

### 6.1. Chạy trực tiếp bằng Maven (Development)

Mở terminal tại thư mục gốc của project và chạy lệnh:

```bash
mvn spring-boot:run
```

Mặc định ứng dụng chạy ở:

```text
http://localhost:8081
```

Nếu cổng `8081` đang bận, bạn có thể đổi cổng bằng lệnh:

```bash
mvn "-Dspring-boot.run.arguments=--server.port=8085" spring-boot:run
```

### 6.2. Build và chạy file JAR (Production)

Bước 1: Build project và tạo ra file JAR có thể thực thi được:

```bash
mvn clean install
```

Hoặc bỏ qua unit test (nếu muốn build nhanh):

```bash
mvn clean install -DskipTests
```

Bước 2: Chạy file JAR vừa build ra trong thư mục `target`:

```bash
java -jar target/demo-spring-webflow-1.0.0.jar
```

### 6.3. Dừng ứng dụng và xử lý lỗi kẹt port

- **Cách 1 (Thông thường):** Mở terminal đang chạy project và nhấn tổ hợp phím `Ctrl + C`.
- **Cách 2 (Khi bị kẹt port 8081 và không thể chạy lại project):** Nếu bạn gặp lỗi "Port 8081 was already in use", bạn cần tìm và kill process đang chiếm port này.

**Trên Windows:**
1. Mở Command Prompt (cmd) dưới quyền Administrator.
2. Tìm PID (Process ID) đang chiếm port 8081:
   ```cmd
   netstat -ano | findstr :8081
   ```
   *Lưu ý cột số cuối cùng chính là PID.*
3. Kill process đó bằng PID (thay `<PID>` bằng số tìm được ở trên):
   ```cmd
   taskkill /F /PID <PID>
   ```

**Trên macOS/Linux:**
1. Tìm PID đang chiếm port 8081:
   ```bash
   lsof -i :8081
   ```
   *Hoặc:*
   ```bash
   netstat -nlp | grep 8081
   ```
2. Kill process:
   ```bash
   kill -9 <PID>
   ```

## 7. Tài khoản demo

```text
username: admin
password: 123456
```

## 8. Gợi ý mở rộng

- thêm flow đăng ký tài khoản
- thêm flow quên mật khẩu
- thêm bước OTP trước khi vào `loginSuccess`
- thay `AuthenticationService` giả lập bằng database hoặc Spring Security
- thêm validation chi tiết bằng Bean Validation (`@NotBlank`)
