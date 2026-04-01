// js/api.js
const API_BASE_URL = 'http://localhost:8080'; // Adjust to your Spring Boot port

const api = {
    // Helper to get the token
    getToken: () => localStorage.getItem('jwtToken'),

    // Helper to get headers
    getHeaders: function(isMultipart = false) {
        const headers = {};
        if (!isMultipart) {
            headers['Content-Type'] = 'application/json';
        }
        const token = this.getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
        return headers;
    },

    // Standard POST request
    post: async function(endpoint, data) {
        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify(data)
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error(`POST ${endpoint} failed:`, error);
            throw error;
        }
    },

    // Standard GET request
    get: async function(endpoint) {
        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`, {
                method: 'GET',
                headers: this.getHeaders()
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error(`GET ${endpoint} failed:`, error);
            throw error;
        }
    },

    // Standard PUT request
    put: async function(endpoint, data) {
        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`, {
                method: 'PUT',
                headers: this.getHeaders(),
                body: JSON.stringify(data)
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error(`PUT ${endpoint} failed:`, error);
            throw error;
        }
    },

    postFormData: async function(endpoint, formData) {
        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`, {
                method: 'POST',
                headers: this.getHeaders(true), // 'true' omits the Content-Type header so the browser handles the file boundary automatically
                body: formData // Send the raw FormData, do NOT JSON.stringify it!
            });
            return await this.handleResponse(response);
        } catch (error) {
            console.error(`POST FormData ${endpoint} failed:`, error);
            throw error;
        }
    },

    // Centralized response handling
    handleResponse: async function(response) {
        const data = await response.json();
        
        if (!response.ok) {
            // Backend sends error messages in the 'message' field
            throw new Error(data.message || 'Something went wrong');
        }
        
        // This is the crucial line: it unwraps the ApiResponse wrapper
        // so your scripts don't have to keep digging for .data
        return data.data !== undefined ? data.data : data; 
    },

    // Logout and clear token
    logout: function() {
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('userRole');
        window.location.href = '../index.html';
    }
};