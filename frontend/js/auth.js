const API_BASE_URL = "http://localhost:8080/api";


/* ================= REGISTER ================= */

const registerForm = document.getElementById("registerForm");

if (registerForm) {

    registerForm.addEventListener("submit", async function (event) {

        event.preventDefault();

        const name =
            document.getElementById("name").value.trim();

        const email =
            document.getElementById("email").value.trim();

        const phone =
            document.getElementById("phone").value.trim();

        const password =
            document.getElementById("password").value;

        const confirmPassword =
            document.getElementById("confirmPassword").value;

        const message =
            document.getElementById("registerMessage");


        /* Password validation */

        if (password !== confirmPassword) {

            message.innerHTML = `
                <div class="alert alert-danger">
                    Passwords do not match.
                </div>
            `;

            return;
        }


        /* Send registration request */

        try {

            const response = await fetch(
                `${API_BASE_URL}/users/register`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify({
                        name: name,
                        email: email,
                        phone: phone,
                        password: password
                    })
                }
            );


            const data = await response.json();


            /* Registration successful */

            if (response.ok) {

                message.innerHTML = `
                    <div class="alert alert-success">
                        Account created successfully!
                        Redirecting to login...
                    </div>
                `;

                registerForm.reset();


                setTimeout(function () {

                    window.location.href = "login.html";

                }, 1500);

            }

            /* Registration failed */

            else {

                message.innerHTML = `
                    <div class="alert alert-danger">
                        ${data}
                    </div>
                `;

            }

        }

        catch (error) {

            console.error("Registration error:", error);

            message.innerHTML = `
                <div class="alert alert-danger">
                    Unable to connect to the server.
                    Please make sure the backend is running.
                </div>
            `;

        }

    });

}
/* ================= LOGIN ================= */

const loginForm = document.getElementById("loginForm");

if (loginForm) {

    loginForm.addEventListener("submit", async function (event) {

        event.preventDefault();

        const email =
            document.getElementById("loginEmail").value.trim();

        const password =
            document.getElementById("loginPassword").value;

        const message =
            document.getElementById("loginMessage");


        try {

            const response = await fetch(
                `${API_BASE_URL}/users/login`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify({
                        email: email,
                        password: password
                    })
                }
            );


            const data = await response.json();


            /* LOGIN SUCCESS */

            if (response.ok) {

                // Save JWT token
                localStorage.setItem(
                    "busgo_token",
                    data.token
                );


                // Save logged-in user
                localStorage.setItem(
                    "busgo_user",
                    JSON.stringify(data.user)
                );


                message.innerHTML = `
                    <div class="alert alert-success">
                        Login successful!
                        Redirecting...
                    </div>
                `;


                /*
                 * Temporary redirect.
                 * We will create the actual dashboard later.
                 */

                setTimeout(function () {

                    window.location.href = "index.html";

                }, 1000);

            }


            /* LOGIN FAILED */

            else {

                message.innerHTML = `
                    <div class="alert alert-danger">
                        ${data}
                    </div>
                `;

            }

        }

        catch (error) {

            console.error("Login error:", error);

            message.innerHTML = `
                <div class="alert alert-danger">
                    Unable to connect to the server.
                    Please make sure the backend is running.
                </div>
            `;

        }

    });

}