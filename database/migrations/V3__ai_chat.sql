USE GymProDB;
GO

IF OBJECT_ID('ai_conversations', 'U') IS NULL
BEGIN
    CREATE TABLE ai_conversations (
        id                      INT IDENTITY(1,1) PRIMARY KEY,
        user_id                 INT NOT NULL,
        title                   NVARCHAR(120) NOT NULL,
        physical_data_consent   BIT NOT NULL DEFAULT 0,
        created_at              DATETIME2 NOT NULL DEFAULT GETDATE(),
        updated_at              DATETIME2 NOT NULL DEFAULT GETDATE(),
        CONSTRAINT FK_ai_conversations_user
            FOREIGN KEY (user_id) REFERENCES users(id)
    );
END
GO

IF OBJECT_ID('ai_messages', 'U') IS NULL
BEGIN
    CREATE TABLE ai_messages (
        id                  BIGINT IDENTITY(1,1) PRIMARY KEY,
        conversation_id     INT NOT NULL,
        role                NVARCHAR(20) NOT NULL,
        content             NVARCHAR(MAX) NOT NULL,
        model               NVARCHAR(80),
        input_tokens        INT,
        output_tokens       INT,
        total_tokens        INT,
        created_at          DATETIME2 NOT NULL DEFAULT GETDATE(),
        CONSTRAINT CK_ai_messages_role CHECK (role IN ('USER', 'ASSISTANT')),
        CONSTRAINT FK_ai_messages_conversation
            FOREIGN KEY (conversation_id) REFERENCES ai_conversations(id)
            ON DELETE CASCADE
    );
END
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = 'IX_ai_conversations_user_updated'
      AND object_id = OBJECT_ID('ai_conversations')
)
    CREATE INDEX IX_ai_conversations_user_updated
        ON ai_conversations(user_id, updated_at DESC);
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = 'IX_ai_messages_conversation_created'
      AND object_id = OBJECT_ID('ai_messages')
)
    CREATE INDEX IX_ai_messages_conversation_created
        ON ai_messages(conversation_id, created_at ASC);
GO
