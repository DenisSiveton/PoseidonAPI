package com.nnk.springboot.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "CurvePoint")
public class CurvePoint {
    // TODO: Map columns in data table CURVEPOINT with corresponding java fields --> DONE
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    private int id;

    @Column (name = "curve_id")
    @NotNull(message = "The Curve Id must not be null")
    private Integer curveId;

    @Column (name = "as_Of_date")
    private String asOfDate;

    @Column (name = "term")
    private Double term;

    @Column (name = "value")
    private Double value;

    @Column (name = "creation_date")
    private String creationDate;

    public CurvePoint() {}

    public CurvePoint(Integer curveId, Double term, Double value) {
        this.curveId = curveId;
        this.term = term;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getCurveId() {
        return curveId;
    }

    public void setCurveId(Integer curveId) {
        this.curveId = curveId;
    }

    public String getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(String asOfDate) {
        this.asOfDate = asOfDate;
    }

    public Double getTerm() {
        return term;
    }

    public void setTerm(Double term) {
        this.term = term;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
