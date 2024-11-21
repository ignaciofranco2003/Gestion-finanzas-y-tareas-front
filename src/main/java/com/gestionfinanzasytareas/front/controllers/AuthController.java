package com.gestionfinanzasytareas.front.controllers;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Scanner;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletResponse;


@Controller
public class AuthController {
    

    private final RestTemplate restTemplate;
    
    @Autowired
    public AuthController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public String inicio() {
        return "login.html"; // Retorna la vista de login.html
    }

    @GetMapping("/login")
    public String redirectToLogin() {
        return "login.html"; // Retorna la vista de login.html
    }

    @PostMapping("/logout")
    public String logout() {
        UserSessionManager sessionManager = UserSessionManager.getInstance();
        sessionManager.endSession();
        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpServletResponse response, Model model) {
        try {
            // Crear el cuerpo de la solicitud en JSON
            String LOGIN_URL = "http://localhost:8080/auth/authenticate";
            String requestBody = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);
            HttpURLConnection connection = (HttpURLConnection) new URL(LOGIN_URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // Enviar la solicitud
            connection.getOutputStream().write(requestBody.getBytes());
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_ACCEPTED) {
                UserSessionManager sessionManager = UserSessionManager.getInstance();
                if(sessionManager.isActiveSession()){
                    if(sessionManager.getActiveEmail().equals(email)){
                        if (sessionManager.getActiveUserRole().equals("ADMIN")) {
                            return "redirect:/home-admin";
                        } else {
                            return "redirect:/home-usuario";
                        }
                    }
                }
                // Leer la respuesta para obtener el token
                StringBuilder responseJson = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNext()) {
                    responseJson.append(scanner.nextLine());
                }
                scanner.close();

                JSONObject jsonResponse = new JSONObject(responseJson.toString());
                String token = jsonResponse.getString("jwt");

                // Validar el token utilizando el endpoint validate token
                String validateUrl = "http://localhost:8080/auth/validate-token"; // Cambia "VALIDATE_URL" con el URL del endpoint de validación
                HttpURLConnection validateConnection = (HttpURLConnection) new URL(validateUrl).openConnection();
                validateConnection.setRequestMethod("GET");
                validateConnection.setRequestProperty("Authorization", "Bearer " + token);

                int validateResponseCode = validateConnection.getResponseCode();
                if (validateResponseCode == HttpURLConnection.HTTP_OK) {
                    // Parsear la respuesta de validación
                    StringBuilder validateResponseJson = new StringBuilder();
                    Scanner validateScanner = new Scanner(validateConnection.getInputStream());
                    while (validateScanner.hasNext()) {
                        validateResponseJson.append(validateScanner.nextLine());
                    }
                    validateScanner.close();

                    JSONObject validateJsonResponse = new JSONObject(validateResponseJson.toString());
                    JSONObject claims = validateJsonResponse.getJSONObject("claims");
                    String role = claims.getString("role");
                    Long userId = claims.getLong("userid");
                    Long IDcuenta = claims.getLong("IDcuenta");
                    
                    // Convertir el tiempo de expiración del token a Instant
                    String expirationTimeString = claims.getString("exp");
                    Instant expirationTime = OffsetDateTime.parse(expirationTimeString).toInstant();

                    sessionManager = UserSessionManager.getInstance();

                    // Intentar iniciar sesión si no hay una sesión activa
                    if (sessionManager.loginUser(email, role, userId, IDcuenta, expirationTime)) {
                        // Guardar el token en sessionStorage
                        response.setHeader("Authorization", "Bearer " + token);
                        sessionManager.setToken(token);

                        // Redirigir según el rol
                        if ("ADMIN".equals(role)) {
                            return "redirect:/home-admin";
                        } else {
                            return "redirect:/home-usuario";
                        }
                    } else {
                        model.addAttribute("error", "Ya hay un usuario conectado. Por favor, cierre la sesión actual primero.");
                        return "login";
                    }
                } else {
                    model.addAttribute("error", "Token inválido o expirado.");
                    return "login";
                }
            } else {
                model.addAttribute("error", "El email o la contraseña son incorrectos.");
                return "login";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ocurrió un error al intentar iniciar sesión.");
            return "login";
        }
    }

}