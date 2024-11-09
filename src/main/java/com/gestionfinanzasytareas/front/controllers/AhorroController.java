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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class AhorroController {

    // Cambia esta URL por la correcta según tu configuración
    private static String INGRESO_URL = "http://localhost:8080/api/ahorros";
    
    private final RestTemplate restTemplate;
    
    @Autowired
    public AhorroController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/ahorros/crear")
    public ResponseEntity<String> enviarAhorro(@RequestBody Map<String, Object> request) {
        UserSessionManager sessionManager = UserSessionManager.getInstance();
        try {
            // Extraer valores y manejar conversiones de tipo
            double montoActual = Double.parseDouble(request.get("montoactual").toString());
            double montoFinal = Double.parseDouble(request.get("montometa").toString());
            
            // Crear cuerpo de la solicitud con el formato correcto
            JSONObject requestBody = new JSONObject();
            requestBody.put("nombreAhorro", request.get("nombreahorro"));
            requestBody.put("montoActual", montoActual);
            requestBody.put("montoFinal", montoFinal);
            requestBody.put("cuentaId", sessionManager.getIDcuenta());
            requestBody.put("completado", 0);
    
            // Configurar la conexión HTTP
            HttpURLConnection connection = (HttpURLConnection) new URL(INGRESO_URL+"/create").openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", sessionManager.getToken());
    
            // Enviar la solicitud
            connection.getOutputStream().write(requestBody.toString().getBytes());
    
            // Obtener la respuesta
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return ResponseEntity.ok("Ahorro creado exitosamente.");
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para crear ahorro.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al crear el ahorro: " + responseCode);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el ahorro: " + e.getMessage());
        }
    }

    @PostMapping("/ahorros/editar")
    public ResponseEntity<String> editarAhorro(@RequestBody Map<String, Object> request) {
        UserSessionManager sessionManager = UserSessionManager.getInstance();

        try {
            // Crear el cuerpo de la solicitud en JSON
            // Extraer valores y manejar conversiones de tipo
            double montoActual = Double.parseDouble(request.get("montoActual").toString());
            double montoFinal = Double.parseDouble(request.get("montoFinal").toString());
            boolean completado = (boolean) request.get("completado");

            // Crear cuerpo de la solicitud con el formato correcto
            JSONObject requestBody = new JSONObject();
            requestBody.put("nombreAhorro", request.get("nombreAhorro"));
            requestBody.put("montoActual", montoActual);
            requestBody.put("montoFinal", montoFinal);
            requestBody.put("cuenta",new JSONObject().put("id", sessionManager.getIDcuenta()));
            requestBody.put("completado", completado);

            // Crear conexión HTTP
            @SuppressWarnings("deprecation")
            HttpURLConnection connection = (HttpURLConnection) new URL(INGRESO_URL + "/"+request.get("id")).openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", sessionManager.getToken());

            // Enviar la solicitud
            connection.getOutputStream().write(requestBody.toString().getBytes());

            // Obtener el código de respuesta
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return ResponseEntity.ok("ahorro editado exitosamente.");
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                // Manejar respuesta de acceso denegado
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para editar ahorro.");
            } else {
                // Manejar otro tipo de errores
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al editar el ahorro: " + responseCode);
            }
        } catch (Exception e) {
            // Manejar excepciones
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al editar el ahorro: " + e.getMessage());
        }
    }

    @PostMapping("/ahorros/eliminar/{id}")
    public ResponseEntity<String> eliminarTarea(@PathVariable Long id) {
        UserSessionManager sessionManager = UserSessionManager.getInstance();

        try {
            // Crear el cuerpo de la solicitud en JSON
            JSONObject requestBody = new JSONObject();

            // Crear la URL con el id de la tarea
            URL url = new URL(INGRESO_URL+ "/"+id);
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
                return ResponseEntity.ok("ahorro eliminado exitosamente.");
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                // Manejar respuesta de acceso denegado
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para eliminar el ahorro.");
            } else {
                // Manejar otro tipo de errores
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al eliminar el ahorro: " + responseCode);
            }
        } catch (Exception e) {
            // Manejar excepciones
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el ahorro " + e.getMessage());
        }
    }

    @GetMapping("/ahorros/obtener")
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

                System.out.println(response);
                // Convertir la respuesta JSON en una lista de mapas
                List<Map<String, Object>> ingresos = new ObjectMapper().readValue(response.toString(), List.class);

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
