package dev.harshit.bookmyshow.repositories;

import dev.harshit.bookmyshow.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public Optional<User> findById(Long userId);

    Optional<User> findByEmail(String email);
}
