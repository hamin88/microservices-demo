import React, { useEffect, useState } from "react";
import { createRoot } from "react-dom/client";
import "./styles.css";

const API_BASE_URL = "http://localhost:8080";
const LOGIN_URL = `${API_BASE_URL}/api/auth/login`;
const TOKEN_STORAGE_KEY = "microservices-demo.access-token";

function App() {
  const [username, setUsername] = useState("admin");
  const [password, setPassword] = useState("admin");
  const [token, setToken] = useState("");
  const [message, setMessage] = useState("");
  const [isError, setIsError] = useState(false);
  const [loading, setLoading] = useState(false);
  const [apiResponse, setApiResponse] = useState("Choose an API call.");

  useEffect(() => {
    const storedToken = localStorage.getItem(TOKEN_STORAGE_KEY);
    if (storedToken) {
      setToken(storedToken);
      setMessage("Signed in. Token ready for API calls.");
    }
  }, []);

  function showMessage(nextMessage, nextIsError = false) {
    setMessage(nextMessage);
    setIsError(nextIsError);
  }

  function clearSession(nextMessage = "Signed out.") {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
    setToken("");
    setApiResponse("Choose an API call.");
    showMessage(nextMessage);
  }

  async function login(event) {
    event.preventDefault();
    setLoading(true);
    showMessage("Signing in...");

    try {
      const response = await fetch(LOGIN_URL, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ username, password })
      });

      const payload = await response.json().catch(() => ({}));
      if (!response.ok) {
        throw new Error(payload.message || payload.error || "Login failed.");
      }

      localStorage.setItem(TOKEN_STORAGE_KEY, payload.accessToken);
      setToken(payload.accessToken);
      showMessage("Signed in. Token ready for API calls.");
    } catch (error) {
      localStorage.removeItem(TOKEN_STORAGE_KEY);
      setToken("");
      showMessage(error.message, true);
    } finally {
      setLoading(false);
    }
  }

  async function callApi(endpoint) {
    if (!token) {
      clearSession("Please sign in before calling APIs.");
      setIsError(true);
      return;
    }

    setApiResponse("Loading...");
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

    const contentType = response.headers.get("content-type") || "";
    const body = contentType.includes("application/json")
      ? await response.json()
      : await response.text();

    setApiResponse(JSON.stringify({
      status: response.status,
      ok: response.ok,
      body
    }, null, 2));
  }

  return (
    <main className="shell">
      <section className="login-panel" aria-labelledby="page-title">
        <div className="brand">
          <span className="brand-mark" aria-hidden="true">M</span>
          <div>
            <h1 id="page-title">Microservices Demo</h1>
            <p>Sign in to request a JWT for gateway API calls.</p>
          </div>
        </div>

        <form className="login-form" onSubmit={login}>
          <label htmlFor="username">Username</label>
          <input
            id="username"
            name="username"
            autoComplete="username"
            value={username}
            onChange={(event) => setUsername(event.target.value)}
            required
          />

          <label htmlFor="password">Password</label>
          <input
            id="password"
            name="password"
            type="password"
            autoComplete="current-password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            required
          />

          <button type="submit" disabled={loading}>
            {loading ? "Signing in..." : "Sign in"}
          </button>
          <p className={`message${isError ? " error" : ""}`} role="status" aria-live="polite">
            {message}
          </p>
        </form>
      </section>

      {token && (
        <section className="api-panel">
          <div className="panel-header">
            <div>
              <h2>Authenticated API Calls</h2>
              <p>The access token is attached as a Bearer token.</p>
            </div>
            <button className="secondary" type="button" onClick={() => clearSession()}>
              Log out
            </button>
          </div>

          <div className="actions">
            <button type="button" onClick={() => callApi("/api/students")}>Load students</button>
            <button type="button" onClick={() => callApi("/api/users")}>Load users</button>
            <button type="button" onClick={() => callApi("/api/auth/me")}>Load my access</button>
          </div>

          <label htmlFor="token-output">JWT access token</label>
          <textarea id="token-output" readOnly spellCheck="false" value={token} />

          <label htmlFor="api-output">API response</label>
          <pre id="api-output">{apiResponse}</pre>
        </section>
      )}
    </main>
  );
}

createRoot(document.getElementById("root")).render(<App />);
