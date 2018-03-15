package org.ludus.api;

import org.ludus.backend.algebra.Matrix;
import org.ludus.backend.algebra.Value;
import org.ludus.backend.por.DependencyGraph;

import java.util.*;


/**
 * Given a matrix mapping, calculate a matrix dependency graph.
 *
 * @author Bram van der Sanden
 */
public class MatrixDependencies {
	
	/**
	 * Compute a dependency graph with a dependency between matrices if they commute.
	 * Note that this involves checking all matrix pairs for commutativity.
	 */
	public static DependencyGraph getDependencyGraphMatrixCommute(Map<String, Matrix> mapping) {
        // Add nodes to the dependency graph.
        DependencyGraph depGraph = new DependencyGraph();
        for (String matrixName : mapping.keySet()) {
        	depGraph.addNode(matrixName);
        }

        // Add dependencies.
        for (String matrixA : mapping.keySet()) {
            for (String matrixB : mapping.keySet()) {
            	// Get internal matrix representations.
            	Matrix mA = mapping.get(matrixA);
            	Matrix mB = mapping.get(matrixB);
            	
            	Matrix mAB = mA.multiply(mB);
            	Matrix mBA = mB.multiply(mA);
            	
            	// If matrices do not commute, add the dependency to the graph.
            	if (mAB.compareTo(mBA) != 0) {
            		depGraph.addDependency(matrixA, matrixB);
            	}
            }
        }
        
		return depGraph;
	}

	/**
	 * Compute a dependency graph with a dependency between matrices if they share used resources.
     * We assume that the resource position in each matrix is the same.
	 */
    public static DependencyGraph getDependencyGraphResourceSharing(Map<String, Matrix> mapping) {
        // Compute used resources for each matrix.
        Map<String, Set<Integer>> matrix2usedResourcesMap = new HashMap<>();
        for (String matrixName : mapping.keySet()) {
            matrix2usedResourcesMap.put(matrixName, getUsedResources(mapping.get(matrixName)));
        }

        // Add nodes to the dependency graph.
        DependencyGraph depGraph = new DependencyGraph();
        for (String matrixName : mapping.keySet()) {
        	depGraph.addNode(matrixName);
        }

        // Add dependencies.
        for (String thisMatrix : mapping.keySet()) {
            for (String otherMatrix : mapping.keySet()) {
                if (!Collections.disjoint(matrix2usedResourcesMap.get(thisMatrix),matrix2usedResourcesMap.get(otherMatrix))) {
                	depGraph.addDependency(thisMatrix, otherMatrix);
                }
            }
        }
        return depGraph;
    }

    /**
     * Return the set of row ids that are involved the timing behavior.
     * More specifically, the corresponding row and column vectors are not both identity vectors.
     *
     * @param m input matrix
     * @return set of row ids involved in the timing behavior
     */
    private static Set<Integer> getUsedResources(Matrix m) {
        Set<Integer> resources = new HashSet<>();
        for (int resId = 0; resId < m.getRows(); resId++) {
            if (isResourceUsed(m, resId)) {
                // Resource plays a role in the timing behavior. Add it to the set of used resources.
                resources.add(resId);
            }
        }
        return resources;
    }

    private static boolean isResourceUsed(Matrix m, Integer resId) {
        return !(isIdentityRow(m, resId) && isIdentityColumn(m, resId));
    }

    private static boolean isIdentityRow(Matrix m, Integer row) {
        for (int column = 0; column < m.getColumns(); column++) {
            Value v = m.get(row,column);
            if (row == column) {
                // Value should be zero.
                if (!v.equals(new Value(0.0d))) {
                    return false;
                }
            } else {
                // Value should be -Infinity.
                if (!v.equals(Double.NEGATIVE_INFINITY)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isIdentityColumn(Matrix m, Integer column) {
        for (int row = 0; row < m.getRows(); row++) {
            Value v = m.get(row,column);
            if (row == column) {
                // Value should be zero.
                if (!v.equals(new Value(0.0d))) {
                    return false;
                }
            } else {
                // Value should be -Infinity.
                if (!v.equals(Double.NEGATIVE_INFINITY)) {
                    return false;
                }
            }
        }
        return true;
    }

}
