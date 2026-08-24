package com.globetrotter.dto;

public class AdminStatsResponse {
    private long totalUsers;
    private long totalTrips;
    private long newUsers; // Users created in the last 30 days
    private long adminUsers;

    public AdminStatsResponse() {}

    public AdminStatsResponse(long totalUsers, long totalTrips, long newUsers, long adminUsers) {
        this.totalUsers = totalUsers;
        this.totalTrips = totalTrips;
        this.newUsers = newUsers;
        this.adminUsers = adminUsers;
    }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalTrips() { return totalTrips; }
    public void setTotalTrips(long totalTrips) { this.totalTrips = totalTrips; }

    public long getNewUsers() { return newUsers; }
    public void setNewUsers(long newUsers) { this.newUsers = newUsers; }

    public long getAdminUsers() { return adminUsers; }
    public void setAdminUsers(long adminUsers) { this.adminUsers = adminUsers; }
}
