const BASE_URL = "http://127.0.0.1:8080";

// Get the token from localStorage
function getToken() {
  return localStorage.getItem("token");
}

// Save token after login/register
function saveToken(token) {
  localStorage.setItem("token", token);
}

// Remove token on logout
function logout() {
  localStorage.removeItem("token");
  window.location.href = "login.html";
}

// Check if user is logged in, redirect to login if not
function requireAuth() {
  if (!getToken()) {
    window.location.href = "login.html";
  }
}

// GET request with token
function apiGet(endpoint) {
  return fetch(BASE_URL + endpoint, {
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  }).then(function (response) {
    if (response.status === 401 || response.status === 403) {
      logout(); // token expired, send to login
    }
    return response.json();
  });
}

// POST request with token
function apiPost(endpoint, data) {
  const headers = { "Content-Type": "application/json" };
  if (getToken()) {
    headers["Authorization"] = "Bearer " + getToken();
  }
  return fetch(BASE_URL + endpoint, {
    method: "POST",
    headers: headers,
    body: JSON.stringify(data),
  }).then(function (response) {
    return response.json();
  });
}

// PUT request with token
function apiPut(endpoint, data) {
  return fetch(BASE_URL + endpoint, {
    method: "PUT",
    headers: {
      Authorization: "Bearer " + getToken(),
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  }).then(function (response) {
    return response.json();
  });
}

// DELETE request with token
function apiDelete(endpoint) {
  return fetch(BASE_URL + endpoint, {
    method: "DELETE",
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
}
