/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.trabajos.controladores;

import gui.interfaces.IControladorSeminarios;
import gui.interfaces.IControladorAMTrabajo;
import gui.interfaces.IControladorModificarAlumno;
import gui.interfaces.IControladorModificarProfesor;
import gui.interfaces.IControladorTrabajos;
import gui.interfaces.IGestorTrabajos;
import gui.trabajos.modelos.GestorTrabajos;
import gui.trabajos.modelos.ModeloTablaTrabajos;
import gui.trabajos.vistas.VentanaTrabajos;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
import javax.swing.JTable;


/**
 *
 * @author Nico
 */
public class ControladorTrabajos implements IControladorTrabajos{
    private VentanaTrabajos ventana;
    private int trabajoSeleccionado;//variable que representa la posicion de la fila seleccionada que contiene al trabajo
    private String operacion; 
    
    public ControladorTrabajos(Frame ventanaPadre){
        this.ventana=new VentanaTrabajos(this,ventanaPadre);
        this.ventana.setTitle(IControladorTrabajos.TITULO);
        this.ventana.setLocationRelativeTo(null);
        this.ventana.setVisible(true);
    }

    /**
     * Crea una instancia de ControladorAMTrabajo para crear un nuevo trabajo
     * le pasa al constructor la ventana padre y la operacion a realizar
     * @param evt 
     */
    @Override
    public void btnNuevoClic(ActionEvent evt) {
        JTable tablaTrabajos = this.ventana.verTablaTrabajos();
        this.trabajoSeleccionado = tablaTrabajos.getSelectedRow();
        this.operacion = OPERACION_ALTA;
        IControladorAMTrabajo controlador = new ControladorAMTrabajo(this.ventana, IControladorTrabajos.TRABAJO_NUEVO);
        
    }

    /**
     * Crea una instancia de ControladorAMTrabajo para modificar un trabajo
     * le pasa al constructor la ventana padre, la operacion a realizar y la posicion del trabajo a modificar
     * Añade al trabajo una fecha de finalizacion
     * Si el trabajo ya ha sido finalizado, ofrece la opcion de modificar la fecha de finalizacion
     * @param evt 
     */
    @Override
    public void btnModificarClic(ActionEvent evt) {
        IGestorTrabajos gt= GestorTrabajos.instanciar();
        JTable tablaTrabajos=this.ventana.verTablaTrabajos();
        this.trabajoSeleccionado = tablaTrabajos.getSelectedRow();
        if(trabajoSeleccionado==-1){
            JOptionPane.showMessageDialog(null, "No se ha seleccionado un trabajo para modificar.", IControladorTrabajos.TRABAJO_MODIFICAR, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if(gt.buscarTrabajos(null).get(this.trabajoSeleccionado).verFechaFinalizacion()!=null){
            int seleccion=JOptionPane.showConfirmDialog(null,"El trabajo ya ha sido finalizado, desea modificar la fecha de finalizacion?.", IControladorTrabajos.TRABAJO_MODIFICAR, JOptionPane.YES_NO_OPTION);
            if(seleccion==0){
                this.operacion = OPERACION_MODIFICACION;
                IControladorAMTrabajo controlador = new ControladorAMTrabajo(this.ventana, IControladorTrabajos.TRABAJO_MODIFICAR, trabajoSeleccionado);
            }else{
                gt.cancelar();
                return;
            }
        }else{
            this.operacion = OPERACION_MODIFICACION;
            IControladorAMTrabajo controlador = new ControladorAMTrabajo(this.ventana, IControladorTrabajos.TRABAJO_MODIFICAR, trabajoSeleccionado);
        }
        
    }

    /**
     * Si el trabajo seleccionado no tiene seminarios presentados, ofrece la opcion de borrarlo.
     * @param evt 
     */
    @Override
    public void btnBorrarClic(ActionEvent evt) {
        IGestorTrabajos gt= GestorTrabajos.instanciar();
        JTable tablaTrabajos=this.ventana.verTablaTrabajos();
        this.trabajoSeleccionado = tablaTrabajos.getSelectedRow();
        if(trabajoSeleccionado==-1){
            JOptionPane.showMessageDialog(null, "No se ha seleccionado un trabajo para borrar.", IControladorTrabajos.TRABAJO_MODIFICAR, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if(!gt.buscarTrabajos(null).get(trabajoSeleccionado).tieneSeminarios()){
            int seleccion=JOptionPane.showConfirmDialog(null,IControladorTrabajos.CONFIRMACION_TRABAJO, IControladorTrabajos.TITULO, JOptionPane.YES_NO_OPTION);
            if(seleccion==0){
                gt.borrarTrabajo(gt.buscarTrabajos(null).get(trabajoSeleccionado));
                this.operacion= OPERACION_BAJA;
                return;
            }
            
        }else{
                JOptionPane.showMessageDialog(null, IGestorTrabajos.TRABAJO_CON_SEMINARIO, IControladorTrabajos.OPERACION_SEMINARIOS, JOptionPane.ERROR_MESSAGE);
                gt.cancelar();
        }
        
    }

    /**
     * Metodo que crea una instancia de ControladorSeminarios para llamar a la ventana de seminarios
     * pasa como parametro la ventana padre y el trabajo que corresponde a la fila seleccionada de la tabla
     * @param evt 
     */
    @Override
    public void btnSeminariosClic(ActionEvent evt) {
        IGestorTrabajos gt= GestorTrabajos.instanciar();
        JTable tablaTrabajos=this.ventana.verTablaTrabajos();
        this.trabajoSeleccionado=tablaTrabajos.getSelectedRow();
        if(trabajoSeleccionado==-1){
            JOptionPane.showMessageDialog(null, "No se ha seleccionado un trabajo para modificar sus seminarios.", IControladorTrabajos.TRABAJO_MODIFICAR, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        this.operacion=OPERACION_SEMINARIOS;
//        IControladorSeminarios controlador = new ControladorSeminarios(this.ventana, gt.buscarTrabajos(null).get(trabajoSeleccionado)); //esta linea de codigo produce error (hasta q se añada la clase ControladorSeminarios
        
    }

    /**
     * Si el trabajo no ha sido finalizado, abre una ventana para modificar los profesores participantes
     * ofrece la posibilidad de reemplazar un profesor del trabajo por otro que no haya participado en el mismo
     * @param evt 
     */
    @Override
    public void btnModificarProfesorClic(ActionEvent evt) {
        JTable tablaTrabajos = this.ventana.verTablaTrabajos();
        IGestorTrabajos gt= GestorTrabajos.instanciar();
        this.trabajoSeleccionado=tablaTrabajos.getSelectedRow();
        if(trabajoSeleccionado==-1){
            JOptionPane.showMessageDialog(null, "No se ha seleccionado un trabajo para modificar sus profesores.", IControladorModificarProfesor.TRABAJO_MODIFICAR, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if(gt.buscarTrabajos(null).get(this.trabajoSeleccionado).estaFinalizado()){
            JOptionPane.showMessageDialog(null, "No se pueden modificar los profesores de un trabajo finalizado.", IControladorModificarProfesor.TRABAJO_MODIFICAR, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        IControladorModificarProfesor controlador = new ControladorModificarProfesor(this.ventana,this.trabajoSeleccionado);
    }

    /**
     * Si el trabajo no ha sido finalizado, abre una ventana para modificar a los alumnos participantes
     * ofrece la posibilidad de finalizar la participacion de un alumno en el trabajo seleccionado
     * @param evt 
     */
    @Override
    public void btnModificarAlumnoClic(ActionEvent evt) {
        IGestorTrabajos gt= GestorTrabajos.instanciar();
        JTable tablaTrabajos = this.ventana.verTablaTrabajos();
        this.trabajoSeleccionado=tablaTrabajos.getSelectedRow();
        if(this.trabajoSeleccionado==-1){
            JOptionPane.showMessageDialog(null, "No se ha seleccionado un trabajo para modificar sus alumnos.", IControladorModificarAlumno.TRABAJO_MODIFICAR, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if(gt.buscarTrabajos(null).get(this.trabajoSeleccionado).estaFinalizado()){
            JOptionPane.showMessageDialog(null, "No se pueden modificar los alumnos de un trabajo finalizado.", IControladorModificarProfesor.TRABAJO_MODIFICAR, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        IControladorModificarAlumno controlador = new ControladorModificarAlumno(this.ventana, this.trabajoSeleccionado);
    }

    /**
     * Cierra la ventana
     * @param evt 
     */
    @Override
    public void btnVolverClic(ActionEvent evt) {
        this.ventana.dispose();
    }

    /**
     * Recibe un evento de tipo WindowEvent y es llamado por la ventana de trabajos
     * Si la tabla trabajos no tiene modelo asignado la inicializa
     * Si la tabla ya tiene un modelo asignado, llama al metodo para seleccionar un trabajo de la tabla (y refrescar el contenido de la misma)
     * @param evt 
     */
    @Override
    public void ventanaGanaFoco(WindowEvent evt) {
        JTable tablaTrabajos = this.ventana.verTablaTrabajos();
        if(tablaTrabajos.getModel() instanceof ModeloTablaTrabajos){
            this.seleccionarTrabajoEnTabla(tablaTrabajos);
        }else{
            this.inicializarTablaTrabajos(tablaTrabajos);
        }
        
        this.operacion=IControladorTrabajos.OPERACION_NINGUNA;
    }
    
    /**
     * Asigna a tablaTrabajos el modelo a utilizar y selecciona un trabajo de la misma
     * @param tablaTrabajos 
     */
    private void inicializarTablaTrabajos(JTable tablaTrabajos) {
        ModeloTablaTrabajos mtt = new ModeloTablaTrabajos(null);
        tablaTrabajos.setModel(mtt);

        if (mtt.getRowCount() > 0) {
                     
            tablaTrabajos.setRowSelectionInterval(this.trabajoSeleccionado, this.trabajoSeleccionado);                           
        }
        else
            this.trabajoSeleccionado = -1;//si no hay filas, no se selecciona ninguna
    }
    
    /**
     * Para los variados casos en que la VentanaTrabajos gana foco, este metodo se encarga de seleccionar un trabajo de la tablaTrabajos
     * @param tablaTrabajos 
     */
    private void seleccionarTrabajoEnTabla(JTable tablaTrabajos) {
        IGestorTrabajos GT = GestorTrabajos.instanciar();
        ModeloTablaTrabajos mtt = (ModeloTablaTrabajos)tablaTrabajos.getModel();
        if (this.operacion.equals(OPERACION_ALTA)) { //se accede a nuevo trabajo
            if (GT.verUltimoTrabajo() == -1) {  //se cancela la operacion
                if (this.trabajoSeleccionado != -1) 
                    tablaTrabajos.setRowSelectionInterval(this.trabajoSeleccionado, this.trabajoSeleccionado);               
            }
            else {  //se completa la operacion
                mtt.fireTableDataChanged(); //se refresca la tabla
                tablaTrabajos.setRowSelectionInterval(GT.verUltimoTrabajo(), GT.verUltimoTrabajo()); 
            }
        }
        else if (this.operacion.equals(OPERACION_BAJA)) {//se accede a borrar trabajo
            if (GT.verUltimoTrabajo() == -1)  //se cancela la operacion
                tablaTrabajos.setRowSelectionInterval(this.trabajoSeleccionado, this.trabajoSeleccionado);               
            else {  //se completa la operacion
                mtt.fireTableDataChanged(); 
                if (mtt.getRowCount() > 0) {
                    this.trabajoSeleccionado = 0;
                    tablaTrabajos.setRowSelectionInterval(this.trabajoSeleccionado, this.trabajoSeleccionado);                           
                }//si no quedan trabajos en la tabla
                else
                    this.trabajoSeleccionado = -1;
            }
        } else if(this.operacion.equals(OPERACION_MODIFICACION)){//se accede a modificar trabajo
            if (GT.verUltimoTrabajo() == -1) {  //se cancela la operacion
                if (this.trabajoSeleccionado != -1) 
                    tablaTrabajos.setRowSelectionInterval(this.trabajoSeleccionado, this.trabajoSeleccionado);               
            }else{ // se completa la operacion
                mtt.fireTableDataChanged();
                tablaTrabajos.setRowSelectionInterval(this.trabajoSeleccionado, this.trabajoSeleccionado); 
            }
        } else if(this.operacion.equals(OPERACION_SEMINARIOS)){ // se accede a la ventana seminarios
            mtt.fireTableDataChanged(); //si se completa la operacion, o se cierra la ventana seminarios sin hacer ningun cambio, se actualiza la tabla y se selecciona el trabajo que estaba ya seleccionado
            tablaTrabajos.setRowSelectionInterval(this.trabajoSeleccionado, this.trabajoSeleccionado);
            
        }
        
    }
}
