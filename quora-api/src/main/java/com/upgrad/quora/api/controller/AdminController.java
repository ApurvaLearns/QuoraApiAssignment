package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    private AuthenticationService authenticationService;

    //The controller method is called when  the Request type is Delete. A user needs to be deleted ,once a valid authorization token is provided

    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> deleteUserDetails(@PathVariable("userId") final String userUuid, @RequestHeader("authorization") final String authorization) throws AuthenticationFailedException, UserNotFoundException {

        //System.out.println("Hello1");
        //System.out.println(userUuid);
       // System.out.println(authorization);
        authenticationService.deleteUser(userUuid,authorization);
        UserDeleteResponse userDeleteResponse= new UserDeleteResponse().id(userUuid).status("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);

    }
}
