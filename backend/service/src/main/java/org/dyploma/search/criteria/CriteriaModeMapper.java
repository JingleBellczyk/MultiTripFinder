package org.dyploma.search.criteria;

public class CriteriaModeMapper {
    public static CriteriaMode mapToCriteriaMode(com.openapi.model.CriteriaMode criteriaModeApi) {
        return switch (criteriaModeApi) {
            case PRICE -> CriteriaMode.PRICE;
            case DURATION -> CriteriaMode.DURATION;
        };
    }

    public static com.openapi.model.CriteriaMode mapToCriteriaModeApi(CriteriaMode criteriaMode) {
        return switch (criteriaMode) {
            case PRICE -> com.openapi.model.CriteriaMode.PRICE;
            case DURATION -> com.openapi.model.CriteriaMode.DURATION;
        };
    }
}
