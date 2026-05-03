const nameGroup = document.getElementById('name-group');
const authButton = document.getElementById("auth-btn");
const toggleLink = document.getElementById("toggle-link");
const usernameGroup = document.getElementById('username-group');
let isLogin = true; //true - login mode, false - register mode

function toggleMode() {
    isLogin = !isLogin;
    if (isLogin) {
        nameGroup.style.display = "none";
        usernameGroup.style.display = "none";
        authButton.textContent = "Login";
        toggleLink.textContent = "Don't have an account? Register";
    } else {
        nameGroup.style.display = "flex";
        usernameGroup.style.display = "flex";
        authButton.textContent = "Register";
        toggleLink.textContent = "Already have an account? Login";
    }
}
toggleLink.addEventListener("click", toggleMode);

function handleAuth() {
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;
  if (isLogin) {
    //login mode
    console.log("login mode");
    apiPost("/api/auth/login", { email: email, password: password }).then(
      function (data) {
        saveToken(data.token);
        window.location.href = "index.html";
      },
    );
  } else {
    //register mode
    console.log("register mode");
    const name = document.getElementById("name").value;
    const username = document.getElementById("username").value;
    apiPost("/api/auth/register", {
      name: name,
      username : username,
      email: email,
      password: password,
    }).then(function (data) {
      saveToken(data.token);
      window.location.href = "index.html";
    });
  }
}
authButton.addEventListener("click", handleAuth);
