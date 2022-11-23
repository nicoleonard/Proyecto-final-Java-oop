/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.trabajos.modelos;

import gui.personas.modelos.Profesor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import gui.interfaces.IGestorRolesEnTrabajos;

/**
 *
 * @author Nico
 */
public class GestorRolesEnTrabajos implements IGestorRolesEnTrabajos{
    private static GestorRolesEnTrabajos gestorRET; //variable de clase
    private List<RolEnTrabajo> listaRolesEnTrabajos= new ArrayList<>();
    //constructor privado
    private GestorRolesEnTrabajos(){
    }
    //metodos de clase
    public static GestorRolesEnTrabajos instanciar(){
        if(gestorRET==null)
            gestorRET=new GestorRolesEnTrabajos();
        return gestorRET;
    }

    public List<RolEnTrabajo> verListaRolesEnTrabajos() {
        return listaRolesEnTrabajos;
    }

    private static boolean validarRET(Profesor profesor, Rol rol, LocalDate fechaDesde){
        if (profesor == null) {
            return false;
        }
        if (rol == null) {
            return false;
        }
        if (fechaDesde == null) {
            return false;
        }
        return true;
    }
    
    //implementacion de la interface
    @Override
    public RolEnTrabajo nuevoRolEnTrabajo(Profesor profesor, Rol rol, LocalDate fechaDesde) {  
        if (validarRET(profesor, rol, fechaDesde) == true) {
            return new RolEnTrabajo(profesor, rol, fechaDesde);
            
        }
        return null;
        
    }
    
    
    
}