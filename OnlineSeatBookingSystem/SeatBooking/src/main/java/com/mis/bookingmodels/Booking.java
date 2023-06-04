package com.mis.bookingmodels;

import jakarta.persistence.*;

@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long bookingId;
    private String startDate;
    private String endDate;
    private String buildingName;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    User userinfo;
    private String startTime;
    private String endTime;
    private Integer seatId;

    public Integer getSeatId() {
        return seatId;
    }

    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }

    @Override
    public String toString() {
        return "\n\n\n Booking Details" +
                "\n * BookingId : " + bookingId +
                "\n * StartDate : " + startDate +
                "\n * EndDate : " + endDate +
                "\n * BuildingName : " + buildingName +
                "\n * StartTime : " + startTime +
                "\n * EndTime : " + endTime
                ;
    }
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public Booking(String startDate, String endDate, String startTime, String endTime, User userinfo, String buildingName,Integer seatId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userinfo = userinfo;
        this.buildingName=buildingName;
        this.seatId=seatId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public User getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(User userinfo) {
        this.userinfo = userinfo;
    }

    public Booking() {

    }

    public String toString1() {
        return "Booking Details" +
                "\n * BookingId : " + bookingId +
                "\n * StartDate : " + startDate +
                "\n * EndDate : " + endDate +
                "\n * BuildingName : " + buildingName +
                "\n * StartTime : " + startTime +
                "\n * EndTime : " + endTime
                ;
    }
}
