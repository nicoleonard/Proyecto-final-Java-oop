/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.personas.modelos;


import gui.trabajos.modelos.Rol;
import gui.trabajos.modelos.RolEnTrabajo;
import gui.trabajos.modelos.Trabajo;
import javax.swing.DefaultListModel;

/**
 *
 * @author Nico
 */
public class ModeloListaProfesores extends DefaultListModel{
    
    //este constructor no recibe parametros y llena la lista con los profesores en la lista de profesores del gestor personas
    public ModeloListaProfesores(){
        GestorPersonas gp = GestorPersonas.instanciar();
        for(Profesor p: gp.verListaProfesores()){
            this.addElement(p);
        }
    }
        
    //este constructor recibe como parametro un trabajo y un rol, se usa para llenar las listas de la VentanaAMTrabajo en la operacion modificar trabajo
    public ModeloListaProfesores(Trabajo t, Rol r){
        for(RolEnTrabajo rt: t.verProfesoresConRoles()){
            if(rt.verRol().equals(r))
                this.addElement(rt.verProfesor());
        }
    }
    
    //este constructor recibe como parametro un trabajo y llena la lista con los profesores con roles en el trabajo
    public ModeloListaProfesores(Trabajo t){
        for(RolEnTrabajo rt: t.verProfesoresConRoles()){
            if(rt.verFechaHasta()==null){
                this.addElement(rt.verProfesor());
            }
            
        }
    }
}
