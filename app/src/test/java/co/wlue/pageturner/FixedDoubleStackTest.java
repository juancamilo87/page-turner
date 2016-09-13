package co.wlue.pageturner;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;

import co.wlue.pageturner.utils.FixedDoubleStack;

/**
 * Created by JuanCamilo on 9/13/2016.
 */
public class FixedDoubleStackTest {

    @Test
    public void testInitialize() {
        FixedDoubleStack<Double> testStack = new FixedDoubleStack<>(5, Double.class);
        assertNotEquals("Stack is null.",null,testStack);
    }

    @Test
    public void testEverything() {
        FixedDoubleStack<Double> testStack = new FixedDoubleStack<>(5, Double.class);
        assertNotEquals("Stack is null.",null,testStack);
        assertEquals(5,testStack.size());
        assertEquals("Number of elements before adding element is incorrect",0,testStack.elements());
        assertEquals("Error inserting values", true, testStack.add(2.0,3.0));
        assertEquals("Number of elements after adding element is incorrect",1,testStack.elements());
        assertEquals(2.0, testStack.getTop()[0],0.0);
        assertEquals(2.0, testStack.getStackOne()[0],0.0);
        assertEquals(3.0, testStack.getTop()[1],0.0);
        assertEquals(2.0, testStack.getBottom()[0],0.0);
        assertEquals(3.0, testStack.getBottom()[1],0.0);
        assertEquals(3.0, testStack.getStackTwo()[0],0.0);
        assertEquals("Error inserting values", true, testStack.add(1.9,2.9));
        assertEquals("Number of elements after adding element is correct",2,testStack.elements());
        assertEquals(2.0, testStack.getTop()[0],0.0);
        assertEquals(3.0, testStack.getTop()[1],0.0);
        assertEquals(1.9, testStack.getBottom()[0],0.0);
        assertEquals(2.9, testStack.getBottom()[1],0.0);
        assertEquals(1.9, testStack.getStackOne()[1],0.0);
        assertEquals(2.9, testStack.getStackTwo()[1],0.0);
        assertEquals(2.0, testStack.getStackOne()[0],0.0);
        assertEquals(3.0, testStack.getStackTwo()[0],0.0);
        assertEquals(5,testStack.size());
        assertEquals("Error inserting values", true, testStack.add(2.1,3.2));
        assertEquals("Number of elements after adding element is correct",3,testStack.elements());
        assertEquals(2.1, testStack.getTop()[0],0.0);
        assertEquals(3.2, testStack.getTop()[1],0.0);
        assertEquals(1.9, testStack.getBottom()[0],0.0);
        assertEquals(2.9, testStack.getBottom()[1],0.0);
        assertEquals("Error inserting values", true, testStack.add(2.01,3.1));
        assertEquals("Number of elements after adding element is correct",4,testStack.elements());
        assertEquals(2.1, testStack.getTop()[0],0.0);
        assertEquals(3.2, testStack.getTop()[1],0.0);
        assertEquals(1.9, testStack.getBottom()[0],0.0);
        assertEquals(2.9, testStack.getBottom()[1],0.0);
        assertEquals("Error inserting values", true, testStack.add(1.0,3.0));
        assertEquals("Number of elements after adding element is correct",5,testStack.elements());
        assertEquals(2.1, testStack.getTop()[0],0.0);
        assertEquals(3.2, testStack.getTop()[1],0.0);
        assertEquals(1.0, testStack.getBottom()[0],0.0);
        assertEquals(3.0, testStack.getBottom()[1],0.0);
        assertEquals("Error inserting values", true, testStack.add(5.0,3.0));
        assertEquals("Number of elements after adding element is correct",5,testStack.elements());
        assertEquals(5.0, testStack.getTop()[0],0.0);
        assertEquals(3.0, testStack.getTop()[1],0.0);
        assertEquals(1.9, testStack.getBottom()[0],0.0);
        assertEquals(2.9, testStack.getBottom()[1],0.0);
        assertEquals("Error inserting values", false, testStack.add(0.9,3.0));
        assertEquals("Number of elements after adding element is correct",5,testStack.elements());
        assertEquals(5.0, testStack.getTop()[0],0.0);
        assertEquals(3.0, testStack.getTop()[1],0.0);
        assertEquals(1.9, testStack.getBottom()[0],0.0);
        assertEquals(2.9, testStack.getBottom()[1],0.0);
        assertEquals(5,testStack.size());


        assertEquals(1.9, testStack.getStackOne()[4],0.0);
        assertEquals(2.9, testStack.getStackTwo()[4],0.0);
        assertEquals(2.0, testStack.getStackOne()[3],0.0);
        assertEquals(3.0, testStack.getStackTwo()[3],0.0);
        assertEquals(2.01, testStack.getStackOne()[2],0.0);
        assertEquals(3.1, testStack.getStackTwo()[2],0.0);
        assertEquals(2.1, testStack.getStackOne()[1],0.0);
        assertEquals(3.2, testStack.getStackTwo()[1],0.0);
        assertEquals(5.0, testStack.getStackOne()[0],0.0);
        assertEquals(3.0, testStack.getStackTwo()[0],0.0);
    }

    @Test
    public void distanceOneValueTest() {
        Double[] value = new Double[1];
        Double[] value2 = new Double[1];

        value[0] = 1.0;
        value2[0] = 1.0;

        assertEquals(0.0,MainActivity.distance(value,value2),0.0);

        value[0] = 10.0;
        value2[0] = 5.0;

        assertEquals(true,MainActivity.distance(value,value2)>0);

        value[0] = 5.0;
        value2[0] = 10.0;

        assertEquals(false,MainActivity.distance(value,value2)>0);
    }

    @Test
    public void distanceSeveralPositiveValuesTest() {
        Double[] value = new Double[3];
        Double[] value2 = new Double[3];

        value[0] = 1.0;
        value[1] = 5.0;
        value[2] = 10.0;
        value2[0] = 1.0;
        value2[1] = 5.0;
        value2[2] = 10.0;

        assertEquals(0.0,MainActivity.distance(value,value2),0.0);

        value[0] = 10.0;
        value2[0] = 5.0;

        assertEquals(true,MainActivity.distance(value,value2)>0);

        value[0] = 5.0;
        value2[0] = 10.0;
        assertEquals(false,MainActivity.distance(value,value2)>0);

        value[0] = 10.0;
        value[1] = 200.0;
        value2[0] = 5.0;
        value2[1] = 210.0;

        System.out.println(MainActivity.distance(value,value2));

        assertEquals(true,MainActivity.distance(value,value2)>0);

        ArrayList<Double[]> frequenciesWithOvertones = MainActivity.getAllFrequencies((double) 440, 3);

        Double[] values = new Double[1];
        values[0] = 440.0;

        System.out.println(MainActivity.binarySearch(frequenciesWithOvertones,0,frequenciesWithOvertones.size()-1,value));
    }

}
