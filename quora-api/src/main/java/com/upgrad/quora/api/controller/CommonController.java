package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private AuthenticationService authenticationService;

    //Get method  for fetching the details of a user based on given userId once the access  token is verified
    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUserDetails(@PathVariable("userId") final String userUuid, @RequestHeader("authorization") final String authorization) throws AuthenticationFailedException, UserNotFoundException {
        UserEntity user = authenticationService.getUser(userUuid,authorization);
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().firstName(user.getFirstName()).lastName(user.getLastName()).userName(user.getUserName()).
                emailAddress(user.getEmail()).aboutMe(user.getAboutme()).country(user.getCountry()).dob(user.getDob()).contactNumber(user.getContactnumber());


        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);

    }


}
