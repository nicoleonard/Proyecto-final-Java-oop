/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.trabajos.controladores;

import gui.areas.modelos.Area;
import gui.areas.modelos.ModeloListaAreas;
import gui.interfaces.IControladorAMTrabajo;
import gui.interfaces.IControladorTrabajos;
import gui.interfaces.IGestorAlumnosEnTrabajos;
import gui.interfaces.IGestorRolesEnTrabajos;
import gui.interfaces.IGestorTrabajos;
import gui.personas.modelos.Alumno;
import gui.personas.modelos.ModeloListaAlumnos;
import gui.personas.modelos.ModeloListaProfesores;
import gui.personas.modelos.Profesor;
import gui.trabajos.modelos.AlumnoEnTrabajo;
import gui.trabajos.modelos.GestorAlumnosEnTrabajos;
import gui.trabajos.modelos.GestorRolesEnTrabajos;
import gui.trabajos.modelos.GestorTrabajos;
import gui.trabajos.modelos.Rol;
import gui.trabajos.modelos.RolEnTrabajo;
import gui.trabajos.modelos.Trabajo;
import gui.trabajos.vistas.VentanaAMTrabajo;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Nico
 */
public class ControladorAMTrabajo implements IControladorAMTrabajo {
    private VentanaAMTrabajo ventana;
    private int trabajoSeleccionado;
    /**
     * Constructor del ControladorAMTrabajo
     * esta variante se usa para crear un nuevo trabajo
     * @param ventanaPadre
     * @param titulo de la operacion
     */
    public ControladorAMTrabajo(Dialog ventanaPadre, String titulo) {
        this.ventana = new VentanaAMTrabajo(this, ventanaPadre, titulo);

        this.ventana.verListaAreas().setModel(new ModeloListaAreas());
        this.ventana.verListaAlumnos().setModel(new ModeloListaAlumnos());
        this.ventana.verListaTutor().setModel(new ModeloListaProfesores());
        this.ventana.verListaCotutor().setModel(new ModeloListaProfesores());
        this.ventana.verListaJurado().setModel(new ModeloListaProfesores());
        
        this.ventana.setLocationRelativeTo(null);
        this.ventana.setVisible(true);
    }    
    /**
     * Constructor del ControladorAMTrabajo
     * esta variante se usa para modificar un trabajo seleccionado
     * el constructor se encarga de llenar los campos de la ventana con los datos del trabajo seleccionado
     * @param ventanaPadre
     * @param titulo de la operacion
     * @param t numero entero que representa la posicion del trabajo seleccionado en la lista
     */
    public ControladorAMTrabajo(Dialog ventanaPadre,String titulo, int t){
        this.ventana = new VentanaAMTrabajo(this, ventanaPadre, titulo);
        this.trabajoSeleccionado=t;
        //asigna a los campos de la ventana los datos del trabajo seleccionado
        GestorTrabajos gt = GestorTrabajos.instanciar();
        this.ventana.verListaAreas().setModel(new ModeloListaAreas(gt.verTrabajos().get(t)));
        this.ventana.verListaAlumnos().setModel(new ModeloListaAlumnos(gt.verTrabajos().get(t)));
        this.ventana.verListaTutor().setModel(new ModeloListaProfesores(gt.verTrabajos().get(t),Rol.TUTOR));
        this.ventana.verListaCotutor().setModel(new ModeloListaProfesores(gt.verTrabajos().get(t),Rol.COTUTOR));
        this.ventana.verListaJurado().setModel(new ModeloListaProfesores(gt.verTrabajos().get(t),Rol.JURADO));
        
        this.ventana.verTxtTitulo().setText(gt.verTrabajos().get(t).verTitulo());
        this.ventana.verTxtDuracion().setText(Integer.toString(gt.verTrabajos().get(t).verDuracion()));
        
        GregorianCalendar f;
        f=GregorianCalendar.from(gt.verTrabajos().get(t).verFechaPresentacion().atStartOfDay(ZoneId.systemDefault()));
        this.ventana.verDcFechaPresentacion().setCalendar(f);
        
        f=GregorianCalendar.from(gt.verTrabajos().get(t).verFechaAprobacion().atStartOfDay(ZoneId.systemDefault()));
        this.ventana.verDcFechaAprobacion().setCalendar(f);
        
        
        this.ventana.setLocationRelativeTo(null);
        this.ventana.setVisible(true);
        
   
    }


    /**
     * metodo para llamar a los metodos que guardan los datos del nuevo trabajo o del trabajo modificado
     * @param evt 
     */
    @Override
    public void btnGuardarClic(ActionEvent evt) {
        if(this.ventana.getTitle().equals(IControladorTrabajos.TRABAJO_NUEVO)){
            this.guardarNuevo();
        }
        
        if(this.ventana.getTitle().equals(IControladorTrabajos.TRABAJO_MODIFICAR)){
            this.guardarModificado(this.trabajoSeleccionado);
        }
    }
    
    /**
     * Crea un Trabajo (si los datos son correctos)
     */
    private void guardarNuevo() {
        
        IGestorAlumnosEnTrabajos gaet = GestorAlumnosEnTrabajos.instanciar();
        IGestorRolesEnTrabajos gret = GestorRolesEnTrabajos.instanciar();
        IGestorTrabajos gt = GestorTrabajos.instanciar();
        
        //Obtiene los datos de los campos del titulo, de la duracion y las fechas de presentacion y aprobacion
        String nombre = this.ventana.verTxtTitulo().getText().trim();
        int duracion = Integer.parseInt(this.ventana.stringTxtDuracion().trim());
        LocalDate fechaPresentacion = this.ventana.verFechaPresentacion();
        LocalDate fechaAprobacion = this.ventana.verFechaAprobacion();
        

        
        //Crea una lista de areas con los elementos seleccionados de listaAreas
        List<Area> areas = new ArrayList<>();
        for(Object a: this.ventana.verListaAreas().getSelectedValuesList()){
            areas.add((Area)a);
        }
        //Crea una lista de roles en trabajo con los elementos seleccionados de las listas de profesores para Tutor, Cotutor y Jurado
        if(this.ventana.verListaJurado().getSelectedValuesList().size()>3){
            JOptionPane.showMessageDialog(null, "No se puede seleccionar mas de 3 profesores para el jurado", IControladorTrabajos.TRABAJO_NUEVO, JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<RolEnTrabajo> profesores = new ArrayList<>();
        for(Object a: this.ventana.verListaTutor().getSelectedValuesList()){//añade a la lista el o los tutores seleccionados
            profesores.add(gret.nuevoRolEnTrabajo((Profesor)a,Rol.TUTOR, this.ventana.verFechaPresentacion()));
        }
        
        for(Object a: this.ventana.verListaCotutor().getSelectedValuesList()){//añade a la lista el o los cotutores seleccionados
            profesores.add(gret.nuevoRolEnTrabajo((Profesor)a,Rol.COTUTOR, this.ventana.verFechaPresentacion()));
        }
        

        for(Object a: this.ventana.verListaJurado().getSelectedValuesList()){
            profesores.add(gret.nuevoRolEnTrabajo((Profesor)a,Rol.JURADO, this.ventana.verFechaPresentacion()));
        }
        
        //Crea una lista de alumnos en trabajo con los elementos seleccionados de listaAlumnos
        List<AlumnoEnTrabajo> aet = new ArrayList<>();
        for(Object a: this.ventana.verListaAlumnos().getSelectedValuesList()){
            aet.add(gaet.nuevoAlumnoEnTrabajo((Alumno)a, this.ventana.verFechaPresentacion()));
        }
        
        //Se intenta crear un trabajo nuevo con los datos recibidos de la ventana
        //Si el resultado es exitoso la ventana se cierra, caso contrario se llama al metodo cancelar() y se muestra un mensaje de error
        String resultado = gt.nuevoTrabajo(nombre,duracion,fechaPresentacion,fechaAprobacion,areas,profesores,aet);
        if (!resultado.equals(IGestorTrabajos.EXITO)) {
            gt.cancelar();
            JOptionPane.showMessageDialog(null, resultado, IControladorTrabajos.TRABAJO_NUEVO, JOptionPane.ERROR_MESSAGE);
            
        }
        else
            this.ventana.dispose();
    }
    
    /**
     * Finaliza un trabajo si los datos son correctos
     * Si el resultado es exitoso la ventana se cierra, caso contrario se llama al metodo cancelar() y se muestra un mensaje de error
     * @param trabajoSeleccionado 
     */
    private void guardarModificado(int trabajoSeleccionado){
        GestorTrabajos gt = GestorTrabajos.instanciar();
        String resultado = gt.finalizarTrabajo(gt.verTrabajos().get(trabajoSeleccionado), this.ventana.verFechaFinalizacion());
        if(!resultado.equals(IGestorTrabajos.TRABAJO_FINALIZACION)){
            gt.cancelar();
            JOptionPane.showMessageDialog(null,resultado,IControladorTrabajos.TRABAJO_MODIFICAR, JOptionPane.ERROR_MESSAGE);
        }else{
            this.ventana.dispose();
        }
    }

    // cancela la operacion y cierra la ventana
    @Override
    public void btnCancelarClic(ActionEvent evt) {
        IGestorTrabajos gt = GestorTrabajos.instanciar();
        gt.cancelar();
        this.ventana.dispose();
    }

    @Override
    public void txtDuracionPresionarTecla(KeyEvent evt) {
        char c = evt.getKeyChar();            
        if (!Character.isDigit(c)) { //sólo se aceptan numeros, Enter, Del, Backspace
            switch(c) {
                case KeyEvent.VK_ENTER: 
                    this.guardarNuevo();
                    break;
                case KeyEvent.VK_BACK_SPACE:    
                case KeyEvent.VK_DELETE:
                    break;
                default:
                    evt.consume(); //consume el evento para que no sea procesado por la fuente
            }
        }
    }

    @Override
    public void txtTituloPresionarTecla(KeyEvent evt) {
        char c = evt.getKeyChar();            
        if (!Character.isLetter(c)) { //sólo se aceptan letras, Enter, Del, Backspace y espacio
            switch(c) {
                case KeyEvent.VK_ENTER: 
                    this.guardarNuevo();
                    break;
                case KeyEvent.VK_BACK_SPACE:    
                case KeyEvent.VK_DELETE:
                case KeyEvent.VK_SPACE:
                    break;
                default:
                    evt.consume(); //consume el evento para que no sea procesado por la fuente
            }
        }
    }
    
}
