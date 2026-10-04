# MoMo Sandbox cho GymPro

## 1. Chuẩn bị thông tin Sandbox

Đăng ký MoMo for Business và lấy ba giá trị của môi trường Test:

- `Partner Code`
- `Access Key`
- `Secret Key`

Không đưa các khóa này vào source code, ảnh chụp hoặc Git.

## 2. Cấu hình `.env`

```properties
MOMO_ENABLED=true
MOMO_BASE_URL=https://test-payment.momo.vn
MOMO_PARTNER_CODE=...
MOMO_ACCESS_KEY=...
MOMO_SECRET_KEY=...
MOMO_REDIRECT_URL=https://qvsmt6pq-5173.asse.devtunnels.ms/member/payment/momo
MOMO_IPN_URL=https://qvsmt6pq-8080.asse.devtunnels.ms/api/public/payments/momo/ipn
MOMO_STORE_NAME=GymPro
MOMO_EXPIRATION_MINUTES=15
```

Hai VS Code Dev Tunnel phải được bật và đặt quyền truy cập công khai. MoMo không thể gọi
IPN nếu tunnel cổng `8080` yêu cầu đăng nhập Microsoft/GitHub hoặc đang tắt.
Khởi động frontend bằng đúng Vite trên cổng `5173`; dự án đã cho phép Host của tunnel
`qvsmt6pq-5173.asse.devtunnels.ms` và proxy các yêu cầu `/api` về backend local.

## 3. Cập nhật cơ sở dữ liệu

`transactions` đã có thêm các cột lưu định danh, QR và kết quả từ cổng thanh toán.
Với quy trình một file SQL hiện tại của dự án, chạy lần lượt:

1. `database/RemoveSql.sql`
2. `database/GymProDB.sql`
3. `database/seed data only.sql`

## 4. Chuẩn bị điện thoại test

Tải ứng dụng MoMo Test từ trang MoMo Developers. Ví Sandbox sử dụng tiền thử nghiệm,
không trừ tiền thật. Tạo ví test và nạp số dư thử nghiệm trước buổi demo.

Lưu ý: ứng dụng MoMo Test có thể yêu cầu gỡ ứng dụng MoMo thật trên cùng điện thoại.
Nên dùng điện thoại Android phụ hoặc thiết bị dành riêng cho buổi bảo vệ.

## 5. Luồng kiểm thử

1. Đăng nhập tài khoản Member chưa có giao dịch chờ.
2. Chọn gói tập, thời hạn, mã giảm giá và PT nếu gói yêu cầu.
3. Chọn `MoMo Test` rồi xác nhận điều khoản và giao dịch.
4. Trang GymPro hiển thị QR và tự kiểm tra trạng thái mỗi 2 giây.
5. Dùng MoMo Test quét QR và xác nhận.
6. MoMo POST IPN tới backend qua Dev Tunnel.
7. Backend xác minh HMAC, Partner Code, Order ID, Request ID và số tiền.
8. Giao dịch chuyển sang `CONFIRMED`, gói được áp dụng và email biên nhận được gửi nền.

Nếu giao diện chưa đổi trạng thái do IPN chậm, bấm **Kiểm tra lại với MoMo** để backend
gọi API truy vấn giao dịch. Không dùng tham số trên URL chuyển hướng để xác nhận thanh toán.
Nếu không muốn tiếp tục hoặc QR không tạo được, bấm **Hủy thanh toán** để kết thúc giao dịch
đang chờ và giải phóng mã khuyến mãi đã giữ chỗ trước khi tạo giao dịch mới.

## 6. Giới hạn

- Giao dịch ví MoMo: từ `1.000đ` đến `50.000.000đ`.
- Admin không được duyệt thủ công giao dịch `MOMO`.
- Callback lặp được xử lý idempotent; một giao dịch không thể áp dụng gói hai lần.
- Đặt `MOMO_ENABLED=false` khi không muốn hiển thị phương thức MoMo.
