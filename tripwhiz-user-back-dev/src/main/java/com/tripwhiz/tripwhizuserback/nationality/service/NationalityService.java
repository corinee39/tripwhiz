package com.tripwhiz.tripwhizuserback.nationality.service;

import com.tripwhiz.tripwhizuserback.nationality.dto.NationalityDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class NationalityService {

    private final RestTemplate restTemplate;

    public NationalityService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<NationalityDTO> getAllNationalities() {
        String url = "http://http://localhost:8082/api/nationality";
        NationalityDTO[] response = restTemplate.getForObject(url, NationalityDTO[].class);
        return Arrays.asList(response);
    }

    public NationalityDTO getNationalityById(Long id) {
        String url = "http://localhost:8082/api/nationality/" + id;
        return restTemplate.getForObject(url, NationalityDTO.class);
    }
}
