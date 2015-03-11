package de.codecentric.spa.test.entities;

import de.codecentric.spa.annotations.Entity;
import de.codecentric.spa.annotations.Id;

@Entity
public class DummyAttribute {

    @Id
    public Long id;

    public String dummyName;
    public String dummyValue;

}
