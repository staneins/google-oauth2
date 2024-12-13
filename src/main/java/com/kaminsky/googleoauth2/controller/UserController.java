package com.kaminsky.googleoauth2.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/user")
    public String user(@AuthenticationPrincipal OAuth2User principal, Model model) {
        logger.info(principal.getAttributes().toString());

        model.addAttribute("name", principal.getAttribute("name"));
        model.addAttribute("login", principal.getAttribute("login"));
        model.addAttribute("id", principal.getAttribute("id"));
        model.addAttribute("email", principal.getAttribute("email"));
        return "user";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, null);
        return "redirect:/";
    }
}

