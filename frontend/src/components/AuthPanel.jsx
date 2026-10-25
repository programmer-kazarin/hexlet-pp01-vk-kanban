import { useState } from 'react';
import { login, logout, register } from '../api/users';

export default function AuthPanel() {
  const [mode, setMode] = useState(null);
  const [user, setUser] = useState(null);
  const [error, setError] = useState('');
  const [pending, setPending] = useState(false);

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  function resetForm() {
    setName('');
    setEmail('');
    setPassword('');
    setError('');
  }

  function openMode(nextMode) {
    resetForm();
    setMode(nextMode);
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    setPending(true);

    try {
      const nextUser =
        mode === 'login'
          ? await login({ email, password })
          : await register({ name, email, password });
      setUser(nextUser);
      setMode(null);
      resetForm();
    } catch (err) {
      setError(err.message || 'Ошибка запроса');
    } finally {
      setPending(false);
    }
  }

  async function handleLogout() {
    setError('');
    setPending(true);
    try {
      await logout();
      setUser(null);
    } catch (err) {
      setError(err.message || 'Не удалось выйти');
    } finally {
      setPending(false);
    }
  }

  if (user) {
    return (
      <div className="auth-panel">
        <p className="auth-user">Вы вошли как {user.name}</p>
        <button type="button" onClick={handleLogout} disabled={pending}>
          Выйти
        </button>
        {error ? <p className="auth-error">{error}</p> : null}
      </div>
    );
  }

  return (
    <div className="auth-panel">
      <div className="auth-actions">
        <button type="button" onClick={() => openMode('login')}>
          Вход
        </button>
        <button type="button" onClick={() => openMode('register')}>
          Регистрация
        </button>
      </div>

      {mode ? (
        <form className="auth-form" onSubmit={handleSubmit}>
          <h2>{mode === 'login' ? 'Вход' : 'Регистрация'}</h2>

          {mode === 'register' ? (
            <label>
              Имя
              <input
                type="text"
                name="name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
                autoComplete="name"
              />
            </label>
          ) : null}

          <label>
            Почта
            <input
              type="email"
              name="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              autoComplete="email"
            />
          </label>

          <label>
            Пароль
            <input
              type="password"
              name="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength={6}
              autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
            />
          </label>

          {error ? <p className="auth-error">{error}</p> : null}

          <div className="auth-form-actions">
            <button type="submit" disabled={pending}>
              {pending ? 'Отправка…' : mode === 'login' ? 'Войти' : 'Зарегистрироваться'}
            </button>
            <button type="button" onClick={() => setMode(null)} disabled={pending}>
              Отмена
            </button>
          </div>
        </form>
      ) : null}
    </div>
  );
}
