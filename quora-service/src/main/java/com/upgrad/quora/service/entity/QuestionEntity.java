package com.upgrad.quora.service.entity;


import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
//Mapped to table question in DB
@Entity
@Table(name = "question", schema = "public")

//Different named Queries for fetching the data
@NamedQueries({
        @NamedQuery(name = "getAllquestions" , query = "select q from QuestionEntity q"),
        @NamedQuery(name = "getQuestion" , query = "select q from QuestionEntity q where q.uuid = :uuid"),
        @NamedQuery(name = "getQuestionByUser" , query = "select q from QuestionEntity q where q.user = :user")
})

public class QuestionEntity implements Serializable {

    ///@Id annotation specifies that the corresponding attribute is a primary key
    //@Column annotation specifies that the attribute will be mapped to the column in the DB
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @Size(max = 200)
    private String uuid;

    @Column(name = "CONTENT")
    @NotNull
    @Size(max = 500)
    private String content;

    @Column(name = "DATE")
    @NotNull
    private ZonedDateTime date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }


    //The 'Question' table is mapped to 'User' table with Many:One mapping
    //One Question can belong to  only one user but one user can have multiple  question
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "USER_ID")
    private UserEntity user;
}
