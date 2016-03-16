package com.worth.ifs.assessment.domain;

import com.worth.ifs.application.domain.Response;

//@Entity
public class ResponseAssessment {

//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @ManyToOne
//    @JoinColumn(name="response", referencedColumnName="id")
    private Response response;

    //example
    private Integer score;

    public ResponseAssessment() {
    	// no-arg constructor
    }

    public ResponseAssessment( Response response ) {
        this.response = response;
    }

    public Long getResponseId() {
        return response.getId();
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getScore() {
        return score;
    }


}
