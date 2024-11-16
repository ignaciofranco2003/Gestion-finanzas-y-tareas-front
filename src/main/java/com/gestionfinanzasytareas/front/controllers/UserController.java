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
        
        // Verifica si la sesión está activa y si el rol del usuario es "USER"
        if (!sessionManager.isActiveSession()) {
            model.addAttribute("error", "Tu sesión ha caducado. Inicia sesión nuevamente.");
            return "login";
        }
    
        if ("USER".equals(sessionManager.getActiveUserRole())) {
            return "user_dashboard.html";
        } else {
            model.addAttribute("error", "No tienes permiso para acceder a esta página.");
            return "login";
        }
    }
    
    @GetMapping("/tareas")
    public String tareas(Model model) {
        UserSessionManager sessionManager = UserSessionManager.getInstance();
        
        // Verifica si la sesión está activa
        if (!sessionManager.isActiveSession()) {
            model.addAttribute("error", "Tu sesión ha caducado. Inicia sesión nuevamente.");
            return "login";
        }
    
        // Verifica si el rol del usuario es "USER"
        if ("USER".equals(sessionManager.getActiveUserRole())) {
            return "tareas.html";
        } else {
            model.addAttribute("error", "No tienes permiso para acceder a esta página.");
            return "login";
        }
    }
    
    @GetMapping("/ingresos")
    public String ingresos(Model model) {
        UserSessionManager sessionManager = UserSessionManager.getInstance();
        
        // Verifica si la sesión está activa
        if (!sessionManager.isActiveSession()) {
            model.addAttribute("error", "Tu sesión ha caducado. Inicia sesión nuevamente.");
            return "login";
        }
    
        // Verifica si el rol del usuario es "USER"
        if ("USER".equals(sessionManager.getActiveUserRole())) {
            return "ingresos.html";
        } else {
            model.addAttribute("error", "No tienes permiso para acceder a esta página.");
            return "login";
        }
    }
    
    @GetMapping("/ahorros")
    public String ahorros(Model model) {
        UserSessionManager sessionManager = UserSessionManager.getInstance();
        
        // Verifica si la sesión está activa
        if (!sessionManager.isActiveSession()) {
            model.addAttribute("error", "Tu sesión ha caducado. Inicia sesión nuevamente.");
            return "login";
        }
    
        // Verifica si el rol del usuario es "USER"
        if ("USER".equals(sessionManager.getActiveUserRole())) {
            return "ahorros.html";
        } else {
            model.addAttribute("error", "No tienes permiso para acceder a esta página.");
            return "login";
        }
    }
    
    @GetMapping("/gastos")
    public String gastos(Model model) {
        UserSessionManager sessionManager = UserSessionManager.getInstance();
        
        // Verifica si la sesión está activa
        if (!sessionManager.isActiveSession()) {
            model.addAttribute("error", "Tu sesión ha caducado. Inicia sesión nuevamente.");
            return "login";
        }
    
        // Verifica si el rol del usuario es "USER"
        if ("USER".equals(sessionManager.getActiveUserRole())) {
            return "gastos.html";
        } else {
            model.addAttribute("error", "No tienes permiso para acceder a esta página.");
            return "login";
        }
    }
    
    @GetMapping("/gastos-mensuales")
    public String gastosAnuales(Model model) {
        UserSessionManager sessionManager = UserSessionManager.getInstance();
        
        // Verifica si la sesión está activa
        if (!sessionManager.isActiveSession()) {
            model.addAttribute("error", "Tu sesión ha caducado. Inicia sesión nuevamente.");
            return "login";
        }
    
        // Verifica si el rol del usuario es "USER"
        if ("USER".equals(sessionManager.getActiveUserRole())) {
            return "gastos-mensuales.html";
        } else {
            model.addAttribute("error", "No tienes permiso para acceder a esta página.");
            return "login";
        }
    }
    
}