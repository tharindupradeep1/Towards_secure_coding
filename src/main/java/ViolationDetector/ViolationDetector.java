package ViolationDetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface ViolationDetector {

    String rule1Detection();

    Map<String, ArrayList<Integer>> lce = new HashMap<String, ArrayList<Integer>>();

}
