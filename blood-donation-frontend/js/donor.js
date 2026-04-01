// js/donor.js

document.addEventListener('DOMContentLoaded', async () => {

    // 1. Auth Guard
    const token = api.getToken();
    const role = localStorage.getItem('userRole');

    if (!token || role !== 'DONOR') {
        alert('Unauthorized access. Please login as a Donor.');
        api.logout();
        return;
    }

    // --- Global State for Safe Updates ---
    let currentDonorProfile = {};

    // 2. Load Profile Data
    async function loadDonorProfile() {
        try {
            const profile = await api.get('/donor/profile');
            currentDonorProfile = profile; // Save the full profile data to prevent overwriting

            document.getElementById('donorName').textContent = profile.name;
            document.getElementById('bloodGroupDisplay').textContent = profile.bloodGroup.replace('_', ' ');

            const badge = document.getElementById('verificationStatusBadge');
            badge.textContent = profile.verificationStatus;
            badge.className = `status-badge status-${profile.verificationStatus}`;

            // Show verification button if not verified
            if (profile.verificationStatus !== 'VERIFIED') {
                const btn = document.getElementById('reqVerificationBtn');
                if (btn) btn.style.display = 'inline-block';
            }

            // Pre-fill availability dates if they exist
            if (profile.availableFromDate) document.getElementById('availFrom').value = profile.availableFromDate;
            if (profile.availableToDate) document.getElementById('availTo').value = profile.availableToDate;
            if (profile.willingness) document.getElementById('willingness').value = profile.willingness;

        } catch (error) {
            console.error('Failed to load profile', error);
        }
    }

    // 3. Handle Publishing Availability (PUT Request)
    document.getElementById('availabilityForm')?.addEventListener('submit', async (e) => {
        e.preventDefault();

        // Merge the existing profile fields with the new availability inputs
        const payload = {
            ...currentDonorProfile,
            availableFromDate: document.getElementById('availFrom').value,
            availableToDate: document.getElementById('availTo').value,
            willingness: document.getElementById('willingness').value
        };

        try {
            await api.put('/donor/profile', payload);

            const successMsg = document.getElementById('availSuccess');
            successMsg.style.display = 'inline';
            setTimeout(() => successMsg.style.display = 'none', 3000);
        } catch (error) {
            alert('Error updating availability: ' + error.message);
        }
    });

    // 4. Handle Donation History Upload (Multipart Form Data)
    document.getElementById('donationHistoryForm')?.addEventListener('submit', async (e) => {
        e.preventDefault();

        const file = document.getElementById('certificateFile').files[0];
        const date = document.getElementById('donationDate').value;
        const hospitalId = document.getElementById('historyHospitalId').value;

        if (!file) {
            alert("Please select a certificate file.");
            return;
        }

        const formData = new FormData();
        formData.append('file', file);

        try {
            // Note: Make sure postFormData exists in api.js
            await api.postFormData(`/donor/upload-donation?hospitalId=${hospitalId}&date=${date}`, formData);
            alert('Donation history uploaded successfully! Waiting for verification.');
            window.location.reload();
        } catch (err) {
            alert('Error uploading donation: ' + err.message);
        }
    });

    // 5. Handle Requesting Hospital Verification
    document.getElementById('reqVerificationBtn')?.addEventListener('click', async () => {
        const hospitalId = prompt("Enter the ID of the Hospital to send your verification request:");

        if (!hospitalId) return; // User cancelled

        try {
            await api.post(`/donor/verify-request?hospitalId=${hospitalId}`, {});
            alert('Verification requested successfully! Waiting for hospital approval.');
            window.location.reload();
        } catch (err) {
            alert('Failed to request verification: ' + err.message);
        }
    });

    // 6. Fetch Active Requests (Nearby & Direct)
    async function loadRequests() {
        const container = document.getElementById('requestsContainer');
        container.innerHTML = '<p>Loading requests...</p>';

        try {
            // Fetch both endpoints concurrently
            const [nearbyRequests, directRequests] = await Promise.all([
                api.get('/donor/requests/nearby').catch(() => []),
                api.get('/donor/requests/direct').catch(() => [])
            ]);

            if ((!nearbyRequests || nearbyRequests.length === 0) && (!directRequests || directRequests.length === 0)) {
                container.innerHTML = '<p style="color: #666;">No active requests found at this time.</p>';
                return;
            }

            let html = '';

            // Render Targeted (Direct) Requests first with a highlighted style
            if (directRequests && directRequests.length > 0) {
                html += `<h3 style="color: #d32f2f; margin-top: 15px;">Urgent: Targeted Requests For You</h3>`;
                html += directRequests.map(req => `
                    <div style="border: 2px solid #d32f2f; padding: 15px; margin-bottom: 15px; border-radius: 5px; background-color: #ffebee;">
                        <h4 style="color: #d32f2f; margin-bottom: 5px;">${req.unitsRequired} Units of ${req.bloodGroup.replace('_', ' ')} Needed</h4>
                        <p><strong>Hospital:</strong> ${req.hospitalName}</p>
                        <p><strong>Location:</strong> ${req.location}</p>
                        <p><strong>Urgency:</strong> ${req.urgencyLevel}</p>
                        <span class="status-badge" style="background-color: #d32f2f; color: white;">DIRECT REQUEST</span>
                        <br>
                        <button class="btn btn-primary" style="width: auto; margin-top: 10px; padding: 5px 15px;" onclick="acceptRequest(${req.id})">Accept Request</button>
                    </div>
                `).join('');
            }

            // Render Nearby Requests below
            if (nearbyRequests && nearbyRequests.length > 0) {
                html += `<h3 style="margin-top: 15px;">Nearby Requests</h3>`;
                html += nearbyRequests.map(req => `
                    <div style="border: 1px solid #ddd; padding: 15px; margin-bottom: 10px; border-radius: 5px;">
                        <h4 style="color: #d32f2f; margin-bottom: 5px;">${req.unitsRequired} Units of ${req.bloodGroup.replace('_', ' ')} Needed</h4>
                        <p><strong>Hospital:</strong> ${req.hospitalName}</p>
                        <p><strong>Location:</strong> ${req.location}</p>
                        <p><strong>Urgency:</strong> ${req.urgencyLevel}</p>
                        <button class="btn btn-primary" style="width: auto; margin-top: 10px; padding: 5px 15px;" onclick="acceptRequest(${req.id})">Accept Request</button>
                    </div>
                `).join('');
            }

            container.innerHTML = html;

        } catch (error) {
            // Check if the error is the missing location restriction from the backend
            if (error.message && error.message.includes("location is not set")) {
                container.innerHTML = '<p style="color: #ff9800;">Please ensure your location (Latitude/Longitude) is saved in your profile to view nearby requests.</p>';
            } else {
                container.innerHTML = `<p style="color: red;">Failed to load requests: ${error.message}</p>`;
            }
        }
    }

    // Initialize Dashboard
    loadDonorProfile();
    loadRequests();
});

// --- Global Functions ---

// Handle accepting a request
window.acceptRequest = async function (requestId) {
    if (confirm('Are you sure you want to accept this request? Your contact details will be shared with the receiver.')) {
        try {
            await api.post(`/donor/requests/${requestId}/accept`, {});
            alert('Request accepted successfully! Thank you for saving a life.');
            window.location.reload();
        } catch (error) {
            alert('Failed to accept request: ' + error.message);
        }
    }
};