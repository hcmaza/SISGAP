/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.sisgap.controller;

import ar.edu.undec.sisgap.model.Proyecto;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Hugo
 */
@Stateless
public class ProyectoFacade extends AbstractFacade<Proyecto> {
    @PersistenceContext(unitName = "ar.edu.undec_SYSGAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProyectoFacade() {
        super(Proyecto.class);
    }
    
    public List<Proyecto> buscarProyectoAgente(int agenteid){
        
        List<Proyecto> proyecto;
       try{
           return em.createQuery("select p from Proyecto p join p.agenteid a where a.id = :id ", Proyecto.class).setParameter("id", agenteid).getResultList();
       }catch(Exception e){
           System.out.println("No se pudo realizar la consulta"+e);
           return null;
       }
        
         
    } 
    
    public List<Proyecto> buscarProyectoEstado(int estado){
        
        List<Proyecto> proyecto;
       try{
           return em.createQuery("select p from Proyecto p join p.agenteid a where p.estadoproyectoid.id = :estado", Proyecto.class).setParameter("estado", estado).getResultList();
       }catch(Exception e){
           System.out.println("No se pudo realizar la consulta"+e);
           return null;
       }
        
         
    } 
    
    public List<Proyecto> buscarProyectosAgente(boolean isproyecto,int agenteid){
        
        String consulta="select p from Proyecto p join p.agenteid a where p.agenteid.id= "+agenteid;
        if(isproyecto){
             consulta+=" and p.estadoproyectoid.id=2 or p.estadoproyectoid.id=5 or p.estadoproyectoid.id=6 or p.estadoproyectoid.id=8 or p.estadoproyectoid.id=9 ";
         }else{
            consulta+=" and p.estadoproyectoid.id=1 or p.estadoproyectoid.id=3 or p.estadoproyectoid.id=4 or p.estadoproyectoid.id=7 ";
        }
        List<Proyecto> proyecto;
        
       try{
           return em.createQuery(consulta , Proyecto.class).getResultList();
       }catch(Exception e){
           System.out.println("No se pudo realizar la consulta"+e);
           return null;
       }
        
         
    } 
    
    
    
    public long buscarProyectosEvaluar(int estado){
        
        List<Proyecto> proyecto;
       try{
           return (long)em.createQuery("select count(p) from Proyecto p where p.estadoproyectoid.id = :id ", Long.class).setParameter("id", estado).getSingleResult();
       }catch(Exception e){
           System.out.println("No se pudo realizar la consulta"+e);
           return 0;
       }
        
         
    } 
    
    
}
