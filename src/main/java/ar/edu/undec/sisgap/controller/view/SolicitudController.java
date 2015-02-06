package ar.edu.undec.sisgap.controller.view;

import ar.edu.undec.sisgap.model.Solicitud;
import ar.edu.undec.sisgap.controller.view.util.JsfUtil;
import ar.edu.undec.sisgap.controller.view.util.PaginationHelper;
import ar.edu.undec.sisgap.controller.SolicitudFacade;
import ar.edu.undec.sisgap.model.PresupuestoTarea;
import ar.edu.undec.sisgap.model.Proyecto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DualListModel;

@ManagedBean(name = "solicitudController")
@SessionScoped
public class SolicitudController implements Serializable {

    private Solicitud current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.sisgap.controller.SolicitudFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    private List<Solicitud> itemsDisponibles;
    private List<Solicitud> itemsSolicitados;

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
        selectedItemIndex = -1;
        
        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        PresupuestoTareaController presupuestotareacontroller = (PresupuestoTareaController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoTareaController}", PresupuestoTareaController.class);
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
        EtapaController etapacontroller = (EtapaController) context.getApplication().evaluateExpressionGet(context, "#{etapaController}", EtapaController.class);
        DesembolsoController desembolsocontroller = (DesembolsoController) context.getApplication().evaluateExpressionGet(context, "#{desembolsoController}", DesembolsoController.class);

        // Seteamos la lista de presupuesto tareas para el proyecto actual
        presupuestotareacontroller.establecerListaPresupuestoTareaPorProyecto(proyectocontroller.getSelected().getId());

        // Borramos la lista de items disponibles
        itemsDisponibles = new ArrayList<Solicitud>();

        // Borramos la lista items solicitados
        itemsSolicitados = new ArrayList<Solicitud>();
        
        // Llenamos la lista de solicitudes anteriores
        items = new ListDataModel(getFacade().obtenerPorProyecto(proyectocontroller.getSelected().getId()));

        // Llenamos la lista de items disponibles
        for (PresupuestoTarea p : presupuestotareacontroller.getPresupuestostareas()) {
            Solicitud solicitud = new Solicitud();
            solicitud.setPresupuestotarea(p);
            solicitud.setImporte(p.getTotal());
            
            // buscamos si el presupuestotarea ya fue solicitado anteriormente, de ser asi, restamos el importe o lo removemos
            Iterator i = items.iterator();
            
            while(i.hasNext()){
                Solicitud solicitudAnterior = (Solicitud) i.next();
                
                // si encontramos el presupuestotarea en una solicitud anterior
                if(p.getId() == solicitudAnterior.getPresupuestotarea().getId()){
                    // restamos al importe de la solicitud disponible, el importe de la solicitud anterior
                    solicitud.setImporte(p.getTotal().subtract(solicitudAnterior.getImporte()));
                }
            }
            
            // Agregamos a la lista de solicitudes disponibles si el importe es distinto de cero
            if(solicitud.getImporte().floatValue() != 0.00){
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

        return "Create";
    }

    public String create() {
        try {
//            current.setFechaaprobacion(new Date());
//            current.setFechaejecucion(new Date());
//            current.setFechasolicitud(new Date());
//            current.setObservacion("asdasdasd");
//            
//            // guardamos solicitud
//            getFacade().createWithPersist(current);
//            
//            // Obtenemos el controlador de PresupuestoTarea
//            FacesContext context = FacesContext.getCurrentInstance();
//            PresupuestoTareaController presupuestotareacontroller = (PresupuestoTareaController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoTareaController}", PresupuestoTareaController.class);
//            
//            // para cada presupuestotarea solicitado, creamos un SolicitudItem y lo guardamos
////            for(PresupuestoTarea p : presupuestotareacontroller.getPresupuestostareasitems()){
////                SolicitudItem si = new SolicitudItem();
////                si.setPresupuestoTareaid(p);
////                si.setMonto(p.getTotal());
////                si.setSolicitudid(current);
////                si.setObservacion("askjdajksdk");
////                ejbFacadesi.create(si);
////            }

            try {
                for (Solicitud s : itemsSolicitados) {
                    s.setFechaaprobacion(new Date());
                    s.setFechaejecucion(new Date());
                    s.setFechasolicitud(new Date());
                    s.setObservacion("Observacion askdjlkasd");
                    s.setEstadosolicitudid(current.getEstadosolicitudid());
                    s.setTiposolicitudid(current.getTiposolicitudid());
                    s.setAprobado(false);
                    
                    // Guardamos la solicitud
                    getFacade().create(s);
                }
                System.out.println("SolicitudController - Creacion Finalizada");
            } catch (Exception e) {
                System.out.println("Error en creacion de solicitud");

                e.printStackTrace();
            }

            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SolicitudCreated"));

            return prepareList();
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
//        presupuestotareacontroller.establecerListaPresupuestoTareaPorProyecto(p.getId());
//        
//        // Setear la lista dual del pick list
//        plSolicitudItems = new DualListModel<SolicitudItem>(presupuestotareacontroller.getPresupuestostareas(),new ArrayList<SolicitudItem>());
//    }
    public void agregarItemSolicitado() {

        // agregamos en la lista de solicitados
        itemsSolicitados.add(current);

        // borramos de la lista de disponibles
        itemsDisponibles.remove(current);
    }

    public void quitarItemSolicitado(Solicitud solicitud) {

        // Quita de la lista de solicitados
        this.itemsSolicitados.remove(solicitud);

        // Devuelve el item a la lista de disponibles
        this.itemsDisponibles.add(solicitud);
    }

}
