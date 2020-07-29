package com.keyrus.recommender.web.rest;

import com.keyrus.recommender.UaaApp;
import com.keyrus.recommender.config.SecurityBeanOverrideConfiguration;
import com.keyrus.recommender.domain.UserLogin;
import com.keyrus.recommender.repository.UserLoginRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link UserLoginResource} REST controller.
 */
@SpringBootTest(classes = UaaApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class UserLoginResourceIT {

    private static final String DEFAULT_MSISDN = "AAAAAAAAAA";
    private static final String UPDATED_MSISDN = "BBBBBBBBBB";

    @Autowired
    private UserLoginRepository userLoginRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserLoginMockMvc;

    private UserLogin userLogin;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserLogin createEntity(EntityManager em) {
        UserLogin userLogin = new UserLogin()
            .msisdn(DEFAULT_MSISDN);
        return userLogin;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserLogin createUpdatedEntity(EntityManager em) {
        UserLogin userLogin = new UserLogin()
            .msisdn(UPDATED_MSISDN);
        return userLogin;
    }

    @BeforeEach
    public void initTest() {
        userLogin = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserLogin() throws Exception {
        int databaseSizeBeforeCreate = userLoginRepository.findAll().size();
        // Create the UserLogin
        restUserLoginMockMvc.perform(post("/api/user-logins").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userLogin)))
            .andExpect(status().isCreated());

        // Validate the UserLogin in the database
        List<UserLogin> userLoginList = userLoginRepository.findAll();
        assertThat(userLoginList).hasSize(databaseSizeBeforeCreate + 1);
        UserLogin testUserLogin = userLoginList.get(userLoginList.size() - 1);
        assertThat(testUserLogin.getMsisdn()).isEqualTo(DEFAULT_MSISDN);
    }

    @Test
    @Transactional
    public void createUserLoginWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userLoginRepository.findAll().size();

        // Create the UserLogin with an existing ID
        userLogin.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserLoginMockMvc.perform(post("/api/user-logins").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userLogin)))
            .andExpect(status().isBadRequest());

        // Validate the UserLogin in the database
        List<UserLogin> userLoginList = userLoginRepository.findAll();
        assertThat(userLoginList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllUserLogins() throws Exception {
        // Initialize the database
        userLoginRepository.saveAndFlush(userLogin);

        // Get all the userLoginList
        restUserLoginMockMvc.perform(get("/api/user-logins?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userLogin.getId().intValue())))
            .andExpect(jsonPath("$.[*].msisdn").value(hasItem(DEFAULT_MSISDN)));
    }
    
    @Test
    @Transactional
    public void getUserLogin() throws Exception {
        // Initialize the database
        userLoginRepository.saveAndFlush(userLogin);

        // Get the userLogin
        restUserLoginMockMvc.perform(get("/api/user-logins/{id}", userLogin.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userLogin.getId().intValue()))
            .andExpect(jsonPath("$.msisdn").value(DEFAULT_MSISDN));
    }
    @Test
    @Transactional
    public void getNonExistingUserLogin() throws Exception {
        // Get the userLogin
        restUserLoginMockMvc.perform(get("/api/user-logins/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserLogin() throws Exception {
        // Initialize the database
        userLoginRepository.saveAndFlush(userLogin);

        int databaseSizeBeforeUpdate = userLoginRepository.findAll().size();

        // Update the userLogin
        UserLogin updatedUserLogin = userLoginRepository.findById(userLogin.getId()).get();
        // Disconnect from session so that the updates on updatedUserLogin are not directly saved in db
        em.detach(updatedUserLogin);
        updatedUserLogin
            .msisdn(UPDATED_MSISDN);

        restUserLoginMockMvc.perform(put("/api/user-logins").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedUserLogin)))
            .andExpect(status().isOk());

        // Validate the UserLogin in the database
        List<UserLogin> userLoginList = userLoginRepository.findAll();
        assertThat(userLoginList).hasSize(databaseSizeBeforeUpdate);
        UserLogin testUserLogin = userLoginList.get(userLoginList.size() - 1);
        assertThat(testUserLogin.getMsisdn()).isEqualTo(UPDATED_MSISDN);
    }

    @Test
    @Transactional
    public void updateNonExistingUserLogin() throws Exception {
        int databaseSizeBeforeUpdate = userLoginRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserLoginMockMvc.perform(put("/api/user-logins").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userLogin)))
            .andExpect(status().isBadRequest());

        // Validate the UserLogin in the database
        List<UserLogin> userLoginList = userLoginRepository.findAll();
        assertThat(userLoginList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteUserLogin() throws Exception {
        // Initialize the database
        userLoginRepository.saveAndFlush(userLogin);

        int databaseSizeBeforeDelete = userLoginRepository.findAll().size();

        // Delete the userLogin
        restUserLoginMockMvc.perform(delete("/api/user-logins/{id}", userLogin.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserLogin> userLoginList = userLoginRepository.findAll();
        assertThat(userLoginList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
