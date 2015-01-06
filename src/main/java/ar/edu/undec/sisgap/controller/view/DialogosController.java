/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.sisgap.controller.view;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

/**
 *
 * @author mpaez
 */
@ManagedBean
@ViewScoped
public class DialogosController implements Serializable{
    public DialogosController(){}
    
    public void dialogo(){
        System.out.println("dialogoCrearAgente");
        //RequestContext.getCurrentInstance().openDialog("dialogo");
        //RequestContext.getCurrentInstance().openDialog("/secure/solicitud/dialogo.xhtml");
        RequestContext.getCurrentInstance().openDialog("/secure/dialogos/dialogo.xhtml");
        System.out.println("dialogoCrearAgente fin");
    }
}
