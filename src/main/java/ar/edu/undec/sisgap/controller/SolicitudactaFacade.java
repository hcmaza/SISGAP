/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.undec.sisgap.controller;

import ar.edu.undec.sisgap.model.Solicitudacta;
import java.util.ArrayList;
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
public class SolicitudactaFacade extends AbstractFacade<Solicitudacta> {

    @PersistenceContext(unitName = "ar.edu.undec_SYSGAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SolicitudactaFacade() {
        super(Solicitudacta.class);
    }

    //    select p from Person p where
//    p.email in(
//        select q.email
//        from Person q
//        group by q.email having count(q.email)>1)
//    order by p.email, p.id
    
    public List<Solicitudacta> obtenerPorProyecto(int proyectoid) {
        //Query consulta = em.createQuery("SELECT sa FROM Solicitudacta sa WHERE sa.listaSolicitudes.presupuestotarea.tarea.etapaid.proyectoid.id = :proyectoid", Solicitudacta.class);

        try {
            Query consulta = em.createQuery("SELECT sa FROM Solicitudacta sa WHERE sa.id IN (SELECT s.solicitudactaid.id FROM Solicitud s WHERE s.presupuestotarea.tarea.etapaid.proyectoid.id = :proyectoid)", Solicitudacta.class);
            consulta.setParameter("proyectoid", proyectoid);
            return consulta.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("SolicitudactaFacade - obtenerPorProyecto");
            return new ArrayList<Solicitudacta>();
        }

    }

}
