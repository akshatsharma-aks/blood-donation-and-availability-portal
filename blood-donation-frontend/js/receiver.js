// js/receiver.js

document.addEventListener('DOMContentLoaded', () => {
    // 1. Auth Guard: Kick out if not logged in OR not a receiver
    const token = api.getToken();
    const role = localStorage.getItem('userRole');
    
    if (!token || role !== 'RECEIVER') {
        alert('Unauthorized access. Please login as a Receiver.');
        api.logout();
        return;
    }

    // Set Name (Assuming basic user info endpoint or stored locally)
    loadReceiverProfile();
    loadMyRequests();

    // 2. Handle Creating a Blood Request
    document.getElementById('bloodRequestForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const btn = document.getElementById('submitRequestBtn');
        const msgDiv = document.getElementById('requestMsg');
        
        const payload = {
            bloodGroup: document.getElementById('reqBloodGroup').value,
            unitsRequired: parseInt(document.getElementById('reqUnits').value),
            urgencyLevel: document.getElementById('reqUrgency').value,
            hospitalName: document.getElementById('reqHospital').value,
            location: document.getElementById('reqLocation').value,
            // You can add latitude/longitude here using the Geolocation API if needed
        };

        try {
            btn.disabled = true;
            btn.textContent = 'Broadcasting...';
            
            // FIX: Changed from '/receiver/requests' to '/receiver/request'
            await api.post('/receiver/request', payload);
            
            msgDiv.textContent = 'Request broadcasted successfully!';
            msgDiv.style.color = 'green';
            msgDiv.style.display = 'block';
            
            document.getElementById('bloodRequestForm').reset();
            
            // Reload requests list
            loadMyRequests();
            setTimeout(() => { msgDiv.style.display = 'none'; switchTab('overview'); }, 2000);

        } catch (error) {
            msgDiv.textContent = 'Error: ' + error.message;
            msgDiv.style.color = 'red';
            msgDiv.style.display = 'block';
        } finally {
            btn.disabled = false;
            btn.textContent = 'Broadcast Request';
        }
    });
});

// --- HELPER FUNCTIONS ---

// Simple Tab Switcher
window.switchTab = function(tabName) {
    // Hide all sections
    document.getElementById('section-overview').style.display = 'none';
    document.getElementById('section-create').style.display = 'none';
    document.getElementById('section-search').style.display = 'none';
    
    // Remove active class from all tabs
    document.getElementById('nav-overview').classList.remove('active');
    document.getElementById('nav-create').classList.remove('active');
    document.getElementById('nav-search').classList.remove('active');
    
    // Show selected
    document.getElementById(`section-${tabName}`).style.display = 'block';
    document.getElementById(`nav-${tabName}`).classList.add('active');
}

// Load basic user profile to say "Welcome, Name"
async function loadReceiverProfile() {
    try {
        const profile = await api.get('/user/me'); // Adjust based on your generic user endpoint
        document.getElementById('receiverName').textContent = profile.data.name;
    } catch (e) {
        console.log("Could not load name");
    }
}

// Load requests created by this receiver
async function loadMyRequests() {
    const container = document.getElementById('myRequestsContainer');
    try {
        // Adjust endpoint to match ReceiverController
        const requests = await api.get('/receiver/requests/my-requests'); 
        
        if (requests.length === 0) {
            container.innerHTML = '<p style="color: #666;">You have no active blood requests.</p>';
            return;
        }

        container.innerHTML = requests.map(req => {
            // Determine status color
            let statusColor = '#FF9800'; // Pending (Orange)
            if (req.requestStatus === 'ACCEPTED' || req.requestStatus === 'FULFILLED') statusColor = '#4CAF50'; // Green
            
            let acceptedByText = '';
            if (req.acceptedByDonor) {
                acceptedByText = `<div style="margin-top:10px; padding:10px; background:#e8f5e9; border-radius:4px;">
                                    <strong>Accepted by Donor:</strong> ${req.acceptedByDonor.name} (${req.acceptedByDonor.phone})
                                  </div>`;
            } else if (req.acceptedByHospital) {
                acceptedByText = `<div style="margin-top:10px; padding:10px; background:#e8f5e9; border-radius:4px;">
                                    <strong>Accepted by Hospital:</strong> ${req.acceptedByHospital.hospitalName}
                                  </div>`;
            }

            return `
            <div style="border: 1px solid #ddd; padding: 15px; margin-bottom: 15px; border-radius: 5px; border-left: 5px solid ${statusColor};">
                <div style="display: flex; justify-content: space-between;">
                    <h4 style="margin:0;">${req.unitsRequired} Units of ${req.bloodGroup.replace('_', ' ')}</h4>
                    <span class="status-badge" style="background-color: ${statusColor};">${req.requestStatus}</span>
                </div>
                <p style="margin-top:10px;"><strong>Hospital:</strong> ${req.hospitalName}</p>
                <p><strong>Urgency:</strong> ${req.urgencyLevel}</p>
                <p style="font-size:0.85em; color:#888;">Requested on: ${new Date(req.createdAt).toLocaleString()}</p>
                ${acceptedByText}
            </div>
        `}).join('');

    } catch (error) {
        container.innerHTML = '<p style="color: red;">Failed to load your requests.</p>';
    }
}

// Search Network Functionality using Geolocation
window.searchNetwork = async function() {
    const type = document.getElementById('searchType').value; // DONOR or HOSPITAL
    const resultsContainer = document.getElementById('searchResults');
    
    // If you don't have a blood group dropdown in your search UI yet, you should add one.
    // For now, we will grab it if it exists, otherwise default to O_POS for testing.
    const bgInput = document.getElementById('searchBloodGroup');
    const bloodGroup = bgInput ? bgInput.value : 'O_POS'; 

    resultsContainer.innerHTML = '<p>Requesting location access...</p>';

    if (!navigator.geolocation) {
        resultsContainer.innerHTML = '<p style="color:red;">Geolocation is not supported by your browser.</p>';
        return;
    }

    // Get current GPS coordinates
    navigator.geolocation.getCurrentPosition(async (position) => {
        const lat = position.coords.latitude;
        const lon = position.coords.longitude;
        const radius = 50.0; // Default search radius of 50km

        resultsContainer.innerHTML = '<p>Searching nearby...</p>';

        try {
            let results = [];
            
            if (type === 'DONOR') {
                // Call the Search Donors endpoint
                results = await api.get(`/receiver/search-donors?bloodGroup=${bloodGroup}&lat=${lat}&lon=${lon}&radius=${radius}`);
                
                if (results.length === 0) {
                    resultsContainer.innerHTML = `<p style="color:#666;">No donors found for ${bloodGroup.replace('_', ' ')} within ${radius}km.</p>`;
                    return;
                }

                resultsContainer.innerHTML = results.map(d => `
                    <div style="border: 1px solid #ddd; padding: 10px; margin-bottom: 10px; border-radius: 5px;">
                        <strong>${d.name}</strong> - Blood Group: ${d.bloodGroup.replace('_', ' ')}
                        <br><small>Distance: ${d.distanceKm ? d.distanceKm.toFixed(2) + ' km' : 'Nearby'}</small>
                        <span class="status-badge status-${d.verificationStatus}" style="margin-left:10px;">${d.verificationStatus}</span>
                    </div>
                `).join('');

            } else {
                // Call the Search Hospitals endpoint
                results = await api.get(`/receiver/search-hospitals?lat=${lat}&lon=${lon}&radius=${radius}`);
                
                if (results.length === 0) {
                    resultsContainer.innerHTML = `<p style="color:#666;">No hospitals found within ${radius}km.</p>`;
                    return;
                }

                resultsContainer.innerHTML = results.map(h => `
                    <div style="border: 1px solid #ddd; padding: 10px; margin-bottom: 10px; border-radius: 5px;">
                        <strong>${h.hospitalName}</strong> - ${h.address}
                        <br><small>Distance: ${h.distanceKm ? h.distanceKm.toFixed(2) + ' km' : 'Nearby'}</small>
                        <span class="status-badge status-${h.verificationStatus}" style="margin-left:10px;">
                            ${h.verificationStatus === 'VERIFIED' ? 'Verified Institution' : 'Pending Verification'}
                        </span>
                    </div>
                `).join('');
            }

        } catch (error) {
            resultsContainer.innerHTML = `<p style="color:red;">Search failed: ${error.message}</p>`;
        }
    }, 
    (error) => {
        resultsContainer.innerHTML = `<p style="color:red;">Error getting location: ${error.message}. Please allow location access in your browser.</p>`;
    });
}