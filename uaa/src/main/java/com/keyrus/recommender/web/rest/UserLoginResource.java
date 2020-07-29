package com.keyrus.recommender.web.rest;

import com.keyrus.recommender.domain.UserLogin;
import com.keyrus.recommender.repository.UserLoginRepository;
import com.keyrus.recommender.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.keyrus.recommender.domain.UserLogin}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UserLoginResource {

    private final Logger log = LoggerFactory.getLogger(UserLoginResource.class);

    private static final String ENTITY_NAME = "userLogin";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserLoginRepository userLoginRepository;

    public UserLoginResource(UserLoginRepository userLoginRepository) {
        this.userLoginRepository = userLoginRepository;
    }

    /**
     * {@code POST  /user-logins} : Create a new userLogin.
     *
     * @param userLogin the userLogin to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userLogin, or with status {@code 400 (Bad Request)} if the userLogin has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-logins")
    public ResponseEntity<UserLogin> createUserLogin(@RequestBody UserLogin userLogin) throws URISyntaxException {
        log.debug("REST request to save UserLogin : {}", userLogin);
        if (userLogin.getId() != null) {
            throw new BadRequestAlertException("A new userLogin cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserLogin result = userLoginRepository.save(userLogin);
        return ResponseEntity.created(new URI("/api/user-logins/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-logins} : Updates an existing userLogin.
     *
     * @param userLogin the userLogin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userLogin,
     * or with status {@code 400 (Bad Request)} if the userLogin is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userLogin couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-logins")
    public ResponseEntity<UserLogin> updateUserLogin(@RequestBody UserLogin userLogin) throws URISyntaxException {
        log.debug("REST request to update UserLogin : {}", userLogin);
        if (userLogin.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        UserLogin result = userLoginRepository.save(userLogin);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userLogin.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /user-logins} : get all the userLogins.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userLogins in body.
     */
    @GetMapping("/user-logins")
    public List<UserLogin> getAllUserLogins() {
        log.debug("REST request to get all UserLogins");
        return userLoginRepository.findAll();
    }

    /**
     * {@code GET  /user-logins/:id} : get the "id" userLogin.
     *
     * @param id the id of the userLogin to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userLogin, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-logins/{id}")
    public ResponseEntity<UserLogin> getUserLogin(@PathVariable Long id) {
        log.debug("REST request to get UserLogin : {}", id);
        Optional<UserLogin> userLogin = userLoginRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(userLogin);
    }

    /**
     * {@code DELETE  /user-logins/:id} : delete the "id" userLogin.
     *
     * @param id the id of the userLogin to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-logins/{id}")
    public ResponseEntity<Void> deleteUserLogin(@PathVariable Long id) {
        log.debug("REST request to delete UserLogin : {}", id);
        userLoginRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
