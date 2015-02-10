package ar.edu.undec.sisgap.controller.view;

import ar.edu.undec.sisgap.controller.SolicitudFacade;
import ar.edu.undec.sisgap.model.Solicitudacta;
import ar.edu.undec.sisgap.controller.view.util.JsfUtil;
import ar.edu.undec.sisgap.controller.view.util.PaginationHelper;
import ar.edu.undec.sisgap.controller.SolicitudactaFacade;
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

@ManagedBean(name = "solicitudactaController")
@SessionScoped
public class SolicitudactaController implements Serializable {

    private Solicitudacta current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.sisgap.controller.SolicitudactaFacade ejbFacade;
    @EJB
    private ar.edu.undec.sisgap.controller.SolicitudFacade ejbFacades;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    
    private List<Solicitud> listaSolicitudes;
    
    private List<Solicitud> listaSolicitudesSeleccionadas;

    public SolicitudactaController() {
    }

    public Solicitudacta getSelected() {
        if (current == null) {
            current = new Solicitudacta();
            selectedItemIndex = -1;
        }
        return current;
    }

    private SolicitudactaFacade getFacade() {
        return ejbFacade;
    }

    public SolicitudFacade getFacades() {
        return ejbFacades;
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
        return "List";
    }

    public String prepareView() {
        current = (Solicitudacta) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
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
        
        // Llenamos la lista de solicitudes que no fueron aprobadas
        listaSolicitudes = getFacades().obtenerIniciadosPorProyecto(proyectocontroller.getSelected().getId());
        
        // Vaciamos la lista de solicitudes seleccionadas
        listaSolicitudesSeleccionadas = new ArrayList<Solicitud>();
        
         // Seteamos la lista de desembolsos para el proyecto actual
        desembolsocontroller.obtenerPorProyecto(proyectocontroller.getSelected().getId());
        
        for(Solicitud s : listaSolicitudes){
            System.out.println("SolicitudActaController - prepareCreate");
            System.out.println(s.toString());
        }
        
        return "Create";
    }

    public String create() {
        try {
            current.setFecha(new Date());
            getFacade().createWithPersist(current);
            
            for(Solicitud s : listaSolicitudesSeleccionadas){
                s.setSolicitudactaid(current);
                s.setFechaaprobacion(new Date());
                getFacades().edit(s);
            }
            
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SolicitudactaCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Solicitudacta) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
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
        current = (Solicitudacta) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
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

}
