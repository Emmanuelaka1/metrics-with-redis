
package com.test.projet.customer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository repo;

    public CustomerService(CustomerRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Customer insertCustomer(String name, String email) {
        return repo.save(new Customer(name, email));
    }

    @Transactional
    public Customer updateCustomer(Long id, String name, String email) {
        Customer c = repo.findById(id).orElseThrow();
        c.setName(name);
        c.setEmail(email);
        return repo.save(c);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        repo.deleteById(id);
    }
}
