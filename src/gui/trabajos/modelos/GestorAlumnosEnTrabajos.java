/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.trabajos.modelos;

import gui.interfaces.IGestorAlumnosEnTrabajos;
import gui.personas.modelos.Alumno;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nico
 */
public class GestorAlumnosEnTrabajos implements IGestorAlumnosEnTrabajos{
    
    private static GestorAlumnosEnTrabajos gestorAET; //variable de clase
    private List<AlumnoEnTrabajo> listaAlumnosEnTrabajos= new ArrayList<>();
    //constructor privado
    private GestorAlumnosEnTrabajos(){
    }

    public List<AlumnoEnTrabajo> verListaAlumnosEnTrabajos() {
        return listaAlumnosEnTrabajos;
    }
    
    //metodos de clase
    public static GestorAlumnosEnTrabajos instanciar(){
        if(gestorAET==null)
            gestorAET=new GestorAlumnosEnTrabajos();
        return gestorAET;
    }
    
    //implementacion de la interface
       
    private static boolean validarAET(Alumno alumno, LocalDate fechaDesde){
        if (alumno == null) {
            return false;
        }
        if (fechaDesde == null) {
            return false;
        }
        return true;
    }
    
    /**
     * Crea un nuevo AlumnoEnTrabajo
     * @param alumno alumno que participa en el trabajo
     * @param fechaDesde fecha a partir de la cual el alumno comienza en el trabajo
     * @return AlumnoEnTrabajo  - objeto AlumnoEnTrabajo en caso que ....
    */  
    @Override
    public AlumnoEnTrabajo nuevoAlumnoEnTrabajo(Alumno alumno, LocalDate fechaDesde) {
        if (validarAET(alumno, fechaDesde) == true) {
            return new AlumnoEnTrabajo(alumno, fechaDesde);
        }
        return null;
    }
}
