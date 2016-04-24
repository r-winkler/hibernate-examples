package ch.rwi.persistence;

import ch.rwi.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByFirstName(String firstName);

}
