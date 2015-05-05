package ar.edu.undec.sisgap.controller.view;

import ar.edu.undec.sisgap.controller.EstadosolicitudFacade;
import ar.edu.undec.sisgap.controller.SolicitudFacade;
import ar.edu.undec.sisgap.model.Solicitudacta;
import ar.edu.undec.sisgap.controller.view.util.JsfUtil;
import ar.edu.undec.sisgap.controller.view.util.PaginationHelper;
import ar.edu.undec.sisgap.controller.SolicitudactaFacade;
import ar.edu.undec.sisgap.model.Estadosolicitud;
import ar.edu.undec.sisgap.model.Solicitud;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
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

@ManagedBean(name = "solicitudactaController")
@SessionScoped
public class SolicitudactaController implements Serializable {

    private Solicitudacta current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.sisgap.controller.SolicitudactaFacade ejbFacade;
    @EJB
    private ar.edu.undec.sisgap.controller.SolicitudFacade ejbFacades;
    @EJB
    EstadosolicitudFacade ejbFacadeestado;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    private List<Solicitud> listaSolicitudes;

    private List<Solicitud> listaSolicitudesSeleccionadas;
    
    private Solicitud solicitudActual;

    public SolicitudactaController() {
    }

    public Solicitudacta getSelected() {
        if (current == null) {
            current = new Solicitudacta();
            selectedItemIndex = -1;
        }
        return current;
    }

    public void setSelected(Solicitudacta solicitudacta) {
        current = solicitudacta;
    }

    private SolicitudactaFacade getFacade() {
        return ejbFacade;
    }

    public SolicitudFacade getFacades() {
        return ejbFacades;
    }

    public EstadosolicitudFacade getEjbFacadeestado() {
        return ejbFacadeestado;
    }

    public List<Solicitud> getListaSolicitudes() {
        return listaSolicitudes;
    }

    public void setListaSolicitudes(List<Solicitud> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }

    public List<Solicitud> getListaSolicitudesSeleccionadas() {
        return listaSolicitudesSeleccionadas;
    }

    public void setListaSolicitudesSeleccionadas(List<Solicitud> listaSolicitudesSeleccionadas) {
        this.listaSolicitudesSeleccionadas = listaSolicitudesSeleccionadas;
    }

    public Solicitud getSolicitudActual() {
        return solicitudActual;
    }

    public void setSolicitudActual(Solicitud solicitudActual) {
        this.solicitudActual = solicitudActual;
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
        //current = (Solicitudacta) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Solicitudacta();
        selectedItemIndex = -1;

        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        //SolicitudController solicitudcontroller = (SolicitudController) context.getApplication().evaluateExpressionGet(context, "#{solicitudController}", SolicitudController.class);
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
        DesembolsoController desembolsocontroller = (DesembolsoController) context.getApplication().evaluateExpressionGet(context, "#{desembolsoController}", DesembolsoController.class);
        RendicionController rendicioncontroller = (RendicionController) context.getApplication().evaluateExpressionGet(context, "#{rendicionController}", RendicionController.class);
        PresupuestoController presupuestocontroller = (PresupuestoController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoController}", PresupuestoController.class);
        PresupuestoTareaController presupuestotareacontroller = (PresupuestoTareaController) context.getApplication().evaluateExpressionGet(context, "#{presupuestoTareaController}", PresupuestoTareaController.class);
        EtapaController etapacontroller = (EtapaController) context.getApplication().evaluateExpressionGet(context, "#{etapaController}", EtapaController.class);
        
        // Seteamos el presupuesto
        presupuestocontroller.findProyecto(proyectocontroller.getSelected().getId());
        presupuestocontroller.sumarGastosView();
        
        // Seteamos el tree de etapas y tareas para el proyecto actual
        etapacontroller.armarTreeEtapasYTareasPorProyecto();
        
        // armamos el arbol de nodos de presupuesto tarea
        presupuestotareacontroller.armarPresupuestoGeneral();
        
        // Seteamos las rendiciones para los indicadores
        rendicioncontroller.prepararRendicion();

        // Llenamos la lista de solicitudes que no fueron aprobadas
        listaSolicitudes = getFacades().obtenerIniciadasPorProyecto(proyectocontroller.getSelected().getId());

        // Vaciamos la lista de solicitudes seleccionadas
        listaSolicitudesSeleccionadas = new ArrayList<Solicitud>();

        // Seteamos la lista de desembolsos para el proyecto actual
        desembolsocontroller.obtenerPorProyecto(proyectocontroller.getSelected().getId());

        for (Solicitud s : listaSolicitudes) {
            System.out.println("SolicitudActaController - prepareCreate");
            System.out.println(s.toString());
        }

        return "CreateSolicitudActa";
    }

    public String create() {
        try {

            // Persistimos el Acta de Solicitud
            current.setFecha(new Date());
            getFacade().createWithPersist(current);

            // Estado de la solicitud
            Estadosolicitud estado;

            try {
                // Estado de la Solicitud = "Aprobada"
                estado = getEjbFacadeestado().find(2);
            } catch (Exception e) {
                estado = null;
                System.out.println("EstadosolicitudFacade: problema de recuperacion");
                e.printStackTrace();
            }

            // Para cada Solicitud seleccionada, actualizar con el nuevo estado, la fecha de aprobacion 
            // y el nro de acta correspondiente
            for (Solicitud s : listaSolicitudesSeleccionadas) {
                s.setSolicitudactaid(current);
                s.setFechaaprobacion(new Date());
                s.setEstadosolicitudid(estado);
                getFacades().edit(s);
            }

            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SolicitudactaCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String rechazar() {
        try {

            // Persistimos el Acta de Solicitud
            // current.setFecha(new Date());
            // getFacade().createWithPersist(current);
            // Estado de la solicitud
            Estadosolicitud estado;

            try {
                // Estado de la Solicitud = "Rechazada"
                estado = getEjbFacadeestado().find(3);
            } catch (Exception e) {
                estado = null;
                System.out.println("EstadosolicitudFacade: problema de recuperacion");
                e.printStackTrace();
            }

            // Para cada Solicitud seleccionada, actualizar con el nuevo estado, la fecha de aprobacion 
            // y el nro de acta correspondiente
            for (Solicitud s : listaSolicitudesSeleccionadas) {
                //s.setSolicitudactaid(current);
                s.setFechaaprobacion(new Date());
                s.setEstadosolicitudid(estado);
                getFacades().edit(s);
            }

            JsfUtil.addSuccessMessage("Las solicitudes fueron rechazadas satisfactoriamente");
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }
    
    public void agregarAprobada(){
        
        // obtenermos el estado de la solicitud "Aprobada" id=2
        Estadosolicitud estadoAprobada;
        
        try{
            estadoAprobada = this.ejbFacadeestado.find(2);
        } catch(Exception e){
            estadoAprobada = null;
            e.printStackTrace();
        }
        
        // cambiamos el estado de la solicitud agregada
        this.solicitudActual.setEstadosolicitudid(estadoAprobada);
        
        // damos la fecha de aprobacion
        this.solicitudActual.setFechaaprobacion(new Date());
        
        // agregamos a la lista
        this.listaSolicitudesSeleccionadas.add(solicitudActual);
        
        // quitamos de la lista de disponibles
        this.listaSolicitudes.remove(solicitudActual);
    }
    
    public void agregarRechazada(){
        
        // obtenermos el estado de la solicitud "Rechazada" id=3
        Estadosolicitud estadoRechazada;
        
        try{
            estadoRechazada = this.ejbFacadeestado.find(3);
        } catch(Exception e){
            estadoRechazada = null;
            e.printStackTrace();
        }
        
        // cambiamos el estado de la solicitud agregada
        this.solicitudActual.setEstadosolicitudid(estadoRechazada);
        
        // damos la fecha de rechazo
        this.solicitudActual.setFechaaprobacion(new Date());
        
        // agregamos a la lista de seleccionadas
        this.listaSolicitudesSeleccionadas.add(solicitudActual);
        
        // quitamos de la lista de disponibles
        this.listaSolicitudes.remove(solicitudActual);
 
    }
    
    public void eliminarDeLista(Solicitud solicitud){
        
        // obtenermos el estado de la solicitud "Iniciada" id=1
        Estadosolicitud estadoIniciada;
        
        try{
            estadoIniciada = this.ejbFacadeestado.find(1);
        } catch(Exception e){
            estadoIniciada = null;
            e.printStackTrace();
        }
        
        // cambiamos el estado de la solicitud eliminada
        solicitud.setEstadosolicitudid(estadoIniciada);
        
        // cambiamos la fecha de aprobacion a null y vaciamos la observacion
        solicitud.setFechaaprobacion(null);
        solicitud.setObsevaluacion("");
        
        // la quitamos de la lista
        this.listaSolicitudesSeleccionadas.remove(solicitud);
        
        // la agregamos nuevamente a los disponibles
        this.listaSolicitudes.add(solicitud);
    }
    
    public float sumarAprobadas(){
        
        float r = 0f;
        
        if(listaSolicitudesSeleccionadas != null && listaSolicitudesSeleccionadas.size() > 0){
            for(Solicitud s : listaSolicitudesSeleccionadas){
                // si esta aprobada, sumamos su valor
                if(s.getEstadosolicitudid().getId().equals(2)){
                    r = r + s.getImporte().floatValue();
                }
            }
        }
        
        return r;
    }
    
    public float sumarRechazadas(){
        float r = 0f;
        
        if(listaSolicitudesSeleccionadas != null && listaSolicitudesSeleccionadas.size() > 0){
            for(Solicitud s : listaSolicitudesSeleccionadas){
                // si esta rechazada, sumamos su valor
                if(s.getEstadosolicitudid().getId().equals(3)){
                    r = r + s.getImporte().floatValue();
                }
            }
        }
        
        return r;
    }

    public String prepareEdit() {
        //current = (Solicitudacta) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SolicitudactaUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        //current = (Solicitudacta) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SolicitudactaDeleted"));
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

    @FacesConverter(forClass = Solicitudacta.class)
    public static class SolicitudactaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SolicitudactaController controller = (SolicitudactaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "solicitudactaController");
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
            if (object instanceof Solicitudacta) {
                Solicitudacta o = (Solicitudacta) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Solicitudacta.class.getName());
            }
        }

    }

    public void obtenerPorProyecto(int proyectoid) {
        items = new ListDataModel(this.ejbFacade.obtenerPorProyecto(proyectoid));
    }

    

}
