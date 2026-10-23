CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL
);

COMMENT ON TABLE users IS 'Пользователи системы';
COMMENT ON COLUMN users.id IS 'Идентификатор пользователя';
COMMENT ON COLUMN users.email IS 'Адрес почты для входа';
COMMENT ON COLUMN users.password_hash IS 'Хеш пароля';
COMMENT ON COLUMN users.name IS 'Отображаемое имя';

CREATE TABLE boards (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    owner_id INTEGER NOT NULL REFERENCES users (id)
);

COMMENT ON TABLE boards IS 'Канбан-доски';
COMMENT ON COLUMN boards.id IS 'Идентификатор доски';
COMMENT ON COLUMN boards.title IS 'Название доски';
COMMENT ON COLUMN boards.owner_id IS 'Владелец доски';

CREATE TABLE board_members (
    id SERIAL PRIMARY KEY,
    board_id INTEGER NOT NULL REFERENCES boards (id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users (id),
    UNIQUE (board_id, user_id)
);

COMMENT ON TABLE board_members IS 'Участники доски, кроме владельца';
COMMENT ON COLUMN board_members.id IS 'Идентификатор участия';
COMMENT ON COLUMN board_members.board_id IS 'Доска';
COMMENT ON COLUMN board_members.user_id IS 'Участник';

CREATE TABLE columns (
    id SERIAL PRIMARY KEY,
    board_id INTEGER NOT NULL REFERENCES boards (id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL
);

COMMENT ON TABLE columns IS 'Колонки доски';
COMMENT ON COLUMN columns.id IS 'Идентификатор колонки';
COMMENT ON COLUMN columns.board_id IS 'Доска, к которой относится колонка';
COMMENT ON COLUMN columns.title IS 'Название колонки';
COMMENT ON COLUMN columns.position IS 'Порядок колонки на доске слева направо';

CREATE TABLE cards (
    id SERIAL PRIMARY KEY,
    column_id INTEGER NOT NULL REFERENCES columns (id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL DEFAULT '',
    position INTEGER NOT NULL,
    assignee_id INTEGER REFERENCES users (id),
    deadline DATE
);

COMMENT ON TABLE cards IS 'Карточки задач';
COMMENT ON COLUMN cards.id IS 'Идентификатор карточки';
COMMENT ON COLUMN cards.column_id IS 'Колонка, в которой лежит карточка';
COMMENT ON COLUMN cards.title IS 'Заголовок карточки';
COMMENT ON COLUMN cards.description IS 'Описание карточки';
COMMENT ON COLUMN cards.position IS 'Порядок карточки внутри колонки';
COMMENT ON COLUMN cards.assignee_id IS 'Исполнитель карточки';
COMMENT ON COLUMN cards.deadline IS 'Дедлайн карточки';

CREATE TABLE comments (
    id SERIAL PRIMARY KEY,
    card_id INTEGER NOT NULL REFERENCES cards (id) ON DELETE CASCADE,
    author_id INTEGER NOT NULL REFERENCES users (id),
    body TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE comments IS 'Комментарии к карточкам';
COMMENT ON COLUMN comments.id IS 'Идентификатор комментария';
COMMENT ON COLUMN comments.card_id IS 'Карточка, к которой относится комментарий';
COMMENT ON COLUMN comments.author_id IS 'Автор комментария';
COMMENT ON COLUMN comments.body IS 'Текст комментария';
COMMENT ON COLUMN comments.created_at IS 'Время создания комментария';
