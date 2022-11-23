/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.trabajos.modelos;

import gui.interfaces.IGestorTrabajos;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Nico
 */
public class ModeloTablaTrabajos extends AbstractTableModel{
    
    private List<Trabajo> trabajos = new ArrayList<>();
    private List<String> nombresColumnas = new ArrayList<>();
    
    public static final String COLUMNA_TITULO = "Titulo";
    public static final String COLUMNA_DURACION = "Duracion";
    public static final String COLUMNA_FECHAPRESENTACION = "Presentacion";
    public static final String COLUMNA_FECHAAPROBACION = "Aprobacion";
    public static final String COLUMNA_FECHAFINALIZACION = "Finalizacion";
    public static final String COLUMNA_SEMINARIOS = "Seminarios";
    
    public ModeloTablaTrabajos(String titulo) {
        this.nombresColumnas.add(COLUMNA_TITULO);
        this.nombresColumnas.add(COLUMNA_DURACION);
        this.nombresColumnas.add(COLUMNA_FECHAPRESENTACION);
        this.nombresColumnas.add(COLUMNA_FECHAAPROBACION);
        this.nombresColumnas.add(COLUMNA_FECHAFINALIZACION);
        this.nombresColumnas.add(COLUMNA_SEMINARIOS);
        
        IGestorTrabajos GT = GestorTrabajos.instanciar();        
        this.trabajos = GT.buscarTrabajos(titulo);
        Collections.sort(trabajos);
    } 
    
    @Override
    public Object getValueAt(int fila, int columna) {
        
        Trabajo t = this.trabajos.get(fila);
        String patron = "dd/MM/yyyy";
        switch(columna){
            case 0: return t.verTitulo();
            
            case 1: return t.verDuracion();
            
            case 2: return t.verFechaPresentacion().format(DateTimeFormatter.ofPattern(patron));
            
            case 3: if(t.verFechaAprobacion()==null){//
                        return "-";
                    }
                    return t.verFechaAprobacion().format(DateTimeFormatter.ofPattern(patron));
                    
            case 4: if(t.verFechaFinalizacion()==null){
                        return "-";
                    }
                    return t.verFechaFinalizacion().format(DateTimeFormatter.ofPattern(patron));
                    
            case 5: if(t.tieneSeminarios()==false){
                        return "-";
                    }else{
                        return t.verSeminarios().get(t.verSeminarios().size()).verFechaExposicion().format(DateTimeFormatter.ofPattern(patron));
                    }  
            default: return null;
        }
    }


    
    @Override
    public int getColumnCount() {
        return this.nombresColumnas.size();
    }

    
    @Override
    public int getRowCount() {
        return this.trabajos.size();
    }

    
    @Override
    public String getColumnName(int columna) {
        return this.nombresColumnas.get(columna);
    }
    


    
    public Trabajo obtenerTrabajo (int fila) {
        return this.trabajos.get(fila);
    }  
    
}
