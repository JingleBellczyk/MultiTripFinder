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
import org.springframework.data.jpa.domain.Specification;
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
        for (int i = 0; i < 5; i++) {
            List<Transfer> transfers = new ArrayList<>();
            List<PlaceInTrip> places = new ArrayList<>();
            for (int j = 0; j < search.getPlacesToVisit().size(); j++) {
                if (j < search.getPlacesToVisit().size() - 1) {
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
                PlaceInTrip placeInTrip = generatePlaceInTrip(search.getPlacesToVisit().get(j));
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
        return searchRepository.findByName(name).orElseThrow(() -> new NotFoundException("Search not found"));
    }

    @Override
    public Search createUserSearch(Integer userId, Search search, List<PlaceInSearch> places, List<String> tagNames) {
        searchValidator.validateSearch(search);
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
    public Page<Search> getUserSearches(Integer userId, SearchFilterRequest filterRequest, Pageable pageable) {
        Specification<Search> spec = Specification.where(SearchSpecification.withOptimizationCriteria(filterRequest.getOptimizationCriteria()))
                .and(SearchSpecification.withTransportModes(filterRequest.getTransportModes()))
                .and(SearchSpecification.withTags(filterRequest.getTags()))
                .and(SearchSpecification.withSaveDate(filterRequest.getSaveDate()));
        return searchRepository.findAll(spec, pageable);
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

        List<SearchTag> tagsToAssociate = new ArrayList<>(existingTags);
        List<SearchTag> newTags = tagNamesProcessed.stream()
                .filter(tagName -> !existingTagMap.containsKey(tagName))
                .map(tagName -> SearchTag.builder().name(tagName).build())
                .collect(Collectors.toList());

        if (!newTags.isEmpty()) {
            searchTagRepository.saveAll(newTags);
            tagsToAssociate.addAll(newTags);
        }

        search.setTags(tagsToAssociate);
    }
}
