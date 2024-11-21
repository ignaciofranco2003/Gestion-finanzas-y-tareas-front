package com.gestionfinanzasytareas.front.controllers;

import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
public class HomeController {

    @SuppressWarnings("unused")
    private final RestTemplate restTemplate;
    
    @Autowired
    public HomeController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/create-user")
    public String redirectToLogin() {
        return "crearusuario.html"; // Retorna la vista de login.html
    }

    @GetMapping("/recoverpassword")
    public String redirecToRecoverPass() {
        return "recuperarContraseña.html";
    }

    @GetMapping("/newpassword")
    public String redirecToNewPass() {
        return "nuevaContraseña.html";
    }
    
    @SuppressWarnings("deprecation")
    @PostMapping("/verificar-email")
    public ResponseEntity<String> verificarEmail(@RequestParam String email) {
        try {
            // Crear el cuerpo de la solicitud en JSON
            JSONObject requestBody = new JSONObject();
            requestBody.put("email", email);

            // Crear conexión HTTP
            HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/recoverpassword/verificar-email").openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // Enviar la solicitud
            connection.getOutputStream().write(requestBody.toString().getBytes());

            // Obtener el código de respuesta
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return ResponseEntity.ok("Email valido");
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El email no está registrado");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error: " + responseCode);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    @PostMapping("/enviar-codigo")
    public ResponseEntity<String> enviarEmail(@RequestParam String email) {
        try {
            // Crear el cuerpo de la solicitud en JSON
            JSONObject requestBody = new JSONObject();
            requestBody.put("destinatario", email);
            requestBody.put("asunto","Codigo de verificacion");
            requestBody.put("mensaje","");

            // Crear conexión HTTP
            HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/recoverpassword/enviar-codigo").openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // Enviar la solicitud
            connection.getOutputStream().write(requestBody.toString().getBytes());

            // Obtener el código de respuesta
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return ResponseEntity.ok("Codigo enviado");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No fue posible enviar el codigo");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/pablo")
    public String pablo() {
        return "pablo.html";
    }

    @SuppressWarnings("deprecation")
    @PostMapping("/verificar-codigo")
    public ResponseEntity<String> verificarCodigo(@RequestParam String email, @RequestParam String codigo) {
        try {
            // Crear el cuerpo de la solicitud en JSON
            JSONObject requestBody = new JSONObject();
            requestBody.put("email", email);
            requestBody.put("codigo", codigo);

            // Crear conexión HTTP
            HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/recoverpassword/validar-codigo").openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // Enviar la solicitud
            connection.getOutputStream().write(requestBody.toString().getBytes());

            // Obtener el código de respuesta
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return ResponseEntity.ok("Código verificado correctamente.");
            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Código de verificación incorrecto.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error: " + responseCode);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }


    @GetMapping("/nuevapass")
    public String cambiarpass(@RequestParam String email, @RequestParam String codigo) {
        return "nuevaContraseña.html";
    }
    

    @SuppressWarnings("deprecation")
    @PostMapping("/cambiar-pass")
    public ResponseEntity<String> cambiarpassPOST(@RequestBody CambiarReq cambiarReq) {
        try {
            // Crear el cuerpo de la solicitud en JSO
            JSONObject requestBody = new JSONObject();
            requestBody.put("email", cambiarReq.getEmail());
            requestBody.put("codigo", cambiarReq.getCodigo());
            requestBody.put("nuevaPassword", cambiarReq.getPassword());

            // Crear conexión HTTP
            HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/recoverpassword/cambiar-password").openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // Enviar la solicitud
            connection.getOutputStream().write(requestBody.toString().getBytes());

            // Obtener el código de respuesta
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return ResponseEntity.ok("Código verificado correctamente.");
            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Código de verificación incorrecto.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error: " + responseCode);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }


    static class CambiarReq{
        private String email;
        private String codigo;
        private String password;

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public String getCodigo() {
            return codigo;
        }
        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }
}
