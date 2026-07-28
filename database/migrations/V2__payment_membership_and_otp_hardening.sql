USE GymProDB;
GO

-- Non-destructive migration for databases created before the payment hardening.
IF COL_LENGTH('memberships', 'version') IS NULL
    ALTER TABLE memberships ADD version BIGINT NOT NULL
        CONSTRAINT DF_memberships_version DEFAULT 0;
GO

DECLARE @membershipStatusConstraint sysname;
SELECT TOP (1) @membershipStatusConstraint = cc.name
FROM sys.check_constraints cc
JOIN sys.columns c
  ON c.object_id = cc.parent_object_id
 AND c.column_id = cc.parent_column_id
WHERE cc.parent_object_id = OBJECT_ID('memberships')
  AND c.name = 'status';

IF @membershipStatusConstraint IS NOT NULL
    EXEC('ALTER TABLE memberships DROP CONSTRAINT [' + @membershipStatusConstraint + ']');

ALTER TABLE memberships ADD CONSTRAINT CK_memberships_status
    CHECK (status IN ('PENDING', 'ACTIVE', 'EXPIRED', 'PAUSED', 'CANCELLED'));
GO

IF COL_LENGTH('transactions', 'requested_duration_days') IS NULL
    ALTER TABLE transactions ADD requested_duration_days INT NULL;
IF COL_LENGTH('transactions', 'requested_package_id') IS NULL
    ALTER TABLE transactions ADD requested_package_id INT NULL;
IF COL_LENGTH('transactions', 'requested_pt_id') IS NULL
    ALTER TABLE transactions ADD requested_pt_id INT NULL;
IF COL_LENGTH('transactions', 'operation_applied') IS NULL
    -- Mọi transaction đã tồn tại được tạo bởi luồng cũ, vốn áp dụng membership ngay.
    ALTER TABLE transactions ADD operation_applied BIT NOT NULL
        CONSTRAINT DF_transactions_operation_applied DEFAULT 1 WITH VALUES;
IF COL_LENGTH('transactions', 'version') IS NULL
    ALTER TABLE transactions ADD version BIGINT NOT NULL
        CONSTRAINT DF_transactions_version DEFAULT 0;
GO

-- Các transaction tạo sau migration phải chờ admin duyệt mới được áp dụng.
IF EXISTS (
    SELECT 1 FROM sys.default_constraints
    WHERE name = 'DF_transactions_operation_applied'
      AND parent_object_id = OBJECT_ID('transactions')
)
BEGIN
    ALTER TABLE transactions DROP CONSTRAINT DF_transactions_operation_applied;
    ALTER TABLE transactions ADD CONSTRAINT DF_transactions_operation_applied
        DEFAULT 0 FOR operation_applied;
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_transactions_requested_package')
    ALTER TABLE transactions ADD CONSTRAINT FK_transactions_requested_package
        FOREIGN KEY (requested_package_id) REFERENCES packages(id);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_transactions_requested_pt')
    ALTER TABLE transactions ADD CONSTRAINT FK_transactions_requested_pt
        FOREIGN KEY (requested_pt_id) REFERENCES users(id);
GO

IF OBJECT_ID('otps', 'U') IS NULL
BEGIN
    CREATE TABLE otps (
        id               INT IDENTITY(1,1) PRIMARY KEY,
        email            NVARCHAR(100) NOT NULL,
        otp              NVARCHAR(6) NOT NULL,
        expiration_time  DATETIME2 NOT NULL,
        created_at       DATETIME2 NOT NULL DEFAULT GETDATE(),
        used             BIT NOT NULL DEFAULT 0,
        failed_attempts  INT NOT NULL DEFAULT 0
    );
END
ELSE IF COL_LENGTH('otps', 'failed_attempts') IS NULL
BEGIN
    ALTER TABLE otps ADD failed_attempts INT NOT NULL
        CONSTRAINT DF_otps_failed_attempts DEFAULT 0;
END
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = 'IX_otps_email_expiration' AND object_id = OBJECT_ID('otps')
)
    CREATE INDEX IX_otps_email_expiration ON otps(email, expiration_time DESC);
GO
