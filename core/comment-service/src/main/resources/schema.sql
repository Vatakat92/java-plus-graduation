DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    text TEXT NOT NULL,
    published_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,

    -- Проверка на пустой текст
    CONSTRAINT comments_text_not_empty CHECK (LENGTH(TRIM(text)) > 0)
);

-- Индексы для таблицы comments (добавлены)
CREATE INDEX IF NOT EXISTS ix_comments_event_id ON comments(event_id);
CREATE INDEX IF NOT EXISTS ix_comments_user_id ON comments(user_id);
CREATE INDEX IF NOT EXISTS ix_comments_deleted ON comments(deleted);
CREATE INDEX IF NOT EXISTS ix_comments_published_on ON comments(published_on);
CREATE INDEX IF NOT EXISTS ix_comments_event_deleted ON comments(event_id, deleted);