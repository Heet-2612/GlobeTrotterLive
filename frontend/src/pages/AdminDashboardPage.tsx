import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { UserResponse } from '../types';
import { useAuth } from '../context/AuthContext';
import { Button, LoadingState } from '../components/common/UIComponents';
import { formatCurrency } from '../utils/currencyUtils';
import { Users, Map, TrendingUp, ShieldAlert, CheckCircle, XCircle } from 'lucide-react';

export const AdminDashboardPage: React.FC = () => {
  const { isAdmin } = useAuth();
  const [stats, setStats] = useState<any>(null);
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [trips, setTrips] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'overview' | 'users' | 'trips'>('overview');

  useEffect(() => {
    if (!isAdmin) return;
    const fetchData = async () => {
      try {
        setLoading(true);
        const [statsData, usersData, tripsData] = await Promise.all([
          api.getAdminStats(),
          api.getAdminUsers(),
          api.getAdminTrips()
        ]);
        setStats(statsData);
        setUsers(usersData);
        setTrips(tripsData);
      } catch (err) {
        console.error('Failed to load admin data', err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [isAdmin]);

  const handlePromote = async (id: number) => {
    if (!window.confirm('Are you sure you want to promote this user to ADMIN?')) return;
    try {
      await api.promoteUser(id);
      setUsers(users.map(u => u.id === id ? { ...u, role: 'ADMIN' } : u));
    } catch (e: any) {
      alert(e.response?.data?.message || 'Error promoting user');
    }
  };

  const handleDemote = async (id: number) => {
    if (!window.confirm('Are you sure you want to demote this ADMIN to USER?')) return;
    try {
      await api.demoteUser(id);
      setUsers(users.map(u => u.id === id ? { ...u, role: 'USER' } : u));
    } catch (e: any) {
      alert(e.response?.data?.message || 'Error demoting user');
    }
  };

  if (!isAdmin) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-16 text-center">
        <ShieldAlert className="mx-auto h-16 w-16 text-rose-500 mb-4" />
        <h1 className="text-3xl font-bold text-slate-900 mb-2">Access Denied</h1>
        <p className="text-slate-600">You do not have permission to view the admin dashboard.</p>
      </div>
    );
  }

  if (loading) return <LoadingState message="Loading Admin Dashboard..." />;

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <div className="flex justify-between items-end mb-8">
        <div>
          <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">Admin Dashboard</h1>
          <p className="text-slate-500 mt-1">Manage users, view stats, and monitor platform activity.</p>
        </div>
      </div>

      <div className="flex space-x-2 border-b border-slate-200 mb-8">
        {(['overview', 'users', 'trips'] as const).map(tab => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`px-4 py-2 text-sm font-semibold capitalize border-b-2 transition-colors ${
              activeTab === tab
                ? 'border-emerald-500 text-emerald-700'
                : 'border-transparent text-slate-500 hover:text-slate-700'
            }`}
          >
            {tab}
          </button>
        ))}
      </div>

      {activeTab === 'overview' && stats && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
            <div className="flex items-center text-emerald-600 mb-2">
              <Users size={20} className="mr-2" />
              <h3 className="font-semibold">Total Users</h3>
            </div>
            <p className="text-3xl font-bold text-slate-900">{stats.totalUsers}</p>
          </div>
          <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
            <div className="flex items-center text-blue-600 mb-2">
              <Map size={20} className="mr-2" />
              <h3 className="font-semibold">Total Trips</h3>
            </div>
            <p className="text-3xl font-bold text-slate-900">{stats.totalTrips}</p>
          </div>
          <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
            <div className="flex items-center text-amber-600 mb-2">
              <TrendingUp size={20} className="mr-2" />
              <h3 className="font-semibold">New Users (30d)</h3>
            </div>
            <p className="text-3xl font-bold text-slate-900">{stats.newUsers}</p>
          </div>
          <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
            <div className="flex items-center text-indigo-600 mb-2">
              <ShieldAlert size={20} className="mr-2" />
              <h3 className="font-semibold">Administrators</h3>
            </div>
            <p className="text-3xl font-bold text-slate-900">{stats.adminUsers}</p>
          </div>
        </div>
      )}

      {activeTab === 'users' && (
        <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-slate-200">
              <thead className="bg-slate-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">User</th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Role</th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Joined</th>
                  <th className="px-6 py-3 text-right text-xs font-semibold text-slate-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-slate-200">
                {users.map(u => (
                  <tr key={u.id}>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex flex-col">
                        <span className="font-medium text-slate-900">{u.name}</span>
                        <span className="text-sm text-slate-500">{u.email}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                        u.role === 'ADMIN' ? 'bg-indigo-100 text-indigo-800' : 'bg-slate-100 text-slate-800'
                      }`}>
                        {u.role}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-500">
                      {new Date(u.createdAt!).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium space-x-2">
                      {u.role === 'USER' ? (
                        <button onClick={() => handlePromote(u.id)} className="text-indigo-600 hover:text-indigo-900">Promote</button>
                      ) : (
                        <button onClick={() => handleDemote(u.id)} className="text-rose-600 hover:text-rose-900">Demote</button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {activeTab === 'trips' && (
        <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-slate-200">
              <thead className="bg-slate-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Trip Name</th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">User</th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Dates</th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Created</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-slate-200">
                {trips.map(t => (
                  <tr key={t.id}>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="font-medium text-slate-900">{t.name}</span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex flex-col">
                        <span className="text-sm font-medium text-slate-900">{t.userName}</span>
                        <span className="text-xs text-slate-500">{t.userEmail}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-500">
                      {t.startDate} - {t.endDate}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-500">
                      {new Date(t.createdAt).toLocaleDateString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};
