/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.sisgap.controller.view;

import ar.edu.undec.sisgap.controller.PresupuestoTareaFacade;
import ar.edu.undec.sisgap.controller.RubroFacade;
import ar.edu.undec.sisgap.controller.SolicitudFacade;
import ar.edu.undec.sisgap.model.PresupuestoTarea;
import ar.edu.undec.sisgap.model.Rubro;
import ar.edu.undec.sisgap.model.Solicitud;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;

/**
 *
 * @author mpaez
 */
@ManagedBean(name = "indicadoresController")
@SessionScoped
public class IndicadoresController implements Serializable {
    
    @EJB
    private SolicitudFacade solicitudFacade;
    @EJB
    private PresupuestoTareaFacade presupuestoTareaFacade;
    @EJB
    private RubroFacade rubroFacade;
    
    private List<Solicitud> listaSolicitudesAprobadas;
    private List<PresupuestoTarea> listaPresupuestosTarea;
    //private Set<Entry<String, Float>> listaSaldosRubro;
    private HashMap<String,Float> listaSaldosRubro;
    
    private List<String> columnasListaSaldosRubro;
    private List<Float> valoresListaSaldosRubro;
    
    public SolicitudFacade getSolicitudFacade() {
        return solicitudFacade;
    }

    public PresupuestoTareaFacade getPresupuestoTareaFacade() {
        return presupuestoTareaFacade;
    }

    public RubroFacade getRubroFacade() {
        return rubroFacade;
    }
    
    public List<Solicitud> getListaSolicitudesAprobadas() {
        return listaSolicitudesAprobadas;
    }

    public void setListaSolicitudesAprobadas(List<Solicitud> listaSolicitudesAprobadas) {
        this.listaSolicitudesAprobadas = listaSolicitudesAprobadas;
    }

    public List<PresupuestoTarea> getListaPresupuestosTarea() {
        return listaPresupuestosTarea;
    }

    public void setListaPresupuestosTarea(List<PresupuestoTarea> listaPresupuestosTarea) {
        this.listaPresupuestosTarea = listaPresupuestosTarea;
    }

    public List<String> getColumnasListaSaldosRubro() {
        return columnasListaSaldosRubro;
    }

    public List<Float> getValoresListaSaldosRubro() {
        return valoresListaSaldosRubro;
    }
    
    

//    public Set<Entry<String, Float>> getListaSaldosRubro() {
//        return listaSaldosRubro;
//    }
//
//    public void setListaSaldosRubro(Set<Entry<String, Float>> listaSaldosRubro) {
//        this.listaSaldosRubro = listaSaldosRubro;
//    }
    public HashMap<String, Float> getListaSaldosRubro() {
        return listaSaldosRubro;
    }

    public void setListaSaldosRubro(HashMap<String, Float> listaSaldosRubro) {
        this.listaSaldosRubro = listaSaldosRubro;
    }
    
    
    /**
     * Creates a new instance of IndicadoresController
     */
    public IndicadoresController() {

    }

//    public Map<String, Float> obtenerSaldosPorRubro() {
//    public Set<Entry<String, Float>> obtenerSaldosPorRubro() {
    public void obtenerSaldosPorRubro(){
        
        HashMap<String,Float> saldos = new HashMap<String,Float>();
        
        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
        
        // Llenamos la lista de presupuestos tarea por proyecto
        listaPresupuestosTarea = getPresupuestoTareaFacade().obtenerPorProyecto(proyectocontroller.getSelected().getId());

        // Llenamos la lista de solicitudes anteriores
        listaSolicitudesAprobadas = getSolicitudFacade().obtenerAprobadasPorProyecto(proyectocontroller.getSelected().getId());
        
        // Lista de solicitudes disponibles
        List<Solicitud> listaSolicitudesDisponibles = new ArrayList<Solicitud>();      
        
        // Llenamos el hashmap
        for(Rubro r : getRubroFacade().findAll()){
            saldos.put(r.getRubro(), 0.0f);
        }

        // Obtenemos los importes de solicitud disponibles
        for (PresupuestoTarea p : listaPresupuestosTarea) {
            
            Solicitud solicitud = new Solicitud();
            solicitud.setPresupuestotarea(p);
            solicitud.setImporte(p.getTotal());
            solicitud.setDisponible(solicitud.getImporte());


            // buscamos si el presupuestotarea ya fue solicitado anteriormente, de ser asi, restamos el importe o lo removemos
            Iterator i = listaSolicitudesAprobadas.iterator();

            while (i.hasNext()) {
                Solicitud solicitudAnterior = (Solicitud) i.next();

                // si encontramos el presupuestotarea en una solicitud anterior y siendo una solicitud aprobada
                if (p.getId() == solicitudAnterior.getPresupuestotarea().getId()) {
                    // restamos al importe de la solicitud disponible, el importe de la solicitud anterior
                    solicitud.setImporte(p.getTotal().subtract(solicitudAnterior.getImporte()));
                    solicitud.setDisponible(solicitud.getImporte());

                }
            }

            // Agregamos a la lista de solicitudes disponibles si el importe es distinto de cero y sumamos al saldo
            if (solicitud.getImporte().floatValue() != 0.00) {
                listaSolicitudesDisponibles.add(solicitud);
                
                saldos.put(solicitud.getPresupuestotarea().getRubro().getRubro(), saldos.get(solicitud.getPresupuestotarea().getRubro().getRubro()) + solicitud.getDisponible().floatValue());
            }
        }
        
        for(Map.Entry<String,Float> e : saldos.entrySet()){
            System.out.println("Saldo Rubro: " + e.getKey() + " = " + e.getValue());
        }

        //return saldos.entrySet();
        
        //listaSaldosRubro = saldos.entrySet();
        listaSaldosRubro = saldos;
        
        columnasListaSaldosRubro = new ArrayList<String>(saldos.keySet());
        valoresListaSaldosRubro = new ArrayList<Float>(saldos.values());


    }
    
    public static class SaldoRubro{
        
        private String nombre;
        private float monto;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public float getMonto() {
            return monto;
        }

        public void setMonto(float monto) {
            this.monto = monto;
        }
        
        
    }

}
