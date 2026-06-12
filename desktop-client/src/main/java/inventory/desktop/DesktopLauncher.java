package inventory.desktop;

/**
 * Entry point dùng cho executable JAR.
 *
 * JavaFX không nên để Main-Class trỏ trực tiếp vào class extends Application
 * khi chạy bằng java -jar, vì launcher của JVM có thể báo thiếu JavaFX runtime.
 * Class trung gian này không extends Application nên tránh được lỗi đó.
 */
public final class DesktopLauncher {
    private DesktopLauncher() {
    }

    public static void main(String[] args) {
        inventory.desktop.MainApp.main(args);
    }
}
