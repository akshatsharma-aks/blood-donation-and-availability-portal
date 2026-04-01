// js/hospital.js

document.addEventListener('DOMContentLoaded', () => {
    // 1. Auth Guard
    const token = api.getToken();
    const role = localStorage.getItem('userRole');
    
    if (!token || role !== 'HOSPITAL') {
        alert('Unauthorized access. Please login as a Hospital.');
        api.logout();
        return;
    }

    let isHospitalVerified = false;

    // 2. Load Hospital Profile & Status
    async function loadHospitalProfile() {
        try {
            // Adjust endpoint to match HospitalController
            const profile = await api.get('/hospital/profile'); 
            
            document.getElementById('hospitalName').textContent = profile.data.hospitalName;
            
            const badge = document.getElementById('hospitalStatusBadge');
            badge.textContent = profile.verificationStatus;
            badge.className = `status-badge status-${profile.verificationStatus}`;

            if (profile.verificationStatus === 'VERIFIED') {
                isHospitalVerified = true;
            } else {
                document.getElementById('unverifiedWarning').style.display = 'block';
            }

        } catch (error) {
            console.error('Failed to load hospital profile', error);
        }
    }

    // 3. Load & Manage Inventory
    async function loadInventory() {
        const container = document.getElementById('inventoryContainer');
        try {
            // Fetch current inventory array mapping to BloodInventory entity
            const inventory = await api.get('/hospital/inventory'); 
            
            // All 8 blood groups
            const allGroups = ['A_POS', 'A_NEG', 'B_POS', 'B_NEG', 'AB_POS', 'AB_NEG', 'O_POS', 'O_NEG'];
            
            // Create a lookup map from backend data
            const invMap = {};
            inventory.forEach(item => {
                invMap[item.bloodGroup] = item.unitsAvailable;
            });

            container.innerHTML = allGroups.map(bg => {
                const currentUnits = invMap[bg] || 0;
                const displayName = bg.replace('_', ' ');
                return `
                    <div class="inventory-card">
                        <h2>${displayName}</h2>
                        <p>Units Available</p>
                        <input type="number" id="inv-${bg}" value="${currentUnits}" min="0">
                        <button class="btn btn-primary" style="padding: 5px; font-size: 12px;" onclick="updateInventory('${bg}')">Update</button>
                    </div>
                `;
            }).join('');

        } catch (error) {
            container.innerHTML = '<p style="color: red;">Failed to load inventory.</p>';
        }
    }

    // 4. Load Pending Donor Verifications
    async function loadPendingVerifications() {
        const container = document.getElementById('verificationsContainer');
        
        if (!isHospitalVerified) {
            container.innerHTML = '<p style="color: #666;">You must be verified by the Admin before you can verify donors.</p>';
            return;
        }

        try {
            // Adjust to match your endpoint for VerificationRequest entities assigned to this hospital
            const requests = await api.get('/hospital/verifications/pending');
            
            if (requests.length === 0) {
                container.innerHTML = '<p style="color: #666;">No pending donor verifications.</p>';
                return;
            }

            container.innerHTML = requests.map(req => `
                <div style="border: 1px solid #ddd; padding: 15px; margin-bottom: 10px; border-radius: 5px; display: flex; justify-content: space-between; align-items: center;">
                    <div>
                        <h4 style="margin-bottom: 5px;">${req.donor.name}</h4>
                        <p style="font-size: 14px; color: #666;">Requested on: ${new Date(req.requestedAt).toLocaleDateString()}</p>
                    </div>
                    <div>
                        <button class="btn btn-primary" style="width: auto; padding: 5px 15px; background: #4CAF50;" onclick="resolveVerification(${req.id}, 'VERIFIED')">Approve</button>
                        <button class="btn btn-secondary" style="width: auto; padding: 5px 15px; background: #F44336; color: white;" onclick="resolveVerification(${req.id}, 'REJECTED')">Reject</button>
                    </div>
                </div>
            `).join('');

        } catch (error) {
            container.innerHTML = '<p style="color: red;">Failed to load verification requests.</p>';
        }
    }

    // Initial Load Sequence
    async function initialize() {
        await loadHospitalProfile();
        loadInventory();
        loadPendingVerifications();
    }
    
    initialize();

    // --- Global Functions for Button Clicks ---

    window.updateInventory = async function(bloodGroup) {
        const units = document.getElementById(`inv-${bloodGroup}`).value;
        const payload = { bloodGroup: bloodGroup, unitsAvailable: parseInt(units) };
        
        try {
            // FIX: Changed from '/hospital/inventory/update' to '/hospital/inventory'
            await api.post('/hospital/inventory', payload); 
            
            alert(`${bloodGroup.replace('_', ' ')} inventory updated to ${units} units.`);
        } catch (error) {
            alert('Failed to update inventory: ' + error.message);
        }
    };

    window.resolveVerification = async function(requestId, status) {
        if(!confirm(`Are you sure you want to mark this donor as ${status}?`)) return;

        try {
            // Endpoint to update VerificationRequest status and DonorProfile verification expiry
            await api.post(`/hospital/verifications/${requestId}/resolve`, { status: status });
            alert(`Donor verification ${status.toLowerCase()} successfully.`);
            loadPendingVerifications(); // Refresh list
        } catch (error) {
            alert('Failed to resolve verification: ' + error.message);
        }
    };

});

// Tab Switcher logic (same as receiver)
window.switchTab = function(tabName) {
    document.getElementById('section-inventory').style.display = 'none';
    document.getElementById('section-verify').style.display = 'none';
    document.getElementById('section-requests').style.display = 'none';
    
    document.getElementById('nav-inventory').classList.remove('active');
    document.getElementById('nav-verify').classList.remove('active');
    document.getElementById('nav-requests').classList.remove('active');
    
    document.getElementById(`section-${tabName}`).style.display = 'block';
    document.getElementById(`nav-${tabName}`).classList.add('active');
}