package com.example.mygymcomplete;

public class GymMember {
    private String memberId;
    private String fullName;
    private String mobileNo;
    private String email;
    private String registrationCode;
    private String startDate;
    private String dueDate;
    private String totalAmount;
    private String dueAmount;
    private String paidAmount;
    private String age;
    private String weight; // New field for weight
    private String height; // New field for height
    private String healthIssues; // New field for health issues
    private String gender;
    private String selectedPackage;
    private String photoUrl; // New field for storing the photo URL

    public GymMember() {
        // Default constructor required for Firebase
    }

    public GymMember(String memberId, String fullName, String mobileNo, String email, String registrationCode,
                     String startDate, String dueDate, String totalAmount, String dueAmount, String paidAmount,
                     String age, String weight, String height, String healthIssues, String gender, String selectedPackage, String photoUrl) {
        this.memberId = memberId;
        this.fullName = fullName;
        this.mobileNo = mobileNo;
        this.email = email;
        this.registrationCode = registrationCode;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.totalAmount = totalAmount;
        this.dueAmount = dueAmount;
        this.paidAmount = paidAmount;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.healthIssues = healthIssues;
        this.gender = gender;
        this.selectedPackage = selectedPackage;
        this.photoUrl = photoUrl; // Initialize photo URL
    }

    // Getters and setters
    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(String dueAmount) {
        this.dueAmount = dueAmount;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getHealthIssues() {
        return healthIssues;
    }

    public void setHealthIssues(String healthIssues) {
        this.healthIssues = healthIssues;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSelectedPackage() {
        return selectedPackage;
    }

    public void setSelectedPackage(String selectedPackage) {
        this.selectedPackage = selectedPackage;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
