package com.dbtech.system.models;

import java.time.LocalDate;

public class Staff {
    private int staffId;
    private String fullName;
    private String role;
    private String phoneNumber;
    private String workDays;
    private double salary;
    private LocalDate hiringDate;

    public Staff(int staffId, String fullName, String role, String phoneNumber, String workDays, double salary, LocalDate hiringDate) {
        this.staffId = staffId;
        this.fullName = fullName;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.workDays = workDays;
        this.salary = salary;
        this.hiringDate = hiringDate;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWorkDays() {
        return workDays;
    }

    public void setWorkDays(String workDays) {
        this.workDays = workDays;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public LocalDate getHiringDate() {
        return hiringDate;
    }

    public void setHiringDate(LocalDate hiringDate) {
        this.hiringDate = hiringDate;
    }
}
