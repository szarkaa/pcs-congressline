package hu.congressline.pcs.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import hu.congressline.pcs.config.ApplicationProperties;
import hu.congressline.pcs.service.dto.publiccompanydata.DetailResponse;
import hu.congressline.pcs.service.dto.publiccompanydata.SearchResponse;

@Service
public class PublicCompanyDataService {
    private static final String SEARCH_URI = "/search";
    private static final String X_API_KEY = "X-Api-Key";

    private final ApplicationProperties properties;
    private final RestClient restClient;

    public PublicCompanyDataService(ApplicationProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder().defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    @SuppressWarnings("MissingJavadocMethod")
    public SearchResponse searchByName(String name) {
        String uri = UriComponentsBuilder
            .fromUriString(properties.getPublicCompanyData().getBaseUrl() + SEARCH_URI)
            .queryParam("name", name)
            //.queryParam("limit", 0-100) default 50 -- search result number
            //.queryParam("sensitivity", 0-4) default 0 -- search sensitivity 0 with typo error - 4 just exact matches
            //.queryParam("businessType", "all") //default company -- all, company, individualEntrepreneur, publicSector, nonProfit, otherOrganization
            .toUriString();

        return restClient.get()
            .uri(uri)
            .header(X_API_KEY, properties.getPublicCompanyData().getApiKey())
            .retrieve()
            .body(SearchResponse.class);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public SearchResponse searchByVatNumber(String vatNumber) {
        String uri = UriComponentsBuilder
            .fromUriString(properties.getPublicCompanyData().getBaseUrl() + SEARCH_URI)
            .queryParam("vatNumber", vatNumber)
            .toUriString();

        return restClient.get()
            .uri(uri)
            .header(X_API_KEY, properties.getPublicCompanyData().getApiKey())
            .retrieve()
            .body(SearchResponse.class);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public DetailResponse getCompanyDetail(String id) {
        String uri = UriComponentsBuilder
            .fromUriString(properties.getPublicCompanyData().getBaseUrl() + "/detail")
            .queryParam("id", id)
            .toUriString();

        return restClient.get()
            .uri(uri)
            .header(X_API_KEY, properties.getPublicCompanyData().getApiKey())
            .retrieve()
            .body(DetailResponse.class);
    }
}
