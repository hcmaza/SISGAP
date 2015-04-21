package ar.edu.undec.sisgap.controller.view;

import ar.edu.undec.sisgap.controller.ConfiguracionFacade;
import ar.edu.undec.sisgap.controller.EstadosolicitudFacade;
import ar.edu.undec.sisgap.model.Solicitud;
import ar.edu.undec.sisgap.controller.view.util.JsfUtil;
import ar.edu.undec.sisgap.controller.view.util.PaginationHelper;
import ar.edu.undec.sisgap.controller.SolicitudFacade;
import ar.edu.undec.sisgap.controller.TiposolicitudFacade;
import ar.edu.undec.sisgap.model.Estadosolicitud;
import ar.edu.undec.sisgap.model.PresupuestoTarea;
import ar.edu.undec.sisgap.model.Proyecto;
import ar.edu.undec.sisgap.model.Rubro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.chart.MeterGaugeChartModel;

@ManagedBean(name = "solicitudController")
@SessionScoped
public class SolicitudController implements Serializable {

    private Solicitud current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.sisgap.controller.SolicitudFacade ejbFacade;
    @EJB
    private ar.edu.undec.sisgap.controller.ConfiguracionFacade ejbFacadec;
    @EJB
    private TiposolicitudFacade ejbFacadeTipo;
    @EJB
    private EstadosolicitudFacade ejbFacadeEstado;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    private List<Solicitud> itemsDisponibles = null;
    private List<Solicitud> itemsSolicitados = null;
    private List<Solicitud> itemsAprobados = null;
    private List<Solicitud> itemsDisponiblesNuevo = null;
    
    // Charts
    private MeterGaugeChartModel indicadorEjecutado;
    
    //tabs solicitudes
    private String tabseleccionado = "Anticipo";
    

    public SolicitudController() {
    }

    public Solicitud getSelected() {
        if (current == null) {
            current = new Solicitud();
            selectedItemIndex = -1;
        }
        return current;
    }

    public void setSelected(Solicitud solicitud) {
        current = solicitud;
    }

    private SolicitudFacade getFacade() {
        return ejbFacade;
    }

    public ConfiguracionFacade getEjbFacadec() {
        return ejbFacadec;
    }

    public TiposolicitudFacade getEjbFacadeTipo() {
        return ejbFacadeTipo;
    }

    public EstadosolicitudFacade getEjbFacadeEstado() {
        return ejbFacadeEstado;
    }

    public List<Solicitud> getItemsDisponibles() {
        return itemsDisponibles;
    }

    public void setItemsDisponibles(List<Solicitud> itemsDisponibles) {
        this.itemsDisponibles = itemsDisponibles;
    }

    public List<Solicitud> getItemsSolicitados() {
        return itemsSolicitados;
    }

    public void setItemsSolicitados(List<Solicitud> itemsSolicitados) {
        this.itemsSolicitados = itemsSolicitados;
    }

    public List<Solicitud> getItemsAprobados() {
        return itemsAprobados;
    }

    public void setItemsAprobados(List<Solicitud> itemsAprobados) {
        this.itemsAprobados = itemsAprobados;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "ListPorProyecto";
    }

    public String prepareView() {
        //current = (Solicitud) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Solicitud();
        current.setFechasolicitud(new Date());
        selectedItemIndex = -1;

        armarSolicitudesYDesembolsos();

        return "CreateSolicitud";
    }
    
    public void armarSolicitudesYDesembolsos(){
        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        PresupuestoTareaController presupuestotareacontroller = (PresupuestoTareaController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoTareaController}", PresupuestoTareaController.class);
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
        EtapaController etapacontroller = (EtapaController) context.getApplication().evaluateExpressionGet(context, "#{etapaController}", EtapaController.class);
        DesembolsoController desembolsocontroller = (DesembolsoController) context.getApplication().evaluateExpressionGet(context, "#{desembolsoController}", DesembolsoController.class);

        // Seteamos la lista de presupuesto tareas para el proyecto actual
        presupuestotareacontroller.establecerListaPresupuestoTareaBienesPorProyecto(proyectocontroller.getSelected().getId(), tabseleccionado);

        // Borramos la lista de items disponibles
        itemsDisponibles = new ArrayList<Solicitud>();

        // Borramos la lista items solicitados
        itemsSolicitados = new ArrayList<Solicitud>();

        // Llenamos la lista de solicitudes anteriores
        items = new ListDataModel(getFacade().obtenerPorProyecto(proyectocontroller.getSelected().getId()));

        // Llenamos la lista de solicitudes aprobadas anteriores
        itemsAprobados = getFacade().obtenerAprobadasPorProyecto(proyectocontroller.getSelected().getId());
        

        // Llenamos la lista de items disponibles
        for (PresupuestoTarea p : presupuestotareacontroller.getPresupuestostareas()) {
            Solicitud solicitud = new Solicitud();
            solicitud.setFechasolicitud(new Date());
            solicitud.setPresupuestotarea(p);
            solicitud.setImporte(p.getTotal());
            solicitud.setDisponible(solicitud.getImporte());
            solicitud.setEstadosolicitudid(ejbFacadeEstado.find(1));

            // buscamos si el presupuestotarea ya fue solicitado anteriormente, de ser asi, restamos el importe o lo removemos
            Iterator i = items.iterator();

            while (i.hasNext()) {
                Solicitud solicitudAnterior = (Solicitud) i.next();

                // si encontramos el presupuestotarea en una solicitud anterior y no es una solicitud con estado "Rechazada"
                //if (p.getId() == solicitudAnterior.getPresupuestotarea().getId() && solicitudAnterior.getEstadosolicitudid().getId() != 3) {
                if (p.getId() == solicitudAnterior.getPresupuestotarea().getId()) {
                    // restamos al importe de la solicitud disponible, el importe de la solicitud anterior
                    solicitud.setImporte(p.getTotal().subtract(solicitudAnterior.getImporte()));
                    solicitud.setDisponible(solicitud.getImporte());
                    System.out.println("llllll");
                    
                }
            }

            // Agregamos a la lista de solicitudes disponibles si el importe es distinto de cero
            if (solicitud.getImporte().floatValue() != 0.00) {
                itemsDisponibles.add(solicitud);
            }
        }
        
        // Vaciamos la lista de presupuestos tareas solicitados
        //presupuestotareacontroller.vaciarListaPresupuestoTareaSolicitadosPorProyecto();
        //items = new ListDataModel(new ArrayList<Solicitud>());
        // Seteamos el tree de etapas y tareas para el proyecto actual
        etapacontroller.armarTreeEtapasYTareasPorProyecto();

        // Seteamos la lista de desembolsos para el proyecto actual
        desembolsocontroller.obtenerPorProyecto(proyectocontroller.getSelected().getId());
        
        //h:coloco la copia de itemsolicitados tal cual es al principio
        this.itemsDisponiblesNuevo = this.itemsDisponibles;
        
        //h:filtro por defecto para anticipo
         this.itemsDisponibles = new ArrayList<Solicitud>();
         for(Solicitud s : this.itemsDisponiblesNuevo ){
             if(!s.getPresupuestotarea().getRubro().getId().equals(4) && !s.getPresupuestotarea().getRubro().getId().equals(5) ){
                   this.itemsDisponibles.add(s);
             }
        }
            
       
    }

    public String create() {
        try {

            // Guardamos cada solicitud realizada
            try {
                for (Solicitud s : itemsSolicitados) {

                    s.setFechasolicitud(new Date());
                    s.setEstadosolicitudid(getEjbFacadeEstado().find(1));
                    //s.setTiposolicitudid(current.getTiposolicitudid());

                    // Guardamos la solicitud
                    getFacade().create(s);
                }
                System.out.println("SolicitudController - Creacion Finalizada");
            } catch (Exception e) {
                System.out.println("Error en creacion de solicitud");

                e.printStackTrace();
            }

            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SolicitudCreated"));

            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        //current = (Solicitud) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SolicitudUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        //current = (Solicitud) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "ListPorProyecto";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SolicitudDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    @FacesConverter(forClass = Solicitud.class)
    public static class SolicitudControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SolicitudController controller = (SolicitudController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "solicitudController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Solicitud) {
                Solicitud o = (Solicitud) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Solicitud.class.getName());
            }
        }

    }

    /**
     * Obtener Solicitudes por proyecto
     *
     * @param proyectoid
     * @param tabseleccionado
     */
    public void obtenerPorProyecto(int proyectoid) {
        items = new ListDataModel(this.ejbFacade.obtenerPorProyecto(proyectoid));

//        Iterator i = items.iterator();
//
//        while (i.hasNext()) {
//            System.out.println("SOLICITUD CONTROLLER: obtenerPorProyecto: " + ((Solicitud) i.next()).getId());
//        }
    }

//    public void establecerSolicitudItemsDisponibles(Proyecto p){
//        //this.setSolicitudItems(new DualListModel<SolicitudItem>);
//        
//        FacesContext context = FacesContext.getCurrentInstance();
//        
//        //ProyectoController proyectocontroller= (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
//        PresupuestoTareaController presupuestotareacontroller= (PresupuestoTareaController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoTareaController}", PresupuestoTareaController.class);
//        
//        // Obtener los presupuestos_tarea del proyecto
//        presupuestotareacontroller.establecerListaPresupuestoTareaBienesPorProyecto(p.getId());
//        
//        // Setear la lista dual del pick list
//        plSolicitudItems = new DualListModel<SolicitudItem>(presupuestotareacontroller.getPresupuestostareas(),new ArrayList<SolicitudItem>());
//    }
    
    public void agregarItemSolicitado() {

        float maxanticipo = 0;
        System.out.println("tipo de solicitud---------"+ejbFacadeTipo.findWithTiposolicitud(tabseleccionado));
        current.setTiposolicitudid(ejbFacadeTipo.findWithTiposolicitud(tabseleccionado));
        Solicitud solicitud = current;

        /*// Validar que se haya elegido un tipo de solicitud
        if (current.getTiposolicitudid().getTiposolicitud() == null) {
            return;
        } else {
            System.out.println("Current Tipo de Solicitud: " + current.getTiposolicitudid().getTiposolicitud());
        }
                */
        // Validar que no supere la cantidad disponible (los desembolsos menos las solicitudes anteriores)
        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        DesembolsoController desembolsocontroller = (DesembolsoController) context.getApplication().evaluateExpressionGet(context, "#{desembolsoController}", DesembolsoController.class);

        float dineroDisponible = desembolsocontroller.sumarDesembolsos() - sumarSolicitudesAprobadas();

        System.out.println("dinero disponible : " + dineroDisponible);
        System.out.println("sumarSolicitado() : " + sumarSolicitado());
        System.out.println("current.getImporte() : " + current.getImporte().floatValue());
               
        if (sumarSolicitado() + current.getImporte().floatValue() > dineroDisponible) {
            System.out.println("El importe del item a solicitar supera el dinero disponible");

            FacesMessage mensaje = new FacesMessage("Error", "El importe del item a solicitar supera el dinero disponible");
            context.addMessage(null, mensaje);

            // Restauramos el valor de la solicitad al total de presupuesto tarea
            current.setImporte(current.getDisponible());

            return;
        }

        // Obtenemos el monto maximo para anticipos desde la configuracion
        try {
            maxanticipo = Float.parseFloat(ejbFacadec.findAtributo("maxanticipo").getValor());
           // System.out.println("maxanticipo=" + String.valueOf(maxanticipo));
        } catch (Exception e) {
            System.out.println("Error en obtencion de parametro 'maxanticipo' desde la base de datos");
            e.printStackTrace();
        }

        // Validar que si es un anticipo, no supere la cantidad permitida para este tipo de solicitud
        if (this.tabseleccionado.equals("Anticipo") && (sumarSolicitadoAnticipo() + current.getImporte().floatValue()) > maxanticipo) {

            System.out.println("En un anticipo no se puede superar el valor maximo de un anticipo");

            context.addMessage(null, new FacesMessage("Inválido", "Al Solicitar un anticipo no se puede superar el máximo permitido. El monto máximo permitido es de $" + maxanticipo));

            // Restauramos el valor de la solicitad al total de presupuesto tarea
            current.setImporte(current.getDisponible());

            return;
        }

        // Transferencia de objetos entre listas
        // Completo
        if (solicitud.getImporte().floatValue() == solicitud.getDisponible().floatValue()) {

            System.out.println("Transferencia TOTAL");

            // borramos de la lista de disponibles
            ListIterator iDisp1 = itemsDisponibles.listIterator();
            //h
            Solicitud sencontrado = new Solicitud();
            
            while (iDisp1.hasNext()) {
                Solicitud sd = (Solicitud) iDisp1.next();

                if (sd.getPresupuestotarea().getId().equals(solicitud.getPresupuestotarea().getId())) {
                    iDisp1.remove();
                    //h
                    sencontrado = solicitud;
                    
                    
                }
            }
            
            //h
            if(sencontrado != null){
                System.out.println("sencontrado");
                System.out.println(sencontrado.getPresupuestotarea().getDescripcion());
                System.out.println(sencontrado.getImporte());
                System.out.println("sencontrado");
                Iterator i = this.itemsDisponiblesNuevo.iterator();
                while(i.hasNext()){
                    Solicitud s = (Solicitud)i.next();
                    if(s.getPresupuestotarea().equals(sencontrado.getPresupuestotarea())){
                        i.remove();
                       
                    }
                }
            
               
            }
            // agregamos a la lista de solicitados
            //itemsSolicitados.add(solicitud);
            // agregamos a la lista de solicitados, checkeamos si hay un item del 
            // mismo presupuestotarea anterior, si es asi lo sumamos
            ListIterator iSols1 = itemsSolicitados.listIterator();
            boolean encontradod = false;

            while (iSols1.hasNext()) {
                Solicitud sd = (Solicitud) iSols1.next();

                if (sd.getPresupuestotarea().getId().equals(solicitud.getPresupuestotarea().getId())) {
                    iSols1.remove();

                    sd.setImporte(solicitud.getImporte().add(sd.getImporte()));

                    iSols1.add(sd);

                    encontradod = true;
                }
            }

            if (!encontradod) {
                itemsSolicitados.add(solicitud);
            }

            // Parcial
        } else {

            //System.out.println("CURRENT: " + solicitud.getId() + " - Desc: " + solicitud.getPresupuestotarea().getDescripcion());
            System.out.println("Transferencia PARCIAL");

           

            // agregamos a la lista de solicitados, checkeamos si hay un item del 
            // mismo presupuestotarea anterior, si es asi lo sumamos
           ListIterator iSols2 = itemsSolicitados.listIterator();
            boolean encontradop = false;

            while (iSols2.hasNext()) {
                Solicitud sd = (Solicitud) iSols2.next();

                if (sd.getPresupuestotarea().getId().equals(solicitud.getPresupuestotarea().getId())) {
                    iSols2.remove();

                    sd.setImporte(solicitud.getImporte().add(sd.getImporte()));

                    iSols2.add(sd);

                    encontradop = true;
                }
            }

            if (!encontradop) {
                itemsSolicitados.add(solicitud);
            }

            // agregamos a la lista de disponibles, una solicitud con el monto restante
            Solicitud sDisponible = solicitud;
            System.out.println("---------solic disponible-------"+solicitud.getDisponible());
            System.out.println("---------importe-------"+solicitud.getImporte());
            sDisponible.setDisponible(solicitud.getDisponible().subtract(solicitud.getImporte()));
            System.out.println("ooooo");
           //sDisponible.setImporte(sDisponible.getDisponible());
           int contador = 0;
           int puesto = 0;
           for(Solicitud s : this.itemsDisponiblesNuevo){
              
               if(s.getPresupuestotarea().equals(solicitud.getPresupuestotarea())){
                   puesto = contador;
               }
               ++contador;
           }
           this.itemsDisponiblesNuevo.set(puesto, sDisponible);
            //hthis.itemsDisponiblesNuevo.add(sDisponible);
            contador = 0;
           puesto = 0;
           for(Solicitud s : this.itemsDisponibles){
              
               if(s.getPresupuestotarea().equals(solicitud.getPresupuestotarea())){
                   puesto = contador;
               }
               ++contador;
           }
           this.itemsDisponibles.set(puesto, sDisponible);
            
            System.out.println("------");
          
           
        }
    }

    public void quitarItemSolicitado(Solicitud solicitud) {

        System.out.println("quitar: solicitud-disponible: " + solicitud.getDisponible() + " solicitud-importe: " + solicitud.getImporte());

        // Si se quita un objeto que se solicito completo
        if (solicitud.getDisponible().floatValue() == solicitud.getImporte().floatValue()) {

            // se quita de la lista de solicitados
            Iterator i = this.itemsSolicitados.iterator();
            while(i.hasNext()){
                if(((Solicitud)i.next()).getPresupuestotarea().equals(solicitud.getPresupuestotarea())){
                    i.remove();
                }
            }
           // this.itemsSolicitados.remove(solicitud);

            // se devuelve el item a la lista de disponibles
            this.itemsDisponibles.add(solicitud);
            //h
            this.itemsDisponiblesNuevo.add(solicitud);
            System.out.println("quitar 1");
            // Si se quita un objeto que fue solicitado parcialmente
        } else {
            // Quita de la lista de solicitados
            Iterator it = this.itemsSolicitados.iterator();
            while(it.hasNext()){
                if(((Solicitud)it.next()).getPresupuestotarea().equals(solicitud.getPresupuestotarea())){
                    it.remove();
                }
            }
            // buscamos la solicitud disponible para sumar el monto
            ListIterator i = itemsDisponibles.listIterator();

            while (i.hasNext()) {

                Solicitud s = (Solicitud) i.next();

                if (s.getPresupuestotarea().getId() == solicitud.getPresupuestotarea().getId()) {
                    // creamos una nueva solicitud para sumar nuevamente los montos
                    Solicitud sDisponible = new Solicitud(solicitud);
                    sDisponible.setDisponible(solicitud.getImporte().add(s.getDisponible()));
                    sDisponible.setImporte(sDisponible.getDisponible());
                    System.out.println("iiiiiii");
                    // modificamos la solicitud con los montos correctos
                    i.set(sDisponible);
                    
                    break;

                    //s.setDisponible(s.getDisponible().add(solicitud.getImporte()));
                    //itemsDisponibles.add(s);
                }
            }
            
               // buscamos la solicitudNuevo disponible para sumar el monto
            ListIterator in = itemsDisponiblesNuevo.listIterator();

            while (in.hasNext()) {

                Solicitud s = (Solicitud) in.next();

                if (s.getPresupuestotarea().getId() == solicitud.getPresupuestotarea().getId()) {
                    // creamos una nueva solicitud para sumar nuevamente los montos
                    Solicitud sDisponible = new Solicitud(solicitud);
                    sDisponible.setDisponible(solicitud.getImporte().add(s.getDisponible()));
                    sDisponible.setImporte(sDisponible.getDisponible());
                    System.out.println("iiiiiii");
                    // modificamos la solicitud con los montos correctos
                    in.set(sDisponible);
                   
                    break;

                    //s.setDisponible(s.getDisponible().add(solicitud.getImporte()));
                    //itemsDisponibles.add(s);
                }
            }


//            for (Solicitud s : itemsDisponibles) {
//                if (s.getPresupuestotarea().getId() == solicitud.getPresupuestotarea().getId()) {
//
//                    // borramos el encontrado
//                    itemsDisponibles.remove(s);
//
//                    // creamos una nueva solicitud para sumar nuevamente los montos
//                    Solicitud sDisponible = new Solicitud(solicitud);
//                    sDisponible.setDisponible(solicitud.getImporte().add(s.getDisponible()));
//                    sDisponible.setImporte(sDisponible.getDisponible());
//
//                    // agregamos la solicitud con los montos correctos
//                    itemsDisponibles.add(sDisponible);
//                    
//                    break;
//
//                    //s.setDisponible(s.getDisponible().add(solicitud.getImporte()));
//                    //itemsDisponibles.add(s);
//                }
//            }
        }
    }

    public float sumarDisponible() {
        float r = 0;

        for (Solicitud s : itemsDisponibles) {
            //r += s.getImporte().floatValue();
            r += s.getDisponible().floatValue();
        }

        return r;
    }

    public float sumarSolicitado() {
        float r = 0;

        for (Solicitud s : itemsSolicitados) {
            r += s.getImporte().floatValue();
        }

        return r;
    }
    
    public float sumarSolicitadoAnticipo() {
        float r = 0;

        for (Solicitud s : itemsSolicitados) {
            if(s.getTiposolicitudid().getId()==1){
                r += s.getImporte().floatValue();
            }
        }

        return r;
    }

    public float sumarSolicitudesAprobadas() {
        float r = 0;

        for (Solicitud s : itemsAprobados) {
            r += s.getImporte().floatValue();
        }

        return r;
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
         
        return new MeterGaugeChartModel(54, intervalos);
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
    
    public HashMap<String,Float> obtenerSaldosRubro(){

        HashMap<String,Float> saldos = new HashMap<String,Float>();
        
        FacesContext context = FacesContext.getCurrentInstance();
        RubroController rubrocontroller = (RubroController) context.getApplication().evaluateExpressionGet(context, "#{rubroController}", RubroController.class);        
        
        for(Rubro r : rubrocontroller.getRubroslist()){
            saldos.put(r.getRubro(), 0.0f);
        }
        
        // Para cada solicitud disponible
        for(Solicitud sd : itemsDisponibles){
            saldos.put(sd.getPresupuestotarea().getRubro().getRubro(), saldos.get(sd.getPresupuestotarea().getRubro().getRubro()) + sd.getDisponible().floatValue() );
           // System.out.println("Saldo Rubro: " + sd.getPresupuestotarea().getRubro().getRubro() + " - " + saldos.get(sd.getPresupuestotarea().getRubro().getRubro()));
        }
        
        
        return saldos;
    }
    
    //Cuando clickea un tab de solicitudes elije un tipo de solicitud por default es 0
    public void onTabChange(TabChangeEvent event) {
        tabseleccionado = event.getTab().getTitle();
        System.out.println("------------------"+tabseleccionado);
        this.itemsDisponibles = new ArrayList<Solicitud>();
        //filtro por rubros
            if(tabseleccionado.equals("Anticipo") | tabseleccionado.equals("Adquisición")){
                for(Solicitud s : this.itemsDisponiblesNuevo ){
                    // Si NO es de Recursos Humanos, Consultoria y Traslados
                    if(!s.getPresupuestotarea().getRubro().getId().equals(4) && !s.getPresupuestotarea().getRubro().getId().equals(5) && !s.getPresupuestotarea().getRubro().getId().equals(7) ){
                        this.itemsDisponibles.add(s);
                    }
                }
            }
            
            // Si es de Recursos Humanos, Consultoria
            if(tabseleccionado.equals("Certificación")){
                for(Solicitud s : this.itemsDisponiblesNuevo ){
                    
                    if(s.getPresupuestotarea().getRubro().getId().equals(4) | s.getPresupuestotarea().getRubro().getId().equals(5) ){
                        this.itemsDisponibles.add(s);
                    }
                }
            }
            
            if(tabseleccionado.equals("Ampliación de Presupuesto")){
                for(Solicitud s : this.itemsDisponibles ){
                    
                   // if(s.getPresupuestotarea().getRubro().getId().equals(4) | s.getPresupuestotarea().getRubro().getId().equals(5) ){
                        this.itemsDisponibles.add(s);
                   // }
                }
            }

        
    }

    public String getTabseleccionado() {
        return tabseleccionado;
    }

    public void setTabseleccionado(String tabselecionado) {
        this.tabseleccionado = tabselecionado;
    }
    
    public List<Solicitud> getItemsDisponiblesNuevo() {
        return itemsDisponiblesNuevo;
    }

    public void setItemsDisponiblesNuevo(List<Solicitud> itemsDisponiblesNuevo) {
        this.itemsDisponiblesNuevo = itemsDisponiblesNuevo;
    }

    public void subirArchivoSolicitud(FileUploadEvent event) {
        System.out.println("Subiendo Archivo de Solicitud - Presupuesto");

        try {
            //current = new Archivorendicion();
            current.setArchivonombre(event.getFile().getFileName());
            current.setArchivo(event.getFile().getContents());
            //getListaArchivos().add(current);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Información", "El archivo" + current.getArchivonombre() + " fue subido satisfactoriamente!"));
        } catch (Exception e) {
            System.out.println("Excepcion en RendicionController - subirArchivo");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error", "Ocurrió un error al subir el archivo"));
            e.printStackTrace();
        }
    }
    
}
