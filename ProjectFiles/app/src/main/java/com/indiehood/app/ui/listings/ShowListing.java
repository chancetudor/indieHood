package com.indiehood.app.ui.listings;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ShowListing implements Serializable {
    private String bandName;
    private String venueName;
    private String startDay;
    private String endDay;
    private String startTime;
    private String endTime;
    private Double addressLat;
    private Double addressLong;
    private Double price;
    private String address1;
    private String address2;
    private String description;
    private Boolean userInterested;
    private Integer numberInterested;
    private String showID;


    public String startTimeFormatted;
    public String endTimeFormatted;
    public String priceFormatted;
    public String dateYear;
    public String dateMonth;
    public String dateDay;
    public String dateEndYear;
    public String dateEndMonth;
    public String dateEndDay;

  //  private Boolean mUserFavoritedBand;
  //  private Boolean mUserFavoritedVenue;
    //private String mDate;     //this will be converted into an image displayed rather than just an image of a calendar

    public void formatValues() {
        this.startTimeFormatted = formatTime(this.getStartTime()); //format HH:MM PM/AM 12 hr
        this.endTimeFormatted = formatTime(this.getEndTime());
        this.priceFormatted = formatPrice();
        String[] timeIntervals = getStartDay().split("-");
        this.dateYear = timeIntervals[0];
        this.dateMonth = formatMonth(timeIntervals[1]);
        this.dateDay = formatDay(Integer.parseInt(timeIntervals[2]));
        String[] timeIntervals2 = getEndDay().split("-");
        this.dateEndYear = timeIntervals[0];
        this.dateEndMonth = formatMonth(timeIntervals2[1]);
        this.dateEndDay = formatDay(Integer.parseInt(timeIntervals2[2]));
    }

    /*
    Formatting functions
     */
    private String formatMonth(String month) {
        switch (month) {
            case "01":
                month = "January";
                break;
            case "02":
                month = "February";
                break;
            case "03":
                month = "March";
                break;
            case "04":
                month = "April";
                break;
            case "05":
                month = "May";
                break;
            case "06":
                month = "June";
                break;
            case "07":
                month = "July";
                break;
            case "08":
                month = "August";
                break;
            case "09":
                month = "September";
                break;
            case "10":
                month = "October";
                break;
            case "11":
                month = "November";
                break;
            case "12":
                month = "December";
                break;
        }
        return month;
    }

    private String formatDay(int day) {
        String modifier;
        switch(day) {
            case 1:
                modifier = "st";
                break;
            case 2:
                modifier = "nd";
                break;
            case 3:
                modifier = "rd";
                break;
            default:
                modifier = "th";
                break;
        }
        return day + modifier;
    }

    private String formatTime(String time) { //format HH:MM 24 hr
        String[] divide = time.split(":");
        if (divide.length == 2) {
            int hours = Integer.parseInt(divide[0]);
            String designation;
            if (hours >= 12) {
                designation = "PM";
                if (hours != 12) {
                    hours = hours - 12;
                }
            } else {
                designation = "AM";
            }
            return hours + ":" + divide[1] + " " + designation;
        }
        else {
            return time;
        }
    }

    private String formatPrice() {
        Double price = getPrice();
        if (price == 0.0) {
            return "FREE";
        }
        else {
            NumberFormat formatter = new DecimalFormat("#0.00");
            String formatted = formatter.format(price);
            return "$" + formatted;
        }
    }

    /*
    Constructor
     */
    public ShowListing() {
        //emptyConstructor needed

    }



    /*
        Getters and Setters
         */
    public String getShowID() {
        return showID;
    }

    public void setShowID(String showID) {
        this.showID = showID;
    }
    public String getBandName() {
        return bandName;
    }

    public void setBandName(String bandName) {
        this.bandName = bandName;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public Double getAddressLat() {
        return addressLat;
    }

    public void setAddressLat(Double addressLat) {
        this.addressLat = addressLat;
    }

    public Double getAddressLong() {
        return addressLong;
    }

    public void setAddressLong(Double addressLong) {
        this.addressLong = addressLong;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getUserInterested() {
        return userInterested;
    }

    public void setUserInterested(Boolean userInterested) {
        this.userInterested = userInterested;
    }

    public Integer getNumberInterested() {
        return numberInterested;
    }

    public void setNumberInterested(Integer numberInterested) {
        this.numberInterested = numberInterested;
    }
    /*
    End Setters/Getters
     */

    /*
    Helper Text
     */
    public String getInterestedText() {
        if (getNumberInterested() == 1) {
            return getNumberInterested() + " person is interested";
        }
        else {
            return getNumberInterested() + " people are interested";
        }
    }

}
