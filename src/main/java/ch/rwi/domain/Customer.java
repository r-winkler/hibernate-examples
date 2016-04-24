package ch.rwi.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Objects;

@Entity
public class Customer {

    @Id
    @GeneratedValue(generator = "CustomerSeq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "CustomerSeq", sequenceName = "CUSTOMER_SEQ")
    private Long id;

    private String firstName;

    private String surName;

    public Customer(){

    }

    public Customer(String firstName, String surName){
        this.firstName = firstName;
        this.surName = surName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        Customer that = (Customer) o;
        return Objects.equals(getFirstName(), that.getFirstName()) &&
                Objects.equals(getSurName(), that.getSurName());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getFirstName(), getSurName());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", getId())
                .append("firstName", getFirstName())
                .append("lastName", getSurName())
                .toString();
    }

}