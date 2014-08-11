/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ar.edu.undec.sisgap.model;

import java.util.Date;
import java.util.List;
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
public class EtapaTest {
    
    public EtapaTest() {
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
     * Test of getId method, of class Etapa.
     */
    @Test
    public void testGetId() {
        System.out.println("getId");
        Etapa instance = new Etapa();
        Integer expResult = null;
        Integer result = instance.getId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setId method, of class Etapa.
     */
    @Test
    public void testSetId() {
        System.out.println("setId");
        Integer id = null;
        Etapa instance = new Etapa();
        instance.setId(id);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEtapa method, of class Etapa.
     */
    @Test
    public void testGetEtapa() {
        System.out.println("getEtapa");
        Etapa instance = new Etapa();
        String expResult = "";
        String result = instance.getEtapa();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setEtapa method, of class Etapa.
     */
    @Test
    public void testSetEtapa() {
        System.out.println("setEtapa");
        String etapa = "";
        Etapa instance = new Etapa();
        instance.setEtapa(etapa);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFechainicio method, of class Etapa.
     */
    @Test
    public void testGetFechainicio() {
        System.out.println("getFechainicio");
        Etapa instance = new Etapa();
        Date expResult = null;
        Date result = instance.getFechainicio();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFechainicio method, of class Etapa.
     */
    @Test
    public void testSetFechainicio() {
        System.out.println("setFechainicio");
        Date fechainicio = null;
        Etapa instance = new Etapa();
        instance.setFechainicio(fechainicio);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFechafin method, of class Etapa.
     */
    @Test
    public void testGetFechafin() {
        System.out.println("getFechafin");
        Etapa instance = new Etapa();
        Date expResult = null;
        Date result = instance.getFechafin();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFechafin method, of class Etapa.
     */
    @Test
    public void testSetFechafin() {
        System.out.println("setFechafin");
        Date fechafin = null;
        Etapa instance = new Etapa();
        instance.setFechafin(fechafin);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDias method, of class Etapa.
     */
    @Test
    public void testGetDias() {
        System.out.println("getDias");
        Etapa instance = new Etapa();
        Integer expResult = null;
        Integer result = instance.getDias();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDias method, of class Etapa.
     */
    @Test
    public void testSetDias() {
        System.out.println("setDias");
        Integer dias = null;
        Etapa instance = new Etapa();
        instance.setDias(dias);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEstado method, of class Etapa.
     */
    @Test
    public void testGetEstado() {
        System.out.println("getEstado");
        Etapa instance = new Etapa();
        String expResult = "";
        String result = instance.getEstado();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setEstado method, of class Etapa.
     */
    @Test
    public void testSetEstado() {
        System.out.println("setEstado");
        String estado = "";
        Etapa instance = new Etapa();
        instance.setEstado(estado);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProyectoid method, of class Etapa.
     */
    @Test
    public void testGetProyectoid() {
        System.out.println("getProyectoid");
        Etapa instance = new Etapa();
        Proyecto expResult = null;
        Proyecto result = instance.getProyectoid();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setProyectoid method, of class Etapa.
     */
    @Test
    public void testSetProyectoid() {
        System.out.println("setProyectoid");
        Proyecto proyectoid = null;
        Etapa instance = new Etapa();
        instance.setProyectoid(proyectoid);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTareaList method, of class Etapa.
     */
    @Test
    public void testGetTareaList() {
        System.out.println("getTareaList");
        Etapa instance = new Etapa();
        List<Tarea> expResult = null;
        List<Tarea> result = instance.getTareaList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTareaList method, of class Etapa.
     */
    @Test
    public void testSetTareaList() {
        System.out.println("setTareaList");
        List<Tarea> tareaList = null;
        Etapa instance = new Etapa();
        instance.setTareaList(tareaList);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hashCode method, of class Etapa.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        Etapa instance = new Etapa();
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class Etapa.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object object = null;
        Etapa instance = new Etapa();
        boolean expResult = false;
        boolean result = instance.equals(object);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class Etapa.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Etapa instance = new Etapa();
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
