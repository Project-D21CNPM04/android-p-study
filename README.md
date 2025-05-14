# 📱 P-Study: Trợ lý học tập Android

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-brightgreen" alt="Platform Android">
  <img src="https://img.shields.io/badge/Kotlin-1.9.0-blue" alt="Kotlin 1.9.0">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License MIT">
  <img src="https://img.shields.io/badge/Version-1.0.0-orange" alt="Version 1.0.0">
</p>

<p align="center">
  <img src="https://raw.githubusercontent.com/simple-icons/simple-icons/develop/icons/android.svg" width="100" height="100" alt="P-Study Logo">
</p>

P-Study là một ứng dụng trợ lý học tập toàn diện cho Android giúp học sinh tổ chức tài liệu học tập, tạo thẻ ghi nhớ, câu đố và sơ đồ tư duy từ ghi chú của họ, đồng thời tối ưu hóa trải nghiệm học tập bằng các tính năng được hỗ trợ bởi AI.

## 📋 Mục lục

- [🌟 Tính năng](#-tính-năng)
- [🏗️ Kiến trúc](#️-kiến-trúc)
- [🛠️ Công nghệ sử dụng](#️-công-nghệ-sử-dụng)
- [📊 Thiết kế hệ thống](#-thiết-kế-hệ-thống)
- [📄 Cấu trúc mã nguồn](#-cấu-trúc-mã-nguồn)
- [🚀 Cài đặt](#-cài-đặt)
- [🎮 Cách sử dụng](#-cách-sử-dụng)
- [📱 Màn hình](#-màn-hình)
- [🧩 Mô-đun](#-mô-đun)
- [🤝 Đóng góp](#-đóng-góp)
- [📜 Giấy phép](#-giấy-phép)

## 🌟 Tính năng

- **🔐 Xác thực người dùng**: Đăng ký và đăng nhập an toàn với Firebase Auth
- **📝 Ghi chú**: Tạo và quản lý ghi chú học tập từ nhiều nguồn khác nhau
  - Nhập văn bản
  - Trích xuất nội dung từ URL
  - Tải lên PDF/tài liệu
  - Chuyển đổi âm thanh thành văn bản
  - Chuyển đổi hình ảnh thành văn bản
- **🧠 Công cụ học tập được hỗ trợ bởi AI**:
  - Tạo thẻ ghi nhớ
  - Tạo câu đố
  - Hiển thị sơ đồ tư duy
  - Tạo tóm tắt
- **📂 Tổ chức**: Hệ thống thư mục để tổ chức tài liệu học tập
- **🔔 Nhắc nhở**: Nhắc nhở phiên học tập với thông báo
- **🌓 Chế độ tối**: Hỗ trợ giao diện sáng và tối
- **📊 Theo dõi tiến độ**: Giám sát tiến độ học tập của bạn

## 🏗️ Kiến trúc

P-Study tuân theo mô hình kiến trúc **MVVM (Model-View-ViewModel)** với các nguyên tắc **Clean Architecture**:

```
┌─────────────────────────────────────────────────────────────┐
│                       Presentation Layer                     │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────────┐  │
│  │   Activity  │◄──►│  ViewModel  │◄──►│   UI State      │  │
│  └─────────────┘    └─────────────┘    └─────────────────┘  │
└───────────────────────────┬─────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────┐
│                        Domain Layer                          │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────────┐  │
│  │ Use Cases   │◄──►│   Entities  │◄──►│  Repositories   │  │
│  └─────────────┘    └─────────────┘    └─────────────────┘  │
└───────────────────────────┬─────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────┐
│                         Data Layer                           │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────────┐  │
│  │ Repository  │◄──►│   Remote    │◄──►│     Local       │  │
│  │   Impl      │    │ Data Source │    │  Data Source    │  │
│  └─────────────┘    └─────────────┘    └─────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Dependency Injection

Chúng tôi sử dụng **Dagger Hilt** cho dependency injection để đảm bảo:
- Liên kết lỏng lẻo giữa các thành phần
- Khả năng kiểm thử
- Khả năng mở rộng
- Khả năng bảo trì

## 🛠️ Công nghệ sử dụng

P-Study sử dụng các công cụ và thư viện phát triển Android hiện đại:

### 📱 Frontend
- **Kotlin** - Ngôn ngữ lập trình chính
- **Jetpack Components**:
  - ViewModel - Để quản lý dữ liệu liên quan đến UI
  - Room - Để lưu trữ cơ sở dữ liệu cục bộ
  - LiveData/Flow - Để xử lý dữ liệu phản ứng
  - ViewBinding - Để truy cập view an toàn về kiểu
- **Material Design Components** - Để có giao diện người dùng/trải nghiệm người dùng nhất quán
- **Coroutines & Flow** - Cho các hoạt động bất đồng bộ

### 🔄 Tích hợp Backend
- **Retrofit** - Cho giao tiếp API RESTful
- **Gson** - Cho việc chuyển đổi JSON
- **OkHttp** - Cho HTTP client

### 🔐 Xác thực
- **Firebase Auth** - Cho xác thực người dùng

### 📜 Các thư viện khác
- **Markwon** - Để hiển thị Markdown
- **Lottie** - Cho hoạt ảnh

## 📊 Thiết kế hệ thống

### Sơ đồ luồng dữ liệu

```
┌───────────┐    ┌───────────┐    ┌───────────┐    ┌───────────┐
│           │    │           │    │           │    │           │
│  UI Layer │───►│ ViewModel │───►│ Use Cases │───►│Repository │
│           │    │           │    │           │    │           │
└───────────┘    └───────────┘    └───────────┘    └───────────┘
       ▲                                                  │
       │                                                  │
       │                                                  ▼
       │                                           ┌───────────┐
       │                                           │           │
       └───────────────────────────────────────────┤  Sources  │
                                                   │           │
                                                   └───────────┘
```

### Kiến trúc thành phần

```
┌─────────────────────────────────────────────────────────────┐
│                       P-Study App                            │
└─────────────────────────────────────────────────────────────┘
          │                              │
          ▼                              ▼
┌─────────────────┐              ┌─────────────────┐
│     :app        │◄────────────►│    :base        │
└─────────────────┘              └─────────────────┘
```

## 📄 Cấu trúc mã nguồn

```
android/
├── app/                  # Module ứng dụng chính
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/pstudy/
│   │   │   │   ├── data/           # Thành phần lớp dữ liệu
│   │   │   │   │   └── notification/
│   │   │   │   ├── di/             # Module dependency injection
│   │   │   │   ├── ext/            # Hàm mở rộng Kotlin
│   │   │   │   ├── permission/      # Xử lý quyền
│   │   │   │   ├── view/           # Thành phần UI
│   │   │   │   │   ├── authentication/  # Đăng nhập/đăng ký
│   │   │   │   │   ├── folder/      # Quản lý thư mục
│   │   │   │   │   ├── home/        # Màn hình chính
│   │   │   │   │   ├── input/       # Nhập ghi chú
│   │   │   │   │   ├── result/      # Hiển thị kết quả
│   │   │   │   │   └── settings/    # Cài đặt ứng dụng
│   │   │   │   └── PStudyApp.kt     # Lớp ứng dụng
│   │   │   ├── res/             # Tài nguyên (layouts, drawables, v.v.)
│   │   │   └── AndroidManifest.xml
│   │   ├── androidTest/         # Kiểm thử thiết bị
│   │   └── test/                # Kiểm thử đơn vị
│   ├── build.gradle.kts         # Tệp build module app
│   └── proguard-rules.pro       # Quy tắc ProGuard
├── base/                        # Module cơ sở với các thành phần dùng chung
│   ├── src/                     # Tệp nguồn cho module cơ sở
│   └── build.gradle.kts         # Tệp build module cơ sở
├── gradle/                      # Cấu hình Gradle
├── build.gradle.kts             # Tệp build cấp dự án
└── settings.gradle.kts          # Cài đặt dự án
```

## 🚀 Cài đặt

### Yêu cầu
- Android Studio Arctic Fox hoặc mới hơn
- JDK 11 hoặc cao hơn
- Android SDK (API level 24+)

### Hướng dẫn cài đặt

1. **Sao chép kho lưu trữ**
   ```bash
   git clone https://github.com/your-username/android-p-study.git
   cd android-p-study
   ```

2. **Mở trong Android Studio**
   - Khởi chạy Android Studio
   - Chọn "Open an existing Android Studio project"
   - Điều hướng đến thư mục đã sao chép và nhấp vào "Open"

3. **Xây dựng dự án**
   - Đợi Gradle sync hoàn thành
   - Chọn "Build > Make Project" từ menu

4. **Chạy ứng dụng**
   - Kết nối thiết bị Android hoặc sử dụng máy ảo
   - Chọn "Run > Run 'app'" từ menu

## 🎮 Cách sử dụng

### Xác thực người dùng
1. Khởi chạy ứng dụng
2. Đăng ký bằng email hoặc đăng nhập với thông tin đăng nhập hiện có
3. Tùy chọn sử dụng các tùy chọn đăng nhập xã hội

### Tạo tài liệu học tập
1. Điều hướng đến màn hình Nhập
2. Chọn phương thức nhập của bạn (văn bản, URL, tệp, âm thanh, hình ảnh)
3. Cung cấp nội dung hoặc tải lên tài liệu của bạn
4. Nhấn "Xử lý" để phân tích và lưu trữ nội dung của bạn

### Tạo công cụ học tập
1. Mở bất kỳ ghi chú đã lưu nào
2. Chọn công cụ bạn muốn tạo (thẻ ghi nhớ, câu đố, sơ đồ tư duy hoặc tóm tắt)
3. AI sẽ xử lý nội dung của bạn và tạo công cụ đã chọn
4. Lưu hoặc sửa đổi nội dung đã tạo nếu cần

### Tổ chức nội dung
1. Tạo thư mục để tổ chức tài liệu liên quan
2. Sử dụng chức năng tìm kiếm để tìm nội dung cụ thể
3. Đặt lời nhắc cho các buổi học

## 📱 Màn hình

Ứng dụng bao gồm các màn hình chính sau:

- **Đăng nhập/Đăng ký**: Xác thực người dùng
- **Trang chủ**: Bảng điều khiển với tài liệu gần đây và truy cập nhanh vào các tính năng
- **Nhập**: Các phương pháp khác nhau để nhập tài liệu học tập
- **Kết quả**: Hiển thị nội dung đã xử lý
- **Thư mục**: Tổ chức tài liệu học tập
- **Cài đặt**: Tùy chọn ứng dụng và hồ sơ người dùng

## 🧩 Mô-đun

### Module :app
Chứa mã ứng dụng chính, bao gồm các thành phần UI, logic nghiệp vụ và xử lý dữ liệu.

### Module :base
Chứa các thành phần dùng chung, tiện ích, lớp cơ sở và các phần mở rộng được sử dụng trong toàn bộ ứng dụng.

## 🤝 Đóng góp

Chúng tôi hoan nghênh đóng góp! Dưới đây là cách bạn có thể đóng góp:

1. Fork kho lưu trữ
2. Tạo nhánh tính năng (`git checkout -b feature/amazing-feature`)
3. Commit các thay đổi của bạn (`git commit -m 'Add some amazing feature'`)
4. Push lên nhánh (`git push origin feature/amazing-feature`)
5. Mở Pull Request

## 📜 Giấy phép

Dự án này được cấp phép theo Giấy phép MIT - xem tệp LICENSE để biết chi tiết.

---

<p align="center">
  Được tạo với ❤️ bởi Nhóm P-Study
</p>
