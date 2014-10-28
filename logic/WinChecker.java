package logic;
import java.util.ArrayList;
import java.util.Hashtable;

import polar.game.*;

/*
 * A class for translating game map information into first-order logic
 * and resolving to determine a winner.
 */
public class WinChecker {
	
	private final int RADIANS = 12;
	private final int LAYERS = 4;
	static WinChecker win;
	
	public static void main(String args[]) {
		win = new WinChecker();
		Substitution thing = win.new Substitution();
		thing.addSubstitution("x", "f(g)");
		thing.addSubstitution("y", "A");
		thing.addSubstitution("x", "oberlin");
		System.out.println(thing);
	}
	
	public static void resolve() {

	}

	public static Substitution unify(String x, String y, Substitution theta) {
		if (theta.getSub().size() == 0) { // if theta is failure
			return win.new Substitution(); //failure
		} else if (x == y) {
			return theta;
		} else if (isVariable(x)) {
			return unifyVar(x,y,theta);
		} else if (isVariable(y)) {
			return unifyVar(y,x,theta);
		} else if ( isCompound(x) && isCompound(y) ) {
			return unify(getArgs(x), getArgs(y), unify(operator(x), operator(y), theta));
		} else if (isList(x) && isList(y)) {
				return unify(rest(x), rest(y), unify(first(x), first(y), theta));
		} else return win.new Substitution();
	}
	
	public static Substitution unifyVar(String var, String x, Substitution theta) {
		if (in(var, theta) != "") { //var is already in the substitution
			return unify(in(var, theta), x, theta);
		} else if (in(x, theta) != "") {
			return unify(var, in(x, theta), theta);
		} else if (occurCheck(var, x)) {
			return win.new Substitution(); //failure
		}
		else return theta.addSubstitution(var,x);
	}
	
	private static boolean occurCheck(String var, String x) {
		//check whether the var occurs inside the term x
		return false;
	}
	
	public static String in(String s, Substitution theta) {
		return "";
	}
	
	public static String operator(Object x) {
		return "";
	}
	
	public static String rest(Object x) {
		return "";
	}
	
	public static String first(Object x) {
		return "";
	}
	
	public static String getArgs(Object x) {
		return "";
	}
	
	public static boolean isList(Object x) {
		return false;
	}
	
	public static boolean isVariable(Object x) {
		return false;
	}
	
	public static boolean isCompound(Object x) {
		return false;
	}

    public static boolean win(Character player, Map map) {
    	return false;
    }

    public static boolean marked(Map map, PolarCoordinate loc, Character Player) {
        return false;
    }

    public static boolean inLine(PolarCoordinate p1, PolarCoordinate p2, 
        PolarCoordinate p3, PolarCoordinate p4 ) {
        return false;
    }
    
    /*
     * A data structure that represents a substitution string.
     * Each entry of -sub- contains a variable substitution.
     */
    public class Substitution {
    	
    	//Substitution format: < (original, sub),...., (original, sub) >
    	private ArrayList<SubElement> sub;
    	
    	public Substitution() {
    		sub = new ArrayList<SubElement>();
    	}
    	
    	public Substitution addSubstitution(String original, String substitute) {
    		SubElement term = new SubElement(original, substitute);
    		sub.add(term);
    		return this;
    	}
    	
    	public ArrayList<SubElement> getSub() {
    		return sub;
    	}
    	
    	public String toString() {
    		String out = "{";
    		for (SubElement s : sub) {
    			out += s + ", ";
    		}
    		return out.substring(0, out.length() - 2) + "}";
    	}
    	
    }
    
    /*
     * A single variable substitution in a Hashtable
     */
    public class SubElement {
    	
    	public Hashtable<String, String> element;
    	
    	public SubElement(String original, String substitute) {
    		element = new Hashtable<String, String>();
    		element.put("Original", original);
    		element.put("Substitute", substitute);
    	}
    	
    	public String toString() {
    		return element.get("Original") + "/" + element.get("Substitute");
    	}
    	
    }

}
