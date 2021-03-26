package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    AnswerService answerService;

    //The controller method is called for a Post Type Request.An answer needs to be created for the question id passed in the argument
    @RequestMapping (method = RequestMethod.POST, path = "/question/{questionId}/answer/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@PathVariable("questionId") final String questionId,@RequestHeader("authorization") final String authorization, final AnswerRequest answerRequest) throws AuthorizationFailedException, InvalidQuestionException {

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setAns(answerRequest.getAnswer());

        AnswerEntity createdAnswer = answerService.createAnswer(questionId,authorization,answerEntity);
        AnswerResponse answerResponse= new AnswerResponse().id(createdAnswer.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.OK);


    }

    //Put Request for updating an answer. Based on the answerId  provided , answer content would be updated with this controller method.Only owner can edit an answer
    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(@PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String authorization, final AnswerEditRequest answerEditRequest) throws AuthorizationFailedException, AnswerNotFoundException {
        String content= answerEditRequest.getContent();
        AnswerEntity answerEntity=answerService.editAnswer(answerId,authorization,content);
        AnswerEditResponse answerEditResponse= new AnswerEditResponse().id(answerId).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    //Delete Request for deleting an answer. Answer would be deleted once access token and other validations are performed. Only owner
    //and admin can delete an answer
    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {

        answerService.deleteAnswer(answerId,authorization);
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answerId).status("ANSWER DELETED");

        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }


    //Get Request for fetching all answers to a particular question Id passed in the request
    @RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList<AnswerDetailsResponse>> getAllAnswers(@PathVariable("questionId") final String questionId,@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        ArrayList<AnswerEntity> list1= answerService.getAnswers(questionId,authorization);

        ArrayList<AnswerDetailsResponse> list2 = new ArrayList<AnswerDetailsResponse>();

        for(AnswerEntity a1 : list1)
        {
            list2.add(new AnswerDetailsResponse().id(a1.getUuid()).answerContent(a1.getAns()).questionContent(a1.getQuestion().getContent()));
        }

        return new ResponseEntity<ArrayList<AnswerDetailsResponse>>(list2, HttpStatus.OK);

    }

}
