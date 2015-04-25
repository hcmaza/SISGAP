package ar.edu.undec.sisgap.controller.view;

import ar.edu.undec.sisgap.model.Archivorendicion;
import ar.edu.undec.sisgap.controller.view.util.JsfUtil;
import ar.edu.undec.sisgap.controller.view.util.PaginationHelper;
import ar.edu.undec.sisgap.controller.ArchivorendicionFacade;
import com.lowagie.text.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.faces.event.PhaseId;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean(name = "archivorendicionController")
@SessionScoped
public class ArchivorendicionController implements Serializable {

    private Archivorendicion current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.sisgap.controller.ArchivorendicionFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    private List<Archivorendicion> listaArchivos;

    public ArchivorendicionController() {
    }

    public Archivorendicion getSelected() {
        if (current == null) {
            current = new Archivorendicion();
            selectedItemIndex = -1;
        }
        return current;
    }

    public void setSelected(Archivorendicion archivorendicion) {
        current = archivorendicion;
    }

    private ArchivorendicionFacade getFacade() {
        return ejbFacade;
    }

    public List<Archivorendicion> getListaArchivos() {
        if (listaArchivos == null) {
            listaArchivos = new ArrayList<Archivorendicion>();
        }
        return listaArchivos;
    }

    public void setListaArchivos(List<Archivorendicion> listaArchivos) {
        this.listaArchivos = listaArchivos;
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
        current = (Archivorendicion) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Archivorendicion();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ArchivorendicionCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Archivorendicion) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ArchivorendicionUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Archivorendicion) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ArchivorendicionDeleted"));
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

    @FacesConverter(forClass = Archivorendicion.class)
    public static class ArchivorendicionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ArchivorendicionController controller = (ArchivorendicionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "archivorendicionController");
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
            if (object instanceof Archivorendicion) {
                Archivorendicion o = (Archivorendicion) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Archivorendicion.class.getName());
            }
        }
    }
    
    public void nuevoArchivoRendicion(){
       System.out.println("Nuevo Archivo Rendicion Inicio"); 
       //current = null;
       current = new Archivorendicion();
       System.out.println("Nuevo Archivo rendicion Fin"); 
    }
    
    public void agregarArchivoLista(){
        System.out.println("Agregar Archivo rendicion Inicio"); 
        getListaArchivos().add(current);
        System.out.println("Lista Archivos Rendicion: " + getListaArchivos().size()); 
        System.out.println("Agregar Archivo rendicion Fin"); 
    }

    public void subirArchivoRendicion(FileUploadEvent event) {
        System.out.println("Subiendo Archivo de Rendición");

        try {
            //current = new Archivorendicion();
            current.setNombrearchivo(event.getFile().getFileName());
            current.setArchivo(event.getFile().getContents());
            //getListaArchivos().add(current);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Información", "El archivo" + current.getNombrearchivo() + " fue subido satisfactoriamente!"));
        } catch (Exception e) {
            System.out.println("Excepcion en RendicionController - subirArchivo");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error", "Ocurrió un error al subir el archivo"));
            e.printStackTrace();
        }
    }

    public void removerArchivoLista() {
        getListaArchivos().remove(current);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Información", "El comprobante del proveedor: " + current.getProveedor() + " - Nº: " +  current.getNrofactura() + " fue borrado"));
    }

    public StreamedContent obtenerImagen() throws IOException {

        StreamedContent imagen = null;

        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
            String nombreArchivo = context.getExternalContext().getRequestParameterMap().get("archivo");

            for (Archivorendicion ar : getListaArchivos()) {
                if (ar.getNombrearchivo().equals(nombreArchivo)) {
                    return new DefaultStreamedContent(new ByteArrayInputStream(ar.getArchivo()));
                }
            }
        }
        return null;
    }

}
