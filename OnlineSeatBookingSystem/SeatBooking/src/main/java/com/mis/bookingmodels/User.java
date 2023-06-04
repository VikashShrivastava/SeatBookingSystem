package com.mis.bookingmodels;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "user_table")
public class User {
    @Id
    private String userId;
    private String name;

    public User() {

    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", password='" + password + '\'' +
                ", userType=" + userType +
                ", bookingList=" + bookingList +
                '}';
    }

    public String getUserId() {
        return userId;
    }
    public User(String userId, String password){
        this.userId = userId;
        this.password = password;
    }

    public User(String userId, String name, String userEmail, String password, int userType, List<Booking> bookingList) {
        this.userId = userId;
        this.name = name;
        this.userEmail = userEmail;
        this.password = password;
        this.userType = userType;
        this.bookingList = bookingList;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public List<Booking> getBookingList() {
        return bookingList;
    }

    public void setBookingList(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    private String userEmail;
    private String password;
    private int userType;

    @OneToMany(mappedBy = "userinfo", cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    List<Booking>bookingList;
}
