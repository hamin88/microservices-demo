const TOKEN_URL = "http://localhost:8180/realms/microservices-demo/protocol/openid-connect/token";
const CLIENT_ID = "gateway-client";
const TOKEN_STORAGE_KEY = "microservices-demo.access-token";

const loginForm = document.querySelector("#login-form");
const loginButton = document.querySelector("#login-button");
const loginMessage = document.querySelector("#login-message");
const apiPanel = document.querySelector("#api-panel");
const tokenOutput = document.querySelector("#token-output");
const apiOutput = document.querySelector("#api-output");
const logoutButton = document.querySelector("#logout-button");

function setMessage(message, isError = false) {
  loginMessage.textContent = message;
  loginMessage.classList.toggle("error", isError);
}

function setAuthenticated(token) {
  localStorage.setItem(TOKEN_STORAGE_KEY, token);
  tokenOutput.value = token;
  apiPanel.hidden = false;
  setMessage("Signed in. Token ready for API calls.");
}

function clearAuthenticated() {
  localStorage.removeItem(TOKEN_STORAGE_KEY);
  tokenOutput.value = "";
  apiOutput.textContent = "Choose an API call.";
  apiPanel.hidden = true;
}

async function login(username, password) {
  if (username !== "admin" || password !== "admin") {
    throw new Error("Only admin/admin is allowed for this demo.");
  }

  const body = new URLSearchParams({
    client_id: CLIENT_ID,
    username,
    password,
    grant_type: "password"
  });

  const response = await fetch(TOKEN_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded"
    },
    body
  });

  const payload = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(payload.error_description || payload.error || "Login failed.");
  }

  return payload.access_token;
}

async function callApi(endpoint) {
  const token = localStorage.getItem(TOKEN_STORAGE_KEY);
  if (!token) {
    clearAuthenticated();
    setMessage("Please sign in before calling APIs.", true);
    return;
  }

  apiOutput.textContent = "Loading...";
  const response = await fetch(endpoint, {
    headers: {
      Authorization: `Bearer ${token}`
    }
  });

  const contentType = response.headers.get("content-type") || "";
  const payload = contentType.includes("application/json")
    ? await response.json()
    : await response.text();

  apiOutput.textContent = JSON.stringify({
    status: response.status,
    ok: response.ok,
    body: payload
  }, null, 2);
}

loginForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  loginButton.disabled = true;
  setMessage("Signing in...");

  const formData = new FormData(loginForm);
  try {
    const token = await login(
      String(formData.get("username") || ""),
      String(formData.get("password") || "")
    );
    setAuthenticated(token);
  } catch (error) {
    clearAuthenticated();
    setMessage(error.message, true);
  } finally {
    loginButton.disabled = false;
  }
});

document.querySelectorAll("[data-endpoint]").forEach((button) => {
  button.addEventListener("click", () => callApi(button.dataset.endpoint));
});

logoutButton.addEventListener("click", () => {
  clearAuthenticated();
  setMessage("Signed out.");
});

const storedToken = localStorage.getItem(TOKEN_STORAGE_KEY);
if (storedToken) {
  setAuthenticated(storedToken);
}
