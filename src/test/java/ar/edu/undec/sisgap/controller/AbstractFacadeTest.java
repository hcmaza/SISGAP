/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.sisgap.controller;

import ar.edu.undec.sisgap.model.Agente;
import ar.edu.undec.sisgap.model.Usuario;
import java.util.List;
import javax.persistence.EntityManager;
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
public class AbstractFacadeTest {
    
    public AbstractFacadeTest() {
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
     * Test of getEntityManager method, of class AbstractFacade.
     */
    @Test
    public void testGetEntityManager() {
        System.out.println("getEntityManager");
        AbstractFacade instance = null;
        EntityManager expResult = null;
        EntityManager result = instance.getEntityManager();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of create method, of class AbstractFacade.
     */
    @Test
    public void testCreate() {
        System.out.println("create");
        Object entity = null;
        AbstractFacade instance = null;
        instance.create(entity);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createWithPersist method, of class AbstractFacade.
     */
    @Test
    public void testCreateWithPersist() {
        System.out.println("createWithPersist");
        Object entity = null;
        AbstractFacade instance = null;
        instance.createWithPersist(entity);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of edit method, of class AbstractFacade.
     */
    @Test
    public void testEdit() {
        System.out.println("edit");
        Object entity = null;
        AbstractFacade instance = null;
        instance.edit(entity);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of remove method, of class AbstractFacade.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        Object entity = null;
        AbstractFacade instance = null;
        instance.remove(entity);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of find method, of class AbstractFacade.
     */
    @Test
    public void testFind() {
        System.out.println("find");
        Object id = null;
        AbstractFacade instance = null;
        Object expResult = null;
        Object result = instance.find(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of find2 method, of class AbstractFacade.
     */
    @Test
    public void testFind2() {
        System.out.println("find2");
        String valor1 = "";
        String valor2 = "";
        AbstractFacade instance = null;
        Usuario expResult = null;
        Usuario result = instance.find2(valor1, valor2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findAgente method, of class AbstractFacade.
     */
    @Test
    public void testFindAgente() {
        System.out.println("findAgente");
        String nombreusuario = "";
        AbstractFacade instance = null;
        Agente expResult = null;
        Agente result = instance.findAgente(nombreusuario);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findUsuarioNombreEmail method, of class AbstractFacade.
     */
    @Test
    public void testFindUsuarioNombreEmail() {
        System.out.println("findUsuarioNombreEmail");
        String nombreusuario = "";
        AbstractFacade instance = null;
        Usuario expResult = null;
        Usuario result = instance.findUsuarioNombreEmail(nombreusuario);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findAll method, of class AbstractFacade.
     */
    @Test
    public void testFindAll() {
        System.out.println("findAll");
        AbstractFacade instance = null;
        List expResult = null;
        List result = instance.findAll();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findRange method, of class AbstractFacade.
     */
    @Test
    public void testFindRange() {
        System.out.println("findRange");
        int[] range = null;
        AbstractFacade instance = null;
        List expResult = null;
        List result = instance.findRange(range);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of count method, of class AbstractFacade.
     */
    @Test
    public void testCount() {
        System.out.println("count");
        AbstractFacade instance = null;
        int expResult = 0;
        int result = instance.count();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public class AbstractFacadeImpl extends AbstractFacade {

        public AbstractFacadeImpl() {
            super(null);
        }

        public EntityManager getEntityManager() {
            return null;
        }
    }
    
}
