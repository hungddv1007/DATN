IF DB_ID('GymProDB') IS NOT NULL
BEGIN
    ALTER DATABASE GymProDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE GymProDB;
END
GO

CREATE DATABASE GymProDB;
GO
USE GymProDB;
GO

CREATE TABLE vai_tro (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    ten         NVARCHAR(20) NOT NULL UNIQUE   -- ADMIN, PT, MEMBER
);

CREATE TABLE nguoi_dung (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    vai_tro_id      INT NOT NULL,
    email           NVARCHAR(100) NOT NULL UNIQUE,
    mat_khau        NVARCHAR(255) NOT NULL,
    ho_ten          NVARCHAR(100) NOT NULL,
    so_dien_thoai   NVARCHAR(20),
    anh_dai_dien    NVARCHAR(500),
    trang_thai      BIT DEFAULT 1,
    ngay_tao        DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (vai_tro_id) REFERENCES vai_tro(id)
);

CREATE TABLE ho_so_pt (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    nguoi_dung_id   INT NOT NULL UNIQUE,
    chuyen_mon      NVARCHAR(255),
    tieu_su         NVARCHAR(MAX),
    chung_chi       NVARCHAR(MAX),
    diem_danh_gia   DECIMAL(2,1) DEFAULT 0,
    FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id)
);

CREATE TABLE ho_so_hoi_vien (
    id                  INT IDENTITY(1,1) PRIMARY KEY,
    nguoi_dung_id       INT NOT NULL UNIQUE,
    the_trang           NVARCHAR(MAX),     -- PT ghi khi danh gia the luc ban dau
    FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id)
);

CREATE TABLE goi_tap (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    ten             NVARCHAR(50) NOT NULL,
    gia             DECIMAL(12,0) NOT NULL,
    so_ngay         INT NOT NULL,
    mo_ta           NVARCHAR(MAX),
    co_pt           BIT DEFAULT 0,
    duoc_chon_pt    BIT DEFAULT 0,
    co_che_do_an    BIT DEFAULT 0
);

CREATE TABLE khuyen_mai (
    id                  INT IDENTITY(1,1) PRIMARY KEY,
    ma_khuyen_mai       NVARCHAR(50) NOT NULL UNIQUE,
    phan_tram_giam      INT NOT NULL,
    goi_tap_id          INT,               -- NULL = ap dung tat ca goi tap
    ngay_bat_dau        DATE NOT NULL,
    ngay_ket_thuc       DATE NOT NULL,
    luot_su_dung_toi_da INT,
    luot_da_su_dung     INT DEFAULT 0,
    dang_hoat_dong      BIT DEFAULT 1,
    FOREIGN KEY (goi_tap_id) REFERENCES goi_tap(id)
);

CREATE TABLE dang_ky_goi_tap (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    nguoi_dung_id   INT NOT NULL,
    goi_tap_id      INT NOT NULL,
    pt_id           INT,                   -- NULL = Goi Basic khong co PT
    ngay_bat_dau    DATE NOT NULL,
    ngay_ket_thuc   DATE NOT NULL,
    trang_thai      NVARCHAR(20) DEFAULT N'HOAT_DONG'
                    CHECK (trang_thai IN (N'HOAT_DONG', N'HET_HAN', N'TAM_DUNG', N'DA_HUY')),
    ly_do_tam_dung  NVARCHAR(255),
    ngay_tao        DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id),
    FOREIGN KEY (goi_tap_id) REFERENCES goi_tap(id),
    FOREIGN KEY (pt_id) REFERENCES nguoi_dung(id)
);

CREATE TABLE giao_dich (
    id                      INT IDENTITY(1,1) PRIMARY KEY,
    dang_ky_goi_tap_id      INT NOT NULL,
    khuyen_mai_id           INT,
    so_tien                 DECIMAL(12,0) NOT NULL,
    so_tien_goc             DECIMAL(12,0),
    phuong_thuc_thanh_toan  NVARCHAR(20)
                            CHECK (phuong_thuc_thanh_toan IN (N'TIEN_MAT', N'CHUYEN_KHOAN', N'TRUC_TUYEN')),
    trang_thai              NVARCHAR(20) DEFAULT N'CHO_DUYET'
                            CHECK (trang_thai IN (N'CHO_DUYET', N'DA_XAC_NHAN', N'DA_HUY')),
    nguoi_xac_nhan          INT,
    ngay_tao                DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (dang_ky_goi_tap_id) REFERENCES dang_ky_goi_tap(id),
    FOREIGN KEY (khuyen_mai_id) REFERENCES khuyen_mai(id),
    FOREIGN KEY (nguoi_xac_nhan) REFERENCES nguoi_dung(id)
);

CREATE TABLE bai_tap (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    ten             NVARCHAR(100) NOT NULL,
    nhom_co         NVARCHAR(50),
    mo_ta           NVARCHAR(MAX),
    duong_dan_video NVARCHAR(500),
    nguoi_tao       INT NOT NULL,
    FOREIGN KEY (nguoi_tao) REFERENCES nguoi_dung(id)
);

CREATE TABLE lo_trinh_tap_luyen (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    pt_id           INT NOT NULL,
    hoi_vien_id     INT,                   -- NULL = lo trinh mau (template)
    ten             NVARCHAR(200) NOT NULL,
    la_ban_mau      BIT DEFAULT 0,
    trang_thai      NVARCHAR(20) DEFAULT N'BAN_NHAP'
                    CHECK (trang_thai IN (N'BAN_NHAP', N'DA_GIAO', N'HOAN_THANH')),
    ngay_bat_dau    DATE,
    ngay_tao        DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (pt_id) REFERENCES nguoi_dung(id),
    FOREIGN KEY (hoi_vien_id) REFERENCES nguoi_dung(id)
);

CREATE TABLE buoi_tap (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    lo_trinh_id     INT NOT NULL,
    tuan_thu        INT NOT NULL,
    ngay_thu        INT NOT NULL,
    ten             NVARCHAR(100),         -- Vi du: "Ngay tap nguc", "Ngay nghi"
    la_ngay_nghi    BIT DEFAULT 0,
    FOREIGN KEY (lo_trinh_id) REFERENCES lo_trinh_tap_luyen(id) ON DELETE CASCADE
);

CREATE TABLE chi_tiet_buoi_tap (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    buoi_tap_id     INT NOT NULL,
    bai_tap_id      INT NOT NULL,
    so_hiep         INT DEFAULT 3,
    so_lan_lap      INT DEFAULT 10,
    muc_ta_kg       DECIMAL(5,1),
    ghi_chu         NVARCHAR(255),
    FOREIGN KEY (buoi_tap_id) REFERENCES buoi_tap(id) ON DELETE CASCADE,
    FOREIGN KEY (bai_tap_id) REFERENCES bai_tap(id)
);

CREATE TABLE diem_danh (
    id                      INT IDENTITY(1,1) PRIMARY KEY,
    hoi_vien_id             INT NOT NULL,
    buoi_tap_id             INT NOT NULL,
    thoi_gian_diem_danh     DATETIME2 DEFAULT GETDATE(),
    trang_thai              BIT DEFAULT 1,         -- 1 = co mat, 0 = vang
    FOREIGN KEY (hoi_vien_id) REFERENCES nguoi_dung(id),
    FOREIGN KEY (buoi_tap_id) REFERENCES buoi_tap(id)
);

CREATE TABLE ghi_chu_pt (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    pt_id           INT NOT NULL,
    hoi_vien_id     INT NOT NULL,
    noi_dung        NVARCHAR(MAX) NOT NULL,
    ngay_tao        DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (pt_id) REFERENCES nguoi_dung(id),
    FOREIGN KEY (hoi_vien_id) REFERENCES nguoi_dung(id)
);

CREATE TABLE nhan_xet_pt (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    pt_id           INT NOT NULL,
    hoi_vien_id     INT NOT NULL,
    lo_trinh_id     INT,
    noi_dung        NVARCHAR(MAX) NOT NULL,
    ngay_tao        DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (pt_id) REFERENCES nguoi_dung(id),
    FOREIGN KEY (hoi_vien_id) REFERENCES nguoi_dung(id),
    FOREIGN KEY (lo_trinh_id) REFERENCES lo_trinh_tap_luyen(id)
);

CREATE TABLE che_do_an (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    hoi_vien_id     INT NOT NULL,
    pt_id           INT NOT NULL,
    ngay            DATE NOT NULL,
    bua_sang        NVARCHAR(MAX),
    bua_trua        NVARCHAR(MAX),
    bua_toi         NVARCHAR(MAX),
    FOREIGN KEY (hoi_vien_id) REFERENCES nguoi_dung(id),
    FOREIGN KEY (pt_id) REFERENCES nguoi_dung(id)
);

CREATE TABLE danh_gia_pt (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    hoi_vien_id     INT NOT NULL,
    pt_id           INT NOT NULL,
    so_sao          INT NOT NULL CHECK (so_sao BETWEEN 1 AND 5),
    nhan_xet        NVARCHAR(MAX),
    ngay_tao        DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (hoi_vien_id) REFERENCES nguoi_dung(id),
    FOREIGN KEY (pt_id) REFERENCES nguoi_dung(id)
);

CREATE TABLE bai_viet (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    tac_gia_id      INT NOT NULL,
    tieu_de         NVARCHAR(300) NOT NULL,
    noi_dung        NVARCHAR(MAX) NOT NULL,
    anh_dai_dien    NVARCHAR(500),
    trang_thai      NVARCHAR(20) DEFAULT N'DA_DANG'
                    CHECK (trang_thai IN (N'BAN_NHAP', N'DA_DANG')),
    ngay_tao        DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (tac_gia_id) REFERENCES nguoi_dung(id)
);

CREATE TABLE thong_bao (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    nguoi_nhan_id   INT NOT NULL,
    nguoi_gui_id    INT,
    tieu_de         NVARCHAR(200) NOT NULL,
    noi_dung        NVARCHAR(MAX),
    da_doc          BIT DEFAULT 0,
    ngay_tao        DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (nguoi_nhan_id) REFERENCES nguoi_dung(id),
    FOREIGN KEY (nguoi_gui_id) REFERENCES nguoi_dung(id)
);