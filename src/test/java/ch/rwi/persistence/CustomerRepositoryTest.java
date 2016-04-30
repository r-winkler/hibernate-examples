package ch.rwi.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import ch.rwi.domain.Product;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import ch.rwi.domain.Customer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CustomerRepository repository;

    private long start;

    @Before
    public void startTime() {
        start = System.currentTimeMillis();
    }

    @After
    public void stopTime() throws SQLException {
        System.out.println("Time = " + (System.currentTimeMillis() - start));
    }

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
     * If the id in an entity is set (either manually or by the sequence generation strategy ), then hibernate has to perform an additional
     * select-statement when calling merge. Only this way, hibernate can decide if it is a new entity or an already existing one. Depending on that,
     * hibernate performs an insert- or an update-statement.
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
    public void batchInsertWithHibernate() {
        for (int i = 0; i < 100; i++) {
            Customer customer = new Customer("Firstname" + i, "Surname" + i);
            this.em.persist(customer);
            if (i % 50 == 0) { // 50, same as the JDBC batch size
                // flush a batch of inserts and release memory:
                this.em.flush();
                this.em.clear();
            }
        }
        List<Customer> customers = this.repository.findAll();

        assertThat(customers).hasSize(100);
    }

    @Test
    public void batchInsertWithJdbc() throws SQLException {
        String DB_CONNECTION = "jdbc:h2:mem:testdb";
        String DB_USER = "sa";
        String DB_PASSWORD = "";
        String insertCustomerSQL = "INSERT INTO CUSTOMER"
                + "(ID, FIRST_NAME, SUR_NAME) VALUES"
                + "(CUSTOMER_SEQ.NEXTVAL,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
                PreparedStatement preparedStatement = conn.prepareStatement(insertCustomerSQL)) {
            for (int i = 0; i < 1000000; i++) {
                preparedStatement.setString(1, "Firstname" + i);
                preparedStatement.setString(2, "Surname" + i);
                preparedStatement.executeUpdate();
            }
        }

        List<Customer> customers = this.repository.findAll();
        assertThat(customers).hasSize(1000000);

    }

    @Test
    public void batchInsertWithHibernateWithParentAndChild() {
        for (int i = 0; i < 1000; i++) {
            Customer customer = new Customer("Firstname" + i, "Surname" + i);
            List<Product> products = new ArrayList<>();
            for (int j = 0; j < 500; j++) {
                Product product = new Product("Product" + j);
                products.add(product);
            }
            customer.setProducts(products);
            this.em.persist(customer);
            if (i % 20 == 0) { // 20, same as the JDBC batch size
                // flush a batch of inserts and release memory:
                this.em.flush();
                this.em.clear();
            }
        }

        List<Customer> customers = this.repository.findAll();
        assertThat(customers).hasSize(1000);
        for (Customer customer : customers) {
            assertThat(customer.getProducts()).hasSize(500);
        }
    }

    @Test
    public void batchInsertWithJdbcWithParentAndChild() throws SQLException {
        String DB_CONNECTION = "jdbc:h2:mem:testdb";
        String DB_USER = "sa";
        String DB_PASSWORD = "";
        String insertCustomerSQL = "INSERT INTO CUSTOMER"
                + "(ID, FIRST_NAME, SUR_NAME) VALUES"
                + "(CUSTOMER_SEQ.NEXTVAL,?,?)";
        String insertProductSQL = "INSERT INTO PRODUCT"
                + "(ID, CUSTOMER_ID, NAME) VALUES"
                + "(PRODUCT_SEQ.NEXTVAL,CUSTOMER_SEQ.CURRVAL,?)";
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
                PreparedStatement pstCustomer = conn.prepareStatement(insertCustomerSQL);
                PreparedStatement pstProduct = conn.prepareStatement(insertProductSQL)) {
            for (int i = 0; i < 1000; i++) {
                pstCustomer.setString(1, "Firstname" + i);
                pstCustomer.setString(2, "Surname" + i);
                pstCustomer.executeUpdate();
                for (int j = 0; j < 500; j++) {
                    pstProduct.setString(1, "Name" + j);
                    pstProduct.executeUpdate();
                }
            }
        }

        List<Customer> customers = this.repository.findAll();
        assertThat(customers).hasSize(1000);
        for (Customer customer : customers) {
            assertThat(customer.getProducts()).hasSize(500);
        }

    }
}
