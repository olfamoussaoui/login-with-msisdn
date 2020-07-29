package com.keyrus.recommender.repository;

import com.keyrus.recommender.domain.UserLogin;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the UserLogin entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserLoginRepository extends JpaRepository<UserLogin, Long> {

    @Query("select userLogin from UserLogin userLogin where userLogin.user.login = ?#{principal.username}")
    List<UserLogin> findByUserIsCurrentUser();
    
    Optional<UserLogin> findByMsisdn(String login);
}
