-- pt_notes is the canonical PT-to-member note feature.
-- Preserve the legacy table for rollback, but copy any unique legacy rows once.
IF OBJECT_ID(N'dbo.pt_comments', N'U') IS NOT NULL
   AND OBJECT_ID(N'dbo.pt_notes', N'U') IS NOT NULL
BEGIN
    INSERT INTO dbo.pt_notes (pt_id, member_id, content, created_at)
    SELECT c.pt_id, c.member_id, c.content, c.created_at
    FROM dbo.pt_comments c
    WHERE NOT EXISTS (
        SELECT 1
        FROM dbo.pt_notes n
        WHERE n.pt_id = c.pt_id
          AND n.member_id = c.member_id
          AND n.content = c.content
          AND ISNULL(n.created_at, '19000101') = ISNULL(c.created_at, '19000101')
    );
END;
