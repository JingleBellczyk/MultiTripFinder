package org.dyploma.trip.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dyploma.tag.Tag;
import org.dyploma.trip.transfer.Transfer;
import org.dyploma.trip.place.PlaceInTrip;

import java.sql.Date;
import java.util.List;

@Data
@Builder
@Entity
@Table(name = "trip")
@AllArgsConstructor
@NoArgsConstructor
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private Date startDate;
    private Date endDate;
    private Date saveDate;
    private int passengerCount;
    private double totalCost;
    private int totalTransferTime;
    private int duration;

    @OneToMany(mappedBy = "trip")
    private List<Transfer> transfers;

    @OneToMany(mappedBy = "trip")
    private List<PlaceInTrip> places;

    @ManyToMany
    @JoinTable(
            name = "trip_tag",
            joinColumns = @JoinColumn(name = "trip_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;
}