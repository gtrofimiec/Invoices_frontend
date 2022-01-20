package com.myprojects.invoices_frontend.clients;

import com.myprojects.invoices_frontend.apis.ceidgapi.CeidgApiClient;
import com.myprojects.invoices_frontend.config.CustomersConfig;
import com.myprojects.invoices_frontend.domain.Customers;
import com.myprojects.invoices_frontend.domain.dtos.CustomersDto;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomersClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(CeidgApiClient.class);

    private final RestTemplate restTemplate;
    private final CustomersConfig customersConfig;

    public CustomersClient(RestTemplate restTemplate, CustomersConfig customersConfig) {
        this.restTemplate = restTemplate;
        this.customersConfig = customersConfig;
    }

    public List<CustomersDto> getCustomers() {
        URI url = UriComponentsBuilder.fromHttpUrl(customersConfig.getCustomersEndpoint())
                .build()
                .encode()
                .toUri();
        try {
            CustomersDto[] customersEntity = restTemplate.getForObject(url, CustomersDto[].class);
            if(customersEntity != null) {
                LOGGER.info("Customers database was sucessfully loaded");
                return Arrays.stream(customersEntity)
                        .collect(Collectors.toList());
            } else {
                LOGGER.warn("Customers database could not be retrieved or it is empty");
                return new ArrayList<>();
            }

        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public void saveCustomer(CustomersDto customerDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        URI url = UriComponentsBuilder.fromHttpUrl(customersConfig.getCustomersEndpoint())
                .build()
                .encode()
                .toUri();
        try {
            HttpEntity<CustomersDto> request = new HttpEntity<>(customerDto, headers);
            Customers sentData = restTemplate.postForObject(url, request, Customers.class);
            if(sentData != null) {
                LOGGER.info("Customer " + sentData.getFullName() + " has been correctly sent");
            }
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void updateCustomer(@NotNull CustomersDto customerDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        URI url = UriComponentsBuilder.fromHttpUrl(customersConfig.getCustomersEndpoint())
                .path("/" + customerDto.getId())
                .build()
                .encode()
                .toUri();
        try {
            HttpEntity<CustomersDto> request = new HttpEntity<>(customerDto, headers);
            restTemplate.exchange(url, HttpMethod.PUT, request, CustomersDto.class);
            LOGGER.info("Customer " + customerDto.getFullName() + " has been updated");
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void deleteCustomer(@NotNull CustomersDto customerDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(customersConfig.getCustomersEndpoint())
                .path("/" + customerDto.getId())
                .build()
                .encode()
                .toUri();
        try {
            restTemplate.delete(url);
            LOGGER.info("Customer " + customerDto.getFullName() + "has been deleted");
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}