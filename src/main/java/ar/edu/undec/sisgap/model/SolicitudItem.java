/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.sisgap.model;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mpaez
 */
@Entity
@Table(name = "solicitud_item", catalog = "SISGAP", schema = "POSTGRES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SolicitudItem.findAll", query = "SELECT s FROM SolicitudItem s"),
    @NamedQuery(name = "SolicitudItem.findById", query = "SELECT s FROM SolicitudItem s WHERE s.id = :id"),
    @NamedQuery(name = "SolicitudItem.findByAprobado", query = "SELECT s FROM SolicitudItem s WHERE s.aprobado = :aprobado"),
    @NamedQuery(name = "SolicitudItem.findByObservacion", query = "SELECT s FROM SolicitudItem s WHERE s.observacion = :observacion"),
    @NamedQuery(name = "SolicitudItem.findByMonto", query = "SELECT s FROM SolicitudItem s WHERE s.monto = :monto")})
public class SolicitudItem implements Serializable {
    @JoinColumn(name = "presupuesto_tareaid", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private PresupuestoTarea presupuestoTareaid;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "aprobado")
    private Boolean aprobado;
    @Size(max = 200)
    @Column(name = "observacion", length = 200)
    private String observacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "monto", precision = 10, scale = 2)
    private BigDecimal monto;
    @JoinColumn(name = "solicitudid", referencedColumnName = "id")
    @ManyToOne
    private Solicitud solicitudid;

    public SolicitudItem() {
    }

    public SolicitudItem(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getAprobado() {
        return aprobado;
    }

    public void setAprobado(Boolean aprobado) {
        this.aprobado = aprobado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Solicitud getSolicitudid() {
        return solicitudid;
    }

    public void setSolicitudid(Solicitud solicitudid) {
        this.solicitudid = solicitudid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SolicitudItem)) {
            return false;
        }
        SolicitudItem other = (SolicitudItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.edu.undec.sisgap.model.SolicitudItem[ id=" + id + " ]";
    }

    public PresupuestoTarea getPresupuestoTareaid() {
        return presupuestoTareaid;
    }

    public void setPresupuestoTareaid(PresupuestoTarea presupuestoTareaid) {
        this.presupuestoTareaid = presupuestoTareaid;
    }
    
}
