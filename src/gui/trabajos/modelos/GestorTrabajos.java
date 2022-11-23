/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.trabajos.modelos;

import gui.areas.modelos.Area;
import gui.areas.modelos.GestorAreas;
import gui.interfaces.IGestorTrabajos;
import gui.personas.modelos.Alumno;
import gui.personas.modelos.GestorPersonas;
import gui.personas.modelos.Profesor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestorTrabajos implements IGestorTrabajos {
    private final String NOMBRE_ARCHIVO = "./Trabajos.txt";
    //nombre del archivo con los trabajos    
    private final char SEPARADOR = ';'; 
    //caracter usado como separador 
    private final String VALORES_NULOS = "-";
    //cadena usada para los valores nulos (fecha de exposición y/o cotutor)
    
    private List<Trabajo> trabajos = new ArrayList<>();    
    private static GestorTrabajos gestor;
    
    private int ultimoTrabajo;
    //sirve para manejar la tabla tablaTrabajos
    
    /**
     * Constructor
    */                                            
    private GestorTrabajos() {   
        this.leerArchivo();
    }
    
    /**
     * Método estático que permite crear una única instancia de GestorTrabajos
     * @return GestorTrabajos
    */                                                            
    public static GestorTrabajos instanciar() {
        if (gestor == null) 
            gestor = new GestorTrabajos();            
        return gestor;
    }     
    
    public List<Trabajo> verTrabajos() {
        Collections.sort(trabajos);
        return trabajos;
    }
    
    /**
     * Crea un nuevo trabajo
     * La fecha de aprobación debe ser igual o posterior a la de presentación
     * El tutor y el cotutor (en caso que hubiera) deben ser distintos
     * El jurado debe estar formado por 3 profesores distintos
     * El tutor no puede pertenecer al jurado
     * El cotutor (si hubiera) tampoco puede pertenecer al jurado
     * Por lo menos debe participar un alumno, y el mismo no debe estar actualmente en otro trabajo (con fecha de finalización no nula)
     * Si hay más de un alumno, deben ser distintos y ninguno debe estar en otro trabajo actualmente (con fecha de finalización no nula)
     * @param titulo título del trabajo
     * @param duracion duración del trabajo (en meses)
     * @param fechaPresentacion fecha en que se presenta el trabajo a la comisión académica para tratar su aprobación
     * @param fechaAprobacion fecha en que la comisión académica aprueba la propuesta de trabajo
     * @param areas áreas del trabajo
     * @param profesores lista con los profesores que actúan como tutor, cotutor (si hubiera) y jurado
     * @param aet alumnos que realizan el trabajo
     * @return String  - cadena con el resultado de la operación (ERROR_TITULO_DURACION | ERROR_AREAS | ERROR_FECHAS | ERROR_TUTOR_COTUTOR | ERROR_JURADO | ERROR_ALUMNOS | ESCRITURA_ERROR | EXITO)
    */                                                                    
    @Override
    public String nuevoTrabajo(String titulo, int duracion, LocalDate fechaPresentacion, LocalDate fechaAprobacion, List<Area> areas, List<RolEnTrabajo> profesores, List<AlumnoEnTrabajo> aet) {
        //control titulo y duracion
        if(titulo.isEmpty() || titulo==null || duracion<=0){
            return IGestorTrabajos.ERROR_TITULO_DURACION;
        }else{
            for(Trabajo t: this.trabajos){
                if(t.verTitulo().trim().equalsIgnoreCase(titulo.trim())){
                    return ERROR_TITULO_DURACION;
                }
            }
        }
        
        //controles fechas
        if(fechaPresentacion==null || fechaAprobacion==null){
            return IGestorTrabajos.ERROR_FECHAS;
        }else{
            if(fechaPresentacion.isAfter(LocalDate.MAX) || fechaPresentacion.isBefore(LocalDate.MIN)){
                return IGestorTrabajos.ERROR_FECHAS;
            }
        }
        
        if(fechaAprobacion!=null){
            if(fechaAprobacion.isBefore(fechaPresentacion) || fechaAprobacion.isAfter(LocalDate.MAX)){
                return IGestorTrabajos.ERROR_FECHAS;
            }
        }
        
        //control de lista de profesores con rol y alumnos
        
        
        //control tutor/cotutor
        if(profesores.isEmpty()){
            return IGestorTrabajos.ERROR_TUTOR_COTUTOR;
        }else{ //busca al cotutor si es q hay, y lo compara con el tutor
            for(RolEnTrabajo r: profesores){
                if(r.verRol()==Rol.COTUTOR){
                    for(RolEnTrabajo r2: profesores){
                        if(r2.verRol()==Rol.TUTOR){
                            if(r.equals(r2)){
                                return IGestorTrabajos.ERROR_TUTOR_COTUTOR;
                            }
                        }
                    }
                    
                }
            }
        }
        
        //control areas
        if(areas.isEmpty()){
            return IGestorTrabajos.ERROR_AREAS;
        }
        
        //control jurado con fecha de aprobacion
        if(fechaAprobacion!=null){
            int i=0;
            for(RolEnTrabajo r: profesores){
                if(r.verRol()==Rol.JURADO){//recorre la lista de profesores para encontrar un jurado
                    i++;
                }
            }
            if(i==0){ //si no tiene jurado, devuelve error
                return IGestorTrabajos.ERROR_JURADO;
            }
        }
        //control jurado con o sin fecha de aprobacion
        for(RolEnTrabajo r: profesores){
            if(r.verRol()==Rol.JURADO){
                for(RolEnTrabajo r2: profesores){
                    if(!r.equals(r2) && r2.verRol()==Rol.JURADO){//recorre la lista de profesores para encontrar un jurado que sea distinto a el primero (r)
                        if(r.verProfesor().equals(r2.verProfesor())){//compara los dos jurados encontrados para verificar si es q son la misma persona
                            return IGestorTrabajos.ERROR_JURADO;
                        }
                    }
                }
            }
        }
        
        //control jurado/tutor/cotutor
        
        for(RolEnTrabajo r: profesores){
            if(r.verRol()==Rol.JURADO){
                for(RolEnTrabajo r2: profesores){
                    if(r.equals(r2) && (r2.verRol()==Rol.TUTOR || r2.verRol()==Rol.COTUTOR)){
                        return IGestorTrabajos.ERROR_JURADO;
                    }
                }
            }
        }
        
        //control alumnos
        if(aet.isEmpty()){
            return IGestorTrabajos.ERROR_ALUMNOS;
        }else{
            for(AlumnoEnTrabajo a: aet){
                for(AlumnoEnTrabajo b: aet){
                    if(!a.equals(b) && a.verAlumno().equals(b.verAlumno())){
                        return IGestorTrabajos.ERROR_ALUMNOS;
                    }
                }
                
            }
        }
        //control adicional alumnos
        for(Trabajo t: this.trabajos){
           for(AlumnoEnTrabajo a: aet){
               if(t.verAlumnos().contains(a)){
                   return IGestorTrabajos.ERROR_ALUMNOS;
               }
           }
        }
        
        Trabajo T = new Trabajo(titulo,duracion,areas,fechaPresentacion,fechaAprobacion,profesores,aet);
        GestorRolesEnTrabajos gret =GestorRolesEnTrabajos.instanciar();
        GestorAlumnosEnTrabajos gaet = GestorAlumnosEnTrabajos.instanciar();
        GestorTrabajos GT = GestorTrabajos.instanciar();
        for(RolEnTrabajo ret: T.verProfesoresConRoles()){
            gret.verListaRolesEnTrabajos().add(ret);
        }
        for(AlumnoEnTrabajo a: T.verAlumnos()){
            gaet.verListaAlumnosEnTrabajos().add(a);
        }
        GT.verTrabajos().add(T);
        this.ultimoTrabajo = this.verTrabajos().indexOf(T);
           
        String resultado=GT.escribirArchivo();
        if(resultado == ESCRITURA_OK){
            
            return IGestorTrabajos.EXITO;
        }else{
            return IGestorTrabajos.ESCRITURA_ERROR;
        }
    }   
       
    /**
     * Busca si existe un trabajo con el título especificado (total o parcialmente)
     * Si no se especifica un título, devuelve todos los trabajos
     * Obtiene todos los trabajos creados, ordenados según el criterio especificado
     * Este método es usado por la clase ModeloTablaTrabajos
     * @param titulo título del trabajo
     * @return List<Trabajo>  - lista con los trabajos ordenados según el criterio especificado
     */
    @Override
    public List<Trabajo> buscarTrabajos(String titulo) {
        List<Trabajo> trabajosBuscados = new ArrayList<>();
        if (titulo != null) {
            for(Trabajo trabajo : this.trabajos) {
                if (trabajo.verTitulo().toLowerCase().contains(titulo.toLowerCase()))
                    trabajosBuscados.add(trabajo);
            }
            Collections.sort(trabajosBuscados);
            return trabajosBuscados;
        }
        else
            Collections.sort(trabajos);
            return this.trabajos;
    } 
    
    /**
     * Busca si existe un trabajo que coincida con el título especificado
     * Si no hay un trabajo con el título especicado, devuelve null
     * @param titulo título del trabajo a buscar
     * @return Trabajo  - objeto Trabajo cuyo título coincida con el especificado, o null
     */
    @Override
    public Trabajo dameTrabajo(String titulo) {
        for(Trabajo t: this.trabajos){
            if(t.verTitulo().equalsIgnoreCase(titulo.trim())){
                return t;
            }
        }
        return null;
    }    
    
    /**
     * Busca si hay al menos un trabajo con el profesor especificado
     * A este método lo usa la clase GestorPersonas
     * @param profesor profesor a buscar
     * @return boolean  - true si hay al menos un trabajo con el profesor especificado
     */
    @Override
    public boolean hayTrabajosConEsteProfesor(Profesor profesor) {
        for(Trabajo t: this.trabajos){
            for(RolEnTrabajo rt: t.verProfesoresConRoles()){
                if(rt.verProfesor().equals(profesor)){
                    return true;
                }
            }
        }
        return false;
    }   
    
    /**
     * Busca si hay al menos un trabajo con el alumno especificado
     * A este método lo usa la clase GestorPersonas
     * @param alumno alumno a buscar
     * @return boolean  - true si hay al menos un trabajo con el alumno especificado
     */
    @Override
    public boolean hayTrabajosConEsteAlumno(Alumno alumno) {
        for(Trabajo t: this.trabajos){
            for(AlumnoEnTrabajo at: t.verAlumnosActuales()){
                if(at.verAlumno().equals(alumno)){
                    return true;
                }
            }
        }
        return false;
    }   
    
    /**
     * Finaliza un trabajo asignándole su fecha de exposición, con lo cual termina el trabajo
     * Cuando termina un trabajo, también termina la participación de todos los profesores (tutor, cotutor y jurado) y alumnos en el mismo
     * @param trabajo trabajo a finalizar
     * @param fechaFinalizacion fecha en que los alumnos exponen el trabajo
     * @return String  - cadena con el resultado de la operación (ERROR_FECHA_EXPOSICION | ESCRITURA_ERROR | EXITO)
    */                                                                    
    @Override
    public String finalizarTrabajo(Trabajo trabajo, LocalDate fechaFinalizacion) {
        GestorTrabajos GT = GestorTrabajos.instanciar();
       
        if(fechaFinalizacion == null){
           return ERROR_FECHA_EXPOSICION;
        }
       
        if(fechaFinalizacion.isBefore(GT.dameTrabajo(trabajo.verTitulo()).verFechaAprobacion())){
           return ERROR_FECHA_EXPOSICION;
        }
       
        GT.dameTrabajo(trabajo.verTitulo()).asignarFechaFinalizacion(fechaFinalizacion);
       
        for(AlumnoEnTrabajo aet: GT.dameTrabajo(trabajo.verTitulo()).verAlumnos()){
            aet.asignarFechaHasta(fechaFinalizacion);
            aet.asignarRazon(TRABAJO_FINALIZACION);
        }
        
        for(RolEnTrabajo ret: GT.dameTrabajo(trabajo.verTitulo()).verProfesoresConRoles()){
            ret.asignarFechaHasta(fechaFinalizacion);
            ret.asignarRazon(TRABAJO_FINALIZACION);
        }
        this.ultimoTrabajo=GT.verTrabajos().indexOf(trabajo);
        String resultado=GT.escribirArchivo();
        if(resultado==ESCRITURA_OK){
           return TRABAJO_FINALIZACION;
        }
        return ESCRITURA_ERROR;
    }    
    
    /**
     * Borra un trabajo siempre y cuando no tenga seminarios presentados
     * @param trabajo trabajo a borrar
     * @return String  - cadena con el resultado de la operación (TRABAJO_CON_SEMINARIO | ESCRITURA_ERROR | EXITO)
     */
    @Override
    public String borrarTrabajo(Trabajo trabajo) {
        GestorTrabajos GT = GestorTrabajos.instanciar();
        if(GT.verTrabajos().isEmpty()){
            return "Lista de trabajos vacia.";
        }
        if(!GT.dameTrabajo(trabajo.verTitulo()).tieneSeminarios()){
            GT.verTrabajos().remove(trabajo);
            GT.ultimoTrabajo=0;
            String resultado = GT.escribirArchivo();
            if(resultado == ESCRITURA_OK){
                return EXITO;
            }else{
                return ESCRITURA_ERROR;
            }
            
        }else{
            return TRABAJO_CON_SEMINARIO;
        }
    }

    /**
     * Reemplaza un profesor del trabajo. 
     * Al profesor que se reemplaza se le asigna su fecha de finalización y razón por la que finaliza su tarea
     * El nuevo profesor tiene el mismo rol del profesor que reemplaza, y comienza su tarea en la fecha en que finaliza el profesor que se reemplaza
     * El nuevo profesor no puede ocupar 
     * @param trabajo trabajo al cual se reemplazará un profesor
     * @param profesorReemplazado profesor que se reemplaza
     * @param fechaHasta fecha de finalización del profesor que se reemplaza (debe ser posterior a la fecha de inicio)
     * @param razon razón por la que se reemplaza al profesor
     * @param nuevoProfesor nuevo profesor
     * @return String  - cadena con el resultado de la operación (TRABAJO_INEXISTENTE | TRABAJO_REEMPLAZAR_PROFESOR_ERROR | TRABAJO_REEMPLAZAR_PROFESOR_DUPLICADO | TRABAJO_REEMPLAZAR_PROFESOR_INEXISTENTE | TRABAJO_REEMPLAZAR_PROFESOR_ERROR | ESCRITURA_ERROR | EXITO)
     */
    @Override
    public String reemplazarProfesor(Trabajo trabajo, Profesor profesorReemplazado, LocalDate fechaHasta, String razon, Profesor nuevoProfesor) {
        GestorTrabajos GT= GestorTrabajos.instanciar();
        GestorRolesEnTrabajos GRET= GestorRolesEnTrabajos.instanciar();
        
            if(razon.isEmpty()|| razon==null){
                return TRABAJO_REEMPLAZAR_PROFESOR_ERROR;
            }
            if(profesorReemplazado.equals(nuevoProfesor)){
                return TRABAJO_REEMPLAZAR_PROFESOR_DUPLICADO;
            }
            if(nuevoProfesor==null || fechaHasta==null){
                return TRABAJO_REEMPLAZAR_PROFESOR_ERROR;
            }
            
            for(RolEnTrabajo ret: trabajo.verProfesoresConRoles()){
                if(ret.verProfesor().equals(nuevoProfesor)){
                    return TRABAJO_REEMPLAZAR_PROFESOR_DUPLICADO;
                }
            }
            
            if(GT.dameTrabajo(trabajo.verTitulo())!= null ){
                for(RolEnTrabajo ret: trabajo.verProfesoresConRoles()){
                    
                    if(ret.verProfesor().equals(profesorReemplazado)){
                        if(fechaHasta.isBefore(trabajo.verFechaPresentacion())){
                            return TRABAJO_REEMPLAZAR_PROFESOR_ERROR;
                        }
                        ret.asignarRazon(razon);
                        ret.asignarFechaHasta(fechaHasta);
                        
                        
                        trabajo.verProfesoresConRoles().add(GRET.nuevoRolEnTrabajo(nuevoProfesor, ret.verRol(), fechaHasta));
                        String resultado= GT.escribirArchivo();
                        if(resultado==ESCRITURA_OK){
                            return EXITO;
                        }
                        return ESCRITURA_ERROR;
                    }
                }
                return TRABAJO_REEMPLAZAR_PROFESOR_INEXISTENTE;
            }
            return TRABAJO_INEXISTENTE;
    }

    /**
     * Permite que un alumno pueda terminar su participación en el trabajo
     * @param trabajo trabajo al cual se finalizará la participación del alumno
     * @param alumno alumno que finaliza su participación en el trabajo
     * @param fechaHasta fecha de finalización del alumno en el trabajo (debe ser posterior a la fecha de inicio)
     * @param razon razón por la que el alumno finaliza su participación en el trabajo
     * @return String  - cadena con el resultado de la operación (TRABAJO_INEXISTENTE | TRABAJO_FINALIZAR_ALUMNO_ERROR | TRABAJO_FINALIZAR_ALUMNO_INEXISTENTE | TRABAJO_FINALIZAR_ALUMNO_ERROR | ESCRITURA_ERROR | EXITO)
     */
    @Override
    public String finalizarAlumno(Trabajo trabajo, Alumno alumno, LocalDate fechaHasta, String razon) {
        GestorTrabajos GT= GestorTrabajos.instanciar();
        
        
        if(razon.isEmpty() || razon==null){
            return TRABAJO_FINALIZAR_ALUMNO_ERROR;
        }
        
        if(GT.dameTrabajo(trabajo.verTitulo())!=null){
            for(AlumnoEnTrabajo aet: GT.dameTrabajo(trabajo.verTitulo()).verAlumnos()){
//                if(aet.getUnAlumno().equals(alumno) && GT.dameTrabajo(trabajo.verTitulo()).getListaAET().size()==1){          //por si hace falta restringir el abandono del ultimo alumno del trabajo
//                    return "No puede abandonar el unico alumno del trabajo.";
//                }

                
                if(aet.verAlumno().equals(alumno)){
                    if(aet.verFechaHasta()!=null){
                        return IGestorTrabajos.TRABAJO_FINALIZAR_ALUMNO_INEXISTENTE;
                    }
                    
                    if(fechaHasta.isBefore(GT.dameTrabajo(trabajo.verTitulo()).verFechaPresentacion())){
                        return TRABAJO_FINALIZAR_ALUMNO_ERROR;
                    }
                    aet.asignarRazon(razon);
                    aet.asignarFechaHasta(fechaHasta);
                    String resultado= GT.escribirArchivo();
                    if(resultado == ESCRITURA_OK){
                        return EXITO;
                    }
                    return ESCRITURA_ERROR;
                }
            }
            return TRABAJO_FINALIZAR_ALUMNO_INEXISTENTE;
        }
        return TRABAJO_INEXISTENTE;
    }                    
            
    /**
     * Busca si hay al menos un trabajo con el área especificada
     * A este método lo usa la clase GestorAreas
     * @param area área a buscar
     * @return boolean  - true si hay al menos un trabajo con el área especificada
     */
    @Override
    public boolean hayTrabajosConEsteArea(Area area) {
        for(Trabajo t: this.trabajos){
            if(t.verAreas().contains(area)){
                return true;
            }
        }
        return false;
    }

    /**
     * Devuelve la posición del último trabajo agregado/modificado
     * Sirve para manejar la tabla tablaTrabajos
     * Si cuando se agrega/modifica un trabajo se cancela la operación, devuelve - 1
     * Cada vez que se agrega/modifica un trabajo, este valor toma la posición del trabajo agregado/modificado en el ArrayList
     * @return int  - posición del último trabajo agregado/modificado
     */    
    @Override
    public int verUltimoTrabajo() {
        return this.ultimoTrabajo;
    }
        
    /**
     * Asigna en -1 la variable que controla el último trabajo agregado/modificado
     * Sirve para manejar la tabla tablaTrabajos
     */
    @Override
    public void cancelar() {
        this.ultimoTrabajo = -1;
    }
                    
    public String escribirArchivo(){
        File f = new File(NOMBRE_ARCHIVO);
        String patron = "dd/MM/yyyy";
        String fechaExposicion;
        
        try{
            if(!f.exists()){
                f.createNewFile();
            }
        }
        catch(IOException ioe){
            return ARCHIVO_INEXISTENTE;
        }
        
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(f))){
            for(Trabajo trabajo : this.verTrabajos()) {
                LocalDate fPresentacion = trabajo.verFechaPresentacion();
                String fechaPresentacion = fPresentacion.format(DateTimeFormatter.ofPattern(patron)); 
                
                LocalDate fAprobacion = trabajo.verFechaAprobacion();
                String fechaAprobacion = fAprobacion.format(DateTimeFormatter.ofPattern(patron)); 
                
                LocalDate fExposicion = trabajo.verFechaFinalizacion();
                if (fExposicion != null)
                    fechaExposicion = fExposicion.format(DateTimeFormatter.ofPattern(patron)); 
                else
                    fechaExposicion = VALORES_NULOS;
                
                List<RolEnTrabajo> ret = trabajo.verProfesoresConRoles();
                List<AlumnoEnTrabajo> aet = trabajo.verAlumnos();
                
                String cadena = trabajo.verTitulo();
                cadena += SEPARADOR + Integer.toString(trabajo.verDuracion()) + SEPARADOR;
                cadena += fechaPresentacion + SEPARADOR;
                cadena += fechaAprobacion + SEPARADOR;
                cadena += fechaExposicion;                
                
                int cantCareas = trabajo.verAreas().size();
                int cantTutores = trabajo.cantidadProfesoresConRol(Rol.TUTOR);
                int cantCoTutores = trabajo.cantidadProfesoresConRol(Rol.COTUTOR);
                int cantJurados = trabajo.cantidadProfesoresConRol(Rol.JURADO);
                int cantAlumnos = trabajo.cantidadAlumnos();
                cadena += SEPARADOR + Integer.toString(cantCareas);
                cadena += SEPARADOR + Integer.toString(cantTutores);
                cadena += SEPARADOR + Integer.toString(cantCoTutores);
                cadena += SEPARADOR + Integer.toString(cantJurados);
                cadena += SEPARADOR + Integer.toString(cantAlumnos);
                
                //todas las áreas del trabajo
                for(Area area : trabajo.verAreas())
                    cadena += SEPARADOR + area.toString();
                
                //todos los docentes que participan en el trabajo
                for(RolEnTrabajo rolEnTrabajo : ret) {
                    cadena += SEPARADOR + Integer.toString(rolEnTrabajo.verProfesor().verDNI()) + SEPARADOR;
                    
                    LocalDate fDesde = rolEnTrabajo.verFechaDesde();
                    String fechaDesde = fDesde.format(DateTimeFormatter.ofPattern(patron)); 
                    cadena += fechaDesde + SEPARADOR;
                    
                    LocalDate fHasta = rolEnTrabajo.verFechaHasta();
                    String fechaHasta;
                    if (fHasta != null)
                        fechaHasta = fHasta.format(DateTimeFormatter.ofPattern(patron)); 
                    else
                        fechaHasta = VALORES_NULOS;
                    cadena += fechaHasta + SEPARADOR;
                    
                    String razon;
                    if(rolEnTrabajo.verRazon() != null)
                        razon = rolEnTrabajo.verRazon();
                    else
                        razon = VALORES_NULOS;
                    cadena += razon;
                }
                
                //todos los alumnos que participan en el trabajo
                for(int i = 0; i < aet.size(); i++) {
                    AlumnoEnTrabajo alumnoEnTrabajo = aet.get(i);
                    
                    cadena += SEPARADOR + alumnoEnTrabajo.verAlumno().verCX() + SEPARADOR;
                    
                    LocalDate fDesde = alumnoEnTrabajo.verFechaDesde();
                    String fechaDesde = fDesde.format(DateTimeFormatter.ofPattern(patron));
                    cadena += fechaDesde + SEPARADOR;
                    
                    LocalDate fHasta = alumnoEnTrabajo.verFechaHasta();
                    String fechaHasta;
                    if (fHasta != null)
                        fechaHasta = fHasta.format(DateTimeFormatter.ofPattern(patron)); 
                    else
                        fechaHasta = VALORES_NULOS;
                    cadena += fechaHasta + SEPARADOR;
                    
                    String razon;
                    if(alumnoEnTrabajo.verRazon() != null)
                        razon = alumnoEnTrabajo.verRazon();
                    else
                        razon = VALORES_NULOS;
                    cadena += razon;
                }
                
                bw.write(cadena);
                bw.newLine();
            }
            return ESCRITURA_OK;
        }
        catch(IOException ioe){
            return ESCRITURA_ERROR;
        }
    }
    
    private String leerArchivo() {
        File file = new File(NOMBRE_ARCHIVO);
        if (file.exists()) {
            GestorAreas ga = GestorAreas.instanciar();
            GestorPersonas gp = GestorPersonas.instanciar();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String cadena;
                while((cadena = br.readLine()) != null) {
                    String[] vector = cadena.split(Character.toString(SEPARADOR));
                    String titulo = vector[0];
                    int duracion = Integer.parseInt(vector[1]);
                                       
                    String fPresentacion = vector[2];
                    LocalDate fechaPresentacion = this.transformarCadenaAFecha(fPresentacion);
                    
                    String fAprobacion = vector[3];
                    LocalDate fechaAprobacion = this.transformarCadenaAFecha(fAprobacion);
                    
                    String fExposicion = vector[4];
                    LocalDate fechaExposicion = null;
                    if (!fExposicion.equals(VALORES_NULOS))
                        fechaExposicion = this.transformarCadenaAFecha(fExposicion);
                    
                    int cantAreas = Integer.parseInt(vector[5]);
                    int cantTutores = Integer.parseInt(vector[6]);
                    int cantCoTutores = Integer.parseInt(vector[7]);
                    int cantJurados = Integer.parseInt(vector[8]);
                    int cantAlumnos = Integer.parseInt(vector[9]);

                    int primerArea = 10; //posición donde está la primer área
                    int ultimaArea = primerArea + cantAreas - 1; //posición donde está la última área                    
                    int dniPrimerTutor = ultimaArea + 1;  //posición donde está el dni del primer tutor
                    int razonUltimoTutor = (dniPrimerTutor + cantTutores * 4) - 1; //posición donde está la razón del último tutor
                    int dniPrimerCoTutor = razonUltimoTutor + 1; //posición donde está el dni del primer cotutor
                    int razonUltimoCoTutor = (dniPrimerCoTutor + cantCoTutores * 4) - 1; //posición donde está la razón del último cotutor
                    int dniPrimerJurado = razonUltimoCoTutor + 1; //posición donde está el dni del primer jurado
                    int razonUltimoJurado = (dniPrimerJurado + cantJurados * 4) - 1; //posición donde está la razón del último jurado
                    int cxPrimerAlumno = razonUltimoJurado + 1; //posición donde está el cx del primer alumno
                    int razonUltimoAlumno = (cxPrimerAlumno + cantAlumnos * 4) - 1; //posición donde está la razón del último alumno

                    List<Area> areas = new ArrayList<>();
                    for(int i = primerArea; i <= ultimaArea; ) {
                        String nombreArea = vector[i++];
                        areas.add(ga.dameArea(nombreArea));
                    }
                    
                    List<RolEnTrabajo> ret = new ArrayList<>();                    
                    for(int i = dniPrimerTutor; i <= razonUltimoTutor; ) {  //se leen los tutores                  
                        int dniTutor = Integer.parseInt(vector[i++]);
                        Profesor tutor = gp.dameProfesor(dniTutor);
                        
                        String fDesde = vector[i++];
                        LocalDate fechaDesde = this.transformarCadenaAFecha(fDesde);
                        
                        String fHasta = vector[i++];
                        LocalDate fechaHasta = null;
                        if (!fHasta.equals(VALORES_NULOS))
                            fechaHasta = this.transformarCadenaAFecha(fHasta);
                        
                        String razon = vector[i++];
                        ret.add(new RolEnTrabajo(tutor, Rol.TUTOR, fechaDesde, fechaHasta, razon));
                    }
                    
                    for(int i = dniPrimerCoTutor; i <= razonUltimoCoTutor; ) {  //se leen los cotutores                  
                        int dniCoTutor = Integer.parseInt(vector[i++]);
                        Profesor cotutor = gp.dameProfesor(dniCoTutor);
                        
                        String fDesde = vector[i++];
                        LocalDate fechaDesde = this.transformarCadenaAFecha(fDesde);
                        
                        String fHasta = vector[i++];
                        LocalDate fechaHasta = null;
                        if (!fHasta.equals(VALORES_NULOS))
                            fechaHasta = this.transformarCadenaAFecha(fHasta);
                        
                        String razon = vector[i++];
                        ret.add(new RolEnTrabajo(cotutor, Rol.COTUTOR, fechaDesde, fechaHasta, razon));
                    }                    
                    
                    for(int i = dniPrimerJurado; i <= razonUltimoJurado; ) {  //se leen los jurados
                        int dniJurado = Integer.parseInt(vector[i++]);
                        Profesor jurado = gp.dameProfesor(dniJurado);
                        
                        String fDesde = vector[i++];
                        LocalDate fechaDesde = this.transformarCadenaAFecha(fDesde);
                        
                        String fHasta = vector[i++];
                        LocalDate fechaHasta = null;
                        if (!fHasta.equals(VALORES_NULOS))
                            fechaHasta = this.transformarCadenaAFecha(fHasta);
                        
                        String razon = vector[i++];
                        ret.add(new RolEnTrabajo(jurado, Rol.JURADO, fechaDesde, fechaHasta, razon));
                    }                                        
                                        
                    List<AlumnoEnTrabajo> aet = new ArrayList<>();
                    for(int i = cxPrimerAlumno; i <= razonUltimoAlumno; ) {  //se leen los alumnos
                        String cx = vector[i++];
                        Alumno alumno = gp.dameAlumno(cx); 
                        
                        String fDesde = vector[i++];
                        LocalDate fechaDesde = this.transformarCadenaAFecha(fDesde);
                        
                        String fHasta = vector[i++];
                        LocalDate fechaHasta = null;
                        if (!fHasta.equals(VALORES_NULOS))
                            fechaHasta = this.transformarCadenaAFecha(fHasta);
                        
                        String razon = vector[i++];
                        aet.add(new AlumnoEnTrabajo(alumno, fechaDesde, fechaHasta, razon));                        
                    }
                    
                    Trabajo trabajo = new Trabajo(titulo, duracion, areas, fechaPresentacion, fechaAprobacion, fechaExposicion, ret, aet);
                    this.trabajos.add(trabajo);
                }
                return LECTURA_OK;
            }
            catch (IOException ioe) {
                return LECTURA_ERROR;
            }
        }
        return ARCHIVO_INEXISTENTE;
    }  
    
        /**
     * Transforma una fecha en cadena de la forma dd/mm/aaaa en un objeto LocalDate
     * @param cadenaFecha cadena con la fecha a transformar
     * @return LocalDate  - objeto LocalDate transformado
     */
    private LocalDate transformarCadenaAFecha(String cadenaFecha) {
        String[] vector = cadenaFecha.split(Character.toString('/'));
        int dia = Integer.parseInt(vector[0]);
        int mes = Integer.parseInt(vector[1]);
        int anio = Integer.parseInt(vector[2]);
        return LocalDate.of(anio, mes, dia);
    }
    
    

}
