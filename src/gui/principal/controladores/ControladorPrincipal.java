/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.principal.controladores;

import gui.areas.controladores.ControladorAreas;
import gui.areas.modelos.GestorAreas;
import gui.interfaces.IControladorAreas;
import gui.interfaces.IControladorPrincipal;
import gui.interfaces.IControladorTrabajos;
import gui.interfaces.IGestorAreas;
import gui.interfaces.IGestorPersonas;
import gui.interfaces.IGestorTrabajos;
import gui.personas.modelos.Cargo;
import gui.personas.modelos.GestorPersonas;
import gui.principal.vistas.VentanaPrincipal;
import gui.trabajos.controladores.ControladorTrabajos;
import gui.trabajos.modelos.GestorAlumnosEnTrabajos;
import gui.trabajos.modelos.GestorRolesEnTrabajos;
import gui.trabajos.modelos.GestorTrabajos;
import gui.trabajos.modelos.Rol;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import javax.swing.JOptionPane;

public class ControladorPrincipal implements IControladorPrincipal {
    private VentanaPrincipal ventana;

    /**
     * Constructor
     * Muestra la ventana principal
     */
    public ControladorPrincipal() {
        this.ventana = new VentanaPrincipal(this);
        this.ventana.setLocationRelativeTo(null);
        this.ventana.setVisible(true);
    }

    /**
     * Acción a ejecutar cuando se selecciona el botón Areas
     * @param evt evento
     */                            
    @Override
    public void btnAreasClic(ActionEvent evt) {
        IControladorAreas controlador = new ControladorAreas(this.ventana);
    }

    /**
     * Acción a ejecutar cuando se selecciona el botón Personas
     * @param evt evento
     */                            
    @Override
    public void btnPersonasClic(ActionEvent evt) {
    }

    /**
     * Acción a ejecutar cuando se selecciona el botón Trabajos
     * @param evt evento
     */                            
    @Override
    public void btnTrabajosClic(ActionEvent evt) {
        IControladorTrabajos controlador = new ControladorTrabajos(this.ventana);
    }
    
    /**
     * Acción a ejecutar cuando se selecciona el botón Salir
     * @param evt evento
     */                            
    @Override
    public void btnSalirClic(ActionEvent evt) {
        int opcion = JOptionPane.showOptionDialog(null, CONFIRMACION, TITULO_VENTANA, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, this);
        if (opcion == JOptionPane.YES_OPTION) {
            this.ventana.dispose();
            System.exit(0);
        }       
    }
        
    public static void main(String[] args) {
        IControladorPrincipal controladorPrincipal = new ControladorPrincipal();
        IGestorPersonas GP = GestorPersonas.instanciar();
        IGestorTrabajos GT = GestorTrabajos.instanciar();
        IGestorAreas GA = GestorAreas.instanciar();
        
        GP.nuevoProfesor("Olmedo", "Dario", 2822, Cargo.ADJUNTO);
        GP.nuevoProfesor("Griego","Ricardo",3121,Cargo.ADG);
        GP.nuevoProfesor("Griego","Clodomiro",3122,Cargo.ADG);
        GP.nuevoProfesor("Griego","Rafael",3124,Cargo.ADG);
        GP.nuevoProfesor("Panza","Sancho",3126,Cargo.ADG);
        GP.nuevoProfesor("Skywalker","Lucas",3232,Cargo.ADG);
        GP.nuevoProfesor("Juanes","Juan",3333,Cargo.ADG);
        GP.nuevoAlumno("Jarpancho", "Demaciano", 5455, "03212");
        GP.nuevoAlumno("Elizordon", "Romario", 5459, "03216");
        GP.nuevoAlumno("Elizordon", "Jazmin Floricienta", 5421, "34214");
        GestorRolesEnTrabajos GRT = GestorRolesEnTrabajos.instanciar();
        GestorAlumnosEnTrabajos GAET = GestorAlumnosEnTrabajos.instanciar();
        GRT.verListaRolesEnTrabajos().add(GRT.nuevoRolEnTrabajo(GP.dameProfesor(2822), Rol.TUTOR,LocalDate.of(2019, 10, 3) ));
//        GRT.getListaRolEnTrabajo().add(GRT.nuevoRolEnTrabajo(LocalDate.of(2019, 10, 3), GP.dameProfesor(3122), Rol.COTUTOR));  //si activo causa error por un profesor tutor y cotutor
//        GRT.verListaRolesEnTrabajos().add(GRT.nuevoRolEnTrabajo(GP.dameProfesor(3121), Rol.COTUTOR,LocalDate.of(2019, 10, 3))); //si activo la llamada a borrarProfesor de mas arriba, esta linea agregara un RolEnTrabajo null a la lista del gestor
//        GRT.verListaRolesEnTrabajos().add(GRT.nuevoRolEnTrabajo(GP.dameProfesor(3122), Rol.JURADO,LocalDate.of(2019, 10, 3)));
//        GRT.verListaRolesEnTrabajos().add(GRT.nuevoRolEnTrabajo(GP.dameProfesor(3124), Rol.JURADO,LocalDate.of(2019, 10, 3)));
//        GRT.verListaRolesEnTrabajos().add(GRT.nuevoRolEnTrabajo(GP.dameProfesor(3123), Rol.JURADO,LocalDate.of(2019, 10, 3)));
//        
//        GAET.verListaAlumnosEnTrabajos().add(GAET.nuevoAlumnoEnTrabajo(GP.dameAlumno("34214"),LocalDate.of(2019, 10, 3)));
        
//        String r =GT.nuevoTrabajo("Trabajo", 5, LocalDate.of(2019,9,3), LocalDate.of(2019,10,2), GA.buscarAreas("software"), GRT.verListaRolesEnTrabajos(), GAET.verListaAlumnosEnTrabajos());
        

        
    }    
}
