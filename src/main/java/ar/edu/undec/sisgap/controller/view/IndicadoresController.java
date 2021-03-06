/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.sisgap.controller.view;

import ar.edu.undec.sisgap.controller.DesembolsoFacade;
import ar.edu.undec.sisgap.controller.PresupuestoTareaFacade;
import ar.edu.undec.sisgap.controller.RubroFacade;
import ar.edu.undec.sisgap.controller.SolicitudFacade;
import ar.edu.undec.sisgap.model.Desembolso;
import ar.edu.undec.sisgap.model.PresupuestoTarea;
import ar.edu.undec.sisgap.model.Rubro;
import ar.edu.undec.sisgap.model.Solicitud;
import ar.edu.undec.sisgap.model.Proyecto;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
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
    private DesembolsoFacade desembolsoFacade;
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

    // Ejecutado por Fecha
    private LineChartModel chartEjecutadoPorFecha;
    //private List<ItemRubro> listaEjecutadoFecha;
    //private PieChartModel chartEjecutadoPorRubro;

    // Saldo del proyecto
    private float saldoProyecto = 0.0f;
    
    // Monto ejecutado del proyecto
    private float ejecutadoProyecto = 0.0f;

    // Monto rendido por proyecto
    private float rendidoProyecto = 0.0f;

    // Monto pendiente de rendicion por proyecto
    private float pendienteRendicionProyecto;
    
    float porcentajeEjecutado = 0.0f;
    
//    // total desembolsado
//    private float totalDesembolsado;

    public SolicitudFacade getSolicitudFacade() {
        return solicitudFacade;
    }

    public DesembolsoFacade getDesembolsoFacade() {
        return desembolsoFacade;
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

    public LineChartModel getChartEjecutadoPorFecha() {
        return chartEjecutadoPorFecha;
    }

    public float getSaldoProyecto() {
        return saldoProyecto;
    }

    public float getEjecutadoProyecto() {
        return ejecutadoProyecto;
    }
    
     public void setEjecutadoProyecto(float ejecutado) {
        this.ejecutadoProyecto=ejecutado;
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

    public float getPorcentajeEjecutado() {
        return porcentajeEjecutado;
    }

    public void setPorcentajeEjecutado(float porcentajeEjecutado) {
        this.porcentajeEjecutado = porcentajeEjecutado;
    }
    
    public String getPorcentajeEjecutadoString(){
        return String.format("%.02f", porcentajeEjecutado);
    }
    

//    public float getTotalDesembolsado() {
//        return totalDesembolsado;
//    }

    /**
     * Creates a new instance of IndicadoresController
     */
    public IndicadoresController() {

    }

    /**
     * Método que ejecuta los calculos de obtencion de saldos y ejecucion por
     * rubro
     *
     */
    public void obtenerCalculosPorRubro() {

        calcularTotalesPorProyecto();

        calcularSaldosPorRubro();

        calcularEjecutadoPorRubro();

        crearIndicadorEjecutado();

        generarChartEjecutadoPorRubro();
        
        generarChartEjecutadoPorFecha();
        
        //calcularTotalDesembolsado();
        
        calcularSaldoProyecto();

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
        listaSolicitudes.addAll(getSolicitudFacade().obtenerEjecucionPorProyecto(proyectocontroller.getSelected().getId()));
        listaSolicitudes.addAll(getSolicitudFacade().obtenerRendicionAEvaluarPorProyecto(proyectocontroller.getSelected().getId()));
        listaSolicitudes.addAll(getSolicitudFacade().obtenerRendidasPorProyecto(proyectocontroller.getSelected().getId()));

        // Lista de solicitudes disponibles
        List<Solicitud> listaSolicitudesDisponibles = new ArrayList<Solicitud>();

        // Llenamos el hashmap de saldos y la lista de ejecucion
        for (Rubro r : getRubroFacade().findAll()) {
            saldos.put(r.getAbreviado(), 0.0f);
        }

        // Obtenemos los importes de solicitud disponibles
        for (PresupuestoTarea p : listaPresupuestosTarea) {

            Solicitud solicitud = new Solicitud();
            solicitud.setPresupuestotarea(p);
            solicitud.setImporte(p.getTotal());
            solicitud.setDisponible(solicitud.getImporte());

            // buscamos si el presupuestotarea ya fue solicitado anteriormente y aprobado o ejecutado, de ser asi, restamos el importe o lo removemos
            Iterator i = listaSolicitudes.iterator();

            while (i.hasNext()) {
                Solicitud solicitudAprobadaOEnEjecucion = (Solicitud) i.next();

                // si encontramos el presupuestotarea en una solicitud anterior y siendo una solicitud aprobada
                if (p.getId() == solicitudAprobadaOEnEjecucion.getPresupuestotarea().getId()) {
                    // restamos al importe de la solicitud disponible, el importe de la solicitud anterior
                    //solicitud.setImporte(p.getTotal().subtract(solicitudAprobadaOEnEjecucion.getImporte()));
                    solicitud.setImporte(solicitud.getImporte().subtract(solicitudAprobadaOEnEjecucion.getImporte()));
                    solicitud.setDisponible(solicitud.getImporte());
                }
            }

            // Agregamos a la lista de solicitudes disponibles si el importe es distinto de cero y sumamos al saldo
            if (solicitud.getImporte().floatValue() != 0.00) {
                listaSolicitudesDisponibles.add(solicitud);

                // Acumulamos en la lista de saldos
                saldos.put(solicitud.getPresupuestotarea().getRubro().getAbreviado(), saldos.get(solicitud.getPresupuestotarea().getRubro().getAbreviado()) + solicitud.getDisponible().floatValue());

                

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

        // Llenamos la lista de solicitudes (Aprobadas, en ejecucion, evaluacion y Rendidas)
        listaSolicitudes = this.getSolicitudFacade().obtenerAprobadasPorProyecto(proyectocontroller.getSelected().getId());
        listaSolicitudes.addAll(this.getSolicitudFacade().obtenerEjecucionPorProyecto(proyectocontroller.getSelected().getId()));
        listaSolicitudes.addAll(this.getSolicitudFacade().obtenerRendicionAEvaluarPorProyecto(proyectocontroller.getSelected().getId()));
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
                }
            }

        }

        // Ejecutado por Rubro
        listaEjecutadoRubro = ejecutado;

    }

    public void generarChartEjecutadoPorRubro() {
        chartEjecutadoPorRubro = new PieChartModel();

        for (ItemRubro ir : listaEjecutadoRubro) {
            chartEjecutadoPorRubro.set(ir.nombrerubro, ir.monto);
        }

        chartEjecutadoPorRubro.setMouseoverHighlight(true);
        chartEjecutadoPorRubro.setDiameter(65);
        chartEjecutadoPorRubro.setShowDataLabels(true);

        //chartEjecutadoPorRubro.setTitle("Simple Pie");
        //chartEjecutadoPorRubro.setLegendPosition("w");
        chartEjecutadoPorRubro.setExtender("torta");
        chartEjecutadoPorRubro.setSeriesColors("21B2CE,FF964A,5ACB73,9C4DAD,CE4131,DED7A5");

    }

    public void generarChartEjecutadoPorFecha() {

        List<Solicitud> colSolicitudes;
        List<Desembolso> colDesembolsos;

        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);

        // Llenamos la lista de solicitudes (Aprobadas)
        colSolicitudes = this.getSolicitudFacade().obtenerAprobadasPorProyecto(proyectocontroller.getSelected().getId());
        colSolicitudes.addAll(this.getSolicitudFacade().obtenerEjecucionPorProyecto(proyectocontroller.getSelected().getId()));
        colSolicitudes.addAll(this.getSolicitudFacade().obtenerRendicionAEvaluarPorProyecto(proyectocontroller.getSelected().getId()));
        colSolicitudes.addAll(this.getSolicitudFacade().obtenerRendidasPorProyecto(proyectocontroller.getSelected().getId()));
        
        // Llenamos la lista de desembolsos
        colDesembolsos = this.getDesembolsoFacade().obtenerPorProyecto(proyectocontroller.getSelected().getId());

        // Ordenar las colecciones por fecha
        // SOLICITUDES
        Collections.sort(colSolicitudes, new Comparator<Solicitud>() {
            public int compare(Solicitud o1, Solicitud o2) {
                if (o1.getFechasolicitud() == null || o2.getFechasolicitud() == null) {
                    return 0;
                }
                return o1.getFechasolicitud().compareTo(o2.getFechasolicitud());
            }
        });
        
        // DESEMBOLSOS
        Collections.sort(colDesembolsos, new Comparator<Desembolso>() {
            public int compare(Desembolso o1, Desembolso o2) {
                if (o1.getFechacarga() == null || o2.getFechacarga()== null) {
                    return 0;
                }
                return o1.getFechacarga().compareTo(o2.getFechacarga());
            }
        });
        
        // Formato de fecha
        //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
        
        // Serie de Solicitudes
        chartEjecutadoPorFecha = new LineChartModel();
        
        LineChartSeries solcitudes = new LineChartSeries();
        //solcitudes.setLabel("Solicitudes");
 
        //solcitudes.set("2014-01-01", 51);
        
        float as = 0f;
        
        for(Solicitud s : colSolicitudes){
            as = as + s.getImporte().floatValue();
            solcitudes.set(sdf.format(s.getFechaaprobacion()),as);
        }
 
        // Serie de Desembolsos
        LineChartSeries desembolsos = new LineChartSeries();
        //desembolsos.setLabel("Desembolsos");
 
        float ad = 0f;
        
        for(Desembolso d : colDesembolsos){
            ad = ad + d.getMonto().floatValue();
            desembolsos.set(sdf.format(d.getFechacarga()),ad);
        }

        chartEjecutadoPorFecha.addSeries(desembolsos);
        chartEjecutadoPorFecha.addSeries(solcitudes);
         
        chartEjecutadoPorFecha.setTitle(""); // Zoom for Details
        chartEjecutadoPorFecha.setZoom(false);
        chartEjecutadoPorFecha.getAxis(AxisType.Y); //.setLabel("Values");
        DateAxis axis = new DateAxis(""); // Fechas
        //axis.setTickAngle(-50);
        //axis.setMax("2016-02-01");
        axis.setTickFormat("%b %#d, %y"); // %b %#d, %y
        
        chartEjecutadoPorFecha.setLegendRows(0);
        chartEjecutadoPorFecha.setLegendCols(0);
        chartEjecutadoPorFecha.setShadow(false);
        chartEjecutadoPorFecha.setShowPointLabels(false);
         
        chartEjecutadoPorFecha.getAxes().put(AxisType.X, axis);
        
        chartEjecutadoPorFecha.setExtender("ejecutadofecha");
        chartEjecutadoPorFecha.setSeriesColors("21B2CE,CE4131,FF964A,5ACB73,9C4DAD,DED7A5");

    }

    // Indicador de Ejecucion
    public MeterGaugeChartModel getIndicadorEjecutado() {

        if (indicadorEjecutado == null) {
            crearIndicadorEjecutado();
        }

        return indicadorEjecutado;
    }

    private MeterGaugeChartModel inicializarModeloIndicadorPorcentaje() {
        List<Number> intervalos = new ArrayList<Number>() {
            {
                add(25);
                add(50);
                add(75);
            }
        };

        
        porcentajeEjecutado = (ejecutadoProyecto / totalPresupuestoProyecto) * 100;
        
        //return new MeterGaugeChartModel(Integer.parseInt(String.valueOf(ejecutado)), intervalos);
        return new MeterGaugeChartModel((int) porcentajeEjecutado, intervalos);
    }

    private void crearIndicadorEjecutado() {
        indicadorEjecutado = inicializarModeloIndicadorPorcentaje();

        indicadorEjecutado.setSeriesColors("66cc66,93b75f,E7E658,cc6666");

        indicadorEjecutado.setGaugeLabelPosition("bottom");
        //indicadorEjecutado.setShowTickLabels(false);

        //indicadorEjecutado.setIntervalInnerRadius(25);
        //indicadorEjecutado.setIntervalOuterRadius(70);

        indicadorEjecutado.setExtender("indicador");
        
        indicadorEjecutado.setSeriesColors("21B2CE,9C4DAD,FF964A,5ACB73,CE4131,DED7A5");

    }

    public void calcularTotalesPorProyecto() {
        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);

        totalPresupuestoProyecto = this.getPresupuestoTareaFacade().obtenerTotalPorProyecto(proyectocontroller.getSelected().getId());
    }

    public void calcularPendienteRendicion() {
        this.pendienteRendicionProyecto = 12588.12f;
    }

    private void calcularSaldoProyecto() {
        
        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        DesembolsoController desembolsocontroller = (DesembolsoController) context.getApplication().evaluateExpressionGet(context, "#{desembolsoController}", DesembolsoController.class);
        
        float totalDesembolsado = desembolsocontroller.sumarDesembolsos();
        
        saldoProyecto = totalDesembolsado - ejecutadoProyecto;
    }
    
    public void calcularSaldoTotalProyecto() {
        
        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        DesembolsoController desembolsocontroller = (DesembolsoController) context.getApplication().evaluateExpressionGet(context, "#{desembolsoController}", DesembolsoController.class);
        
        float totalDesembolsado = desembolsocontroller.sumarDesembolsos();
        
        saldoProyecto = totalDesembolsado - ejecutadoProyecto;
    }
    
    public float calcularEjecutadoPorProyecto(Integer idProyecto) {

        List<Solicitud> listaSolicitudes;

        // Total Ejecutado del proyecto
        ejecutadoProyecto = 0.0f;        

        // Llenamos la lista de solicitudes (Aprobadas, en ejecucion, evaluacion y Rendidas)
        listaSolicitudes = this.getSolicitudFacade().obtenerAprobadasPorProyecto(idProyecto);
        listaSolicitudes.addAll(this.getSolicitudFacade().obtenerEjecucionPorProyecto(idProyecto));
        listaSolicitudes.addAll(this.getSolicitudFacade().obtenerRendicionAEvaluarPorProyecto(idProyecto));
        listaSolicitudes.addAll(this.getSolicitudFacade().obtenerRendidasPorProyecto(idProyecto));
       
        for (Solicitud solicitud : listaSolicitudes) {
            ejecutadoProyecto += solicitud.getImporte().floatValue();
        }
        return ejecutadoProyecto;
    }
    
     public String calcularPorcenjateEjecutadoPorProyecto(Integer idProyecto) {
        totalPresupuestoProyecto = this.getPresupuestoTareaFacade().obtenerTotalPorProyecto(idProyecto); 
        ejecutadoProyecto=this.calcularEjecutadoPorProyecto(idProyecto);
        porcentajeEjecutado = (ejecutadoProyecto / totalPresupuestoProyecto) * 100;
        return String.format("%.02f", porcentajeEjecutado);
    }
     
     public float calcularTotalesPorProyecto(Integer idProyecto) {
        totalPresupuestoProyecto = this.getPresupuestoTareaFacade().obtenerTotalPorProyecto(idProyecto);        
        return totalPresupuestoProyecto;
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
    
//    public void calcularTotalDesembolsado() {
//        
//        // Obtenemos los controladores necesarios
//        FacesContext context = FacesContext.getCurrentInstance();
//        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
//        
//        totalDesembolsado = 0f;
//        
//        // Llenamos la lista de desembolsos
//        List<Desembolso> colDesembolsos;
//        
//        try{
//            colDesembolsos = this.getDesembolsoFacade().obtenerPorProyecto(proyectocontroller.getSelected().getId());
//        } catch(Exception e){
//            colDesembolsos = new ArrayList<Desembolso>();
//        }
//        
//        for(Desembolso d : colDesembolsos){
//            totalDesembolsado = totalDesembolsado + d.getMonto().floatValue();
//        }
//    }

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
