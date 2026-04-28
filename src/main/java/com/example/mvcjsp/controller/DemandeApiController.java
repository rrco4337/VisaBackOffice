package com.example.mvcjsp.controller;

import com.example.mvcjsp.dto.DemandeStatusResponse;
import com.example.mvcjsp.model.Demande;
import com.example.mvcjsp.repository.DemandeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/demandes")
public class DemandeApiController {
    private final DemandeRepository demandeRepository;

    public DemandeApiController(DemandeRepository demandeRepository) {
        this.demandeRepository = demandeRepository;
    }

    @GetMapping
    public List<DemandeStatusResponse> searchByPassport(@RequestParam("passportNumber") String passportNumber) {
        if (passportNumber == null || passportNumber.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "passportNumber is required");
        }

        return demandeRepository.findByPasseportNumeroOrderByDateDemandeDesc(passportNumber)
                .stream()
                .map(DemandeStatusResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public DemandeStatusResponse getById(@PathVariable Long id) {
        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable"));
        return DemandeStatusResponse.from(demande);
    }
}
