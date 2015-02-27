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
import org.primefaces.model.chart.MeterGaugeChartModel;
import org.primefaces.model.chart.PieChartModel;

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

    private List<Solicitud> listaSolicitudes;
    private List<PresupuestoTarea> listaPresupuestosTarea;
    private HashMap<String, Float> listaSaldosRubro;

    private List<String> columnasListaSaldosRubro;
    private List<Float> valoresListaSaldosRubro;
    
    // Total presupuestado proyecto
    private float totalPresupuestoProyecto = 0.0f;
    
    // grafico de ejecutado del proyecto
    private MeterGaugeChartModel indicadorEjecutado;

    // Ejecutado por rubro
    private List<ItemRubro> listaEjecutadoRubro;
    private PieChartModel chartEjecutadoPorRubro;
    
    // Saldo del proyecto
    private float saldoProyecto = 0.0f;
    
    // Monto ejecutado del proyecto
    private float ejecutadoProyecto = 0.0f;
    
    // Monto rendido por proyecto
    private float rendidoProyecto;
    
    // Monto pendiente de rendicion por proyecto
    private float pendienteRendicionProyecto;

    public SolicitudFacade getSolicitudFacade() {
        return solicitudFacade;
    }

    public PresupuestoTareaFacade getPresupuestoTareaFacade() {
        return presupuestoTareaFacade;
    }

    public RubroFacade getRubroFacade() {
        return rubroFacade;
    }

    public List<Solicitud> getListaSolicitudes() {
        return listaSolicitudes;
    }

    public void setListaSolicitudes(List<Solicitud> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
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

    public PieChartModel getChartEjecutadoPorRubro() {
        return chartEjecutadoPorRubro;
    }

    public float getSaldoProyecto() {
        return saldoProyecto;
    }

    public float getEjecutadoProyecto() {
        return ejecutadoProyecto;
    }

    public float getTotalPresupuestoProyecto() {
        return totalPresupuestoProyecto;
    }

    public float getRendidoProyecto() {
        return rendidoProyecto;
    }

    public float getPendienteRendicionProyecto() {
        return pendienteRendicionProyecto;
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

        calcularTotalesPorProyecto();
        
        calcularSaldosPorRubro();

        calcularEjecutadoPorRubro();
        
        crearIndicadorEjecutado();
        
        generarChartEjecutadoPorRubro();


    }

    public void calcularSaldosPorRubro() {
        
        // Saldo del proyecto
        saldoProyecto = 0.0f;
        
        // Saldos por Rubro
        HashMap<String, Float> saldos = new HashMap<String, Float>();

        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);

        // Llenamos la lista de presupuestos tarea por proyecto
        listaPresupuestosTarea = getPresupuestoTareaFacade().obtenerPorProyecto(proyectocontroller.getSelected().getId());

        // Llenamos la lista de solicitudes anteriores
        listaSolicitudes = getSolicitudFacade().obtenerAprobadasPorProyecto(proyectocontroller.getSelected().getId());

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
            Iterator i = listaSolicitudes.iterator();

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
                
                saldoProyecto += solicitud.getDisponible().floatValue();
                
            }
        }

        // Saldos por Rubro
        listaSaldosRubro = saldos;

        columnasListaSaldosRubro = new ArrayList<String>(saldos.keySet());
        valoresListaSaldosRubro = new ArrayList<Float>(saldos.values());
    }

    public void calcularEjecutadoPorRubro() {

        List<Solicitud> listaSolicitudes;
        
        // Total Ejecutado del proyecto
        ejecutadoProyecto = 0.0f;

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
            
            //Acumulamos en el total ejecutado del proyecto
            ejecutadoProyecto += solicitud.getImporte().floatValue();
            
            
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
    
    public void generarChartEjecutadoPorRubro(){
        chartEjecutadoPorRubro = new PieChartModel();
        
        for(ItemRubro ir : listaEjecutadoRubro){
            chartEjecutadoPorRubro.set(ir.nombrerubro, ir.monto);
        }
        
        chartEjecutadoPorRubro.setMouseoverHighlight(true);
        chartEjecutadoPorRubro.setDiameter(65);
        chartEjecutadoPorRubro.setShowDataLabels(true);

         
        //chartEjecutadoPorRubro.setTitle("Simple Pie");
        //chartEjecutadoPorRubro.setLegendPosition("w");
        chartEjecutadoPorRubro.setExtender("torta");
        
    }
    
    // Indicador de Ejecucion
    
    public MeterGaugeChartModel getIndicadorEjecutado() {

        if (indicadorEjecutado == null){
            crearIndicadorEjecutado();
        }
        
        return indicadorEjecutado;
    }
    
    private MeterGaugeChartModel inicializarModeloIndicadorPorcentaje() {
        List<Number> intervalos = new ArrayList<Number>(){{
            add(25);
            add(50);
            add(75);
            add(100);
        }};
        
        float ejecutado = 0.0f;
        ejecutado = (totalPresupuestoProyecto * ejecutadoProyecto) / 100;
         
        //return new MeterGaugeChartModel(Integer.parseInt(String.valueOf(ejecutado)), intervalos);
        return new MeterGaugeChartModel((Number)ejecutado,intervalos);
    }
 
    private void crearIndicadorEjecutado() {
        indicadorEjecutado = inicializarModeloIndicadorPorcentaje();

        indicadorEjecutado.setSeriesColors("66cc66,93b75f,E7E658,cc6666");

        indicadorEjecutado.setGaugeLabelPosition("bottom");
        //indicadorEjecutado.setShowTickLabels(false);
        
        indicadorEjecutado.setIntervalInnerRadius(85);
        indicadorEjecutado.setIntervalOuterRadius(80);
        
        indicadorEjecutado.setExtender("indicador");
        
    }
    
    public void calcularTotalesPorProyecto(){
        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
        
        totalPresupuestoProyecto = this.getPresupuestoTareaFacade().obtenerTotalPorProyecto(proyectocontroller.getSelected().getId());
        
        
        
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

    public static class ItemRubro {

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
