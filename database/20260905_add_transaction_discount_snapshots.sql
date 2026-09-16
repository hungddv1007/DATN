-- Chay file nay MOT LAN tren database GymProDB hien co truoc khi khoi dong backend.
-- Muc dich: dong bang ten ma va phan tram giam cua giao dich da ton tai.

USE GymProDB;
GO

IF COL_LENGTH('dbo.transactions', 'promotion_code_snapshot') IS NULL
    ALTER TABLE dbo.transactions ADD promotion_code_snapshot NVARCHAR(50) NULL;
GO

IF COL_LENGTH('dbo.transactions', 'referral_code_snapshot') IS NULL
    ALTER TABLE dbo.transactions ADD referral_code_snapshot NVARCHAR(50) NULL;
GO

IF COL_LENGTH('dbo.transactions', 'discount_percent_snapshot') IS NULL
    ALTER TABLE dbo.transactions ADD discount_percent_snapshot INT NULL;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.check_constraints
    WHERE name = 'CK_transactions_discount_snapshot'
      AND parent_object_id = OBJECT_ID('dbo.transactions')
)
    ALTER TABLE dbo.transactions ADD CONSTRAINT CK_transactions_discount_snapshot
        CHECK (discount_percent_snapshot IS NULL OR discount_percent_snapshot BETWEEN 0 AND 100);
GO

UPDATE transaction_record
SET promotion_code_snapshot = COALESCE(transaction_record.promotion_code_snapshot, promotion.code),
    discount_percent_snapshot = COALESCE(transaction_record.discount_percent_snapshot, promotion.discount_percent)
FROM dbo.transactions AS transaction_record
JOIN dbo.promotions AS promotion ON promotion.id = transaction_record.promotion_id
WHERE transaction_record.promotion_code_snapshot IS NULL
   OR transaction_record.discount_percent_snapshot IS NULL;
GO

UPDATE transaction_record
SET referral_code_snapshot = COALESCE(transaction_record.referral_code_snapshot, sale_code.code),
    discount_percent_snapshot = COALESCE(
        transaction_record.discount_percent_snapshot,
        transaction_record.customer_discount_percent
    )
FROM dbo.transactions AS transaction_record
JOIN dbo.sales_referral_codes AS sale_code ON sale_code.id = transaction_record.sale_code_id
WHERE transaction_record.promotion_id IS NULL
  AND (transaction_record.referral_code_snapshot IS NULL
       OR transaction_record.discount_percent_snapshot IS NULL);
GO

PRINT N'Da them va backfill snapshot ma giam gia cho lich su giao dich.';
GO
