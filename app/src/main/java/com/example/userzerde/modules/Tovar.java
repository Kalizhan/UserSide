package com.example.userzerde.modules;

import java.io.Serializable;
import java.util.Comparator;

public class Tovar implements Serializable {
    String name, code, photo, date, dopInfo;
    int quantity, satyldyShtuk;
    Long price;


    public Tovar(){}

    public Tovar(String name, String code,  String photo, Long price, int quantity, int satyldyShtuk, String date, String dopInfo) {
        this.name = name;
        this.code = code;
        this.price = price;
        this.photo = photo;
        this.quantity = quantity;
        this.satyldyShtuk = satyldyShtuk;
        this.date = date;
        this.dopInfo = dopInfo;
    }

    public static Comparator<Tovar> tovarNameComparator = new Comparator<Tovar>() {

        public int compare(Tovar s1, Tovar s2) {
            String bookName1 = s1.getName().toUpperCase();
            String bookName2 = s2.getName().toUpperCase();

            //ascending order
            return bookName1.compareTo(bookName2);

            //descending order
            //return StudentName2.compareTo(StudentName1);
        }
    };

    public static Comparator<Tovar> tovarDateComparator = new Comparator<Tovar>() {

        public int compare(Tovar s1, Tovar s2) {
            String date1 = s1.getDate();
            String date2 = s2.getDate();

            //ascending order
            return date2.compareTo(date1);

            //descending order
            //return StudentName2.compareTo(StudentName1);
        }
    };

    public static Comparator<Tovar> tovarSatylym = new Comparator<Tovar>() {

        public int compare(Tovar s1, Tovar s2) {
            int shtuk1 = s1.getSatyldyShtuk();
            int shtuk2 = s2.getSatyldyShtuk();

            //ascending order
//            return bookAuthor2.compareTo(bookAuthor1);
            return (int) (shtuk2-shtuk1);

            //descending order
            //return StudentName2.compareTo(StudentName1);
        }
    };

    public static Comparator<Tovar> tovarQuanityAvailabilityComparator = new Comparator<Tovar>() {

        public int compare(Tovar s1, Tovar s2) {

            int rating1 = s1.getQuantity();
            int rating2 = s2.getQuantity();

            /*For ascending order*/
//            return rating2.compareTo(rating1);
            return (rating2-rating1);

            /*For descending order*/
            //rollno2-rollno1;
        }
    };

    public static Comparator<Tovar> tovarPriceComparator = new Comparator<Tovar>() {

        public int compare(Tovar s1, Tovar s2) {

            Long rating1 = s1.getPrice();
            Long rating2 = s2.getPrice();

            /*For ascending order*/
//            return rating2.compareTo(rating1);
            return (int) (rating1-rating2);

            /*For descending order*/
            //rollno2-rollno1;
        }
    };

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSatyldyShtuk() {
        return satyldyShtuk;
    }

    public void setSatyldyShtuk(int satyldyShtuk) {
        this.satyldyShtuk = satyldyShtuk;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDopInfo() {
        return dopInfo;
    }

    public void setDopInfo(String dopInfo) {
        this.dopInfo = dopInfo;
    }
}
