import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './User.css';

const API_BASE_URL = import.meta.env.VITE_BACKEND_URL ?? 'http://localhost:8080';

interface UserData {
  userId: number;
  name: string;
  email: string;
  preference?: {
    maxBudget?: number;
    preferredClimate?: string;
    tripLength?: number;
    homeAirport?: string;
    currency?: string;
  };
}

const User = () => {
  const [userData, setUserData] = useState<UserData | null>(null);
  const [editMode, setEditMode] = useState(false);
  const [editedData, setEditedData] = useState<UserData | null>(null);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchUserData();
  }, []);

  const fetchUserData = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/user`, {
        credentials: 'include',
      });

      if (response.ok) {
        const data = await response.json();
        setUserData(data);
        setEditedData(data);
      } else {
        setError('Failed to load user data.');
      }
    } catch (err) {
      console.error('Error fetching user data', err);
      setError('Unable to load user data.');
    } finally {
      setLoading(false);
    }
  };

  const handleSaveProfile = async () => {
    if (!editedData) return;

    setIsSubmitting(true);
    setError('');
    setMessage('');

    try {
      const response = await fetch(`${API_BASE_URL}/api/user/updateProfile`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify(editedData),
      });

      if (response.ok) {
        const updatedData = await response.json();
        setUserData(updatedData);
        setEditedData(updatedData);
        setEditMode(false);
        setMessage('Profile updated successfully!');
      } else {
        setError('Failed to update profile.');
      }
    } catch (err) {
      console.error('Error updating profile', err);
      setError('Unable to update profile.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCancelEdit = () => {
    setEditedData(userData);
    setEditMode(false);
    setError('');
    setMessage('');
  };

  const handleLogout = async () => {
    setError('');
    setMessage('');
    setIsSubmitting(true);

    try {
      const response = await fetch(`${API_BASE_URL}/logout`, {
        method: 'POST',
        credentials: 'include',
      });

      if (response.ok) {
        setMessage('You have been logged out.');
        navigate('/login', { replace: true });
      } else {
        setError('Logout failed. Please try again.');
      }
    } catch (err) {
      console.error('Logout error', err);
      setError('Unable to reach the server.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDeleteProfile = async () => {
    setIsSubmitting(true);
    setError('');
    setMessage('');

    try {
      const response = await fetch(`${API_BASE_URL}/api/user/deleteProfile`, {
        method: 'DELETE',
        credentials: 'include',
      });

      if (response.ok) {
        setMessage('Profile deleted successfully.');
        setTimeout(() => {
          navigate('/', { replace: true });
        }, 1000);
      } else {
        setError('Failed to delete profile.');
      }
    } catch (err) {
      console.error('Delete profile error', err);
      setError('Unable to delete profile.');
    } finally {
      setIsSubmitting(false);
      setShowDeleteConfirm(false);
    }
  };

  if (loading) {
    return (
      <div className="loading">
        <p className="loading-text">Loading...</p>
      </div>
    );
  }

  return (
    <div className="user-page">
      <div className="user-container">
        <div className="user-header">
          <h1 className="user-title">My Account</h1>
          <button onClick={() => navigate('/')} className="btn-back">
            Back to Home
          </button>
        </div>

        {error && (
          <div className="alert alert-error">
            {error}
          </div>
        )}

        {message && (
          <div className="alert alert-success">
            {message}
          </div>
        )}

        {userData && editedData && (
          <div className="user-grid">
            {/* Personal Info */}
            <div className="user-card">
              <div className="card-header">
                <h2 className="card-title">Personal Information</h2>
                {!editMode && (
                  <button onClick={() => setEditMode(true)} className="btn-edit">
                    Edit Profile
                  </button>
                )}
              </div>

              <div className="field-list">
                <div className="field-row">
                  <span className="field-label">Name:</span>
                  {editMode ? (
                    <input
                      type="text"
                      value={editedData.name || ''}
                      onChange={(e) => setEditedData({ ...editedData, name: e.target.value })}
                      className="field-input"
                      placeholder="Enter your name"
                    />
                  ) : (
                    <span className="field-value">
                      {userData.name || <span className="field-value-empty">Not set</span>}
                    </span>
                  )}
                </div>

                <div className="field-row">
                  <span className="field-label" title="Email cannot be changed">Email:</span>
                  <span className="field-value">{userData.email}</span>
                </div>
              </div>

              {editMode && (
                <div className="button-group">
                  <button
                    onClick={handleSaveProfile}
                    disabled={isSubmitting}
                    className="btn-save"
                  >
                    {isSubmitting ? 'Saving...' : 'Save Changes'}
                  </button>
                  <button
                    onClick={handleCancelEdit}
                    disabled={isSubmitting}
                    className="btn-cancel"
                  >
                    Cancel
                  </button>
                </div>
              )}
            </div>

            {/* Travel Preferences */}
            <div className="user-card">
              <h2 className="card-title" style={{ marginBottom: '24px' }}>Travel Preferences</h2>

              <div className="field-list">
                <div className="field-row">
                  <span className="field-label" title="3-letter airport code (e.g., DUB)">Home Airport:</span>
                  {editMode ? (
                    <input
                      type="text"
                      value={editedData.preference?.homeAirport || ''}
                      onChange={(e) => setEditedData({
                        ...editedData,
                        preference: { ...editedData.preference, homeAirport: e.target.value.toUpperCase() }
                      })}
                      className="field-input-small text-uppercase text-center"
                      placeholder="DUB"
                      maxLength={3}
                    />
                  ) : (
                    <span className="field-value">
                      {userData.preference?.homeAirport || <span className="field-value-empty">Not set</span>}
                    </span>
                  )}
                </div>

                <div className="field-row">
                  <span className="field-label">Max Budget:</span>
                  {editMode ? (
                    <div className="input-group">
                      <select
                        value={editedData.preference?.currency || 'EUR'}
                        onChange={(e) => setEditedData({
                          ...editedData,
                          preference: { ...editedData.preference, currency: e.target.value }
                        })}
                        className="field-select"
                      >
                        <option value="EUR">€</option>
                        <option value="USD">$</option>
                        <option value="GBP">£</option>
                      </select>
                      <input
                        type="number"
                        value={editedData.preference?.maxBudget || ''}
                        onChange={(e) => setEditedData({
                          ...editedData,
                          preference: { ...editedData.preference, maxBudget: Number(e.target.value) }
                        })}
                        className="field-input-small"
                        placeholder="1000"
                      />
                    </div>
                  ) : (
                    <span className="field-value">
                      {userData.preference?.maxBudget
                        ? `${userData.preference.currency || 'EUR'} ${userData.preference.maxBudget}`
                        : <span className="field-value-empty">Not set</span>}
                    </span>
                  )}
                </div>

                <div className="field-row">
                  <span className="field-label">Climate:</span>
                  {editMode ? (
                    <select
                      value={editedData.preference?.preferredClimate || ''}
                      onChange={(e) => setEditedData({
                        ...editedData,
                        preference: { ...editedData.preference, preferredClimate: e.target.value }
                      })}
                      className="field-select"
                      style={{ width: '192px' }}
                    >
                      <option value="">Select...</option>
                      <option value="Tropical">Tropical</option>
                      <option value="Temperate">Temperate</option>
                      <option value="Cold">Cold</option>
                      <option value="Desert">Desert</option>
                    </select>
                  ) : (
                    <span className="field-value">
                      {userData.preference?.preferredClimate || <span className="field-value-empty">Not set</span>}
                    </span>
                  )}
                </div>

                <div className="field-row">
                  <span className="field-label">Trip Length:</span>
                  {editMode ? (
                    <div className="input-group">
                      <input
                        type="number"
                        value={editedData.preference?.tripLength || ''}
                        onChange={(e) => setEditedData({
                          ...editedData,
                          preference: { ...editedData.preference, tripLength: Number(e.target.value) }
                        })}
                        className="field-input-small"
                        style={{ width: '96px' }}
                        placeholder="7"
                        min="1"
                      />
                      <span style={{ color: '#9ca3af', fontSize: '14px' }}>days</span>
                    </div>
                  ) : (
                    <span className="field-value">
                      {userData.preference?.tripLength
                        ? `${userData.preference.tripLength} days`
                        : <span className="field-value-empty">Not set</span>}
                    </span>
                  )}
                </div>
              </div>
            </div>

            {/* Account Actions */}
            <div className="user-card" style={{ gridColumn: '1 / -1' }}>
              <h2 className="card-title" style={{ marginBottom: '16px' }}>Account Actions</h2>
              <div style={{ display: 'flex', gap: '16px' }}>
                <button
                  onClick={handleLogout}
                  disabled={isSubmitting}
                  className="btn-logout"
                >
                  {isSubmitting ? 'Logging out…' : 'Log out'}
                </button>
                <button
                  onClick={() => setShowDeleteConfirm(true)}
                  disabled={isSubmitting}
                  className="btn-delete"
                >
                  Delete Profile
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Delete Confirmation Pop-up */}
        {showDeleteConfirm && (
          <div className="modal-overlay" onClick={() => setShowDeleteConfirm(false)}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
              <h3 className="modal-title">Delete Profile</h3>
              <p className="modal-text">
                Are you sure you want to delete your profile? This action cannot be undone.
                All your data, including preferences and search history, will be permanently deleted.
              </p>
              <div className="modal-buttons">
                <button
                  onClick={() => setShowDeleteConfirm(false)}
                  className="btn-modal-cancel"
                  disabled={isSubmitting}
                >
                  Cancel
                </button>
                <button
                  onClick={handleDeleteProfile}
                  className="btn-confirm-delete"
                  disabled={isSubmitting}
                >
                  {isSubmitting ? 'Deleting...' : 'Delete Profile'}
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default User;

