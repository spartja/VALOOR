CREATE TABLE chat_session (
    id                  CHAR(36) NOT NULL PRIMARY KEY,
    user_id             INT NOT NULL,
    chatbot_type        VARCHAR(40) NOT NULL,
    last_interaction_id VARCHAR(512),
    created_at          DATETIME(6) NOT NULL,
    updated_at          DATETIME(6) NOT NULL,
    CONSTRAINT fk_chat_session_user
        FOREIGN KEY (user_id) REFERENCES usuario (id_usuario) ON DELETE CASCADE,
    INDEX idx_chat_session_user_updated (user_id, updated_at)
);

CREATE TABLE chat_message (
    id_chat_message BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    session_id      CHAR(36) NOT NULL,
    role            VARCHAR(12) NOT NULL,
    message         TEXT NOT NULL,
    created_at      DATETIME(6) NOT NULL,
    CONSTRAINT fk_chat_message_session
        FOREIGN KEY (session_id) REFERENCES chat_session (id) ON DELETE CASCADE,
    INDEX idx_chat_message_session_created (session_id, created_at)
);
