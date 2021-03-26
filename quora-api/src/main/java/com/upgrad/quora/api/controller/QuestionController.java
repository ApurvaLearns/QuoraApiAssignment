package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    QuestionService  questionService;

    //Post Method to create a question , once the access token is verified
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String authorization, final QuestionRequest questionRequest) throws AuthorizationFailedException {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());
        final QuestionEntity createdQuestionEntity=questionService.createQuestion(questionEntity,authorization);

        QuestionResponse questionResponse= new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }


    //Get Method to get  all question ,once the access token is verified

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
      ArrayList<QuestionEntity> list1= questionService.getDetails(authorization);

      ArrayList<QuestionDetailsResponse> list2 = new ArrayList<QuestionDetailsResponse>();

      for(QuestionEntity q1 : list1)
      {
          list2.add(new QuestionDetailsResponse().id(q1.getUuid()).content(q1.getContent()));
      }

        return new ResponseEntity<ArrayList<QuestionDetailsResponse>>(list2, HttpStatus.OK);

    }

    //Put method to edit a question once the access token is verified. Only the owner of question can edit an answer
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization, final QuestionEditRequest questionEditRequest) throws AuthorizationFailedException, InvalidQuestionException {
        String content= questionEditRequest.getContent();
        QuestionEntity questionEntity =questionService.editQuestion(questionId,authorization,content);
        QuestionEditResponse questionEditResponse= new QuestionEditResponse().id(questionEntity.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

    //Delete Request to delete a question , once the access token is verified .Only owner and admin can delete a question
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        questionService.deleteQuestion(questionId,authorization);
        QuestionDeleteResponse questionDeleteResponse= new QuestionDeleteResponse().id(questionId).status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

   // Get Request to get all questions posted by a user, once access token is verified.
    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable("userId") final String userId,@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        System.out.println(userId);
        System.out.println(authorization);
        ArrayList<QuestionEntity> list1= questionService.getAllQuestionsByUser(userId,authorization);
        ArrayList<QuestionDetailsResponse> list2 = new ArrayList<QuestionDetailsResponse>();
        for(QuestionEntity q1 : list1)
        {
            list2.add(new QuestionDetailsResponse().id(q1.getUuid()).content(q1.getContent()));
        }

        return new ResponseEntity<ArrayList<QuestionDetailsResponse>>(list2, HttpStatus.OK);
    }

}
