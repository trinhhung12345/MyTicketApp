# MyTicketApp

**MyTicketApp** là một ứng dụng Android hiện đại được xây dựng để quản lý và đặt vé. Dự án được phát triển tuân thủ các nguyên tắc của **Clean Architecture** và sử dụng các công nghệ mới nhất trong hệ sinh thái Android.

## 🏗 Cấu trúc dự án

Dự án được chia thành các module rõ rệt để đảm bảo tính đóng gói và dễ dàng bảo trì:

*   **`:app` (Presentation Layer):** Chứa mã nguồn UI (Jetpack Compose), ViewModels, và DI setup cho tầng ứng dụng. Đây là nơi điều hướng và hiển thị dữ liệu cho người dùng.
*   **`:domain` (Domain Layer):** Chứa các Business Logic, Models (POJO), và Interfaces cho Repositories. Module này hoàn toàn độc lập với các thư viện Android và tầng Data.
*   **`:data` (Data Layer):** Chứa các triển khai của Repositories, API Services (Retrofit), Local Database và các Mapper để chuyển đổi dữ liệu từ tầng Data sang Domain.

## 🛠 Thư viện và Công nghệ

Dự án sử dụng các công nghệ tiên tiến nhất:

*   **Ngôn ngữ:** [Kotlin](https://kotlinlang.org/) (v2.0.21) với Kotlin Symbol Processing (KSP).
*   **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) với Material Design 3.
*   **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) (Dagger Hilt) để quản lý phụ thuộc.
*   **Networking:** [Retrofit 2](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) để tương tác với RESTful APIs.
*   **Serialization:** [Gson](https://github.com/google/gson) để xử lý dữ liệu JSON.
*   **Architecture:** Clean Architecture kết hợp với mô hình **MVVM** (Model-View-ViewModel).
*   **Lifecycle:** Android Architecture Components (ViewModel, Lifecycle Runtime).

## 🚀 Cách chạy ứng dụng

1.  **Yêu cầu hệ thống:**
    *   Android Studio Ladybug (hoặc mới hơn).
    *   JDK 17+.
    *   Android SDK 34+.

2.  **Các bước thực hiện:**
    *   Clone repository này về máy.
    *   Mở dự án bằng Android Studio.
    *   Chờ Gradle đồng bộ hóa (Sync project).
    *   Chọn thiết bị mô phỏng (Emulator) hoặc thiết bị thật.
    *   Nhấn nút **Run** (biểu tượng Play màu xanh).

## 💻 Hướng dẫn phát triển

*   **Thêm tính năng mới:**
    1.  Định nghĩa Data Model và Repository Interface trong module `:domain`.
    2.  Triển khai Repository và API Service trong module `:data`.
    3.  Tạo ViewModel và các Composable screens trong module `:app`.
*   **Quản lý thư viện:** Các phiên bản thư viện được tập trung quản lý tại file `gradle/libs.versions.toml`.
*   **Dependency Injection:** Luôn sử dụng `@Inject` và `@HiltViewModel` để đảm bảo tính module hóa và dễ test.

---
*Phát triển bởi [Tên của bạn/Team]*
