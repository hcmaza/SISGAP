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
    private HashMap<String, Float> listaSaldosRubro;

    private List<String> columnasListaSaldosRubro;
    private List<Float> valoresListaSaldosRubro;

    // Ejecutado por rubro
    private List<ItemRubro> listaEjecutadoRubro;

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

    public HashMap<String, Float> getListaSaldosRubro() {
        return listaSaldosRubro;
    }

    public void setListaSaldosRubro(HashMap<String, Float> listaSaldosRubro) {
        this.listaSaldosRubro = listaSaldosRubro;
    }

    public List<ItemRubro> getListaEjecutadoRubro() {
        return listaEjecutadoRubro;
    }

    /**
     * Creates a new instance of IndicadoresController
     */
    public IndicadoresController() {

    }

    /**
     * Método que ejecuta los calculos de obtencion de saldos y ejecucion por rubro
     * 
     */
    public void obtenerCalculosPorRubro() {

        calcularSaldosPorRubro();

        calcularEjecutadoPorRubro();

    }

    public void calcularSaldosPorRubro() {
        // Saldos por Rubro
        HashMap<String, Float> saldos = new HashMap<String, Float>();

        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);

        // Llenamos la lista de presupuestos tarea por proyecto
        listaPresupuestosTarea = getPresupuestoTareaFacade().obtenerPorProyecto(proyectocontroller.getSelected().getId());

        // Llenamos la lista de solicitudes anteriores
        listaSolicitudesAprobadas = getSolicitudFacade().obtenerAprobadasPorProyecto(proyectocontroller.getSelected().getId());

        // Lista de solicitudes disponibles
        List<Solicitud> listaSolicitudesDisponibles = new ArrayList<Solicitud>();

        // Llenamos el hashmap de saldos y la lista de ejecucion
        for (Rubro r : getRubroFacade().findAll()) {
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
                Solicitud solicitudAprobada = (Solicitud) i.next();

                // si encontramos el presupuestotarea en una solicitud anterior y siendo una solicitud aprobada
                if (p.getId() == solicitudAprobada.getPresupuestotarea().getId()) {
                    // restamos al importe de la solicitud disponible, el importe de la solicitud anterior
                    solicitud.setImporte(p.getTotal().subtract(solicitudAprobada.getImporte()));
                    solicitud.setDisponible(solicitud.getImporte());

                }

            }

            // Agregamos a la lista de solicitudes disponibles si el importe es distinto de cero y sumamos al saldo
            if (solicitud.getImporte().floatValue() != 0.00) {
                listaSolicitudesDisponibles.add(solicitud);

                // Acumulamos en la lista de saldos
                saldos.put(solicitud.getPresupuestotarea().getRubro().getRubro(), saldos.get(solicitud.getPresupuestotarea().getRubro().getRubro()) + solicitud.getDisponible().floatValue());
            }
        }

        // Salida de prueba
        for (Map.Entry<String, Float> e : saldos.entrySet()) {
            System.out.println("Saldo Rubro: " + e.getKey() + " = " + e.getValue());
        }

        // Saldos por Rubro
        listaSaldosRubro = saldos;

        columnasListaSaldosRubro = new ArrayList<String>(saldos.keySet());
        valoresListaSaldosRubro = new ArrayList<Float>(saldos.values());
    }

    public void calcularEjecutadoPorRubro() {

        List<Solicitud> listaSolicitudes;

        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);

        // Llenamos la lista de solicitudes (Aprobadas y Rendidas)
        listaSolicitudes = this.getSolicitudFacade().obtenerAprobadasPorProyecto(proyectocontroller.getSelected().getId());
        listaSolicitudes.addAll(this.getSolicitudFacade().obtenerRendidasPorProyecto(proyectocontroller.getSelected().getId()));

        // Ejecutado por Rubro
        List<ItemRubro> ejecutado = new ArrayList<ItemRubro>();

        // Llenamos el hashmap de saldos y la lista de ejecucion
        for (Rubro r : getRubroFacade().findAll()) {
            ejecutado.add(new ItemRubro(r.getId(), r.getRubro(), 0.0f));
        }

        for (Solicitud solicitud : listaSolicitudes) {
            Iterator j = ejecutado.iterator();

            // Buscamos en la lista de ejecutado por rubro para acumular
            while (j.hasNext()) {
                ItemRubro irt = (ItemRubro) j.next();

                // Acumulamos en la lista de ejecutado por rubro
                if (irt.getId() == solicitud.getPresupuestotarea().getRubro().getId()) {
                    irt.setMonto(irt.getMonto() + solicitud.getImporte().floatValue());
                    System.out.println("ItemRubro monto = " + irt.getMonto());
                }
            }
        }

        for (ItemRubro ir : ejecutado) {
            System.out.println("Ejecutado Rubro: " + ir.getId() + " - " + ir.getNombrerubro() + " = " + ir.getMonto());
        }

        // Ejecutado por Rubro
        listaEjecutadoRubro = ejecutado;

    }

    public static class SaldoRubro {

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

    class ItemRubro {

        private int id;
        private String nombrerubro;
        private float monto;

        public ItemRubro() {
        }

        public ItemRubro(int id, String nombrerubro, float monto) {
            this.id = id;
            this.nombrerubro = nombrerubro;
            this.monto = monto;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNombrerubro() {
            return nombrerubro;
        }

        public void setNombrerubro(String nombrerubro) {
            this.nombrerubro = nombrerubro;
        }

        public float getMonto() {
            return monto;
        }

        public void setMonto(float monto) {
            this.monto = monto;
        }

    }

}
