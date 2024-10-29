package org.dyploma.search.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "search")
public class Search {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
