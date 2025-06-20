package br.com.fiap.safespace.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.fiap.safespace.model.User;
import br.com.fiap.safespace.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                System.out.println("FILTER");

                var header = request.getHeader("Authorization");
                if (header == null){
                    System.out.println("Sem header Authorization");
                    filterChain.doFilter(request, response);
                    return;
                }

                if(!header.startsWith("Bearer ")){
                    System.out.println("Token não começa com Bearer");
                    response.setStatus(401);
                    response.getWriter().write("""
                        {"message": "Token deve começar com Bearer" }
                    """);   
                    return;
                }

                var token = header.replace("Bearer ", "");
                User user = tokenService.getUserFromToken(token);

                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request, response);
        
    }


    
}