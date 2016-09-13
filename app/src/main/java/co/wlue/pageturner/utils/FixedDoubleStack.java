package co.wlue.pageturner.utils;

import java.lang.reflect.Array;

/**
 * Created by researcher on 05/09/16.
 */
public class FixedDoubleStack<T extends Comparable<T>> {


    private T[] stackOne;
    private T[] stackTwo;
    private Class<T> classType;
    private int size;
    private int bottom;

    public FixedDoubleStack(int size, Class<T> tClass) {
        classType = tClass;
        this.stackOne = (T[]) Array.newInstance(classType, size);
        this.stackTwo = (T[]) Array.newInstance(classType, size);
        this.bottom = -1;
        this.size = size;
    }


    public boolean add(T objOne, T objTwo) {
        boolean reallyInserted = false;
        boolean inserted = false;
        if(elements()==0) {
            reallyInserted = insert(objOne, objTwo, 0);
        }
        else {
            for(int i = elements() - 1; i>=0&&!inserted; i--) {
                if(get(i)[0].compareTo(objOne) > 0) {
                    reallyInserted = insert(objOne, objTwo,i+1);
                    inserted = true;
                }
            }
            if(!inserted) {
                reallyInserted = insert(objOne, objTwo, 0);
            }
        }
        return reallyInserted;
    }

    public T[] getBottom()
    {
        if (bottom < 0) return null;

        T[] obj = (T[]) Array.newInstance(classType,2);
        obj[0] = (T) stackOne[bottom];
        obj[1] = (T) stackTwo[bottom];
        return obj;
    }

    public T[] getTop()
    {
        T[] obj = (T[]) Array.newInstance(classType,2);
        obj[0] = (T) stackOne[0];
        obj[1] = (T) stackTwo[0];
        return obj;
    }

    private boolean insert(T objOne, T objTwo, int position)
    {
        if(bottom < size-1) {
            bottom++;
            shiftDown(position);
            stackOne[position] = objOne;
            stackTwo[position] = objTwo;
            return true;
        }
        else if((position <= size-1)) {
            pop();
            bottom++;
            shiftDown(position);
            stackOne[position] = objOne;
            stackTwo[position] = objTwo;
            return true;
        }
        return false;

    }

    private void shiftDown(int position) {
        for(int i = bottom;i>position; i--) {
            stackOne[i] = stackOne[i - 1];
            stackTwo[i] = stackTwo[i - 1];
        }
    }

    private void pop() {
        stackOne[bottom] = null;
        stackTwo[bottom] = null;
        bottom--;
    }

    public int size()
    {
        return size;
    }

    public int elements()
    {
        return bottom + 1;
    }

    private T[] get(int i) {
        T[] obj = (T[]) Array.newInstance(classType,2);
        obj[0] = (T) stackOne[i];
        obj[1] = (T) stackTwo[i];
        return obj;
    }


    public T[] getStackOne() {
        return stackOne;
    }


    public T[] getStackTwo() {
        return stackTwo;
    }
}
