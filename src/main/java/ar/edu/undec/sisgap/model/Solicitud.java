/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.sisgap.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mpaez
 */
@Entity
@Table(name = "solicitud", catalog = "SISGAP", schema = "POSTGRES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Solicitud.findAll", query = "SELECT s FROM Solicitud s"),
    @NamedQuery(name = "Solicitud.findById", query = "SELECT s FROM Solicitud s WHERE s.id = :id"),
    @NamedQuery(name = "Solicitud.findByFechasolicitud", query = "SELECT s FROM Solicitud s WHERE s.fechasolicitud = :fechasolicitud"),
    @NamedQuery(name = "Solicitud.findByFechaaprobacion", query = "SELECT s FROM Solicitud s WHERE s.fechaaprobacion = :fechaaprobacion"),
    @NamedQuery(name = "Solicitud.findByFechaejecucion", query = "SELECT s FROM Solicitud s WHERE s.fechaejecucion = :fechaejecucion"),
    @NamedQuery(name = "Solicitud.findByObservacion", query = "SELECT s FROM Solicitud s WHERE s.observacion = :observacion")})
public class Solicitud implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "fechasolicitud")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechasolicitud;
    @Column(name = "fechaaprobacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaaprobacion;
    @Column(name = "fechaejecucion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaejecucion;
    @Size(max = 100)
    @Column(name = "observacion", length = 100)
    private String observacion;
    @JoinColumn(name = "tiposolicitudid", referencedColumnName = "id")
    @ManyToOne
    private Tiposolicitud tiposolicitudid;
    @JoinColumn(name = "estadosolicitudid", referencedColumnName = "id")
    @ManyToOne
    private Estadosolicitud estadosolicitudid;
    
    @OneToMany(mappedBy = "solicitudid")
    private List<SolicitudItem> listaSolicitudItems;

    public Solicitud() {
    }

    public Solicitud(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechasolicitud() {
        return fechasolicitud;
    }

    public void setFechasolicitud(Date fechasolicitud) {
        this.fechasolicitud = fechasolicitud;
    }

    public Date getFechaaprobacion() {
        return fechaaprobacion;
    }

    public void setFechaaprobacion(Date fechaaprobacion) {
        this.fechaaprobacion = fechaaprobacion;
    }

    public Date getFechaejecucion() {
        return fechaejecucion;
    }

    public void setFechaejecucion(Date fechaejecucion) {
        this.fechaejecucion = fechaejecucion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Tiposolicitud getTiposolicitudid() {
        return tiposolicitudid;
    }

    public void setTiposolicitudid(Tiposolicitud tiposolicitudid) {
        this.tiposolicitudid = tiposolicitudid;
    }

    public Estadosolicitud getEstadosolicitudid() {
        return estadosolicitudid;
    }

    public void setEstadosolicitudid(Estadosolicitud estadosolicitudid) {
        this.estadosolicitudid = estadosolicitudid;
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
        if (!(object instanceof Solicitud)) {
            return false;
        }
        Solicitud other = (Solicitud) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.edu.undec.sisgap.model.Solicitud[ id=" + id + " ]";
    }
    
}
