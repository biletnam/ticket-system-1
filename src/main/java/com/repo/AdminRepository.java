package com.repo;

import com.Admin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends CrudRepository<Admin, Long> {

    public Admin findFirstByOrderByIdDesc();
}
