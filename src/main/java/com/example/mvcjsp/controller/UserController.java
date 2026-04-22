package com.example.mvcjsp.controller;

import com.example.mvcjsp.dto.EnregistrementDemandeForm;
import com.example.mvcjsp.dto.ValidationResult;
import com.example.mvcjsp.model.enums.DemandeTypeCode;
import com.example.mvcjsp.model.enums.ProfilTypeCode;
import com.example.mvcjsp.repository.NationaliteRepository;
import com.example.mvcjsp.repository.SituationFamilialeRepository;
import com.example.mvcjsp.service.EnregistrementDemandeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.mvcjsp.model.Demande;

import java.util.List;

@Controller
public class UserController {

    private final EnregistrementDemandeService enregistrementDemandeService;
    private final NationaliteRepository nationaliteRepository;
    private final SituationFamilialeRepository situationFamilialeRepository;

    public UserController(
            EnregistrementDemandeService enregistrementDemandeService,
            NationaliteRepository nationaliteRepository,
            SituationFamilialeRepository situationFamilialeRepository
    ){
        this.enregistrementDemandeService = enregistrementDemandeService;
        this.nationaliteRepository = nationaliteRepository;
        this.situationFamilialeRepository = situationFamilialeRepository;
    }

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("demandesRecentes", enregistrementDemandeService.recentes());
        return "index";
    }

    @GetMapping("/demande")
    public String preparationDemande(@RequestParam(required = false) String typeDemande,
                                     @RequestParam(required = false) String typeProfil,
                                     @RequestParam(required = false) Long personneId,
                                     Model model) {
        if (!model.containsAttribute("form")) {
            EnregistrementDemandeForm form = new EnregistrementDemandeForm();
            form.setTypeDemande(typeDemande != null ? DemandeTypeCode.valueOf(typeDemande) : DemandeTypeCode.NOUVEAU_TITRE);
            form.setTypeProfil(typeProfil != null ? ProfilTypeCode.valueOf(typeProfil) : ProfilTypeCode.ETUDIANT);
            form.setPersonneId(personneId);
            model.addAttribute("form", form);
        }

        EnregistrementDemandeForm currentForm = (EnregistrementDemandeForm) model.getAttribute("form");
        if (currentForm == null) {
            currentForm = new EnregistrementDemandeForm();
            currentForm.setTypeDemande(typeDemande != null ? DemandeTypeCode.valueOf(typeDemande) : DemandeTypeCode.NOUVEAU_TITRE);
            currentForm.setTypeProfil(typeProfil != null ? ProfilTypeCode.valueOf(typeProfil) : ProfilTypeCode.ETUDIANT);
            currentForm.setPersonneId(personneId);
            model.addAttribute("form", currentForm);
        }
        model.addAttribute("typesDemande", DemandeTypeCode.values());
        model.addAttribute("typesProfil", ProfilTypeCode.values());
        model.addAttribute("pieces", enregistrementDemandeService.piecesPour(currentForm.getTypeDemande(), currentForm.getTypeProfil()));
        model.addAttribute("nationalites", nationaliteRepository.findAll());
        model.addAttribute("situationsFamiliales", situationFamilialeRepository.findAll());

        if (!model.containsAttribute("errors")) {
            model.addAttribute("errors", List.of());
        }

        return "demande";
    }

    @GetMapping("/demande/search")
    public String searchDemande(@RequestParam("numeroPasseport") String numeroPasseport, RedirectAttributes redirectAttributes) {
        EnregistrementDemandeForm form = enregistrementDemandeService.preparerFormulaireDepuisPasseport(numeroPasseport);
        if (form.getNom() == null) {
            redirectAttributes.addFlashAttribute("errors", List.of("Aucun dossier trouvé pour le passeport : " + numeroPasseport));
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Données pré-remplies pour le passeport : " + numeroPasseport);
            redirectAttributes.addFlashAttribute("form", form);
        }
        return "redirect:/demande";
    }

    @PostMapping("/demandes/enregistrer")
    public String enregistrer(@ModelAttribute("form") EnregistrementDemandeForm form, RedirectAttributes redirectAttributes){
        ValidationResult result = enregistrementDemandeService.validate(form);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result.getErrors());
            redirectAttributes.addFlashAttribute("form", form);
            return "redirect:/demande";
        }

        Demande demande = enregistrementDemandeService.enregistrer(form);
        redirectAttributes.addFlashAttribute("successMessage", "Demande enregistree avec statut " + demande.getStatut() + ".");
        return "redirect:/";
    }
}
