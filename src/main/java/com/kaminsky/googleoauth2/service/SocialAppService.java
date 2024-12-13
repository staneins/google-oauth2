package com.kaminsky.googleoauth2.service;

import com.kaminsky.googleoauth2.entity.Role;
import com.kaminsky.googleoauth2.entity.User;
import com.kaminsky.googleoauth2.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SocialAppService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(SocialAppService.class);

    public SocialAppService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String login = oAuth2User.getAttribute("login");

        logger.info("Аутентифицируем: " + oAuth2User.getAttributes());

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setLogin(login);

                    if (name.equals("admin")) {
                        newUser.setRole(Role.ADMIN);
                    } else {
                        newUser.setRole(Role.USER);
                    }

                    return newUser;
                });

        userRepository.save(user);

        user.setAttributes(oAuth2User.getAttributes());

        return user;
    }
}
