package com.gestionfinanzasytareas.front.controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class TareasController {

    // Cambia esta URL por la correcta según tu configuración
    private static String INGRESO_URL = "http://localhost:8080/api/tareas";
    
    private final RestTemplate restTemplate;
    
    @Autowired
    public TareasController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/tareas/obtener")
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
                List<Map<String, Object>> tareas = (List<Map<String, Object>>) responseMap.get("data");
                
                // // Imprimir las tareas para verificar
                // for (Map<String, Object> tarea : tareas) {
                //     System.out.println(tarea);
                // }
                return ResponseEntity.ok(tareas);
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/tareas/crear")
    public ResponseEntity<String> crearTarea(@RequestBody Map<String, Object> request) {
        UserSessionManager sessionManager = UserSessionManager.getInstance();
    
        try {
            // Crear el cuerpo de la solicitud en JSON
            JSONObject requestBody = new JSONObject();
            requestBody.put("descripcion", request.get("descripcion"));
            requestBody.put("completada", request.getOrDefault("completada", false));
            requestBody.put("cuenta", new JSONObject().put("id", sessionManager.getIDcuenta()));
    
            // Crear conexión HTTP
            HttpURLConnection connection = (HttpURLConnection) new URL(INGRESO_URL+"/create").openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", sessionManager.getToken());
    
            // Enviar la solicitud
            connection.getOutputStream().write(requestBody.toString().getBytes());
    
            // Obtener el código de respuesta
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                return ResponseEntity.ok("Tarea creada exitosamente.");
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                // Manejar respuesta de acceso denegado
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para crear tareas.");
            } else {
                // Manejar otro tipo de errores
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al crear la tarea: " + responseCode);
            }
        } catch (Exception e) {
            // Manejar excepciones
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la tarea: " + e.getMessage());
        }
    }


    @PostMapping("/tareas/completar")
    public ResponseEntity<String> completarTarea(@RequestBody Map<String, Object> request) {
        UserSessionManager sessionManager = UserSessionManager.getInstance();

        try {
            // Crear el cuerpo de la solicitud en JSON
            JSONObject requestBody = new JSONObject();
            requestBody.put("descripcion", request.get("descripcion"));
            requestBody.put("completada", request.getOrDefault("completada", false));
            requestBody.put("cuenta", new JSONObject().put("id", sessionManager.getIDcuenta()));

            // Crear la URL con el id de la tarea
            URL url = new URL(INGRESO_URL+ "/"+request.get("id"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + sessionManager.getToken());

            // Enviar la solicitud
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Obtener el código de respuesta
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return ResponseEntity.ok("Tarea completada exitosamente.");
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                // Manejar respuesta de acceso denegado
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para completar la tarea.");
            } else {
                // Manejar otro tipo de errores
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al completar la tarea: " + responseCode);
            }
        } catch (Exception e) {
            // Manejar excepciones
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al completar la tarea: " + e.getMessage());
        }
    }


    @PostMapping("/tareas/cambiarNombre")
    public ResponseEntity<String> cambiarNombreTarea(@RequestBody Map<String, Object> request) {
        UserSessionManager sessionManager = UserSessionManager.getInstance();

        try {
            // Crear el cuerpo de la solicitud en JSON
            JSONObject requestBody = new JSONObject();
            requestBody.put("descripcion", request.get("descripcion"));
            requestBody.put("completada", request.getOrDefault("completada", false));
            requestBody.put("cuenta", new JSONObject().put("id", sessionManager.getIDcuenta()));

            // Crear la URL con el id de la tarea
            URL url = new URL(INGRESO_URL+ "/"+request.get("id"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + sessionManager.getToken());

            // Enviar la solicitud
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Obtener el código de respuesta
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return ResponseEntity.ok("Tarea completada exitosamente.");
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                // Manejar respuesta de acceso denegado
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para completar la tarea.");
            } else {
                // Manejar otro tipo de errores
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al completar la tarea: " + responseCode);
            }
        } catch (Exception e) {
            // Manejar excepciones
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al completar la tarea: " + e.getMessage());
        }
    }


    @PostMapping("/tareas/eliminar")
    public ResponseEntity<String> eliminarTarea(@RequestBody Map<String, Object> request) {
        UserSessionManager sessionManager = UserSessionManager.getInstance();

        try {
            // Crear el cuerpo de la solicitud en JSON
            JSONObject requestBody = new JSONObject();

            // Crear la URL con el id de la tarea
            URL url = new URL(INGRESO_URL+ "/"+request.get("id"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + sessionManager.getToken());

            // Enviar la solicitud
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Obtener el código de respuesta
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return ResponseEntity.ok("Tarea eliminada exitosamente.");
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                // Manejar respuesta de acceso denegado
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para eliminar la tarea.");
            } else {
                // Manejar otro tipo de errores
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al eliminar la tarea: " + responseCode);
            }
        } catch (Exception e) {
            // Manejar excepciones
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la tarea: " + e.getMessage());
        }
    }

}
