package org.dyploma.place;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "place")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
