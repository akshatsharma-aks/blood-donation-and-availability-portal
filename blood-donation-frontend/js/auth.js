// js/auth.js

document.addEventListener('DOMContentLoaded', () => {
    
    // --- LOGIN LOGIC ---
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const email = document.getElementById('loginEmail').value;
            const password = document.getElementById('loginPassword').value;
            const errorDiv = document.getElementById('loginError');
            const btn = document.getElementById('loginBtn');

// js/auth.js (inside your login form listener)
try {
    btn.disabled = true;
    btn.textContent = 'Logging in...';
    errorDiv.style.display = 'none';

    // Call the backend login endpoint. 
    // Assuming api.js is unwrapping the response, this returns the AuthResponse directly.
    const authData = await api.post('/auth/login', { email, password });

    if (!authData || !authData.token) {
        console.error("Server returned:", authData);
        throw new Error("Invalid response from server");
    }

    // Save JWT and Role to localStorage
    localStorage.setItem('jwtToken', authData.token);
    localStorage.setItem('userRole', authData.role);

    // Redirect based on Role
    switch(authData.role) {
        case 'DONOR':
            window.location.href = 'pages/donor-dashboard.html';
            break;
        case 'RECEIVER':
            window.location.href = 'pages/receiver-dashboard.html';
            break;
        case 'HOSPITAL':
            window.location.href = 'pages/hospital-dashboard.html';
            break;
        case 'ADMIN':
            window.location.href = 'pages/admin-dashboard.html';
            break;
        default:
            throw new Error("Unknown role provided by server: " + authData.role);
    }

} catch (error) {
    errorDiv.textContent = error.message;
    errorDiv.style.display = 'block';
} finally {
    btn.disabled = false;
    btn.textContent = 'Login';
}
        });
    }
});