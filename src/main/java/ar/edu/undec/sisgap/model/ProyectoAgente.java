/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.sisgap.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mPY
 */
@Entity
@Table(name = "proyecto_agente", schema = "ap")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProyectoAgente.findAll", query = "SELECT p FROM ProyectoAgente p"),
    @NamedQuery(name = "ProyectoAgente.findByAgenteid", query = "SELECT p FROM ProyectoAgente p WHERE p.proyectoAgentePK.agenteid = :agenteid"),
    @NamedQuery(name = "ProyectoAgente.findByProyectoid", query = "SELECT p FROM ProyectoAgente p WHERE p.proyectoAgentePK.proyectoid = :proyectoid"),
    @NamedQuery(name = "ProyectoAgente.findByFuncionproyecto", query = "SELECT p FROM ProyectoAgente p WHERE p.funcionproyecto = :funcionproyecto")})
public class ProyectoAgente implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ProyectoAgentePK proyectoAgentePK;
    @Size(max = 35)
    @Column(name = "funcionproyecto")
    private String funcionproyecto;
    @JoinColumn(name = "proyectoid", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Proyecto proyecto;
    @JoinColumn(name = "agenteid", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Agente agente;
    @Column(name = "horasdisponibles")
    private Integer horasdisponibles;
    @Column(name = "horasdedicadas")
    private Integer horasdedicadas;
    @Column(name = "consultorexterno")
    private Boolean consultorexterno;
    @Transient
    private List<TareaAgente> tareasagentes = new ArrayList<TareaAgente>();
    @Column(name = "honorario")
    private BigDecimal honorario;
    
    public ProyectoAgente() {
    }

    public ProyectoAgente(ProyectoAgentePK proyectoAgentePK) {
        this.proyectoAgentePK = proyectoAgentePK;
    }

    public ProyectoAgente(int agenteid, int proyectoid) {
        this.proyectoAgentePK = new ProyectoAgentePK(agenteid, proyectoid);
    }

    public ProyectoAgentePK getProyectoAgentePK() {
        return proyectoAgentePK;
    }

    public void setProyectoAgentePK(ProyectoAgentePK proyectoAgentePK) {
        this.proyectoAgentePK = proyectoAgentePK;
    }

    public String getFuncionproyecto() {
        return funcionproyecto;
    }

    public void setFuncionproyecto(String funcionproyecto) {
        this.funcionproyecto = funcionproyecto;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public Agente getAgente() {
        return agente;
    }

    public void setAgente(Agente agente) {
        this.agente = agente;
    }

    public Integer getHorasdisponibles() {
        return horasdisponibles;
    }

    public void setHorasdisponibles(Integer horasdisponibles) {
        this.horasdisponibles = horasdisponibles;
    }

    public Integer getHorasdedicadas() {
        return horasdedicadas;
    }

    public void setHorasdedicadas(Integer horasdedicadas) {
        this.horasdedicadas = horasdedicadas;
    }

    public Boolean getConsultorexterno() {
        return consultorexterno;
    }

    public void setConsultorexterno(Boolean consultorexterno) {
        this.consultorexterno = consultorexterno;
    }

    public List<TareaAgente> getTareasagentes() {
        return tareasagentes;
    }

    public void setTareasagentes(List<TareaAgente> tareasagentes) {
        this.tareasagentes = tareasagentes;
    }

    public BigDecimal getHonorario() {
        return honorario;
    }

    public void setHonorario(BigDecimal honorario) {
        this.honorario = honorario;
    }
    
    
    
    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (proyectoAgentePK != null ? proyectoAgentePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProyectoAgente)) {
            return false;
        }
        ProyectoAgente other = (ProyectoAgente) object;
        if ((this.proyectoAgentePK == null && other.proyectoAgentePK != null) || (this.proyectoAgentePK != null && !this.proyectoAgentePK.equals(other.proyectoAgentePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.edu.undec.sisgap.model.ProyectoAgente[ proyectoAgentePK=" + proyectoAgentePK + " ]";
    }
    
    // metodos para hacer el resumen de etapas tareas por agente
    
    public int contarEtapas(){
        
        Integer etapaold= Integer.valueOf(0);
        String setapaold = null;
        int contador =0;
        for(TareaAgente ta:this.tareasagentes){
            
           
            if(!tareasagentes.isEmpty()){
                if(ta.getTareaid().getEtapaid().getEtapa().equals(setapaold)){

                }else{
                    contador++;
                    setapaold = ta.getTareaid().getEtapaid().getEtapa();
                    
                }
            }
        }
        return contador;
    }
    
    public int contarTareas(){
        Integer tareaold= Integer.valueOf(0);
        String stareaold = null;
        int contador =0;
        for(TareaAgente ta:this.tareasagentes){
            if(!tareasagentes.isEmpty()){
                if(ta.getTareaid().getTarea().equals(stareaold)){

                }else{
                    contador++;
                    tareaold = ta.getTareaid().getId();
                }
            }
        }
        return contador;
    }
    
    public int contarDiasTareas(){
        Integer tareaold= Integer.valueOf(0);
        String setapaold = null;
        int contador =0;
        for(TareaAgente ta:this.tareasagentes){
            if(!tareasagentes.isEmpty()){
                contador+= ta.getTareaid().getDias();
            }
        }
        return contador;
    }
    
    public int contarHoras(){
        Integer horas= Integer.valueOf(0);
        Agente sagenteold = null;
        
        for(TareaAgente ta:this.tareasagentes){
            
            if(!tareasagentes.isEmpty()){
                System.out.println("Agente  --"+ta.getAgenteid());
                if(ta.getAgenteid().equals(sagenteold)){
                    System.out.println("Agente horas"+ta.getHorasdedicadas());
                    if(this.consultorexterno==null | !this.consultorexterno){
                        System.out.println("Agente horas 2"+ta.getHorasdedicadas());
                        if(ta.getHorasdedicadas()==null){
                            horas+= 0;
                        }else{
                            horas +=  ta.getHorasdedicadas();
                        }
                    }
                }else{
                     if(ta.getHorasdedicadas()==null){
                            horas+= 0;
                        }else{
                            horas +=  ta.getHorasdedicadas();
                        }
                    sagenteold = ta.getAgenteid();
                }
            }
        }
        return horas;
    }
    
}
