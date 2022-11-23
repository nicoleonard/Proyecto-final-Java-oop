/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.trabajos.controladores;

import gui.interfaces.IControladorModificarAlumno;
import gui.interfaces.IGestorTrabajos;
import gui.personas.modelos.Alumno;
import gui.personas.modelos.ModeloListaAlumnos;
import gui.trabajos.modelos.GestorTrabajos;
import gui.trabajos.vistas.VentanaModificarAlumno;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;

/**
 *
 * @author Nico
 */
public class ControladorModificarAlumno implements IControladorModificarAlumno{

    private VentanaModificarAlumno ventana;
    private int trabajoSeleccionado;
    
    /**
     * Constructor de ControladorModificarAlumno
     * asigna modelo a la lista
     * asigna valor a la variable trabajoSeleccionado
     * @param ventanaPadre
     * @param trabajoSeleccionado 
     */
    public ControladorModificarAlumno(Dialog ventanaPadre, int trabajoSeleccionado){
        this.ventana = new VentanaModificarAlumno(this, ventanaPadre);
        this.trabajoSeleccionado=trabajoSeleccionado;
        this.ventana.setTitle(IControladorModificarAlumno.TRABAJO_MODIFICAR);
        
        GestorTrabajos gt = GestorTrabajos.instanciar();
        this.ventana.verListaAlumnosEnTrabajo().setModel(new ModeloListaAlumnos(gt.verTrabajos().get(this.trabajoSeleccionado)));
        this.ventana.setLocationRelativeTo(null);
        this.ventana.setVisible(true);
    }
    
    //Finaliza al alumno seleccionado de la lista de alumnos en trabajo
    //muestra un mensaje de error si los datos son incorrectos y un mensaje informando el exito de la operacion si los datos son correctos
    @Override
    public void btnAceptarClic(ActionEvent evt) {
        IGestorTrabajos gt = GestorTrabajos.instanciar();
        Alumno a =(Alumno)this.ventana.verListaAlumnosEnTrabajo().getSelectedValue();
        String resultado=gt.finalizarAlumno(gt.buscarTrabajos(null).get(this.trabajoSeleccionado), a, this.ventana.verFechaHasta(), this.ventana.verTxtRazon());
        if(!resultado.equals(IGestorTrabajos.EXITO)){
            JOptionPane.showMessageDialog(null, resultado, IControladorModificarAlumno.TRABAJO_MODIFICAR, JOptionPane.ERROR_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(null, "Alumno finalizado", IControladorModificarAlumno.TRABAJO_MODIFICAR, JOptionPane.INFORMATION_MESSAGE);
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
