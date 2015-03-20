/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.incidentmanagement.exception;

/**
 * This class represents an error or incompletion in an Incident object properties.
 * @author javi
 */
public class IncidentDataException extends Exception {

    /**
     * Creates a new instance of <code>IncidentDataException</code> without
     * detail message.
     */
    public IncidentDataException() {
    }

    /**
     * Constructs an instance of <code>IncidentDataException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public IncidentDataException(String msg) {
        super(msg);
    }
}
