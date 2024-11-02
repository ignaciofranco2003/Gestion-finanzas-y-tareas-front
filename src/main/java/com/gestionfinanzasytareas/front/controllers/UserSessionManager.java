package com.gestionfinanzasytareas.front.controllers;

import java.time.Instant;

public class UserSessionManager {
    private static UserSessionManager instance;
    private String token;
    private String currentRole;
    private String currentUserEmail;
    private Long userId;        // Agregar claim para userId
    private Long IDcuenta;      // Agregar claim para IDcuenta
    private boolean activeSession;
    private Instant expirationTime; // Usar tiempo de expiración del token

    // Constructor privado para patrón Singleton
    private UserSessionManager() {}

    // Método para obtener la única instancia
    public static UserSessionManager getInstance() {
        if (instance == null) {
            instance = new UserSessionManager();
        }
        return instance;
    }

    // Iniciar sesión si no hay sesión activa
    public synchronized boolean loginUser(String email, String role, Long userId, Long IDcuenta, Instant expirationTime) {
        if (!activeSession) {
            this.currentUserEmail = email;
            this.currentRole = role;
            this.userId = userId;        // Configurar userId
            this.IDcuenta = IDcuenta;    // Configurar IDcuenta
            this.expirationTime = expirationTime; // Configura el tiempo de expiración desde el token
            this.activeSession = true;
            return true; // Inicio de sesión exitoso
        }
        return false; // Ya hay una sesión activa
    }

    // Finalizar sesión
    public synchronized void endSession() {
        this.token = null;
        this.currentRole = null;
        this.currentUserEmail = null;
        this.userId = null;
        this.IDcuenta = null;
        this.activeSession = false;
        this.expirationTime = null;
    }

    // Verificar si hay una sesión activa y si no ha expirado
    public boolean isActiveSession() {
        if (activeSession && expirationTime != null) {
            if (Instant.now().isAfter(expirationTime)) {
                endSession(); // Expira la sesión si se ha excedido el tiempo
                return false;
            }
            return true;
        }
        return false;
    }

    public void setToken(String token){
        this.token = token;
    }

    public String getToken(){
        return token;
    }

    // Obtener el rol del usuario actual en sesión
    public String getActiveUserRole() {
        return currentRole;
    }

    public String getActiveEmail() {
        return currentUserEmail;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getIDcuenta() {
        return IDcuenta;
    }
}
