package com.arij.fashionecommerce.security.OAthu2;
import com.arij.fashionecommerce.security.JWT.JwtTokenProvider;
import com.arij.fashionecommerce.security.UserPrincipal;
import com.arij.fashionecommerce.service.UserService;
import com.arij.fashionecommerce.entity.AuthProvider;
import com.arij.fashionecommerce.entity.User;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    @Value("${app.oauth2.redirectUri}")
    private String redirectUri;

    public OAuth2AuthenticationSuccessHandler(UserService us, JwtTokenProvider tp) {
        this.userService = us;
        this.tokenProvider = tp;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String providerId = oauthUser.getAttribute("sub"); // google uses 'sub'
        if (providerId == null) {
            providerId = oauthUser.getName();
        }

        // create or update user
        User user = userService.processOAuthPostLogin(email, name, providerId, AuthProvider.GOOGLE);

        // create a Spring Authentication with user's authorities for token generation
        UserPrincipal principal = new UserPrincipal(user);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal.getUsername(), null, principal.getAuthorities());

        String token = tokenProvider.generateToken(auth);

        // redirect to frontend with token (dev). In prod prefer HttpOnly secure cookie.
        String target = redirectUri + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        response.sendRedirect(target);
    }
}