package com.gestionfinanzasytareas.front.controllers;

import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/home-admin")
public class AdminController {

        @GetMapping
        public String homeAdmin(Model model) {
                UserSessionManager sessionManager = UserSessionManager.getInstance();
                if (sessionManager.isActiveSession() && "ADMIN".equals(sessionManager.getActiveUserRole())) {
                        return "admin_dashboard.html";
                } else {
                        model.addAttribute("error", "No tienes permiso para acceder a esta página.");
                        return "login";
                }
        }

        @GetMapping("/ingresos")
        public String homeAdminingresos(Model model) {
                UserSessionManager sessionManager = UserSessionManager.getInstance();
                if (sessionManager.isActiveSession() && "ADMIN".equals(sessionManager.getActiveUserRole())) {
                        return "admin-ingresos.html";
                } else {
                        model.addAttribute("error", "No tienes permiso para acceder a esta página.");
                        return "login";
                }
        }

        @GetMapping("/gastos")
        public String homeAdmingastos(Model model) {
                UserSessionManager sessionManager = UserSessionManager.getInstance();
                if (sessionManager.isActiveSession() && "ADMIN".equals(sessionManager.getActiveUserRole())) {
                        return "admin-gastos.html";
                } else {
                        model.addAttribute("error", "No tienes permiso para acceder a esta página.");
                        return "login";
                }
        }

        @PostMapping("/ingresos/crear")
        public ResponseEntity<String> enviarIngreso(@RequestParam String nombre) {
                UserSessionManager sessionManager = UserSessionManager.getInstance();

                try {
                        String INGRESO_URL =  "http://localhost:8080/backof/categorias-ingreso";

                        // Crear el cuerpo de la solicitud en JSON
                        JSONObject requestBody = new JSONObject();
                        requestBody.put("nombre", nombre);

                        // Crear conexión HTTP
                        HttpURLConnection connection = (HttpURLConnection) new URL(INGRESO_URL).openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setRequestProperty("Authorization", sessionManager.getToken());

                        // Enviar la solicitud
                        connection.getOutputStream().write(requestBody.toString().getBytes());

                        // Obtener el código de respuesta
                        int responseCode = connection.getResponseCode();
                        System.out.println(responseCode);
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                return ResponseEntity.ok("Categoria de ingreso creado exitosamente.");
                        } else if (responseCode == HttpURLConnection.HTTP_CONFLICT) {
                                // Manejar respuesta de acceso denegado
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body("El nombre de la categoria ya existe");
                        } else {
                                // Manejar otro tipo de errores
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                .body("Error al crear la categoria.: " + responseCode);
                        }
                } catch (Exception e) {
                        // Manejar excepciones
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("Error al crear la categoria: " + e.getMessage());
                }
        }

        @PostMapping("/ingresos/editar")
        public ResponseEntity<String> editarIngreso(@RequestBody Request req) {
                UserSessionManager sessionManager = UserSessionManager.getInstance();

                System.out.println(req.getId());
                System.out.println(req.getNombre());

                try {
                        String INGRESO_URL =  "http://localhost:8080/backof/categorias-ingreso/"+req.getId();

                        // Crear el cuerpo de la solicitud en JSON
                        JSONObject requestBody = new JSONObject();
                        requestBody.put("nombre", req.getNombre());

                        // Crear conexión HTTP
                        HttpURLConnection connection = (HttpURLConnection) new URL(INGRESO_URL).openConnection();
                        connection.setRequestMethod("PUT");
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setRequestProperty("Authorization", sessionManager.getToken());

                        // Enviar la solicitud
                        connection.getOutputStream().write(requestBody.toString().getBytes());

                        // Obtener el código de respuesta
                        int responseCode = connection.getResponseCode();
                        System.out.println(responseCode);
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                return ResponseEntity.ok("Categoria de ingreso editada exitosamente.");
                        } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                                // Manejar respuesta de acceso denegado
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body("El nombre de la categoria ya existe");
                        } else {
                                // Manejar otro tipo de errores
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                .body("Error al crear la categoria.: " + responseCode);
                        }
                } catch (Exception e) {
                        // Manejar excepciones
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("Error al crear la categoria: " + e.getMessage());
                }
        }

        @PostMapping("/ingresos/eliminar")
        public ResponseEntity<String> eliminarIngreso(@RequestBody Request req) {
                UserSessionManager sessionManager = UserSessionManager.getInstance();

                System.out.println(req.getId());

                try {
                        String INGRESO_URL =  "http://localhost:8080/backof/categorias-ingreso/"+req.getId();

                        // // Crear el cuerpo de la solicitud en JSON
                        // JSONObject requestBody = new JSONObject();
                        // requestBody.put("nombre", req.getNombre());

                        // Crear conexión HTTP
                        HttpURLConnection connection = (HttpURLConnection) new URL(INGRESO_URL).openConnection();
                        connection.setRequestMethod("DELETE");
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setRequestProperty("Authorization", sessionManager.getToken());

                        // Enviar la solicitud
                        connection.getOutputStream();

                        // Obtener el código de respuesta
                        int responseCode = connection.getResponseCode();
                        System.out.println(responseCode);
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                return ResponseEntity.ok("Categoria de ingreso eliminada exitosamente.");
                        } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                                // Manejar respuesta de acceso denegado
                                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El nombre de la categoria ya existe");
                        } else {
                                // Manejar otro tipo de errores
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                .body("Error al crear la categoria.: " + responseCode);
                        }
                } catch (Exception e) {
                        // Manejar excepciones
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("Error al crear la categoria: " + e.getMessage());
                }
        }



        @PostMapping("/gastos/crear")
        public ResponseEntity<String> enviargastos(@RequestParam String nombre) {
                UserSessionManager sessionManager = UserSessionManager.getInstance();

                try {
                        String INGRESO_URL =  "http://localhost:8080/backof/categorias-gasto";

                        // Crear el cuerpo de la solicitud en JSON
                        JSONObject requestBody = new JSONObject();
                        requestBody.put("nombre", nombre);

                        // Crear conexión HTTP
                        HttpURLConnection connection = (HttpURLConnection) new URL(INGRESO_URL).openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setRequestProperty("Authorization", sessionManager.getToken());

                        // Enviar la solicitud
                        connection.getOutputStream().write(requestBody.toString().getBytes());

                        // Obtener el código de respuesta
                        int responseCode = connection.getResponseCode();
                        System.out.println(responseCode);
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                return ResponseEntity.ok("Categoria de ingreso creado exitosamente.");
                        } else if (responseCode == HttpURLConnection.HTTP_CONFLICT) {
                                // Manejar respuesta de acceso denegado
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body("El nombre de la categoria ya existe");
                        } else {
                                // Manejar otro tipo de errores
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                .body("Error al crear la categoria.: " + responseCode);
                        }
                } catch (Exception e) {
                        // Manejar excepciones
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("Error al crear la categoria: " + e.getMessage());
                }
        }

        @PostMapping("/gastos/editar")
        public ResponseEntity<String> editarGastos(@RequestBody Request req) {
                UserSessionManager sessionManager = UserSessionManager.getInstance();

                System.out.println(req.getId());
                System.out.println(req.getNombre());

                try {
                        String INGRESO_URL =  "http://localhost:8080/backof/categorias-gasto/"+req.getId();

                        // Crear el cuerpo de la solicitud en JSON
                        JSONObject requestBody = new JSONObject();
                        requestBody.put("nombre", req.getNombre());

                        // Crear conexión HTTP
                        HttpURLConnection connection = (HttpURLConnection) new URL(INGRESO_URL).openConnection();
                        connection.setRequestMethod("PUT");
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setRequestProperty("Authorization", sessionManager.getToken());

                        // Enviar la solicitud
                        connection.getOutputStream().write(requestBody.toString().getBytes());

                        // Obtener el código de respuesta
                        int responseCode = connection.getResponseCode();
                        System.out.println(responseCode);
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                return ResponseEntity.ok("Categoria de ingreso editada exitosamente.");
                        } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                                // Manejar respuesta de acceso denegado
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body("El nombre de la categoria ya existe");
                        } else {
                                // Manejar otro tipo de errores
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                .body("Error al crear la categoria.: " + responseCode);
                        }
                } catch (Exception e) {
                        // Manejar excepciones
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("Error al crear la categoria: " + e.getMessage());
                }
        }

        @PostMapping("/gastos/eliminar")
        public ResponseEntity<String> eliminarGasto(@RequestBody Request req) {
                UserSessionManager sessionManager = UserSessionManager.getInstance();

                System.out.println(req.getId());

                try {
                        String INGRESO_URL =  "http://localhost:8080/backof/categorias-gasto/"+req.getId();

                        // // Crear el cuerpo de la solicitud en JSON
                        // JSONObject requestBody = new JSONObject();
                        // requestBody.put("nombre", req.getNombre());

                        // Crear conexión HTTP
                        HttpURLConnection connection = (HttpURLConnection) new URL(INGRESO_URL).openConnection();
                        connection.setRequestMethod("DELETE");
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setRequestProperty("Authorization", sessionManager.getToken());

                        // Enviar la solicitud
                        connection.getOutputStream();

                        // Obtener el código de respuesta
                        int responseCode = connection.getResponseCode();
                        System.out.println(responseCode);
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                return ResponseEntity.ok("Categoria de ingreso eliminada exitosamente.");
                        } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                                // Manejar respuesta de acceso denegado
                                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El nombre de la categoria ya existe");
                        } else {
                                // Manejar otro tipo de errores
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                .body("Error al crear la categoria.: " + responseCode);
                        }
                } catch (Exception e) {
                        // Manejar excepciones
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("Error al crear la categoria: " + e.getMessage());
                }
        }

        static class Request{
                private String nombre;
                private int id;
                
                public String getNombre() {
                        return nombre;
                }
                public void setNombre(String nombre) {
                        this.nombre = nombre;
                }
                public int getId() {
                        return id;
                }
                public void setId(int id) {
                        this.id = id;
                }
                
        }
}
