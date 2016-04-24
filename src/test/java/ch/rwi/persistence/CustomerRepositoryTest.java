/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.rwi.persistence;


import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import ch.rwi.domain.Customer;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CustomerRepository repository;

    @Test
    public void shouldPersistCustomer() {
        em.persist(new Customer("Michael", "Miller"));

        Customer customer = this.repository.findByFirstName("Michael");

        assertThat(customer.getFirstName()).isEqualTo("Michael");
        assertThat(customer.getSurName()).isEqualTo("Miller");
    }

}
