package ar.edu.undec.sisgap.controller.view;

import ar.edu.undec.sisgap.model.Modificacionpresupuesto;
import ar.edu.undec.sisgap.controller.view.util.JsfUtil;
import ar.edu.undec.sisgap.controller.view.util.PaginationHelper;
import ar.edu.undec.sisgap.controller.ModificacionpresupuestoFacade;
import ar.edu.undec.sisgap.model.Etapa;
import ar.edu.undec.sisgap.model.PresupuestoTarea;
import ar.edu.undec.sisgap.model.Solicitud;
import ar.edu.undec.sisgap.model.Tarea;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "modificacionpresupuestoController")
@SessionScoped
public class ModificacionpresupuestoController implements Serializable {

    private Modificacionpresupuesto current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.sisgap.controller.ModificacionpresupuestoFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    
    private TreeNode root = new DefaultTreeNode();
    
    private List<Modificacionpresupuesto> modificaciones;
    
    private PresupuestoItem piSeleccionado;

    public PresupuestoItem getPiSeleccionado() {
        return piSeleccionado;
    }

    public void setPiSeleccionado(PresupuestoItem piSeleccionado) {
        this.piSeleccionado = piSeleccionado;
    }

    public TreeNode getRoot() {
        return root;
    }

    public List<Modificacionpresupuesto> getModificaciones() {
        
        if(modificaciones == null){
            modificaciones = new ArrayList<Modificacionpresupuesto>();
        }
        
        return modificaciones;
    }

    public void setModificaciones(List<Modificacionpresupuesto> modificaciones) {
        this.modificaciones = modificaciones;
    }

    public ModificacionpresupuestoController() {
    }

    public Modificacionpresupuesto getSelected() {
        if (current == null) {
            current = new Modificacionpresupuesto();
            selectedItemIndex = -1;
        }
        return current;
    }

    private ModificacionpresupuestoFacade getFacade() {
        return ejbFacade;
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
        current = (Modificacionpresupuesto) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Modificacionpresupuesto();
        selectedItemIndex = -1;

        FacesContext context = FacesContext.getCurrentInstance();
        SolicitudController solicitudcontroller = (SolicitudController) context.getApplication().evaluateExpressionGet(context, "#{solicitudController}", SolicitudController.class);
        
        solicitudcontroller.armarSolicitudesDesembolsosYRendicion();
        
        armarModificacionesPresupuestoNodos();
        
        return "CreateModificacionPresupuesto";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ModificacionpresupuestoCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Modificacionpresupuesto) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ModificacionpresupuestoUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Modificacionpresupuesto) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ModificacionpresupuestoDeleted"));
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

    @FacesConverter(forClass = Modificacionpresupuesto.class)
    public static class ModificacionpresupuestoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ModificacionpresupuestoController controller = (ModificacionpresupuestoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "modificacionpresupuestoController");
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
            if (object instanceof Modificacionpresupuesto) {
                Modificacionpresupuesto o = (Modificacionpresupuesto) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Modificacionpresupuesto.class.getName());
            }
        }

    }
    
    public void armarModificacionesPresupuestoNodos() {

        FacesContext context = FacesContext.getCurrentInstance();
        EtapaController etapacontroller = (EtapaController) context.getApplication().evaluateExpressionGet(context, "#{etapaController}", EtapaController.class);
        
        SolicitudController solicitudcontroller = (SolicitudController) context.getApplication().evaluateExpressionGet(context, "#{solicitudController}", SolicitudController.class);
        
        BigDecimal sumatotalsolicitudespst = BigDecimal.ZERO;
        
        BigDecimal sumatotaltarea = BigDecimal.ZERO;
        BigDecimal sumatotalsolicitudestarea = BigDecimal.ZERO;
        BigDecimal sumatotalmodificacionestarea = BigDecimal.ZERO;

        BigDecimal sumatotaletapa = BigDecimal.ZERO;
        BigDecimal sumatotalsolicitudesetapa = BigDecimal.ZERO;
        BigDecimal sumatotalmodificacionesetapa = BigDecimal.ZERO;

        List<PresupuestoItem> sumatoriaporetapa = new ArrayList<PresupuestoItem>();
        List<PresupuestoItem> sumatoriaportarea = new ArrayList<PresupuestoItem>();
        List<PresupuestoItem> sumatoriaporpst = new ArrayList<PresupuestoItem>();

        int ie = 0;
        int it = 0;
        int ip = 0;
        
        System.out.println("PASO >> Inicio FOR Etapas");

        for (Etapa etapa : etapacontroller.getEtapas()) {

            sumatotaletapa = BigDecimal.ZERO;
            sumatotalsolicitudesetapa = BigDecimal.ZERO;
            sumatotalmodificacionesetapa = BigDecimal.ZERO;
            
            System.out.println("PASO >> Inicio FOR Tareas");

            for (Tarea tarea : etapa.getTareaList()) {

                sumatotaltarea = BigDecimal.ZERO;
                sumatotalsolicitudestarea = BigDecimal.ZERO;
                sumatotalmodificacionestarea = BigDecimal.ZERO;
                
                System.out.println("PASO >> Inicio FOR PresupuestoTarea");

                for (PresupuestoTarea psTarea : tarea.getPresupuestoTareaList()) {

                    sumatotalsolicitudespst = BigDecimal.ZERO;
                    
                    System.out.println("PASO >> Inicio FOR Solicitudes");
                    
                    // Verificar si hay solicitudes anteriores para el presupuesto_tarea
                    
                    for(Solicitud s : solicitudcontroller.getItemsAprobados() ){
                        
                        if(psTarea.getId().equals(s.getPresupuestotarea().getId())){
                            //psTarea.setTotal(psTarea.getTotal().subtract(s.getImporte()));
                            
                            System.out.println("Suma Solicitudes Antes: " + sumatotalsolicitudespst.floatValue());
                            
                            System.out.println("Presupuesto_tarea: " + psTarea.getId() + " | " + psTarea.getDescripcion());
                            System.out.println("Solicitud: " + s.getPresupuestotarea().getId() + " | " + s.getPresupuestotarea().getDescripcion() + " - Importe= " + s.getImporte().floatValue());
                            
                            sumatotalsolicitudespst = sumatotalsolicitudespst.add(s.getImporte());
                            
                            System.out.println("Suma Solicitudes Despues: " + sumatotalsolicitudespst.floatValue());
                        }
                    }
                    
                    System.out.println("PASO >> Inicio FOR Modificaciones");
                    
                    //  Verificar si hay modificaciones para el presupuesto_tarea
                    for(Modificacionpresupuesto mp : this.getModificaciones()){
                        
                        System.out.println("PASO >> SI 1");
                        
                        if(mp.getPresupuestotareaingreso().getId().equals(psTarea.getId())){
                            sumatotalmodificacionestarea.add(mp.getModificacion());
                        }
                        
                        System.out.println("PASO >> SI 2");
                        
                        if(mp.getPresupuestotareaegreso().getId().equals(psTarea.getId())){
                            sumatotalmodificacionestarea.subtract(mp.getModificacion());
                        }
                    }
                    
                    PresupuestoItem pi3 = this.new PresupuestoItem();
                    pi3.setPresupuestoTarea(psTarea);
                    pi3.setDescripcion(psTarea.getDescripcion());
                    pi3.setImporteOriginal(psTarea.getTotal().floatValue());
                    pi3.setImporteSolicitudes(sumatotalsolicitudespst.floatValue());
                    pi3.setImporteSinModificacion(pi3.getImporteOriginal() - pi3.getImporteSolicitudes());
                    pi3.setImporteModificacion(sumatotalmodificacionestarea.floatValue());
                    pi3.setImporteFinal(pi3.getImporteSinModificacion() + pi3.getImporteModificacion());

                    sumatotaltarea = sumatotaltarea.add(psTarea.getTotal());
                    sumatotalsolicitudestarea = sumatotalsolicitudestarea.add(sumatotalsolicitudespst);
                    
                    sumatoriaporpst.add(ip, pi3);
                    
                    ip++;
                
                }
                
                System.out.println("PASO >> SUMAR TAREA: " + it);
                
                sumatotaletapa = sumatotaletapa.add(sumatotaltarea);
                sumatotalsolicitudesetapa = sumatotalsolicitudesetapa.add(sumatotalsolicitudestarea);
                sumatotalmodificacionesetapa = sumatotalmodificacionesetapa.add(sumatotalmodificacionestarea);
                
                PresupuestoItem pi2 = this.new PresupuestoItem();
                
                PresupuestoTarea pst2 = new PresupuestoTarea();
                pst2.setTarea(tarea);
                pst2.setDescripcion("Tarea: " + tarea.getTarea());
                pi2.setPresupuestoTarea(pst2);
                
                //pi2.setDescripcion("Tarea: " + tarea.getTarea());
                pi2.setImporteOriginal(sumatotaltarea.floatValue());
                pi2.setImporteSolicitudes(sumatotalsolicitudestarea.floatValue());
                pi2.setImporteSinModificacion(pi2.getImporteOriginal() - pi2.getImporteSolicitudes());
                pi2.setImporteModificacion(sumatotalmodificacionestarea.floatValue());
                pi2.setImporteFinal(pi2.getImporteSinModificacion() + sumatotalmodificacionestarea.floatValue());
                
                sumatoriaportarea.add(it, pi2);
                it++;

            }
            
            System.out.println("PASO >> SUMAR ETAPA: " + ie);
            
            PresupuestoItem pi = this.new PresupuestoItem();
            
            PresupuestoTarea pst = new PresupuestoTarea();
            Tarea t = new Tarea();
            t.setEtapaid(etapa);
            pst.setTarea(t);
            pst.setDescripcion("Etapa: " + etapa.getEtapa());
            pi.setPresupuestoTarea(pst);
            
            pi.setImporteOriginal(sumatotaletapa.floatValue());
            pi.setImporteSolicitudes(sumatotalsolicitudesetapa.floatValue());
            pi.setImporteSinModificacion(pi.getImporteOriginal() - pi.getImporteSolicitudes());
            pi.setImporteModificacion(sumatotalmodificacionesetapa.floatValue());
            pi.setImporteFinal(pi.getImporteSinModificacion() + sumatotalmodificacionesetapa.floatValue());

            sumatoriaporetapa.add(ie, pi);

            ie++;

        }
        
        System.out.println("PASO >> ARMAR ROOT");

        root = new DefaultTreeNode(new PresupuestoItem(), null);
        root.setExpanded(false);

        ie = 0;
        it = 0;
        ip = 0;
        
        System.out.println("PASO >> ARMAR ROOT - ETAPAS");

        for (Etapa etapa : etapacontroller.getEtapas()) {

            TreeNode et = new DefaultTreeNode(sumatoriaporetapa.get(ie), root);
            et.setExpanded(false);
            
            System.out.println("PASO >> ARMAR ROOT - TAREAS");

            for (Tarea tarea : etapa.getTareaList()) {
                
                TreeNode tar = new DefaultTreeNode(sumatoriaportarea.get(it), et);
                tar.setExpanded(false);

                System.out.println("PASO >> ARMAR ROOT - PRESUPUESTO_TAREAS");
                
                for (PresupuestoTarea p : tarea.getPresupuestoTareaList()) {
                    //TreeNode tp = new DefaultTreeNode(p, tar);
                    
                    TreeNode tp = new DefaultTreeNode(sumatoriaporpst.get(ip), tar);
                    
                    ip++;
                }
                it++;
            }
            ie++;
        }

        System.out.println("");

    }
    
    public class PresupuestoItem{
        
        private PresupuestoTarea presupuestoTarea;
        private String descripcion;
        private Float importeOriginal;
        private Float importeSolicitudes;
        private Float importeSinModificacion;
        private Float importeModificacion;
        private Float importeFinal;

        public PresupuestoTarea getPresupuestoTarea() {
            return presupuestoTarea;
        }

        public void setPresupuestoTarea(PresupuestoTarea presupuestoTarea) {
            this.presupuestoTarea = presupuestoTarea;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public Float getImporteOriginal() {
            return importeOriginal;
        }

        public void setImporteOriginal(Float importeOriginal) {
            this.importeOriginal = importeOriginal;
        }

        public Float getImporteSolicitudes() {
            return importeSolicitudes;
        }

        public void setImporteSolicitudes(Float importeSolicitudes) {
            this.importeSolicitudes = importeSolicitudes;
        }

        public Float getImporteSinModificacion() {
            return importeSinModificacion;
        }

        public void setImporteSinModificacion(Float importeSinModificacion) {
            this.importeSinModificacion = importeSinModificacion;
        }

        public Float getImporteModificacion() {
            return importeModificacion;
        }

        public void setImporteModificacion(Float importeModificacion) {
            this.importeModificacion = importeModificacion;
        }

        public Float getImporteFinal() {
            return importeFinal;
        }

        public void setImporteFinal(Float importeFinal) {
            this.importeFinal = importeFinal;
        }
    }
    
    public void quitarItemSolicitado(Modificacionpresupuesto modificacion) {

        // se quita de la lista de modificaciones
        Iterator i = this.modificaciones.iterator();
        
        while(i.hasNext()){
            if(((Modificacionpresupuesto) i.next()).getPresupuestotareaegreso().equals(modificacion.getPresupuestotareaegreso()) && ((Modificacionpresupuesto) i.next()).getPresupuestotareaingreso().equals(modificacion.getPresupuestotareaingreso())) {
                i.remove();
            }
        }
    }
    
    public float sumarModificaciones() {
        float r = 0;

        for (Modificacionpresupuesto p : modificaciones) {
            r += p.getModificacion().floatValue();
        }

        return r;
    }

}
