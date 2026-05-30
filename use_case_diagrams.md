# 🗺️ Sơ đồ Use Case - Hệ thống GymPro

Dưới đây là sơ đồ Use Case tổng quát và các sơ đồ phân rã chi tiết cho từng đối tượng (Actor) dựa trên tài liệu phân tích dự án của bạn.

## 1. Sơ đồ Use Case Tổng quát

Sơ đồ này thể hiện cái nhìn toàn cảnh về hệ thống, bao gồm 4 nhóm người dùng chính và các vùng chức năng cốt lõi của họ.

```mermaid
flowchart LR
    %% Định nghĩa Actors
    Khach([Khách vãng lai])
    HV([Hội viên])
    PT([Huấn luyện viên - PT])
    Admin([Quản trị viên])

    %% Các nhóm chức năng lớn
    subgraph HeThong [Hệ thống GymPro]
        UC_CongKhai(Chức năng công khai)
        UC_CaNhan(Quản lý cá nhân & Gói tập)
        UC_TapLuyen(Quá trình tập luyện)
        UC_PT(Quản lý tập luyện Hội viên)
        UC_QuanTri(Quản trị hệ thống)
    end

    %% Liên kết Actor với Use Case
    Khach --> UC_CongKhai
    
    HV --> UC_CongKhai
    HV --> UC_CaNhan
    HV --> UC_TapLuyen
    
    PT --> UC_CongKhai
    PT --> UC_PT
    
    Admin --> UC_QuanTri
    Admin --> UC_CongKhai

    style Khach fill:#f9f,stroke:#333,stroke-width:2px
    style HV fill:#bbf,stroke:#333,stroke-width:2px
    style PT fill:#bfb,stroke:#333,stroke-width:2px
    style Admin fill:#fbb,stroke:#333,stroke-width:2px
```

---

## 2. Sơ đồ phân rã: Khách vãng lai & Xác thực

```mermaid
flowchart LR
    Actor([Khách vãng lai])

    subgraph CongKhai [Trang công khai]
        XemTrangChu(Xem trang chủ)
        XemGoiTap(Xem danh sách gói tập)
        XemBlog(Xem bài viết, tin tức)
    end
    
    subgraph XacThuc [Xác thực]
        DangKý(Đăng ký tài khoản)
        DangNhap(Đăng nhập)
        QuenPass(Quên mật khẩu)
    end

    Actor --> XemTrangChu
    Actor --> XemGoiTap
    Actor --> XemBlog
    Actor --> DangKý
    Actor --> DangNhap
    Actor --> QuenPass

    style Actor fill:#f9f,stroke:#333,stroke-width:2px
```

---

## 3. Sơ đồ phân rã: Hội viên

```mermaid
flowchart LR
    Actor([Hội viên])

    subgraph TaiKhoan [Tài khoản]
        Qlinfo(Quản lý hồ sơ)
        GiaoDich(Xem lịch sử giao dịch)
        ThongBao(Xem thông báo)
    end

    subgraph GoiTap [Gói tập]
        MuaGoi(Đăng ký / Mua gói tập)
        ApMa(Áp dụng khuyến mãi)
        ChonPT(Chọn PT - dành cho VIP)
        MuaGoi -. extend .-> ApMa
        MuaGoi -. extend .-> ChonPT
    end
    
    subgraph TapLuyen [Tập luyện & Dịch vụ]
        XemLoTrinh(Xem lộ trình & bài tập)
        DiemDanh(Điểm danh buổi tập)
        XemCheDoAn(Xem thực đơn - VIP)
        DanhGiaPT(Đánh giá PT)
    end

    Actor --> TaiKhoan
    Actor --> GoiTap
    Actor --> TapLuyen

    style Actor fill:#bbf,stroke:#333,stroke-width:2px
```

---

## 4. Sơ đồ phân rã: Huấn luyện viên (PT)

```mermaid
flowchart LR
    Actor([Huấn luyện viên - PT])

    subgraph HoSoPT [Hồ sơ PT]
        CapNhatHoSo(Cập nhật hồ sơ/chuyên môn)
        XemLichSu(Xem lịch sử đánh giá)
    end

    subgraph QuanLyHoiVien [Quản lý Hội viên]
        XemDanhSachHV(Xem danh sách HV được giao)
        GhiChu(Ghi chú về Hội viên)
        LenLoTrinh(Tạo lộ trình tập luyện)
        NhanXet(Nhận xét buổi tập)
        LenThucDon(Lên chế độ ăn - VIP)
    end
    
    subgraph ThuVien [Thư viện]
        XemBaiTap(Xem thư viện bài tập)
    end

    Actor --> HoSoPT
    Actor --> QuanLyHoiVien
    Actor --> ThuVien

    style Actor fill:#bfb,stroke:#333,stroke-width:2px
```

---

## 5. Sơ đồ phân rã: Quản trị viên (Admin)

```mermaid
flowchart LR
    Actor([Quản trị viên - Admin])

    subgraph QLCore [Quản lý Cốt lõi]
        QLNguoiDung(Quản lý Người dùng/PT/HV)
        QLGoiTap(Quản lý Gói tập)
        QLKhuyenMai(Quản lý Khuyến mãi)
    end

    subgraph QLGiaoDich [Quản lý Tài chính]
        QLHoaDon(Duyệt/Hủy thanh toán)
        ThongKe(Báo cáo Doanh thu)
    end

    subgraph QLNoiDung [Quản lý Nội dung]
        QLBaiTap(Quản lý Thư viện bài tập)
        QLBlog(Quản lý Tin tức/Blog)
        GuiThongBao(Gửi thông báo hệ thống)
    end

    Actor --> QLCore
    Actor --> QLGiaoDich
    Actor --> QLNoiDung

    style Actor fill:#fbb,stroke:#333,stroke-width:2px
```
