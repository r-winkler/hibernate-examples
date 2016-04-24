package ch.rwi.persistence;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import ch.rwi.domain.Customer;

import java.util.List;


@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CustomerRepository repository;

    @Test
    public void persistOneCustomer() {
        this.em.persist(new Customer("Michael", "Miller"));

        Customer customer = this.repository.findByFirstName("Michael");

        assertThat(customer.getFirstName()).isEqualTo("Michael");
        assertThat(customer.getSurName()).isEqualTo("Miller");
    }

    @Test
    public void persistManyCustomersInBatch() {
        for (int i = 0; i < 10000; i++) {
            Customer customer = new Customer("Firstname" + i, "Surname" + i);
            this.em.persist(customer);
            if ( i % 20 == 0 ) { //20, same as the JDBC batch size
                //flush a batch of inserts and release memory:
                this.em.flush();
                this.em.clear();
            }
        }
        List<Customer> customers = this.repository.findAll();

        assertThat(customers).hasSize(10000);
    }

}
