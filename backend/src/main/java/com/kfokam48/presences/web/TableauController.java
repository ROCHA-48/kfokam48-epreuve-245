package com.kfokam48.presences.web;

import com.kfokam48.presences.service.TableauService;
import com.kfokam48.presences.web.dto.LigneTableauResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tableau")
public class TableauController {

    private final TableauService tableauService;

    public TableauController(TableauService tableauService) {
        this.tableauService = tableauService;
    }

    /** Operation imposee : GET /api/tableau?promotionId= -> 200, erreur 404. */
    @GetMapping
    public List<LigneTableauResponse> tableau(@RequestParam Long promotionId) {
        return tableauService.lignes(promotionId);
    }
}
