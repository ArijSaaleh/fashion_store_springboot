package com.arij.fashionecommerce.security;
import com.arij.fashionecommerce.entity.AuthProvider;
import com.arij.fashionecommerce.entity.User;
import com.arij.fashionecommerce.repository.UserRepository;
import com.arij.fashionecommerce.security.JWT.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    public OAuth2AuthenticationSuccessHandler(JwtTokenProvider tokenProvider, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // Check if user exists, if not create
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName(oAuth2User.getAttribute("name"));
            newUser.setProvider(AuthProvider.GOOGLE);
            return userRepository.save(newUser);
        });

        // Generate JWT
        String token = tokenProvider.generateTokenFromEmail(email);

        // Redirect to frontend with JWT
        String redirectUrl = "http://localhost:4200/auth/oauth2/success?token=" + token;
        response.sendRedirect(redirectUrl);
    }
}