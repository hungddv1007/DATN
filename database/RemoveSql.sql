USE [master];
GO

IF DB_ID(N'GymProDB') IS NOT NULL
BEGIN
    ALTER DATABASE [GymProDB]
        SET SINGLE_USER
        WITH ROLLBACK IMMEDIATE;

    DROP DATABASE [GymProDB];

    PRINT N'Đã xóa database GymProDB thành công.';
END
ELSE
BEGIN
    PRINT N'Database GymProDB không tồn tại, không cần xóa.';
END;
GO
