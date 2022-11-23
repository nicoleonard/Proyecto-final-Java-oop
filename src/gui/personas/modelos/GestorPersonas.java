/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.personas.modelos;

import gui.interfaces.IGestorPersonas;
import static gui.interfaces.IGestorPersonas.ESCRITURA_PROFESORES_ERROR;
import gui.trabajos.modelos.AlumnoEnTrabajo;
import gui.trabajos.modelos.GestorTrabajos;
import gui.trabajos.modelos.RolEnTrabajo;
import gui.trabajos.modelos.Trabajo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Tuquina Fernando Nahuel, Orellana Gonzalo
 */
public class GestorPersonas implements IGestorPersonas {

    private static GestorPersonas gestor;
    private File ARCHIVO = new File("personas.txt");
    private final char SEPARADOR = ';';
    List<Profesor> listaProfesores = new ArrayList<>();
    List<Alumno> listaAlumnos = new ArrayList<>();
    private static int ultimoProfesor;
    private static int ultimoAlumno;
    private List<Persona> listaPersonas = new ArrayList<>();
    private final static String nombreArchivo = "personas.txt";

    /**
     * Comparador para ordenar alfabeticamente
     */
    Comparator<Persona> compApellido = (A1, A2) -> A1.verApellidos().toUpperCase().compareTo(A2.verApellidos().toUpperCase());
    Comparator<Persona> compNombre = (A1, A2) -> A1.verNombres().toUpperCase().compareTo(A2.verNombres().toUpperCase());

    private GestorPersonas() {
        this.leer_archivo();
    }
    
    public static GestorPersonas instanciar() {
        if (gestor == null) {
            gestor = new GestorPersonas();
        }
        return gestor;
    }

 
    // <editor-fold desc="Persistencia">
    /**
     * Lee el archivo Personas.txt y guarda su contenido en una listaPersonas
     */
    private void leer_archivo() {
        if (ARCHIVO.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
                String cadena = null;
                while ((cadena = br.readLine()) != null) {
                    String[] vector = cadena.split(Character.toString(SEPARADOR));
                    String Apellido = vector[0];
                    String Nombre = vector[1];
                    int DNI = Integer.parseInt(vector[2]);
                    try {
                        Integer.parseInt(vector[3]);
                        String CX = vector[3];
                        Persona P = new Alumno(Apellido, Nombre, DNI, CX);
                        if (!this.listaPersonas.contains(P)) {
                            this.listaPersonas.add(P);
                        }

                    } catch (NumberFormatException e) {
                        String nombreCargo = vector[3].toUpperCase();
                        Cargo cargo = Cargo.valueOf(nombreCargo);
                        Persona P = new Profesor(Apellido, Nombre, DNI, cargo);
                        if (!this.listaPersonas.contains(P)) {
                            this.listaPersonas.add(P);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error al leer el archivo: " + ARCHIVO.getName());
            }
            this.listaPersonas.sort(compApellido);
        }
    }
    
    /**
     * Guarda listaPersonas en el archivo "personas.txt"
     */
    public void guardar() {

        File f = new File("personas.txt");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {

            for (Persona a : this.listaPersonas) {

                if (a instanceof Alumno) {
                    bw.write(a.verApellidos() + ";");

                    bw.write(a.verNombres() + ";");

                    bw.write(a.verDNI() + ";");

                    bw.write(((Alumno) a).verCX());

                    bw.newLine();
                }

                if (a instanceof Profesor) {
                    bw.write(a.verApellidos() + ";");

                    bw.write(a.verNombres() + ";");

                    bw.write(a.verDNI() + ";");
                    
                    bw.write(((Profesor) a).verCargo().name());

                    bw.newLine();
                }

            }

        } catch (IOException ioe) {
            System.out.println("error");
        }

    }

    //</editor-fold>
    // <editor-fold desc="Controles">

    /**
    * Metodo que revisa si una cadena valida (no vacia ni nula)
    * Devuelve true si lo es, false en caso contrario
    * @param cadena
    * @return 
    */
    private boolean cadena_valida(String cadena) {

        if (!cadena.trim().isEmpty() && !cadena.equals(null)) {
            return true;
        }

        return false;

    }

    /**
     *
     * Controla que la cadena enviada no sea null o una cadena vacia
     *
     * @param str
     * @return True si la cadena es correcta False si la cadena esta vacia o es
     * null
     * @throws NullPointerException
     */
    private boolean controlString(String str) {
        try {                                    //El String no puede apuntar a null
            if (!str.trim().isEmpty())           //El apellido no puede estar vacio
            {
                return true;
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     *
     * Controla que el DNI sea un entero mayor que cero
     *
     * @param DNI
     * @return True si es un entero mayor que cero False de no serlo
     */
    private boolean controlDNI(int DNI) {
        if (DNI > 0) //El DNI no puede ser igual o menor que cero
        {
            return true;
        }
        return false;
    }

    /**
     *
     * Controla que el Cx sea una cadena solo numerica, no puede poseer letras
     * ni simbolos
     *
     * @param Cx
     * @return True si es correcta False en caso contrario
     * @throws NumberFormatException
     */
    private boolean controlCx(String Cx) {                  //El Cx no puede apuntar a null
        try {
            Integer.parseInt(Cx);                       //Debe tener cierto formato                  
            return true;                                //No poseer letras ni simbolos
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //</editor-fold>
    // <editor-fold desc="Metodos Alumnos">
    /**
     * Método que crea un nuevo alumno si cumple con las condiciones requeridas
     * @param apellidos
     * @param nombres
     * @param dni
     * @param cx
     * @return String
     */
    @Override
    public String nuevoAlumno(String apellidos, String nombres, int dni, String cx) {


        if (cadena_valida(apellidos)) {

            if (cadena_valida(nombres)) {

                if (dni > 0) {

                    if (cadena_valida(cx) && Integer.parseInt(cx) > 0) {

                        Persona a = new Alumno(apellidos, nombres, dni, cx);

                        if (!listaPersonas.contains(a)) {
                            this.listaPersonas.add(a);
                            List<Persona> listaAux = new ArrayList<>();
                            listaAux.addAll(this.listaPersonas);
                            listaAux.sort(compApellido);
                            this.ultimoAlumno = listaAux.indexOf(a);
                            this.listaPersonas.sort(compApellido);

                            this.guardar();

                            return EXITO_ALUMNOS;

                        } else {
                            return ALUMNOS_DUPLICADOS;
                        }

                    } else {
                        return ERROR_ALUMNOS;
                    }

                } else {
                    return ERROR_ALUMNOS;
                }
            } else {
                return ERROR_ALUMNOS;
            }

        } else {
            return ERROR_ALUMNOS;
        }

    }
    
    /**
     * Método que modifica los datos de un alumno
     * @param alumno
     * @param apellidos
     * @param nombres
     * @param cx
     * @return String
     */
    @Override
    public String modificarAlumno(Alumno alumno, String apellidos, String nombres, String cx) {


        //Si no se encuentra ese profesor avisa que no se encontró
        if (this.dameAlumno(alumno.verCX()) == null) {
            return ERROR_ALUMNOS;
        }
        
        if (!cadena_valida(apellidos)) {
            return ERROR_ALUMNOS;
        }

        if (!cadena_valida(nombres)) {
            return ERROR_ALUMNOS;
        }

        if (cx == null) {
            return ERROR_ALUMNOS;
        }

        for (Persona p : listaPersonas) {

            if (p instanceof Alumno) {

                if (((Alumno) p).verApellidos().equals(alumno.verApellidos()) && ((Alumno) p).verCX().equals(alumno.verCX()) && p.verDNI() == alumno.verDNI()) {

                    ((Alumno) p).asignarCX(cx);
                    p.asignarApellidos(apellidos);
                    p.asignarNombres(nombres);
                }
            }
        }

        guardar();

        return EXITO_ALUMNOS;

    }
    
    /**
     * Borra un alumno de listaPersonas si cumple con las condiciones
     * @param alumno
     * @return String
     */
    @Override
    public String borrarAlumno(Alumno alumno) {

        GestorTrabajos gestorTrabajos = GestorTrabajos.instanciar();        //Instancia el gestor de trabajos
        int cantidad = 0;                                                   //Cantidad de veces que aparece una persona
        List<Trabajo> listaTrabajos = new ArrayList<>();
        listaTrabajos = gestorTrabajos.verTrabajos();
        List<AlumnoEnTrabajo> listaAlumnos;                                 //Busca los alumnos en cada trabajo
        for (Trabajo T : listaTrabajos) {                                     //Este no deberia aparecer para que pueda ser borrado
            listaAlumnos = T.verAlumnos();
            for (AlumnoEnTrabajo A : listaAlumnos) {
                if (A.verAlumno().equals(alumno)) {
                    cantidad++;
                }
            }
        }
        if (cantidad == 0) {                                                  //Si no aparecio lo borra

            this.listaPersonas.remove(alumno);

            guardar();
            return EXITO_ALUMNOS;
        }
        return TRABAJO_CON_ALUMNO;

    }
    
    /**
     * Método que devuelve una lista con todos los alumnos
     * @return List<Alumno>
     */
    public List<Alumno> verListaAlumnos() {

        List<Alumno> alumnos = new ArrayList<>();          //Array donde se guardaran los alumnos con el apellido a buscar

        for (Persona P : listaPersonas) {

            if (P instanceof Alumno) {
                if (!alumnos.contains((Alumno) P)) {
                    alumnos.add((Alumno) P);
                }
            }

        }
        return alumnos;
    }

    /**
     * Devuelve la cantidad de alumnos que hay
     * @return int
     */
    public int cantAlumnos() {
        this.listaAlumnos = this.verListaAlumnos();
        return listaAlumnos.size();
    }
    
    /**
     * Busca los alumnos que contengan el string del parámetro
     * @param apellidos
     * @return String
     */
    @Override
    public List<Alumno> buscarAlumnos(String apellidos) {
      
        if (apellidos != null){
            List<Alumno> alumnos = new ArrayList<>();          //Array donde se guardaran los alumnos con el apellido a buscar

            for (Alumno p : this.verListaAlumnos()) {
                if (p.verApellidos().toUpperCase().contains(apellidos.toUpperCase())) {

                        alumnos.add(p);

                }
            }
            return alumnos;
        }
        return this.listaAlumnos;  
        
        
        
        
    }
    
    /**
     * Retorna el alumno con el cx coincidente
     * @param cx
     * @return Alumno
     */
    @Override
    public Alumno dameAlumno(String cx) {

        if (!controlCx(cx)) {                                 //Controla que el Cx tenga formato valido
            System.out.println(ERROR_ALUMNOS);
            return null;
        }
        for (Persona P : listaPersonas) {                     //Recorre el array de personas
            if (P instanceof Alumno) {                        //Si la persona es un alumno
                if (cx.equals(((Alumno) P).verCX())) //Mira si los Cx son iguales
                {
                    return (Alumno) P;                      //Si son iguales regresa el alumno (cast)
                }
            }
        }
        return null;                                        //Si no lo encontro retorna null

    }
    
    /**
     * Retorna la posición del alumno en el ArrayList
     * @param alumno
     * @return int
     */
    @Override
    public int ordenAlumno(Alumno alumno) {

        List<Alumno> alumnos = new ArrayList<>();          //Array donde se guardaran los alumnos con el apellido a buscar

        for (Persona P : listaPersonas) {

            if (P instanceof Alumno) {
                alumnos.add((Alumno) P);
            }

        }

        for (Alumno i : alumnos) {
            if (i.equals(alumno)) {
                return alumnos.indexOf(i);
            }
        }
        return -1;

    }
    
    /**
     * Setea el valor de ultimo alumno
     * @param aUltimoAlumno 
     */
    public static void setUltimoAlumno(int aUltimoAlumno) {
        ultimoAlumno = aUltimoAlumno;
    }
    
    /**
     * Reduce en uno el valor de ultimoAlumno
     */
    @Override
    public void cancelarAlumno() {
        this.ultimoAlumno = -1;
    }
    
    /**
     * Devuelve el valor de ultimoAlumno;
     * @return 
     */
    @Override
    public int verUltimoAlumno() {
        return this.ultimoAlumno;
    }

    //</editor-fold>
    
    
    // <editor-fold desc="Metodos Profesor">
    /**
     * Método que crea un nuevo profesor si cumple con las condiciones requeridas
     * @param apellidos
     * @param nombres
     * @param dni
     * @param cargo
     * @return String
     */
    @Override
    public String nuevoProfesor(String apellidos, String nombres, int dni, Cargo cargo) {

        if (cadena_valida(apellidos)) {

            if (cadena_valida(nombres)) {

                if (dni > 0) {

                    Persona a = new Profesor(apellidos, nombres, dni, cargo);

                    if (!listaPersonas.contains(a)) {

                        this.listaPersonas.add(a);
                        List<Persona> listaAux = new ArrayList<>();
                        listaAux.addAll(this.listaPersonas);
                        listaAux.sort(compApellido);
                        this.ultimoProfesor = listaAux.indexOf(a);
                        this.listaPersonas.sort(compApellido);

                        this.guardar();

                        return EXITO_PROFESORES;

                    } else {
                        return PROFESORES_DUPLICADOS;
                    }

                } else {
                    return ERROR_PROFESORES;
                }

            } else {
                return ERROR_PROFESORES;
            }

        } else {
            return ERROR_PROFESORES;
        }

    }
    
    /**
     * Método que modifica los datos de un alumno
     * @param profesor
     * @param apellidos
     * @param nombres
     * @param cargo
     * @return String
     */
    @Override
    public String modificarProfesor(Profesor profesor, String apellidos, String nombres, Cargo cargo) {

        //Si no se encuentra ese profesor avisa que no se encontro
        if (this.dameProfesor(profesor.verDNI()) == null) {
            return ERROR_PROFESORES;
        }

        if (!cadena_valida(apellidos)) {
            return ERROR_PROFESORES;
        }

        if (!cadena_valida(nombres)) {
            return ERROR_PROFESORES;
        }

        if (cargo == null) {
            return ERROR_PROFESORES;
        }

        for (Persona p : listaPersonas) {

            if (p instanceof Profesor) {

                if (((Profesor) p).verApellidos().equals(profesor.verApellidos()) && p.verDNI() == profesor.verDNI()) {

                    ((Profesor) p).asignarCargo(cargo);
                    p.asignarApellidos(apellidos);
                    p.asignarNombres(nombres);

                }

            }

        }

        guardar();

        return EXITO_PROFESORES;

    }
    
    /**
     * Borra un profesor de listaPersonas si cumple con las condiciones
     * @param profesor
     * @return String
     */
    @Override
    public String borrarProfesor(Profesor profesor) {

        GestorTrabajos gestorTrabajos = GestorTrabajos.instanciar();            //Instancia el gestor trabajos
        int cantidad = 0;
        List<Trabajo> listaTrabajos = new ArrayList<>();                        //Lista para guardar trabajos
        listaTrabajos = gestorTrabajos.verTrabajos();
        List<RolEnTrabajo> listaProfesores;
        for (Trabajo T : listaTrabajos) {                                         //Busca en la lista de trabajos
            listaProfesores = T.verProfesoresConRoles();                          //al profesor 
            for (RolEnTrabajo P : listaProfesores) {
                if (P.verProfesor().equals(profesor)) {
                    cantidad++;                                                //Si lo encuentra
                }
            }
        }
        if (cantidad == 0) {                                                      //Si el profesor no se encontro

            this.listaPersonas.remove(profesor);

            guardar();

            return EXITO_PROFESORES;
        }
        return TRABAJO_CON_PROFESOR;
    }
    
    /**
     * Busca los profesores que contengan el string del parámetro
     * @param apellidos
     * @return List<Profesor>
     */
    @Override
    public List<Profesor> buscarProfesores(String apellidos) {
        
        if (apellidos != null){
            List<Profesor> profesores = new ArrayList<>();          //Array donde se guardaran los alumnos con el apellido a buscar

            for (Profesor p : this.verListaProfesores()) {
                if (p.verApellidos().toUpperCase().contains(apellidos.toUpperCase())) {

                        profesores.add(p);

                }
            }
            return profesores;
        }
        return this.listaProfesores; 
    }
    
    /**
     * Devuelve el profesor que coincida con el documento
     * del parámetro
     * @param documento
     * @return Profesor
     */
    @Override
    public Profesor dameProfesor(int documento) {

      //  leer_archivo();

        if (!controlDNI(documento)) {                     //Si el DNI tiene un formato invalido
            System.out.println(EXITO_PROFESORES);
            return null;
        }
        for (Persona P : listaPersonas) {                 //Recorre el array de personas
            if (P instanceof Profesor) {                  //Si la persona es un profesor
                if (P.verDNI() == documento) //Mira si es el mismo DNI
                {
                    return (Profesor) P;                //Si es igual regresa la personas (cast)
                }
            }
        }
        return null;                                    //Si no la encontro retorna null

    }

    /**
     * Devuelve el orden del profesor en el arrayList
     * @param profesor
     * @return int
     */
    @Override
    public int ordenProfesor(Profesor profesor) {

        List<Profesor> profesores = new ArrayList<>();          //Array donde se guardaran los profesores con el apellido a buscar

        for (Persona P : listaPersonas) {

            if (P instanceof Profesor) {
                profesores.add((Profesor) P);
            }

        }

        for (Profesor i : profesores) {
            if (i.equals(profesores)) {
                return profesores.indexOf(i);
            }
        }
        return -1;

    }

    /**
     * Devuelve la cantidad de profesores que hay
     * @return int
     */
    public int cantProfesores() {
        this.listaProfesores = this.verListaProfesores();
        return listaProfesores.size();
    }
    
    /**
     * Devuelve la lista de profesores
     * @return List<Profesor>
     */
    public List<Profesor> verListaProfesores() {

        List<Profesor> profesores = new ArrayList<>();          //Array donde se guardaran los alumnos con el apellido a buscar

        for (Persona P : listaPersonas) {

            if (P instanceof Profesor) {
                if (!profesores.contains((Profesor) P)) {
                    profesores.add((Profesor) P);
                }
            }

        }

        return profesores;

    }
    
    /**
     * Setea ultimoProfesor
     * @param aUltimoProfesor 
     */
    public static void setUltimoProfesor(int aUltimoProfesor) {
        ultimoProfesor = aUltimoProfesor;
    }
    
    /**
     * Devuelve ultimoProfesor
     * @return int
     */
    @Override
    public int verUltimoProfesor() {

        return this.ultimoProfesor;
    }
    
    /**
     * Reduce en uno el valor de ultimoProfesor
     */
    @Override
    public void cancelarProfesor() {
        this.ultimoProfesor = -1;
    }

    //</editor-fold>  
}