package ar.edu.undec.sisgap.controller.view;

import ar.edu.undec.sisgap.controller.ArchivorendicionFacade;
import ar.edu.undec.sisgap.controller.EstadosolicitudFacade;
import ar.edu.undec.sisgap.model.Rendicion;
import ar.edu.undec.sisgap.controller.view.util.JsfUtil;
import ar.edu.undec.sisgap.controller.view.util.PaginationHelper;
import ar.edu.undec.sisgap.controller.RendicionFacade;
import ar.edu.undec.sisgap.controller.SolicitudFacade;
import ar.edu.undec.sisgap.controller.TiposolicitudFacade;
import ar.edu.undec.sisgap.model.Archivorendicion;
import ar.edu.undec.sisgap.model.Estadosolicitud;
import ar.edu.undec.sisgap.model.Solicitud;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import org.primefaces.event.FileUploadEvent;

@ManagedBean(name = "rendicionController")
@SessionScoped
public class RendicionController implements Serializable {

    private Rendicion current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.sisgap.controller.RendicionFacade ejbFacade;
    @EJB
    private ar.edu.undec.sisgap.controller.SolicitudFacade ejbFacades;
    @EJB
    private ar.edu.undec.sisgap.controller.EstadosolicitudFacade ejbFacadees;
    @EJB
    private ar.edu.undec.sisgap.controller.ArchivorendicionFacade ejbFacadear;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    private List<Solicitud> listaSolicitudes;

    private List<Solicitud> listaSolicitudesSeleccionadas;

    public RendicionController() {
    }

    public Rendicion getSelected() {
        if (current == null) {
            current = new Rendicion();
            selectedItemIndex = -1;
        }
        return current;
    }

    public void setSelected(Rendicion rendicion) {
        current = rendicion;
    }

    private RendicionFacade getFacade() {
        return ejbFacade;
    }

    public SolicitudFacade getFacades() {
        return ejbFacades;
    }

    public EstadosolicitudFacade getFacadees() {
        return ejbFacadees;
    }

    public ArchivorendicionFacade getFacadear() {
        return ejbFacadear;
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
        return "ListPorProyecto";
    }

    public String prepareView() {
        //current = (Rendicion) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Rendicion();
        selectedItemIndex = -1;

        System.out.println("Inicio Rendicion - prepareCreate");

        current.setFecha(new Date());

        // Obtenemos los controladores necesarios
        FacesContext context = FacesContext.getCurrentInstance();
        //SolicitudController solicitudcontroller = (SolicitudController) context.getApplication().evaluateExpressionGet(context, "#{solicitudController}", SolicitudController.class);
        ProyectoController proyectocontroller = (ProyectoController) context.getApplication().evaluateExpressionGet(context, "#{proyectoController}", ProyectoController.class);
        ArchivorendicionController archivorendicioncontroller = (ArchivorendicionController) context.getApplication().evaluateExpressionGet(context, "#{archivorendicionController}", ArchivorendicionController.class);

        // Vaciamos la lista de archivos de rendicion
        archivorendicioncontroller.setListaArchivos(new ArrayList<Archivorendicion>());

        // Llenamos la lista de solicitudes "Aprobadas", es decir que ya pueden ser rendidas.
        listaSolicitudes = getFacades().obtenerAprobadasPorProyecto(proyectocontroller.getSelected().getId());

        // Vaciamos la lista de solicitudes seleccionadas
        listaSolicitudesSeleccionadas = new ArrayList<Solicitud>();

        return "Create";
    }

    public String create() {
        try {

            if (!listaSolicitudesSeleccionadas.isEmpty()) {
                //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Información", "El archivo" + current.getNombrearchivo() +  " fue subido satisfactoriamente!"));

                current.setFecha(new Date());

                // se guarda la rendicion
                getFacade().createWithPersist(current);

                // Estado de la solicitud
                Estadosolicitud estado;

                try {
                    // Estado de la Solicitud = "Rendida"
                    estado = getFacadees().find(5);
                } catch (Exception e) {
                    estado = null;
                    System.out.println("EstadosolicitudFacade: problema de recuperacion");
                    e.printStackTrace();
                }

                // Para cada Solicitud seleccionada, actualizar con el nuevo estado y rendicion correspondiente
                for (Solicitud s : listaSolicitudesSeleccionadas) {
                    s.setRendicionid(current);
                    //s.setFechaejecucion(new Date());
                    s.setEstadosolicitudid(estado);
                    getFacades().edit(s);
                }

                // Para cada archivo de rendicion subido
                // Obtenemos el controladores necesario
                FacesContext context = FacesContext.getCurrentInstance();
                ArchivorendicionController arcontroller = (ArchivorendicionController) context.getApplication().evaluateExpressionGet(context, "#{archivorendicionController}", ArchivorendicionController.class);
                
                for(Archivorendicion ar : arcontroller.getListaArchivos()){
                    ar.setRendicionid(current);
                    getFacadear().create(ar);
                }

                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RendicionCreated"));

            }

            return prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        //current = (Rendicion) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RendicionUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        //current = (Rendicion) getItems().getRowData();
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
            // buscar solicitud para actualizarla
            Solicitud s;
            
            try{
                s = getFacades().obtenerPorRendicion(current.getId());
                
                // Sin Rendicion
                s.setRendicionid(null);
            } catch(Exception e){
                s = null;
                e.printStackTrace();
            }
            
            // buscar estado de solicitud anterior: "Aprobada"
            Estadosolicitud es;
            
            try{
                
                es = getFacadees().find(2);
                
            }catch(Exception e){
                es = null;
                e.printStackTrace();
            }
            
            s.setEstadosolicitudid(es);
            
            //Actualizamos la Solicitud
            getFacades().edit(s);
            
            
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RendicionDeleted"));
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

    @FacesConverter(forClass = Rendicion.class)
    public static class RendicionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RendicionController controller = (RendicionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "rendicionController");
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
            if (object instanceof Rendicion) {
                Rendicion o = (Rendicion) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Rendicion.class.getName());
            }
        }

    }

    public void obtenerPorProyecto(int proyectoid) {
        items = new ListDataModel(this.ejbFacade.obtenerPorProyecto(proyectoid));
    }

}
