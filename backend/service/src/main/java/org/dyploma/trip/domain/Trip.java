package org.dyploma.trip.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dyploma.tag.trip_tag.domain.TripTag;
import org.dyploma.trip.transfer.Transfer;
import org.dyploma.trip.place.PlaceInTrip;
import org.dyploma.useraccount.UserAccount;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trip", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "user_account_id"})
})
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("transferOrder ASC")
    private List<Transfer> transfers;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("visitOrder ASC")
    private List<PlaceInTrip> places;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "trip_trip_tag",
            joinColumns = @JoinColumn(name = "trip_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<TripTag> tags;

    private String name;
    private Date startDate;
    private Date endDate;
    private Date saveDate;
    private int passengerCount;
    private double totalCost;
    private int totalTransferTime;
    private int duration;

    public void addTransfer(Transfer transfer) {
        if (transfers == null) {
            transfers = new ArrayList<>();
        }
        transfers.add(transfer);
        transfer.setTrip(this);
    }

    public void addPlace(PlaceInTrip place) {
        if (places == null) {
            places = new ArrayList<>();
        }
        places.add(place);
        place.setTrip(this);
    }
}