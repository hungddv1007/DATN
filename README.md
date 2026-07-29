# GymPro — DATN

Hệ thống quản lý phòng gym gồm backend Spring Boot, frontend React và SQL Server.

## Cấu trúc

```text
DATN/
├─ source/datn/        Backend Java 21, Spring Boot 4, Maven
├─ source/frontend/    React 19, Vite
├─ database/           Schema, seed data và migration SQL Server
└─ .env.example        Danh sách biến môi trường bắt buộc
```

## Chuẩn bị môi trường

- Java 21
- Node.js 22+
- SQL Server
- Maven có thể dùng qua `mvnw`/`mvnw.cmd`

Sao chép `.env.example` thành `.env` tại thư mục gốc để lưu giá trị cục bộ.
Backend và frontend đều đã được cấu hình đọc file này khi chạy từ thư mục dự án.
Không commit `.env`.

Các biến bắt buộc:

- `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET` (Base64, tối thiểu 32 byte entropy)
- `MAIL_USERNAME`, `MAIL_PASSWORD`
- `GOOGLE_CLIENT_ID`
- `VITE_GOOGLE_CLIENT_ID` (giá trị public được frontend sử dụng)
- `GEMINI_API_KEY`

Các biến Gemini có giá trị mặc định phù hợp Free Tier:

- `GEMINI_NUTRITION_MODEL=gemini-3.5-flash-lite`
- `GEMINI_CHAT_MODEL=gemini-3.5-flash-lite`
- `GEMINI_CHAT_COMPLEX_MODEL=gemini-3.6-flash`
- `GEMINI_FREE_TIER_REQUESTS_PER_HOUR=10`
- `GEMINI_FREE_TIER_REQUESTS_PER_DAY=50`
- `GEMINI_CONVERSATION_RETENTION_DAYS=90`
- `CORS_ALLOWED_ORIGINS` cho phép cả `localhost` và `127.0.0.1` ở cổng
  phát triển 5173/3000 theo `.env.example`.

Spring Boot đọc `../../.env` qua `spring.config.import`. Có thể đặt biến
`ENV_FILE` thành đường dẫn khác khi chạy từ một working directory khác.
Vite dùng `envDir: '../../'` và chỉ đưa các biến có tiền tố `VITE_` vào bundle.
Không bao giờ đặt mật khẩu hoặc API key vào biến `VITE_*`.

## Database

Database mới: chạy [database/GymProDB.sql](database/GymProDB.sql), sau đó chạy
seed data nếu cần.

Database đang tồn tại: không chạy lại file khởi tạo. Chạy migration theo thứ tự:

1. [database/migrations/V2__payment_membership_and_otp_hardening.sql](database/migrations/V2__payment_membership_and_otp_hardening.sql)
2. [database/migrations/V3__ai_chat.sql](database/migrations/V3__ai_chat.sql)

Ứng dụng mặc định dùng `ddl-auto=validate`, vì vậy schema phải được migrate trước
khi khởi động. Có thể tạm đặt `JPA_DDL_AUTO=update` chỉ trong môi trường phát triển,
nhưng không nên dùng ở production.

## Chạy dự án

Backend:

```powershell
cd source/datn
.\mvnw.cmd spring-boot:run
```

Frontend:

```powershell
cd source/frontend
npm ci
npm run dev
```

Vite chạy ở `http://localhost:5173` và proxy `/api` sang backend ở
`http://localhost:8080`.

## Kiểm tra

```powershell
cd source/datn
.\mvnw.cmd test

cd ../frontend
npm run lint
npm run build
```

GitHub Actions chạy các bước này cho mọi push và pull request.

## Nền tảng AI

Phân tích dinh dưỡng dùng `gemini-3.5-flash-lite` và Structured Output với JSON
Schema. Kết quả vẫn được kiểm tra phạm vi calo/macro ở backend trước khi trả cho
PT.

Chatbot MEMBER dùng Gemini Interactions API:

- Chế độ thường: `gemini-3.5-flash-lite`.
- Nút **Phân tích sâu**: `gemini-3.6-flash`.
- Phản hồi được stream qua SSE.
- Luôn gửi `store=false`; Google không quản lý lịch sử hội thoại.
- Lịch sử nằm trong `ai_conversations` và `ai_messages`, tự xóa sau 90 ngày.
- Thành viên có thể xóa hội thoại bất kỳ lúc nào.
- Hồ sơ thể chất chỉ được đưa vào ngữ cảnh sau khi thành viên đồng ý.
- Không gửi email, số điện thoại, JWT hay thông tin đăng nhập vào prompt.
- Chatbot chỉ đọc dữ liệu; không sửa lịch tập, thực đơn hay hồ sơ.
- Giới hạn Free Tier được giữ trong bộ nhớ theo từng tài khoản. Khi backend
  khởi động lại, bộ đếm bắt đầu lại; quota thật của Google vẫn được áp dụng.

API key được gửi trong header, có timeout cấu hình bằng `GEMINI_TIMEOUT`, và lỗi
nhà cung cấp không được trả nguyên văn cho client.

Để thử chatbot:

1. Chạy migration V2 và V3.
2. Khởi động backend và frontend.
3. Đăng nhập tài khoản có role `MEMBER`.
4. Mở một trang `/member/*`, sau đó chọn nút **GymPro AI** ở góc phải.

Thông tin do AI cung cấp chỉ mang tính tham khảo và không thay thế tư vấn y tế.

## Lưu ý vận hành

- Upload chỉ nhận JPG/PNG/GIF hợp lệ, tối đa 5 MB mặc định.
- Membership chỉ được kích hoạt/gia hạn/nâng cấp sau khi admin xác nhận giao dịch.
- Scheduler đồng bộ trạng thái `EXPIRED` mỗi giờ.
- Hãy thu hồi GCP API key từng xuất hiện trong lịch sử Git cũ, kể cả khi lịch sử
  branch đã được làm sạch.
