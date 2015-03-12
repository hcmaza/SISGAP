/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.sisgap.controller.view;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/**
 *
 * @author gongono
 */
@ManagedBean
@RequestScoped
public class NotificacionController {

    /**
     * Creates a new instance of NotificacionController
     */
    public NotificacionController() {
        
    }
     public void notificar() {  
        FacesContext context = FacesContext.getCurrentInstance();  
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
       if(context.getExternalContext().isUserInRole("Administrador") & proyectocontroller.buscarIdeasProyectosEvaluar()>0 ){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Ideas proyectos a evaluar " , "Hay "+proyectocontroller.buscarIdeasProyectosEvaluar()+ " proyectos a evaluar !" ));  
       }
    context.addMessage(null, new FacesMessage(" Bienvenido ",""+ context.getExternalContext().getUserPrincipal()));  
    
     
     }  
    
}
