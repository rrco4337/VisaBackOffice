package com.example.mvcjsp.controller;

import com.example.mvcjsp.dto.EnregistrementDemandeForm;
import com.example.mvcjsp.dto.ValidationResult;
import com.example.mvcjsp.model.enums.DemandeTypeCode;
import com.example.mvcjsp.model.enums.ProfilTypeCode;
import com.example.mvcjsp.service.EnregistrementDemandeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {

    private final EnregistrementDemandeService enregistrementDemandeService;

    public UserController(EnregistrementDemandeService enregistrementDemandeService){
        this.enregistrementDemandeService = enregistrementDemandeService;
    }

    @GetMapping("/")
    public String home(Model model){
        if (!model.containsAttribute("form")) {
            EnregistrementDemandeForm form = new EnregistrementDemandeForm();
            form.setTypeDemande(DemandeTypeCode.NOUVEAU_TITRE);
            form.setTypeProfil(ProfilTypeCode.ETUDIANT);
            model.addAttribute("form", form);
        }

        EnregistrementDemandeForm currentForm = (EnregistrementDemandeForm) model.getAttribute("form");
        if (currentForm == null) {
            currentForm = new EnregistrementDemandeForm();
            currentForm.setTypeDemande(DemandeTypeCode.NOUVEAU_TITRE);
            currentForm.setTypeProfil(ProfilTypeCode.ETUDIANT);
            model.addAttribute("form", currentForm);
        }
        model.addAttribute("typesDemande", DemandeTypeCode.values());
        model.addAttribute("typesProfil", ProfilTypeCode.values());
        model.addAttribute("pieces", enregistrementDemandeService.piecesPour(currentForm.getTypeDemande(), currentForm.getTypeProfil()));
        model.addAttribute("demandesRecentes", enregistrementDemandeService.recentes());

        if (!model.containsAttribute("errors")) {
            model.addAttribute("errors", List.of());
        }

        return "index";
    }

    @PostMapping("/demandes/enregistrer")
    public String enregistrer(@ModelAttribute("form") EnregistrementDemandeForm form, RedirectAttributes redirectAttributes){
        ValidationResult result = enregistrementDemandeService.validate(form);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result.getErrors());
            redirectAttributes.addFlashAttribute("form", form);
            return "redirect:/";
        }

        enregistrementDemandeService.enregistrer(form);
        redirectAttributes.addFlashAttribute("successMessage", "Demande enregistree avec statut CREER.");
        return "redirect:/";
    }
}
