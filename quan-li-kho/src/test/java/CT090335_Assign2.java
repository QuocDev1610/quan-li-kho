import java.util.ArrayList;
import java.util.EmptyStackException;
public class CT090335_Assign2 {



        // ==========================================
        // YÊU CẦU 1: Xây dựng lớp Stack<T> tổng quát
        // ==========================================
        public static class Stack<T> {
            // Dùng ArrayList<T> làm cấu trúc lưu trữ bên trong
            private ArrayList<T> elements;

            public Stack() {
                this.elements = new ArrayList<>();
            }

            // Thêm phần tử vào đỉnh ngăn xếp
            public void push(T item) {
                elements.add(item);
            }

            // Lấy và xóa phần tử ở đỉnh ngăn xếp
            public T pop() {
                if (isEmpty()) {
                    throw new EmptyStackException();
                }
                // Xóa và trả về phần tử cuối cùng của ArrayList (đỉnh stack)
                return elements.remove(elements.size() - 1);
            }

            // Xem phần tử ở đỉnh ngăn xếp mà không xóa
            public T peek() {
                if (isEmpty()) {
                    throw new EmptyStackException();
                }
                return elements.get(elements.size() - 1);
            }

            // Kiểm tra ngăn xếp có rỗng không
            public boolean isEmpty() {
                return elements.isEmpty();
            }

            // Trả về số lượng phần tử của ngăn xếp
            public int size() {
                return elements.size();
            }
        }

        // ==========================================
        // YÊU CẦU 2: Phương thức đếm phần tử tổng quát
        // ==========================================
        public static <T> int countElements(T[] array, T target) {
            int count = 0;
            for (T element : array) {
                // Xử lý an toàn tránh lỗi NullPointerException
                if (element == null) {
                    if (target == null) {
                        count++;
                    }
                } else if (element.equals(target)) {
                    count++;
                }
            }
            return count;
        }

        // ==========================================
        // CHẠY THỬ NGHIỆM (TESTING)
        // ==========================================
        public static void main(String[] args) {

            System.out.println("--- 1. Thử nghiệm với Stack<Integer> ---");
            Stack<Integer> intStack = new Stack<>();
            intStack.push(10);
            intStack.push(20);
            intStack.push(30);
            System.out.println("Kích thước ban đầu: " + intStack.size());
            System.out.println("Phần tử trên cùng (peek): " + intStack.peek());
            System.out.println("Lấy ra (pop): " + intStack.pop());
            System.out.println("Kích thước sau khi pop: " + intStack.size());

            System.out.println("\n--- 2. Thử nghiệm với Stack<String> ---");
            Stack<String> stringStack = new Stack<>();
            stringStack.push("Java");
            stringStack.push("C++");
            System.out.println("Stack có rỗng không? " + stringStack.isEmpty());
            System.out.println("Lấy ra (pop): " + stringStack.pop());
            System.out.println("Lấy ra (pop): " + stringStack.pop());
            System.out.println("Stack có rỗng không sau khi lấy hết? " + stringStack.isEmpty());

            System.out.println("\n--- 3. Thử nghiệm hàm countElements ---");
            // Test với mảng số nguyên
            Integer[] intArray = {1, 2, 5, 2, 4, 2, 9};
            int countInt = countElements(intArray, 2);
            System.out.println("Số lần xuất hiện của số '2' trong mảng intArray: " + countInt);

            // Test với mảng chuỗi
            String[] strArray = {"apple", "banana", "orange", "apple", "mango"};
            int countStr = countElements(strArray, "apple");
            System.out.println("Số lần xuất hiện của chữ 'apple' trong mảng strArray: " + countStr);
        }
    }

