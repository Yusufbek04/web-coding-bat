package org.example.condigbat.repository;

import org.example.condigbat.entity.Case;
import org.example.condigbat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {


    Optional<User> getUserByEmail(String email);

    boolean existsById(Integer id);


}
