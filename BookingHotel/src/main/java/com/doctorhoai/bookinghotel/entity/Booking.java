package com.doctorhoai.bookinghotel.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table( name = "booking")
public class Booking {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "check in date is required")
    private LocalDate checkInDate;
    @Future(message = "check out date in required")
    private LocalDate checkOutDate;
    @Min(value = 1, message = "Nummber of adults must not be less than 1")
    private Integer numberOfAdults;
    @Min(value = 0, message = "Nummber of children must not be less than 0")
    private Integer numberOfChildren;
    private Integer totalNumberOfGuest;
    private String bookingConfirmationCode;
    @ManyToOne( fetch = FetchType.EAGER )
    @JoinColumn( name = "user" )
    private User user;
    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn( name = "room" )
    private Room room;

    public void calculateTotalnumberOfGuest(){
        if( this.numberOfChildren != null && this.numberOfAdults != null ){
            this.totalNumberOfGuest = this.numberOfAdults + this.numberOfChildren;
        }
    }

    public void setNumberOfAdults(Integer numberOfAdults) {
        this.numberOfAdults = numberOfAdults;
        calculateTotalnumberOfGuest();
    }

    public void setNumberOfChildren(Integer numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
        calculateTotalnumberOfGuest();
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", numberOfAdults=" + numberOfAdults +
                ", numberOfChildren=" + numberOfChildren +
                ", totalNumberOfGuest=" + totalNumberOfGuest +
                ", bookingConfirmationCode='" + bookingConfirmationCode + '\'' +
                '}';
    }
}
