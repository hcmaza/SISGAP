/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.sisgap.controller;

import ar.edu.undec.sisgap.model.Presupuesto;
import ar.edu.undec.sisgap.model.PresupuestoRubroitem;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Hugo
 */
@Stateless
public class PresupuestoRubroitemFacade extends AbstractFacade<PresupuestoRubroitem> {
    @PersistenceContext(unitName = "ar.edu.undec_SYSGAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PresupuestoRubroitemFacade() {
        super(PresupuestoRubroitem.class);
    }
    
    public List<PresupuestoRubroitem> findByPresupuesto(Presupuesto pr){
        //System.out.println("iiiiiiiiiiiiiiiiiiii"+pr.getId());
        return em.createQuery("select pri from PresupuestoRubroitem pri where pri.presupuesto.id="+pr.getId(), PresupuestoRubroitem.class).getResultList();
    }
    
}
