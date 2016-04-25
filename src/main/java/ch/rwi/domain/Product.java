/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.rwi.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Product {

    @Id
    @GeneratedValue(generator = "ProductSeq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ProductSeq", sequenceName = "PRODUCT_SEQ")
    private Long id;

    private String name;

    public Product(){

    }

    public Product(String name){
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
