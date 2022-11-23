/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.trabajos.controladores;

import gui.interfaces.IControladorModificarProfesor;
import gui.interfaces.IGestorTrabajos;
import gui.personas.modelos.ModeloListaProfesores;
import gui.personas.modelos.Profesor;
import gui.trabajos.modelos.GestorTrabajos;
import gui.trabajos.vistas.VentanaModificarProfesor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import javax.swing.JOptionPane;

/**
 *
 * @author Nico
 */
public class ControladorModificarProfesor implements IControladorModificarProfesor{

    private VentanaModificarProfesor ventana;
    private int trabajoSeleccionado;
    
    /**
     * Constructor de ControladorModificarProfesor
     * asigna modelo a las listas de la ventana 
     * asigna valor a la variable trabajoSeleccionado
     * @param ventanaPadre
     * @param trabajoSeleccionado 
     */
    public ControladorModificarProfesor(Dialog ventanaPadre, int trabajoSeleccionado){
        this.ventana = new VentanaModificarProfesor(this,ventanaPadre);
        this.trabajoSeleccionado=trabajoSeleccionado;
        this.ventana.setTitle(IControladorModificarProfesor.TRABAJO_MODIFICAR);
        
        IGestorTrabajos gt = GestorTrabajos.instanciar();
        this.ventana.verListaProfesoresConRoles().setModel(new ModeloListaProfesores(gt.buscarTrabajos(null).get(this.trabajoSeleccionado)));
        this.ventana.verListaProfesores().setModel(new ModeloListaProfesores());
        
        this.ventana.setLocationRelativeTo(null);
        this.ventana.setVisible(true);
    }
    
    @Override
    public void btnAceptarClic(ActionEvent evt) {
        IGestorTrabajos gt = GestorTrabajos.instanciar();
        
        Profesor profesorReemplazado = (Profesor)this.ventana.verListaProfesoresConRoles().getSelectedValue();
        Profesor nuevoProfesor = (Profesor)this.ventana.verListaProfesores().getSelectedValue();
        LocalDate fechaReemplazo = this.ventana.verDcFechaReemplazo();
        String razon = this.ventana.verTxtRazon();
        
        String resultado = gt.reemplazarProfesor(gt.buscarTrabajos(null).get(this.trabajoSeleccionado), profesorReemplazado, fechaReemplazo, razon, nuevoProfesor);
        
        if(!resultado.equals(IGestorTrabajos.EXITO)){
            JOptionPane.showMessageDialog(null, resultado, IControladorModificarProfesor.TRABAJO_MODIFICAR, JOptionPane.ERROR_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(null, "Profesor reemplazado", IControladorModificarProfesor.TRABAJO_MODIFICAR, JOptionPane.INFORMATION_MESSAGE);
            this.ventana.dispose();
        }
    }

    @Override
    public void btnCancelarClic(ActionEvent evt) {
        this.ventana.dispose();
    }

    @Override
    public void txtRazonPresionarTecla(KeyEvent evt) {
        char c = evt.getKeyChar();            
        if (!Character.isLetter(c)) { //sólo se aceptan letras, Del, Backspace y espacio
            switch(c) {
                
                case KeyEvent.VK_BACK_SPACE:    
                case KeyEvent.VK_DELETE:
                case KeyEvent.VK_SPACE:
                    break;
                default:
                    evt.consume(); //consume el evento para que no sea procesado por la fuente
            }
        }
    }

    @Override
    public void fechaHastaPresionarTecla(KeyEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
