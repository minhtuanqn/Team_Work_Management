package com.nli.probation.controller;

import com.nli.probation.model.ResponseModel;
import com.nli.probation.model.team.CreateTeamModel;
import com.nli.probation.model.team.TeamModel;
import com.nli.probation.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Validated
@RequestMapping("/teams")
public class TeamController {
    private TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * Create new team
     * @param createModel
     * @return response entity contains created model
     */
    @PostMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseModel> createTeam(@Valid @RequestBody CreateTeamModel createModel) {
        TeamModel savedModel = teamService.createTeam(createModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(savedModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }
}
