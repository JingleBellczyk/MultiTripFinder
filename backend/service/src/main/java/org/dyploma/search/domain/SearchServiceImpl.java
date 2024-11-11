package org.dyploma.search.domain;

import org.dyploma.exception.NotFoundException;
import org.dyploma.exception.ValidationException;
import org.dyploma.search.place.PlaceInSearch;
import org.dyploma.search.validator.SearchValidator;
import org.dyploma.tag.search_tag.domain.SearchTag;
import org.dyploma.tag.search_tag.domain.SearchTagRepository;
import org.dyploma.transport.TransportMode;
import org.dyploma.trip.domain.Trip;
import org.dyploma.trip.place.PlaceInTrip;
import org.dyploma.trip.transfer.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class SearchServiceImpl implements SearchService {
    private final SearchRepository searchRepository;
    private final SearchTagRepository searchTagRepository;
    private final SearchValidator searchValidator;

    @Autowired
    public SearchServiceImpl(SearchRepository searchRepository, SearchValidator searchValidator, SearchTagRepository searchTagRepository) {
        this.searchRepository = searchRepository;
        this.searchTagRepository = searchTagRepository;
        this.searchValidator = searchValidator;
    }

    @Override
    public List<Trip> search(SearchRequest search) {
        searchValidator.validateSearchRequest(search);
        List<Trip> trips = new ArrayList<>();
        int overallCities = search.getPlacesToVisit().size() + 2;
        for (int i = 0; i < 5; i++) {
            List<Transfer> transfers = new ArrayList<>();
            List<PlaceInTrip> places = new ArrayList<>();
            for (int j = 0; j < overallCities; j++) {
                if (j < overallCities - 1) {
                    Transfer transfer = Transfer.builder()
                            .transportMode(TransportMode.BUS)
                            .carrier("Carrier")
                            .startDateTime(LocalDateTime.now())
                            .endDateTime(LocalDateTime.now().plusHours(1))
                            .duration(60)
                            .cost(10)
                            .startAddress("Start address")
                            .endAddress("End address")
                            .transferOrder(j)
                            .build();
                    transfers.add(transfer);
                }
                PlaceInTrip placeInTrip;
                if (j == 0) {
                    placeInTrip = generatePlaceInTrip(search.getStartPlace());
                }
                else if (j == overallCities - 1) {
                    placeInTrip = generatePlaceInTrip(search.getEndPlace());
                }
                else {
                    placeInTrip = generatePlaceInTrip(search.getPlacesToVisit().get(j - 1));
                }
                places.add(placeInTrip);
            }
            Trip trip = Trip.builder()
                    .transfers(transfers)
                    .places(places)
                    .startDate(Date.valueOf(LocalDate.now()))
                    .endDate(Date.valueOf(LocalDate.now().plusDays(1)))
                    .passengerCount(1)
                    .totalCost(50)
                    .totalTransferTime(300)
                    .duration(160)
                    .build();
            trips.add(trip);
        }
        return trips;
    }

    private PlaceInTrip generatePlaceInTrip (PlaceInSearch placeInSearch) {
        return PlaceInTrip.builder()
                .country(placeInSearch.getCountry())
                .city(placeInSearch.getCity())
                .isTransfer(false)
                .stayDuration(1)
                .build();
    }

    @Override
    public Search getUserSearchById(Integer userId, Integer searchId) {
        return searchRepository.findById(searchId).orElseThrow(() -> new NotFoundException("Search not found"));
    }

    @Override
    public Search getUserSearchByName(Integer userId, String name) {
        Search search = searchRepository.findByNameWithPlaces(name)
                .orElseThrow(() -> new NotFoundException("Search not found"));
        searchRepository.findWithTags(List.of(search));
        return search;
    }

    @Override
    public Search createUserSearch(Integer userId, Search search, List<PlaceInSearch> places, List<String> tagNames) {
        searchValidator.validateSearch(search);
        for (PlaceInSearch placeInSearch : places) {
            search.addPlaceToVisit(placeInSearch);
        }
        setTagsToSearch(search, tagNames);
        search.setSaveDate(Date.valueOf(LocalDate.now()));
        return searchRepository.save(search);
    }

    @Override
    public void deleteUserSearch(Integer searchId) {
        if (!searchRepository.existsById(searchId)) {
            throw new NotFoundException("Search not found");
        }
        searchRepository.deleteById(searchId);
    }

    @Override
    public Search updateUserSearch(Integer userId, Integer searchId, String name, List<String> tags) {
        Search search = getUserSearchById(userId, searchId);
        if (!name.equals(search.getName()) && searchRepository.existsByName(name)) {
            throw new ValidationException("Search with this name already exists");
        }
        search.setName(name);
        setTagsToSearch(search, tags);
        return searchRepository.save(search);
    }

    @Override
    public Page<Search> getUserSearches(Integer userId, Pageable pageable) {
        return searchRepository.findAll(pageable);
    }

    private void setTagsToSearch(Search search, List<String> tagNames) {
        List<String> tagNamesProcessed = tagNames.stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(tag -> !tag.isBlank())
                .distinct()
                .toList();

        List<SearchTag> existingTags = searchTagRepository.findByNameIn(tagNames);

        Map<String, SearchTag> existingTagMap = existingTags.stream()
                .collect(Collectors.toMap(SearchTag::getName, tag -> tag));

        // Filter out tags that don't exist in the database
        List<SearchTag> tagsToAssociate = new ArrayList<>(existingTags);
        List<SearchTag> newTags = tagNamesProcessed.stream()
                .filter(tagName -> !existingTagMap.containsKey(tagName))
                .map(tagName -> SearchTag.builder().name(tagName).build())
                .collect(Collectors.toList());

        // Save new tags in bulk
        if (!newTags.isEmpty()) {
            searchTagRepository.saveAll(newTags);
            tagsToAssociate.addAll(newTags); // Add new tags to the final list
        }

        // Associate all tags with the search
        search.setTags(tagsToAssociate);
    }
}
