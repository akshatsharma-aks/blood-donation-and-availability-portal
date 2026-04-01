// js/register.js

document.addEventListener('DOMContentLoaded', () => {
    const roleSelect = document.getElementById('regRole');
    const donorFields = document.getElementById('donorFields');
    const hospitalFields = document.getElementById('hospitalFields');
    const locationBtn = document.getElementById('getLocationBtn');
    const registerForm = document.getElementById('registerForm');

    // 1. Dynamic Form Toggling based on Role
    roleSelect.addEventListener('change', (e) => {
        const role = e.target.value;
        
        // Hide all specific fields first
        donorFields.style.display = 'none';
        hospitalFields.style.display = 'none';

        // Clear requirements
        document.getElementById('regHospitalName').required = false;
        document.getElementById('regLicense').required = false;

        // Show relevant fields
        if (role === 'DONOR') {
            donorFields.style.display = 'block';
        } else if (role === 'HOSPITAL') {
            hospitalFields.style.display = 'block';
            document.getElementById('regHospitalName').required = true;
            document.getElementById('regLicense').required = true;
        }
    });

    // 2. HTML5 Geolocation API Implementation
    locationBtn.addEventListener('click', () => {
        if (!navigator.geolocation) {
            alert('Geolocation is not supported by your browser');
            return;
        }

        locationBtn.textContent = 'Locating...';
        locationBtn.disabled = true;

        navigator.geolocation.getCurrentPosition(
            async (position) => {
                const lat = position.coords.latitude;
                const lng = position.coords.longitude;
                
                document.getElementById('regLat').value = lat;
                document.getElementById('regLng').value = lng;

                // Optional: Use a free reverse-geocoding API to get City/State
                try {
                    const response = await fetch(`https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=${lat}&longitude=${lng}&localityLanguage=en`);
                    const data = await response.json();
                    document.getElementById('regCity').value = data.city || data.locality || '';
                    document.getElementById('regState').value = data.principalSubdivision || '';
                } catch (err) {
                    console.warn("Could not auto-fetch city/state. User must enter manually.");
                }

                locationBtn.textContent = '📍 Location Found';
                locationBtn.style.background = '#4CAF50'; // Turn green
            },
            (error) => {
                alert('Unable to retrieve your location. Please enter details manually.');
                locationBtn.textContent = '📍 Detect My Location';
                locationBtn.disabled = false;
            }
        );
    });

    // 3. Form Submission Logic
    // 3. Form Submission Logic
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const errorDiv = document.getElementById('registerError');
        const successDiv = document.getElementById('registerSuccess');
        const btn = document.getElementById('registerBtn');
        const password = document.getElementById('regPassword').value;

        errorDiv.style.display = 'none';
        successDiv.style.display = 'none';

        // 1. Frontend Validation Guard
        if (password.length < 8) {
            errorDiv.textContent = 'Password must be at least 8 characters long.';
            errorDiv.style.display = 'block';
            return;
        }

        // 2. Safely parse location (prevents NaN crashing the backend)
        const latVal = document.getElementById('regLat').value;
        const lngVal = document.getElementById('regLng').value;

        // Build the base DTO payload
        const payload = {
            name: document.getElementById('regName').value,
            email: document.getElementById('regEmail').value,
            password: password,
            phone: document.getElementById('regPhone').value,
            role: document.getElementById('regRole').value,
            latitude: latVal ? parseFloat(latVal) : null,
            longitude: lngVal ? parseFloat(lngVal) : null,
            city: document.getElementById('regCity').value,
            state: document.getElementById('regState').value
        };

        // Append role-specific data
        if (payload.role === 'DONOR') {
            payload.bloodGroup = document.getElementById('regBloodGroup').value;
        } else if (payload.role === 'HOSPITAL') {
            payload.hospitalName = document.getElementById('regHospitalName').value;
            payload.licenseNumber = document.getElementById('regLicense').value;
        }

        try {
            btn.disabled = true;
            btn.textContent = 'Registering...';

            await api.post('/auth/register', payload);
            
            successDiv.textContent = 'Registration successful! Redirecting to login...';
            successDiv.style.display = 'block';
            
            setTimeout(() => {
                window.location.href = 'index.html';
            }, 2000);

        } catch (error) {
            // This will catch the error message returned from your backend
            errorDiv.textContent = error.message;
            errorDiv.style.display = 'block';
            btn.disabled = false;
            btn.textContent = 'Register';
        }
    });
});