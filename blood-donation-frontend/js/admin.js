// js/admin.js

document.addEventListener('DOMContentLoaded', () => {
    // 1. Auth Guard
    const token = api.getToken();
    const role = localStorage.getItem('userRole');
    
    if (!token || role !== 'ADMIN') {
        alert('Unauthorized access. Administrator privileges required.');
        api.logout();
        return;
    }

    // Initialize Dashboard
    loadSystemStats();
    loadPendingHospitals();
    loadAllUsers();

    // 2. Fetch System Statistics
    async function loadSystemStats() {
        try {
            // FIX 1: Changed from '/admin/stats' to '/admin/dashboard-stats'
            const stats = await api.get('/admin/dashboard-stats'); 
            
            document.getElementById('stat-donors').textContent = stats.totalDonors || 0;
            // Note: verifiedDonors and totalDonations are missing from your backend DashboardStatsResponse, 
            // so we will comment them out or map them to existing data.
            // document.getElementById('stat-verified-donors').textContent = stats.verifiedDonors || 0;
            document.getElementById('stat-hospitals').textContent = stats.totalHospitals || 0;
            document.getElementById('stat-blood-units').textContent = stats.availableBloodUnits || 0;
            document.getElementById('stat-requests').textContent = stats.activeBloodRequests || 0; // Fixed key
            document.getElementById('stat-donations').textContent = stats.donationRecords || 0; // Fixed key

        } catch (error) {
            console.error('Failed to load stats', error);
        }
    }

    // 3. Load Pending Hospitals
    async function loadPendingHospitals() {
        const tbody = document.getElementById('pendingHospitalsTable');
        try {
            const hospitals = await api.get('/admin/hospitals/pending');
            
            if (hospitals.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4" style="text-align: center; color: #666;">No pending hospital registrations.</td></tr>';
                return;
            }

            tbody.innerHTML = hospitals.map(h => `
                <tr>
                    <td><strong>${h.hospitalName}</strong></td>
                    <td>${h.licenseNumber}</td>
                    <td>${h.city}, ${h.state}</td>
                    <td>
                        <button class="btn btn-primary" style="padding: 5px 10px; width: auto; background: #4CAF50;" onclick="verifyHospital(${h.id}, 'VERIFIED')">Approve</button>
                        <button class="btn btn-secondary" style="padding: 5px 10px; width: auto; background: #F44336; color: white;" onclick="verifyHospital(${h.id}, 'REJECTED')">Reject</button>
                    </td>
                </tr>
            `).join('');

        } catch (error) {
            tbody.innerHTML = '<tr><td colspan="4" style="color: red; text-align: center;">Failed to load data.</td></tr>';
        }
    }

    // 4. Load All Users for Management
    async function loadAllUsers() {
        const tbody = document.getElementById('usersTable');
        try {
            const users = await api.get('/admin/users'); 
            
            if (users.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4" style="text-align: center; color: #666;">No users found.</td></tr>';
                return;
            }

            tbody.innerHTML = users.map(u => {
                const statusColor = u.enabled ? '#4CAF50' : '#F44336';
                const statusText = u.enabled ? 'Active' : 'Suspended';
                const actionBtn = u.enabled 
                    ? `<button class="btn btn-secondary" style="padding: 5px 10px; width: auto; background: #F44336; color: white;" onclick="toggleUserStatus(${u.id}, false)">Suspend</button>`
                    : `<button class="btn btn-primary" style="padding: 5px 10px; width: auto; background: #4CAF50;" onclick="toggleUserStatus(${u.id}, true)">Activate</button>`;

                // Don't allow admin to suspend themselves easily
                const isSelf = u.role === 'ADMIN' ? 'disabled' : '';

                return `
                <tr>
                    <td>${u.email}</td>
                    <td><span class="status-badge" style="background: #333;">${u.role}</span></td>
                    <td><span style="color: ${statusColor}; font-weight: bold;">${statusText}</span></td>
                    <td>${u.role !== 'ADMIN' ? actionBtn : '<span style="color: #aaa;">Protected</span>'}</td>
                </tr>
            `}).join('');

        } catch (error) {
            tbody.innerHTML = '<tr><td colspan="4" style="color: red; text-align: center;">Failed to load users.</td></tr>';
        }
    }

    // --- Global Actions ---

    // Approve/Reject Hospital
    window.verifyHospital = async function(hospitalId, status) {
        if (!confirm(`Are you sure you want to mark this hospital as ${status}?`)) return;
        
        try {
            // FIX 2: Backend expects URL @RequestParams for verify-hospital, not a JSON body
            await api.post(`/admin/verify-hospital?hospitalId=${hospitalId}&status=${status}`, {});
            
            alert(`Hospital successfully ${status.toLowerCase()}.`);
            loadPendingHospitals(); // Refresh table
            loadSystemStats(); // Refresh stats
        } catch (error) {
            alert('Action failed: ' + error.message);
        }
    }

    // Suspend/Activate User 
    window.toggleUserStatus = async function(userId, enable) {
        const actionText = enable ? 'activate' : 'suspend';
        if (!confirm(`Are you sure you want to ${actionText} this user account?`)) return;
        
        try {
            // FIX 3: Backend uses PUT and has two separate endpoints for suspend/reactivate
            if (enable) {
                await api.put(`/admin/users/${userId}/reactivate`, {});
            } else {
                await api.put(`/admin/users/${userId}/suspend`, {});
            }
            
            alert(`User account ${enable ? 'activated' : 'suspended'} successfully.`);
            loadAllUsers(); // Refresh table
        } catch (error) {
            alert('Action failed: ' + error.message);
        }
    }
});

// Tab Switcher
window.switchTab = function(tabName) {
    document.getElementById('section-overview').style.display = 'none';
    document.getElementById('section-hospitals').style.display = 'none';
    document.getElementById('section-users').style.display = 'none';
    
    document.getElementById('nav-overview').classList.remove('active');
    document.getElementById('nav-hospitals').classList.remove('active');
    document.getElementById('nav-users').classList.remove('active');
    
    document.getElementById(`section-${tabName}`).style.display = 'block';
    document.getElementById(`nav-${tabName}`).classList.add('active');
}