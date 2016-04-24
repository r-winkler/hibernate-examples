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

    /**
     * Straightforward persisting of one entity.
     */
    @Test
    public void persistOneCustomer() {
        this.em.persist(new Customer("Michael", "Miller"));

        Customer customer = this.repository.findByFirstName("Michael");

        assertThat(customer.getFirstName()).isEqualTo("Michael");
        assertThat(customer.getSurName()).isEqualTo("Miller");
    }

    /**
     *  If the id in an entity is set (either manually or by the sequence generation strategy ), then hibernate has to perform an additional
     *  select-statement when calling merge. Only this way, hibernate can decide if it is a new entity or an already existing one. Depending on that,
     *  hibernate performs an insert- or an update-statement.
     */
    @Test
    public void mergeOneCustomer() {
        Customer customer = new Customer("Michael", "Miller");
        customer.setId(1L);
        this.em.merge(customer);

        Customer actual = this.repository.findByFirstName("Michael");

        assertThat(actual.getFirstName()).isEqualTo("Michael");
        assertThat(actual.getSurName()).isEqualTo("Miller");
    }

    /**
     * Batch-inserts are memory-critical as the persistence context (1st level cache) will hold all these entities in merory as long as flush is not
     * called. Calling flush and clear after some inserts will alleviate this issue.
     */
    @Test
    public void persistManyCustomersInBatch() {
        for (int i = 0; i < 1000; i++) {
            Customer customer = new Customer("Firstname" + i, "Surname" + i);
            this.em.persist(customer);
            if ( i % 20 == 0 ) { //20, same as the JDBC batch size
                //flush a batch of inserts and release memory:
                this.em.flush();
                this.em.clear();
            }
        }
        List<Customer> customers = this.repository.findAll();

        assertThat(customers).hasSize(1000);
    }

}
