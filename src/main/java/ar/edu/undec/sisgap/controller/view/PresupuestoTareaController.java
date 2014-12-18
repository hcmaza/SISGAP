package ar.edu.undec.sisgap.controller.view;

import ar.edu.undec.sisgap.model.PresupuestoTarea;
import ar.edu.undec.sisgap.controller.view.util.JsfUtil;
import ar.edu.undec.sisgap.controller.view.util.PaginationHelper;
import ar.edu.undec.sisgap.controller.PresupuestoTareaFacade;
import ar.edu.undec.sisgap.controller.RubroFacade;
import ar.edu.undec.sisgap.model.Etapa;
import ar.edu.undec.sisgap.model.ProyectoAgente;
import ar.edu.undec.sisgap.model.Rubro;
import ar.edu.undec.sisgap.model.Tarea;
import ar.edu.undec.sisgap.model.TareaAgente;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.chart.PieChartModel;

@ManagedBean(name = "presupuestoTareaController")
@SessionScoped
public class PresupuestoTareaController implements Serializable {

    private PresupuestoTarea current;
    private DataModel items = null;
    @EJB
    private ar.edu.undec.sisgap.controller.PresupuestoTareaFacade ejbFacade;
    @EJB
    private RubroFacade ejbFacadeRubro;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private List<PresupuestoTarea> presupuestostareasitems ;
     private List<PresupuestoTarea> presupuestostareas ;
    private PresupuestoTarea current2 = new PresupuestoTarea();
    private TreeNode root = new DefaultTreeNode();
    private PieChartModel pieModelAportes;
    private PieChartModel pieModelRubros;
    private BigDecimal sumagastoorganismo;
    private BigDecimal sumagastocomitente;
    private BigDecimal sumagastouniversidad;
    private BigDecimal sumatotal;
    
    

    public PresupuestoTareaController() {
    }

    public PresupuestoTarea getSelected() {
        if (current == null) {
            current = new PresupuestoTarea();
            selectedItemIndex = -1;
        }
        return current;
    }

    private PresupuestoTareaFacade getFacade() {
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
        current = (PresupuestoTarea) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new PresupuestoTarea();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PresupuestoTareaCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (PresupuestoTarea) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PresupuestoTareaUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (PresupuestoTarea) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PresupuestoTareaDeleted"));
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

    @FacesConverter(forClass = PresupuestoTarea.class)
    public static class PresupuestoTareaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PresupuestoTareaController controller = (PresupuestoTareaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "presupuestoTareaController");
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
            if (object instanceof PresupuestoTarea) {
                PresupuestoTarea o = (PresupuestoTarea) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PresupuestoTarea.class.getName());
            }
        }

    }
    
  public List<PresupuestoTarea> getPresupuestostareasitems()
  {
       return this.presupuestostareasitems;
  }
  
  public void setPresupuestostareasitems(List<PresupuestoTarea> presupuestostareasitems)
  {
    this.presupuestostareasitems = presupuestostareasitems;
  }
  
  public String reinit()
  {
    this.current2 = new PresupuestoTarea();
    this.current2.setAportecomitente(BigDecimal.ZERO);
    this.current2.setAporteorganismo(BigDecimal.ZERO);
    this.current2.setAporteuniversidad(BigDecimal.ZERO);
    this.current2.setCantidad(BigDecimal.ONE);
    this.current2.setCostounitario(BigDecimal.ZERO);
    return null;
  }
  
   public String reinit(Rubro rubro)
  {
     
    this.current = new PresupuestoTarea();
    this.current.setRubro(rubro);
    this.current.setAportecomitente(BigDecimal.ZERO);
    this.current.setAporteorganismo(BigDecimal.ZERO);
    this.current.setAporteuniversidad(BigDecimal.ZERO);
    this.current.setCantidad(BigDecimal.ONE);
    this.current.setCostounitario(BigDecimal.ZERO);
    return null;
  }
  
  public PresupuestoTarea getCurrent2()
  {
    return this.current2;
  }
  
  public void setCurrent2(PresupuestoTarea current2)
  {
    this.current2 = current2;
  }
  
  public void sumarTotalCurrent()
  {
    this.current.setTotal(this.current.getCostounitario().multiply(this.current.getCantidad()));
  }
  
  public boolean verificarAportes()
  {
    int valor = 0;
    BigDecimal sumaaportes = BigDecimal.ZERO;
    
    sumaaportes = this.current.getAportecomitente().add(this.current.getAporteorganismo()).add(this.current.getAporteuniversidad());
   
      if(sumaaportes.longValue()==this.current.getTotal().longValue()){
         return true; 
      }else{
          return false;
      }
  }
  
  public boolean verificarAportes2()
  {
    int valor = 0;
    BigDecimal sumaaportes = BigDecimal.ZERO;
    
    sumaaportes = this.current2.getAportecomitente().add(this.current2.getAporteorganismo()).add(this.current2.getAporteuniversidad());
   
      if(sumaaportes.longValue()==this.current2.getTotal().longValue()){
         return true; 
      }else{
          return false;
      }
  }
  
  @Deprecated
  public void armarPresupuestoNodos()
  {
    
     FacesContext context = FacesContext.getCurrentInstance();
      EtapaController etapacontroller = (EtapaController) context.getApplication().evaluateExpressionGet(context, "#{etapaController}", EtapaController.class);
      BigDecimal sumatotal = BigDecimal.ZERO;
      BigDecimal sumaaportecomitente = BigDecimal.ZERO;
      BigDecimal sumaaporteuniversidad = BigDecimal.ZERO;
      BigDecimal sumaaporteorganismo = BigDecimal.ZERO;
      Etapa etapaold = new Etapa();
      Tarea tareaold = new Tarea();
      List<PresupuestoTarea> sumatoriaporetapa = new ArrayList<PresupuestoTarea>();
      List<PresupuestoTarea> sumatoriaportarea = new ArrayList<PresupuestoTarea>();
      int ie = 0;
      int it = 0;
      for(Etapa etapa : etapacontroller.getEtapas()){
          for(Tarea tarea : etapa.getTareaList()){
              for(PresupuestoTarea pt : tarea.getPresupuestoTareaList()){
                  
//                 if(sumatoriaporetapa.get(ie)==null){
//                    PresupuestoTarea ptx = new PresupuestoTarea();
//                    sumatoriaporetapa.set(ie,ptx);
//                 }
                   sumaaportecomitente = sumaaportecomitente.add(pt.getAportecomitente());
                   sumaaporteuniversidad = sumaaporteuniversidad.add(pt.getAporteuniversidad());
                   sumaaporteorganismo = sumaaporteorganismo.add(pt.getAporteorganismo());
                   
                
                    
                
                 it++; 
              }
              sumatotal = sumaaportecomitente.add(sumaaporteuniversidad.add(sumaaporteorganismo));
             PresupuestoTarea ptx = new PresupuestoTarea();
             ptx.setDescripcion("Etapa - "+etapa.getEtapa());
             ptx.setAportecomitente(sumaaportecomitente);
             ptx.setAporteuniversidad(sumaaporteuniversidad);
             ptx.setAporteorganismo(sumaaporteorganismo);
             ptx.setTotal(sumatotal);
              sumatoriaporetapa.add(ie,ptx);
              ie++;
               sumaaportecomitente=BigDecimal.ZERO;
                sumaaporteuniversidad=BigDecimal.ZERO;
                sumaaporteorganismo=BigDecimal.ZERO;
                sumatotal=BigDecimal.ZERO;
          }
         
      }
     
      this.root.setExpanded(true);
      
      root = new DefaultTreeNode(new PresupuestoTarea(),null);
                 root.setExpanded(true);
      ie=0;          
                 for(Etapa etapa: etapacontroller.getEtapas()){
                   
                    // PresupuestoTarea e = new PresupuestoTarea();
                       
                    // e.setDescripcion("Etapa - " + etapa.getEtapa());
                     
                     TreeNode et = new DefaultTreeNode(sumatoriaporetapa.get(ie),root);
                     et.setExpanded(true);
                     
                     for(Tarea tarea:etapa.getTareaList()){
                         PresupuestoTarea t = new PresupuestoTarea();
                            t.setDescripcion("Tarea - "+tarea.getTarea());
                         TreeNode tar = new DefaultTreeNode(t,et);
                          tar.setExpanded(true);
                         for(PresupuestoTarea p :tarea.getPresupuestoTareaList()){
                             
                             TreeNode tp = new DefaultTreeNode(p,tar);
                            
                         }
                     }
                        ie++;
                 }
                 
              
      System.out.println("");

  }
  
//  public void armarPresupuestosNodos2(Tarea presu){
//      if(this.getPresupuestosrubrositems().size()>0){
//         
//      }else{
//          
//       this.setPresupuestosrubrositems(this.ejbFacade.findByPresupuesto(presu));
//      }
//        this.root = null;
//        this.root = new DefaultTreeNode(new PresupuestoRubroitem(),null);
//       PresupuestoRubroitem pri;
//        TreeNode treeNode;
//        TreeNode treeNodech;
//      this.root.setExpanded(true);
//      this.root.setSelectable(false);
//     
//      for (Rubro rub : this.ejbFacadeRubro.findAll())
//      {
//          
//        pri = new PresupuestoRubroitem();
//        pri.setRubro(rub);
//        treeNode = new DefaultTreeNode(pri, this.root);
//        treeNode.setExpanded(true);
//        for (PresupuestoRubroitem p : getPresupuestosrubrositems()) {
//          if (p.getRubro().equals(pri.getRubro())) {
//            treeNodech = new DefaultTreeNode(p, treeNode);
//              
//          }
//        }
//      }
//   
//  }
  
  public TreeNode getRoot()
  {
    return this.root;
  }
  
  public void setRoot(TreeNode root)
  {
    this.root = root;
  }
  
  public void agregarPresupuesto()
  {
      FacesContext context = FacesContext.getCurrentInstance();
      TareaController tareacontroller = (TareaController) context.getApplication().evaluateExpressionGet(context, "#{tareaController}", TareaController.class);
      
    if (verificarAportes()){
      
      current2.setTarea(tareacontroller.getSelected());
      this.presupuestostareasitems.add(this.current);
      this.armarPresupuestoGeneral();
      this.acomodarTablapresupuestotareaitem();
      
     //this. armarPresupuestoNodos();
     }
    else
    {
      //reinit();
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La suma de Aportes no puede superar al Total"));
    }
    
    armarGraficosPresupuesto();
  }
  
  
  public boolean agregarPresupuestoRRHHCONSULTOR(TareaAgente taprincipal)
  {
       FacesContext context = FacesContext.getCurrentInstance();
      ProyectoAgenteController proyectoagentecontroller = (ProyectoAgenteController) context.getApplication().evaluateExpressionGet(context, "#{proyectoAgenteController}", ProyectoAgenteController.class);
      ProyectoAgente pax= new ProyectoAgente();
    boolean resultado = false;
      
      
       for(ProyectoAgente pa : proyectoagentecontroller.getEquipotrabajo()){
                    if(pa.getAgente().equals(taprincipal.getAgenteid())){
                        pax = pa;
                    }
       }
       
       int i = 0;
           int lugar = -1;
           for(PresupuestoTarea pts:presupuestostareasitems){
               
               if(pts.getDescripcion().equals(taprincipal.getAgenteid().toString())){
                   lugar = i;
               }else{
                   
               }
               ++i;
           }
           
           if(lugar==-1){
              
                 if (verificarAportes2()){
                     this.presupuestostareasitems.add(current2);
                    this.armarPresupuestoGeneral();
                    this.armarGraficosPresupuesto();
                      resultado = true;
                       
                     }
                    else
                    {

                      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La suma de Aportes no puede superar al Total en el rubro "+ current2.getRubro().getRubro() +" y descripcion " +current2.getDescripcion()));
                      resultado = false;
                    }

                
           }else{
               
                if (verificarAportes2()){
                 this.presupuestostareasitems.set(lugar, current2);
                  this.armarPresupuestoGeneral();
                 this.armarGraficosPresupuesto();
                    resultado = true;   
                     }
                    else
                    {

                      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La suma de Aportes no puede superar al Total en el rubro "+ current2.getRubro().getRubro() +" y descripcion " +current2.getDescripcion()));
                    resultado = false;
                    }

           }
          
       
     return resultado;  
      //this. armarPresupuestoNodos();
      //armarGraficosPresupuesto();
  }
  
  public void eliminarPresupuesto(PresupuestoTarea pt)
  {
    int contador = 0;
    int posicion = 0;
    for (PresupuestoTarea p : getPresupuestostareasitems())
    {
      if ((p.getRubro().equals(pt.getRubro())) && 
        (p.getDescripcion().equals(pt.getDescripcion())) && 
        (p.getTotal().equals(pt.getTotal()))) {
        posicion = contador;
      }
      
      contador++;
    }
    getPresupuestostareasitems().remove(posicion);
    this.armarPresupuestoGeneral();
    //getPresupuestosrubrositems().remove(pri);
     
   // armarPresupuestoNodos();
  }
  
  public void eliminarPresupuesto2(PresupuestoTarea pt)
  {
      
    getPresupuestostareasitems().remove(pt);
     this.armarPresupuestoGeneral();
    //ejbFacade.remove(pri);
  }
  
  public void armarGraficosPresupuesto()
  {
    this.pieModelAportes = new PieChartModel();
    this.pieModelRubros = new PieChartModel();
    BigDecimal sumaaportecomitente = BigDecimal.ZERO;
    BigDecimal sumaaporteorganismo = BigDecimal.ZERO;
    BigDecimal sumaaporteuniversidad = BigDecimal.ZERO;
    for (PresupuestoTarea pt : getPresupuestostareasitems())
    {
      sumaaportecomitente = sumaaportecomitente.add(pt.getAportecomitente());
      sumaaporteorganismo = sumaaporteorganismo.add(pt.getAporteorganismo());
      sumaaporteuniversidad = sumaaporteuniversidad.add(pt.getAporteuniversidad());
     
    }
    this.pieModelAportes.set("Aporte Comitente", sumaaportecomitente);
    this.pieModelAportes.set("Aporte Organismo", sumaaporteorganismo);
    this.pieModelAportes.set("Aporte Universidad", sumaaporteuniversidad);
    


    BigDecimal sumarubro = BigDecimal.ZERO;
    for (Rubro rub : this.ejbFacadeRubro.findAll())
    {
      for (PresupuestoTarea p : getPresupuestostareasitems()) {
        if (rub.getRubro().equals(p.getRubro().getRubro())) {
            if(p.getTotal()==null){
                p.setTotal(BigDecimal.ZERO);
            }
            sumarubro = sumarubro.add(p.getTotal());
        }
      }
      this.pieModelRubros.set(rub.getRubro(), sumarubro);
      sumarubro = BigDecimal.ZERO;
    }
  }
  
  public PieChartModel getPieModelAportes()
  {
      
    if (this.pieModelAportes == null)
    {
      this.pieModelAportes = new PieChartModel();
      
      this.pieModelAportes.set("Gasto Organismo", 0);
      this.pieModelAportes.set("Gasto Comitente", 0);
      this.pieModelAportes.set("Gasto Universidad", 0);
     // pieModelAportes.setDiameter(150);
      pieModelAportes.setLegendPosition("e");
      pieModelAportes.setSeriesColors("21B2CE,9C4DAD,FF964A");
      pieModelAportes.setTitle("Aportes");
      pieModelAportes.setSliceMargin(5); 
      pieModelAportes.setShowDataLabels(true);
    }
    return this.pieModelAportes;
  }
  
  public void setPieModelAportes(PieChartModel pieModelAportes)
  {
    this.pieModelAportes = pieModelAportes;
  }
  
  public PieChartModel getPieModelRubros()
  {
    if (this.pieModelRubros == null)
    {
      this.pieModelRubros = new PieChartModel();
      for (Rubro r : this.ejbFacadeRubro.findAll()) {
        this.pieModelRubros.set(r.getRubro(), Integer.valueOf(0));
      }
      //pieModelRubros.setDiameter(150);
        pieModelRubros.setLegendPosition("e");
        pieModelRubros.setSeriesColors("21B2CE,9C4DAD,FF964A,5ACB73,CE4131,DED7A5");
        pieModelRubros.setTitle("Rubros");
        pieModelRubros.setSliceMargin(5); 
        pieModelRubros.setShowDataLabels(true);
    }
    return this.pieModelRubros;
  }
  
  public void setPieModelRubros(PieChartModel pieModelRubros)
  {
    this.pieModelRubros = pieModelRubros;
  }
  //sumo gastos 
  public void sumarGastos(){
     sumagastoorganismo=BigDecimal.ZERO;
        sumagastocomitente=BigDecimal.ZERO;
        sumagastouniversidad=BigDecimal.ZERO;
        sumatotal=BigDecimal.ZERO;
         
            for(PresupuestoTarea pt:this.getPresupuestostareas()){

                this.sumagastocomitente=this.sumagastocomitente.add(pt.getAportecomitente());
                this.sumagastoorganismo=this.sumagastoorganismo.add(pt.getAporteorganismo());
                this.sumagastouniversidad=this.sumagastouniversidad.add(pt.getAporteuniversidad());
               }
            this.sumatotal = (sumagastoorganismo.add(sumagastocomitente)).add(sumagastouniversidad);
   
       
  }
    public BigDecimal getSumagastoorganismo() {
        return sumagastoorganismo;
    }

    public void setSumagastoorganismo(BigDecimal sumagastoorganismo) {
        this.sumagastoorganismo = sumagastoorganismo;
    }

    public BigDecimal getSumagastocomitente() {
        return sumagastocomitente;
    }

    public void setSumagastocomitente(BigDecimal sumagastocomitente) {
        this.sumagastocomitente = sumagastocomitente;
    }

    public BigDecimal getSumagastouniversidad() {
        return sumagastouniversidad;
    }

    public void setSumagastouniversidad(BigDecimal sumagastouniversidad) {
        this.sumagastouniversidad = sumagastouniversidad;
    }

    public BigDecimal getSumatotal() {
        return sumatotal;
    }

    public void setSumatotal(BigDecimal sumatotal) {
        this.sumatotal = sumatotal;
    }
  
  public void acomodarTablapresupuestotareaitem(){
      List<PresupuestoTarea> ptn = new ArrayList<PresupuestoTarea>();
      for(Rubro r:this.ejbFacadeRubro.findAll())
        for(PresupuestoTarea pt:this.presupuestostareasitems){
                if(pt.getRubro().equals(r)){
                    ptn.add(pt);
                }
        }
      this.presupuestostareasitems = ptn;
  }
  
  
  public List<PresupuestoTarea> buscarRubroenlistaPresupuestoTarea(Rubro rubro){
      List<PresupuestoTarea> pt= new ArrayList<PresupuestoTarea>();
      if(this.getPresupuestostareasitems()!=null){
        for(PresupuestoTarea ptn :this.getPresupuestostareasitems()){
            if(rubro.equals(ptn.getRubro())){
                pt.add(ptn);
            }
        }
      }
      return pt;
  }
  
  public BigDecimal sumaraportesOrganismoRubroenlistaPresupuestoTarea(Rubro rubro){
      BigDecimal sumagastoorganismorubro = BigDecimal.ZERO;
      List<PresupuestoTarea> pt= new ArrayList<PresupuestoTarea>();
      if(this.getPresupuestostareasitems()!=null){
        for(PresupuestoTarea ptn :this.getPresupuestostareasitems()){
            if(rubro.equals(ptn.getRubro())){
                sumagastoorganismorubro = sumagastoorganismorubro.add(ptn.getAporteorganismo());
            }
        }
      }
      return sumagastoorganismorubro;
  }
  
  public BigDecimal sumaraportesUniversidadRubroenlistaPresupuestoTarea(Rubro rubro){
      BigDecimal sumagastouniversidadrubro = BigDecimal.ZERO;
      List<PresupuestoTarea> pt= new ArrayList<PresupuestoTarea>();
      if(this.getPresupuestostareasitems()!=null){
        for(PresupuestoTarea ptn :this.getPresupuestostareasitems()){
            if(rubro.equals(ptn.getRubro())){
                sumagastouniversidadrubro = sumagastouniversidadrubro.add(ptn.getAporteuniversidad());
            }
        }
      }
      return sumagastouniversidadrubro;
  }
  
  public BigDecimal sumaraportesComitenteRubroenlistaPresupuestoTarea(Rubro rubro){
      BigDecimal sumagastocomitenterubro = BigDecimal.ZERO;
      List<PresupuestoTarea> pt= new ArrayList<PresupuestoTarea>();
      if(this.getPresupuestostareasitems()!=null){
        for(PresupuestoTarea ptn :this.getPresupuestostareasitems()){
            if(rubro.equals(ptn.getRubro())){
                sumagastocomitenterubro = sumagastocomitenterubro.add(ptn.getAportecomitente());
            }
        }
      }
      return sumagastocomitenterubro;
  }
  
  public BigDecimal sumaraportesTotalesRubroenlistaPresupuestoTarea(Rubro rubro){
      
      BigDecimal sumagastototalrubro = BigDecimal.ZERO;
      BigDecimal sumagastocomitenterubro = BigDecimal.ZERO;
      BigDecimal sumagastouniversidadrubro = BigDecimal.ZERO;
      BigDecimal sumagastoorganismorubro = BigDecimal.ZERO;
      List<PresupuestoTarea> pt= new ArrayList<PresupuestoTarea>();
      if(this.getPresupuestostareasitems()!=null){
        for(PresupuestoTarea ptn :this.getPresupuestostareasitems()){
            if(rubro.equals(ptn.getRubro())){
                sumagastocomitenterubro = sumagastocomitenterubro.add(ptn.getAportecomitente());
                sumagastouniversidadrubro = sumagastouniversidadrubro.add(ptn.getAporteuniversidad());
                sumagastoorganismorubro = sumagastoorganismorubro.add(ptn.getAporteorganismo());
            }
        }
        sumagastototalrubro =  (sumagastocomitenterubro.add(sumagastouniversidadrubro)).add(sumagastoorganismorubro);
      }
      return sumagastototalrubro;
  }

    public List<PresupuestoTarea> getPresupuestostareas() {
                
        return presupuestostareas;
    }

    public void setPresupuestostareas(List<PresupuestoTarea> presupuestostareas) {
        this.presupuestostareas = presupuestostareas;
    }
  
    public void armarPresupuestoGeneral(){
        
        presupuestostareas = new ArrayList<PresupuestoTarea>();
        FacesContext context = FacesContext.getCurrentInstance();
        EtapaController etapacontroller= (EtapaController) context.getApplication().evaluateExpressionGet(context, "#{etapaController}", EtapaController.class);
          
          for(Etapa e : etapacontroller.getEtapas()){
              for(Tarea t : e.getTareaList()){
                  for(PresupuestoTarea pt : t.getPresupuestoTareaList()){
                      System.out.println("-------desc----------------"+pt.getDescripcion());
                      System.out.println("---------comitente--------------"+pt.getAportecomitente());
                      System.out.println("---------universidad--------------"+pt.getAporteuniversidad());
                      System.out.println("---------organismo--------------"+pt.getAporteorganismo());
                      System.out.println("----------cantidaad-------------"+pt.getCantidad());
                      System.out.println("---------costounitario--------------"+pt.getCostounitario());
                      this.presupuestostareas.add(pt);
                       }
           
              }
           
          }
            this.sumarGastos();
             this.armarPresupuestoNodos();
    }
  
  

}
