package com.nli.probation.controller;

import com.nli.probation.model.ResponseModel;
import com.nli.probation.model.team.CreateTeamModel;
import com.nli.probation.model.team.TeamModel;
import com.nli.probation.model.useraccount.CreateUserAccountModel;
import com.nli.probation.model.useraccount.UserAccountModel;
import com.nli.probation.service.UserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Validated
@RequestMapping("/user-accounts")
public class UserAccountController {
    private UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    /**
     * Create new user account
     * @param createModel
     * @return created user account
     */
    @PostMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseModel> createUserAccount(@Valid @RequestBody CreateUserAccountModel createModel) {
        UserAccountModel savedModel = userAccountService.createUserAccount(createModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(savedModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Find user account by id
     * @param id
     * @return response entity contains model
     */
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseModel> findUserAccountById(@PathVariable int id) {
        UserAccountModel foundUserAccount = userAccountService.findUserAccountById(id);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(foundUserAccount)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }
}
