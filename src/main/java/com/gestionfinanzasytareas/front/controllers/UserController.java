package com.gestionfinanzasytareas.front.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home-usuario")
public class UserController {

        @GetMapping
        public String homeUsuario(Model model) {
            UserSessionManager sessionManager = UserSessionManager.getInstance();
            
            // Verifica si la sesión actual es válida para el rol "USER"
            if (sessionManager.isActiveSession() && "USER".equals(sessionManager.getActiveUserRole())) {
                return "user_dashboard.html";
            } else {
                model.addAttribute("error", "No tienes permiso para acceder a esta página o ya hay una sesión activa con otro rol.");
                return "login";
            }
        }
        
        @GetMapping("/tareas")
        public String tareas(Model model) {
            UserSessionManager sessionManager = UserSessionManager.getInstance();
            
            // Verifica si la sesión actual es válida para el rol "USER"
            if (sessionManager.isActiveSession() && "USER".equals(sessionManager.getActiveUserRole())) {
                return "tareas.html";
            } else {

                //MARCAR LA EXCEPCION SI EL TOKEN NO ES VALIDO (LA SESION NO ESTA ACTIVA)

                model.addAttribute("error", "No tienes permiso para acceder a esta página o ya hay una sesión activa con otro rol.");
                return "login";
            }
        }

        @GetMapping("/ingresos")
        public String ingresos(Model model) {
            UserSessionManager sessionManager = UserSessionManager.getInstance();
            
            // Verifica si la sesión actual es válida para el rol "USER"
            if (sessionManager.isActiveSession() && "USER".equals(sessionManager.getActiveUserRole())) {
                return "ingresos.html";
            } else {
                model.addAttribute("error", "No tienes permiso para acceder a esta página o ya hay una sesión activa con otro rol.");
                return "login";
            }
        }

        @GetMapping("/ahorros")
        public String ahorros(Model model) {
            UserSessionManager sessionManager = UserSessionManager.getInstance();
            
            // Verifica si la sesión actual es válida para el rol "USER"
            if (sessionManager.isActiveSession() && "USER".equals(sessionManager.getActiveUserRole())) {
                return "ahorros.html";
            } else {
                model.addAttribute("error", "No tienes permiso para acceder a esta página o ya hay una sesión activa con otro rol.");
                return "login";
            }
        }

        @GetMapping("/gastos")
        public String gastos(Model model) {
            UserSessionManager sessionManager = UserSessionManager.getInstance();
            
            // Verifica si la sesión actual es válida para el rol "USER"
            if (sessionManager.isActiveSession() && "USER".equals(sessionManager.getActiveUserRole())) {
                return "gastos.html";
            } else {
                model.addAttribute("error", "No tienes permiso para acceder a esta página o ya hay una sesión activa con otro rol.");
                return "login";
            }
        }


}
