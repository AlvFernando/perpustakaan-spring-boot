package com.project.perpustakaan;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Report {
    @Id
    Integer id;
    String borrowerName;
    String bookTitle;
    String bookAuthor;
    String duration;
    String expectedReturnDate;
    public String getBorrowerName() {
        return borrowerName;
    }
    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }
    public String getBookTitle() {
        return bookTitle;
    }
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    public String getBookAuthor() {
        return bookAuthor;
    }
    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }
    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }
    public String getExpectedReturnDate() {
        return expectedReturnDate;
    }
    public void setExpectedReturnDate(String expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }
    @Override
    public String toString() {
        return "Report [borrowerName=" + borrowerName + ", bookTitle=" + bookTitle + ", bookAuthor=" + bookAuthor
                + ", duration=" + duration + ", expectedReturnDate=" + expectedReturnDate + "]";
    }
}
