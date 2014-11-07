/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.sisgap.controller;

import ar.edu.undec.sisgap.model.Tarea;
import ar.edu.undec.sisgap.model.Tareaprogreso;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Hugo
 */
@Stateless
public class TareaprogresoFacade extends AbstractFacade<Tareaprogreso> {
    @PersistenceContext(unitName = "ar.edu.undec_SYSGAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TareaprogresoFacade() {
        super(Tareaprogreso.class);
    }
    
    public Tareaprogreso findLastDate(Tarea ta){
        Date fecha = new Date();
        
       try{
           fecha = em.createQuery("select max(t.fecha) from Tareaprogreso t where t.tareaid.id = " +ta.getId()+" group by tareaid", Date.class).getSingleResult();
       }catch(Exception e){
           System.out.println("No se pudo realizar la consulta findlastdate"+e);
           return null;
       }
        
       try{
           return em.createQuery("select t from Tareaprogreso t where t.fecha = :fec", Tareaprogreso.class).setParameter("fec", fecha).getSingleResult();
       }catch(Exception e){
           System.out.println("No se pudo realizar la consulta"+e);
           return null;
       }
         
    }
    
}
