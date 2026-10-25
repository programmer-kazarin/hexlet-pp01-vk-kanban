async function request(path, options = {}) {
  const response = await fetch(path, {
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
    ...options,
  });

  if (response.status === 204) {
    return null;
  }

  const text = await response.text();
  let body = null;
  if (text) {
    try {
      body = JSON.parse(text);
    } catch {
      body = { message: text };
    }
  }

  if (!response.ok) {
    const message = body?.message || body?.error || `Request failed (${response.status})`;
    throw new Error(message);
  }

  return body;
}

export function register({ name, email, password }) {
  return request('/api/users/register', {
    method: 'POST',
    body: JSON.stringify({ name, email, password }),
  });
}

export function login({ email, password }) {
  return request('/api/users/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  });
}

export function logout() {
  return request('/api/users/logout', {
    method: 'POST',
  });
}
