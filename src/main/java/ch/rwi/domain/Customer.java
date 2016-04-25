package ch.rwi.domain;

import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.sun.istack.internal.NotNull;

@Entity
public class Customer {

    @Id
    @GeneratedValue(generator = "CustomerSeq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "CustomerSeq", sequenceName = "CUSTOMER_SEQ")
    private Long id;

    @NotNull
    private String firstName;

    @NotNull
    private String surName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    List<Product> products;

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

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
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
