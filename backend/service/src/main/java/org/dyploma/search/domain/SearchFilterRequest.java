package org.dyploma.search.domain;

import lombok.Builder;
import lombok.Data;
import org.dyploma.search.criteria.CriteriaMode;
import org.dyploma.transport.TransportMode;

import java.sql.Date;
import java.util.List;

@Data
@Builder
public class SearchFilterRequest {
    private CriteriaMode optimizationCriteria;
    private List<TransportMode> transportModes;
    private List<String> tags;
    private Date fromDate;
    private Date toDate;
}
