/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.areas.modelos;

import gui.interfaces.IGestorAreas;
import gui.trabajos.modelos.Trabajo;
import javax.swing.DefaultListModel;

/**
 *
 * @author Nico
 */
public class ModeloListaAreas extends DefaultListModel{
    
    //este constructor no recibe parametros y llena la lista con las areas guardadas en la lista del gestor areas
    public ModeloListaAreas(){
        IGestorAreas ga = GestorAreas.instanciar();
        for(Area a: ga.buscarAreas(null)){
            this.addElement(a);
        }
        
    }
    //este constructor recibe como parametro un trabajo y llena la lista con las areas guardadas en la lista del trabajo
    public ModeloListaAreas(Trabajo t){
        for(Area a: t.verAreas()){
            this.addElement(a);
        }
    }
}
