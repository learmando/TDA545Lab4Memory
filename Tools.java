import java.util.Random;

/**
 *
 * @author Armand Ghaffarpour
 * 
 * This class takes an array of objects and shuffles the elements in a random
 * order with a Fisher-Yates shuffle algorithm.
 *
 */
public class Tools {

    public static void randomOrdering(Object[] o) {
    	//Fisher-Yates shuffle: Go through the whole array and swap the element
    	//we're currently on with a random other element in the array
        int index;
        Random random = new Random();
        for (int i=o.length-1; i>0; i--) {
            index = random.nextInt(i+1);
            if (index != i) {
            	//Swap the elements
            	Object a = o[index];
            	o[index] = o[i];
            	o[i] = a;
            }
        }
    }
}