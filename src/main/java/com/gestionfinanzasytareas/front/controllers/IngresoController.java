package com.gestionfinanzasytareas.front.controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class IngresoController {

    // Cambia esta URL por la correcta según tu configuración
    private static String INGRESO_URL = "http://localhost:8080/api/ingresos";
    
    private final RestTemplate restTemplate;
    
    @Autowired
    public IngresoController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/ingresos/crear")
    public ResponseEntity<String> enviarIngreso(@RequestParam Float monto, @RequestParam int categoriaId) {
        UserSessionManager sessionManager = UserSessionManager.getInstance();

        try {
            // Obtener la fecha actual y formatearla
            LocalDate fechaActual = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String fecha = fechaActual.format(formatter);

            // Crear el cuerpo de la solicitud en JSON
            JSONObject requestBody = new JSONObject();
            requestBody.put("monto", monto);
            requestBody.put("fecha", fecha);
            requestBody.put("cuenta", new JSONObject().put("id", sessionManager.getIDcuenta()));
            requestBody.put("categoria", new JSONObject().put("id", categoriaId));

            // Crear conexión HTTP
            HttpURLConnection connection = (HttpURLConnection) new URL(INGRESO_URL + "/create").openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", sessionManager.getToken());

            // Enviar la solicitud
            connection.getOutputStream().write(requestBody.toString().getBytes());

            // Obtener el código de respuesta
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                return ResponseEntity.ok("Ingreso creado exitosamente.");
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                // Manejar respuesta de acceso denegado
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para crear ingresos.");
            } else {
                // Manejar otro tipo de errores
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el ingreso: " + responseCode);
            }
        } catch (Exception e) {
            // Manejar excepciones
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el ingreso: " + e.getMessage());
        }
    }

    @GetMapping("/ingresos/obtener")
    public ResponseEntity<List<Map<String, Object>>> gastosPorCategorias() {
        UserSessionManager sessionManager = UserSessionManager.getInstance();
        try {
            // Crear conexión HTTP para el endpoint
            HttpURLConnection connection = (HttpURLConnection) new URL(INGRESO_URL + "/cuenta/" + sessionManager.getIDcuenta()).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", sessionManager.getToken());

            // Leer la respuesta del endpoint
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Convertir la respuesta JSON en un mapa de claves y valores
                Map<String, Object> responseMap = new ObjectMapper().readValue(response.toString(), Map.class);
                
                // Extraer solo el contenido de "data"
                List<Map<String, Object>> ingresos = (List<Map<String, Object>>) responseMap.get("data");
                
                return ResponseEntity.ok(ingresos);
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
