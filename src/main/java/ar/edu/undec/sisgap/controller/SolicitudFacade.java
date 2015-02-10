/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.sisgap.controller;

import ar.edu.undec.sisgap.model.Solicitud;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author mpaez
 */
@Stateless
public class SolicitudFacade extends AbstractFacade<Solicitud> {

    @PersistenceContext(unitName = "ar.edu.undec_SYSGAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SolicitudFacade() {
        super(Solicitud.class);
    }

//    select p from Person p where
//    p.email in(
//        select q.email
//        from Person q
//        group by q.email having count(q.email)>1)
//    order by p.email, p.id

    /**
     * Obtiene una lista de solicitud por proyecto (solicitud no tiene relacion con proyecto, solo a traves de PresupuestoTarea)
     * 
     * @param proyectoid
     * @return 
     */
    public List<Solicitud> obtenerPorProyecto(int proyectoid) {
        Query consulta = em.createQuery("SELECT s FROM Solicitud s WHERE s.presupuestotarea.tarea.etapaid.proyectoid.id = :proyectoid", Solicitud.class);
        consulta.setParameter("proyectoid", proyectoid);
        return consulta.getResultList();
    }
    
    public List<Solicitud> obtenerIniciadosPorProyecto(int proyectoid){
        Query consulta = em.createQuery("SELECT s FROM Solicitud s WHERE s.estadosolicitudid.id = 1 AND s.presupuestotarea.tarea.etapaid.proyectoid.id = :proyectoid", Solicitud.class);
        consulta.setParameter("proyectoid", proyectoid);
        return consulta.getResultList();
    }
    
    public Solicitud obtenerPorPresupuestoTarea(int presupuestotareaid){
        Query consulta = em.createQuery("SELECT s FROM Solicitud s WHERE s.presupuestotarea.id = :presupuestotareaid", Solicitud.class);
        consulta.setParameter("presupuestotareaid", presupuestotareaid);
        return (Solicitud) consulta.getSingleResult();
    }
}
