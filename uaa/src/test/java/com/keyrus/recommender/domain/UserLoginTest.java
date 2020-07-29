package com.keyrus.recommender.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.keyrus.recommender.web.rest.TestUtil;

public class UserLoginTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserLogin.class);
        UserLogin userLogin1 = new UserLogin();
        userLogin1.setId(1L);
        UserLogin userLogin2 = new UserLogin();
        userLogin2.setId(userLogin1.getId());
        assertThat(userLogin1).isEqualTo(userLogin2);
        userLogin2.setId(2L);
        assertThat(userLogin1).isNotEqualTo(userLogin2);
        userLogin1.setId(null);
        assertThat(userLogin1).isNotEqualTo(userLogin2);
    }
}
