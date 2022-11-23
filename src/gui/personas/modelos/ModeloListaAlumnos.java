/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.personas.modelos;


import gui.trabajos.modelos.AlumnoEnTrabajo;
import gui.trabajos.modelos.Trabajo;
import javax.swing.DefaultListModel;

/**
 *
 * @author Nico
 */
public class ModeloListaAlumnos extends DefaultListModel {
    
    //este constructor no recibe parametros y llena la lista con los alumnos en la lista de alumnos del gestor personas
    public ModeloListaAlumnos(){
        GestorPersonas gp = GestorPersonas.instanciar();
        for(Alumno a: gp.verListaAlumnos()){
            this.addElement(a);
        }
        
    }
    //este constructor recibe como parametro un trabajo y llena la lista con los alumnos que participan en el trabajo
    public ModeloListaAlumnos(Trabajo t){
        for(AlumnoEnTrabajo a: t.verAlumnos()){
            this.addElement(a.verAlumno());
        }
    }
}
