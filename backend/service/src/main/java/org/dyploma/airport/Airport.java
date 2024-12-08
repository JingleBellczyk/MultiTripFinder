package org.dyploma.airport;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Airports")
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "airport_code")
    private String airportCode;
    @Column(name = "city_code")
    private String cityCode;
    @Column(name = "airport_name")
    private String airportName;
    private String city;
    private String country;
}
