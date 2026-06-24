# 🚀 Hướng dẫn chạy Inventory Management Web UI

## ✅ Status: Project đã sửa xong!

Tất cả lỗi Spring Boot bean conflict đã loại bỏ:
- ✓ Xóa XML legacy (spring-mvc-servlet.xml)
- ✓ Tạo Java config hiện đại (WebMvcConfig.java)  
- ✓ Cấu hình JSP view resolver + static resources
- ✓ Controller HomeController sẵn sàng

---

## 🎯 CÁCH CHẠY APP

### 🏃‍♂️ **Nhanh nhất: Chạy từ IntelliJ (khuyên dùng)**

**Bước 1:** Mở project trong IntelliJ
```
File → Open... → Chọn thư mục D:\ProjectWeb\Project
```

**Bước 2:** Build project
```
Build → Build Project (Ctrl+F9)
```

**Bước 3:** Chạy ProjectApplication
```
- Tìm lớp 'dao.ProjectApplication' ở file explorer bên trái
- Click chuột phải → Run 'ProjectApplication.main()'
- Hoặc: Ctrl+Shift+F10
```

**Bước 4:** Mở browser
```
http://localhost:1610
```

---

### 💻 **Cách 2: Command line (nếu có Maven/Git Bash)**

```powershell
cd D:\ProjectWeb\Project

# Nếu có Maven global
mvn clean compile spring-boot:run

# Nếu có Git Bash
bash ./mvnw.sh clean compile spring-boot:run

# Nếu chạy từ .m2 cache
java -cp "target/classes;..." dao.ProjectApplication
```

---

## 📍 Các URL trong app

| URL | Mô tả |
|-----|-------|
| http://localhost:1610/ | Trang chủ (Home) |
| http://localhost:1610/login | Trang đăng nhập |
| http://localhost:1610/logout | Thoát / Logout |
| http://localhost:1610/resources/** | Static resources (CSS, JS, images) |

---

## ✨ Giao diện sẽ show:

- ✓ Sidebar menu bên trái
- ✓ Top navigation bar
- ✓ Bootstrap CSS + Font Awesome icons
- ✓ Login form đầy đủ
- ✓ Responsive layout

---

## 🔍 Troubleshooting

**Nếu port 1610 bị chiếm:**
```
- Đổi port trong: src/main/resources/application.properties
- server.port=8080 (thay đổi từ 1610)
- Truy cập: http://localhost:8080
```

**Nếu JSP không render:**
- IntelliJ → File → Invalidate Caches / Restart
- Xóa folder: target/
- Run lại

**Nếu static resources (CSS) không load:**
- Kiểm tra: src/main/resources/webapp/static/
- Đảm bảo WebMvcConfig.java có bean viewResolver()

---

## 📊 Log khi app start thành công:

```
INFO  [...] Tomcat initialized with port(s): 1610 (http)
INFO  [...] Started ProjectApplication in X.XXX seconds
```

→ Nếu thấy dòng này = ✅ **APP SẴN SÀNG**


