package org.dyploma.search.domain;

import org.dyploma.algorithm.AlgorithmRequestCreator;
import org.dyploma.algorithm.algorithmGoogleAmadeus.AlgorithmGoogleAmadeusService;
import org.dyploma.algorithm.dto.AlgorithmRequest;
import org.dyploma.exception.ConflictException;
import org.dyploma.exception.NotFoundException;
import org.dyploma.search.place.PlaceInSearch;
import org.dyploma.search.validator.SearchValidator;
import org.dyploma.tag.search_tag.domain.SearchTag;
import org.dyploma.tag.search_tag.domain.SearchTagRepository;
import org.dyploma.transport.TransportMode;
import org.dyploma.trip.domain.Trip;
import org.dyploma.trip.place.PlaceInTrip;
import org.dyploma.trip.transfer.Transfer;
import org.dyploma.useraccount.UserAccount;
import org.dyploma.useraccount.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    private final UserAccountService userAccountService;
    private final SearchValidator searchValidator;

    private final AlgorithmRequestCreator algorithmRequestCreator;
    private final RestTemplate restTemplate;
    private final String processRouteEndpoint = "http://localhost:8000/process_route";

    @Value("${algorithm.option}")
    private String ALGORITHM_OPTION;

    @Autowired
    AlgorithmGoogleAmadeusService algorithmGoogleAmadeusService;

    @Autowired
    public SearchServiceImpl(SearchRepository searchRepository,
                             SearchValidator searchValidator,
                             SearchTagRepository searchTagRepository,
                             UserAccountService userAccountService,
                             AlgorithmRequestCreator algorithmRequestCreator,
                             RestTemplate restTemplate) {
        this.searchRepository = searchRepository;
        this.searchTagRepository = searchTagRepository;
        this.searchValidator = searchValidator;
        this.userAccountService = userAccountService;
        this.algorithmRequestCreator = algorithmRequestCreator;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Trip> search(SearchRequest search) {

        searchValidator.validateSearchRequest(search);

        if (ALGORITHM_OPTION.equals("GOOGLE")) {

            return algorithmGoogleAmadeusService.search(search);
        } else {
            AlgorithmRequest algorithmRequestObject = algorithmRequestCreator.createRequest(search);
            //Object algorithmResponse = restTemplate.postForObject(processRouteEndpoint, algorithmRequestObject, Object.class);

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
                                .transferOrder(j + 1)
                                .build();
                        transfers.add(transfer);
                    }
                    PlaceInTrip placeInTrip = generatePlaceInTrip(search.getPlacesToVisit().get(j));
                    places.add(placeInTrip);
                }
                Trip trip = Trip.builder()
                        .transfers(transfers)
                        .places(places)
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(1))
                        .passengerCount(1)
                        .totalCost(50)
                        .totalTransferTime(300)
                        .duration(160)
                        .build();
                trips.add(trip);
            }
            return trips;
        }
    }

    private PlaceInTrip generatePlaceInTrip(PlaceInSearch placeInSearch) {
        return PlaceInTrip.builder()
                .country(placeInSearch.getCountry())
                .city(placeInSearch.getCity())
                .isTransfer(false)
                .stayDuration(1)
                .visitOrder(placeInSearch.getEntryOrder())
                .build();
    }

    @Override
    public Search getUserSearchById(Integer userId, Integer searchId) {
        UserAccount userAccount = userAccountService.getUserById(userId);
        return searchRepository.findByIdAndUserAccount(searchId, userAccount).orElseThrow(() -> new NotFoundException("Search not found"));
    }

    @Override
    public Search getUserSearchByName(Integer userId, String name) {
        UserAccount userAccount = userAccountService.getUserById(userId);
        return searchRepository.findByNameAndUserAccount(name, userAccount).orElseThrow(() -> new NotFoundException("Search not found"));
    }

    @Override
    public Search createUserSearch(Integer userId, Search search, List<PlaceInSearch> places, List<String> tagNames) {
        places.forEach(search::addPlaceToVisit);
        searchValidator.validateSearch(search);
        UserAccount userAccount = userAccountService.getUserById(userId);
        if (searchRepository.existsByNameAndUserAccount(search.getName(), userAccount)) {
            throw new ConflictException("Search with this name already exists");
        }
        search.setUserAccount(userAccount);
        setTagsToSearch(userAccount, search, tagNames);
        search.setSaveDate(LocalDate.now());
        return searchRepository.save(search);
    }

    @Override
    public void deleteUserSearch(Integer userId, Integer searchId) {
        if (!searchRepository.existsByIdAndUserAccount(searchId, userAccountService.getUserById(userId))) {
            throw new NotFoundException("Search not found");
        }
        searchRepository.deleteById(searchId);
    }

    @Override
    public Search updateUserSearch(Integer userId, Integer searchId, String name, List<String> tags) {
        UserAccount userAccount = userAccountService.getUserById(userId);
        Search search = searchRepository.findByIdAndUserAccount(searchId, userAccount).orElseThrow(() -> new NotFoundException("Search not found"));
        if (!name.equals(search.getName()) && searchRepository.existsByNameAndUserAccount(name, userAccount)) {
            throw new ConflictException("Search with this name already exists");
        }
        search.setName(name);
        setTagsToSearch(userAccount, search, tags);
        return searchRepository.save(search);
    }

    @Override
    public Page<Search> getUserSearches(Integer userId, SearchFilterRequest filterRequest, Pageable pageable) {
        UserAccount userAccount = userAccountService.getUserById(userId);
        Specification<Search> spec = Specification.where(SearchSpecification.belongsToUserAccount(userAccount))
                .and(SearchSpecification.withOptimizationCriteria(filterRequest.getOptimizationCriteria()))
                .and(SearchSpecification.withTransportModes(filterRequest.getTransportModes()))
                .and(SearchSpecification.withTags(filterRequest.getTags()))
                .and(SearchSpecification.withDateRange(filterRequest.getFromDate(), filterRequest.getToDate()));
        return searchRepository.findAll(spec, pageable);
    }

    @Override
    public List<String> getUserSearchNames(Integer userId) {
        UserAccount userAccount = userAccountService.getUserById(userId);
        return searchRepository.findAllNamesByUserAccount(userAccount);
    }

    private void setTagsToSearch(UserAccount userAccount, Search search, List<String> tagNames) {
        List<SearchTag> existingTags = searchTagRepository.findByNameInAndUserAccount(tagNames, userAccount);

        Map<String, SearchTag> existingTagMap = existingTags.stream()
                .collect(Collectors.toMap(SearchTag::getName, tag -> tag));

        List<SearchTag> tagsToAssociate = new ArrayList<>(existingTags);
        List<SearchTag> newTags = tagNames.stream()
                .filter(tagName -> !existingTagMap.containsKey(tagName))
                .map(tagName -> SearchTag.builder().name(tagName).userAccount(userAccount).build())
                .collect(Collectors.toList());

        if (!newTags.isEmpty()) {
            searchTagRepository.saveAll(newTags);
            tagsToAssociate.addAll(newTags);
        }
        search.setTags(tagsToAssociate);
    }
}
