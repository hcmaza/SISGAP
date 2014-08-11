/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.sisgap.controller;

import ar.edu.undec.sisgap.model.Agente;
import ar.edu.undec.sisgap.model.Etapa;
import ar.edu.undec.sisgap.model.Proyecto;
import ar.edu.undec.sisgap.model.Usuario;
import java.util.List;
import javax.ejb.embeddable.EJBContainer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Hugo
 */
public class EtapaFacadeTest {
    
    public EtapaFacadeTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of create method, of class EtapaFacade.
     */
    @Test
    public void testCreate() throws Exception {
        System.out.println("create");
        Etapa entity = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EtapaFacade instance = (EtapaFacade)container.getContext().lookup("java:global/classes/EtapaFacade");
        instance.create(entity);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createWithPersist method, of class EtapaFacade.
     */
    @Test
    public void testCreateWithPersist() throws Exception {
        System.out.println("createWithPersist");
        Etapa entity = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EtapaFacade instance = (EtapaFacade)container.getContext().lookup("java:global/classes/EtapaFacade");
        instance.createWithPersist(entity);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of edit method, of class EtapaFacade.
     */
    @Test
    public void testEdit() throws Exception {
        System.out.println("edit");
        Etapa entity = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EtapaFacade instance = (EtapaFacade)container.getContext().lookup("java:global/classes/EtapaFacade");
        instance.edit(entity);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of remove method, of class EtapaFacade.
     */
    @Test
    public void testRemove() throws Exception {
        System.out.println("remove");
        Etapa entity = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EtapaFacade instance = (EtapaFacade)container.getContext().lookup("java:global/classes/EtapaFacade");
        instance.remove(entity);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of find method, of class EtapaFacade.
     */
    @Test
    public void testFind() throws Exception {
        System.out.println("find");
        Object id = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EtapaFacade instance = (EtapaFacade)container.getContext().lookup("java:global/classes/EtapaFacade");
        Etapa expResult = null;
        Etapa result = instance.find(id);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of find2 method, of class EtapaFacade.
     */
    @Test
    public void testFind2() throws Exception {
        System.out.println("find2");
        String valor1 = "";
        String valor2 = "";
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EtapaFacade instance = (EtapaFacade)container.getContext().lookup("java:global/classes/EtapaFacade");
        Usuario expResult = null;
        Usuario result = instance.find2(valor1, valor2);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findAgente method, of class EtapaFacade.
     */
    @Test
    public void testFindAgente() throws Exception {
        System.out.println("findAgente");
        String nombreusuario = "";
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EtapaFacade instance = (EtapaFacade)container.getContext().lookup("java:global/classes/EtapaFacade");
        Agente expResult = null;
        Agente result = instance.findAgente(nombreusuario);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findUsuarioNombreEmail method, of class EtapaFacade.
     */
    @Test
    public void testFindUsuarioNombreEmail() throws Exception {
        System.out.println("findUsuarioNombreEmail");
        String nombreusuario = "";
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EtapaFacade instance = (EtapaFacade)container.getContext().lookup("java:global/classes/EtapaFacade");
        Usuario expResult = null;
        Usuario result = instance.findUsuarioNombreEmail(nombreusuario);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findAll method, of class EtapaFacade.
     */
    @Test
    public void testFindAll() throws Exception {
        System.out.println("findAll");
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EtapaFacade instance = (EtapaFacade)container.getContext().lookup("java:global/classes/EtapaFacade");
        List<Etapa> expResult = null;
        List<Etapa> result = instance.findAll();
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findRange method, of class EtapaFacade.
     */
    @Test
    public void testFindRange() throws Exception {
        System.out.println("findRange");
        int[] range = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EtapaFacade instance = (EtapaFacade)container.getContext().lookup("java:global/classes/EtapaFacade");
        List<Etapa> expResult = null;
        List<Etapa> result = instance.findRange(range);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of count method, of class EtapaFacade.
     */
    @Test
    public void testCount() throws Exception {
        System.out.println("count");
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EtapaFacade instance = (EtapaFacade)container.getContext().lookup("java:global/classes/EtapaFacade");
        int expResult = 0;
        int result = instance.count();
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findByProyecto method, of class EtapaFacade.
     */
    @Test
    public void testFindByProyecto() throws Exception {
        System.out.println("findByProyecto");
        Proyecto p = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EtapaFacade instance = (EtapaFacade)container.getContext().lookup("java:global/classes/EtapaFacade");
        List<Etapa> expResult = null;
        List<Etapa> result = instance.findByProyecto(p);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of buscarEtapasProyecto method, of class EtapaFacade.
     */
    @Test
    public void testBuscarEtapasProyecto() throws Exception {
        System.out.println("buscarEtapasProyecto");
        int proyectoid = 0;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EtapaFacade instance = (EtapaFacade)container.getContext().lookup("java:global/classes/EtapaFacade");
        List<Etapa> expResult = null;
        List<Etapa> result = instance.buscarEtapasProyecto(proyectoid);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
