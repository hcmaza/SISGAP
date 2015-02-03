/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.sisgap.controller;

import ar.edu.undec.sisgap.model.SolicitudItem;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author mPY
 */
@Stateless
public class SolicitudItemFacade extends AbstractFacade<SolicitudItem> {
    @PersistenceContext(unitName = "ar.edu.undec_SYSGAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SolicitudItemFacade() {
        super(SolicitudItem.class);
    }
    
    public List<SolicitudItem> obtenerPorProyecto(int proyectoid){
        Query consulta = em.createQuery("SELECT si FROM SolicitudItem si WHERE si.presupuestoTareaid.tarea.etapaid.proyectoid.id = :proyectoid", SolicitudItem.class);
        consulta.setParameter("proyectoid", proyectoid);
        return consulta.getResultList();
    }
    
    public List<SolicitudItem> obtenerPorSolicitud(int solicitudid){
        Query consulta = em.createQuery("SELECT si FROM SolicitudItem si WHERE si.solicitudid.id = :solicitudid", SolicitudItem.class);
        consulta.setParameter("solicitudid", solicitudid);
        return consulta.getResultList();
    }
    
}
