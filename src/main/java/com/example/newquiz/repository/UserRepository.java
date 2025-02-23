package com.example.newquiz.repository;

import com.example.newquiz.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNickName(String nickname);
    Optional<User> findByProviderId(String providerId);
    Boolean existsByNickName(String nickname);
    Boolean existsByProviderId(String providerId);
}
