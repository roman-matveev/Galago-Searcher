// BSD License (http://www.galagosearch.org/license)
package org.galagosearch.tupleflow.execution;

/**
 * <p>This specifies how input data should be assigned to outputs
 * in a TupleFlow connection.</p> 
 * 
 * <p>If a connection output has the "Combined" assignment, that means
 * that each stage instance reading that output will recieve all of the
 * input data.  This is the right kind of assignment to use when writing
 * out a single index file, for instance, where you want all the data
 * that was genereated by lots of parsing stage instances.</p>
 * 
 * <p>The "Each" mode means that every output stage instance gets
 * exactly one of the output streams.  Notice that a connection can be 
 * hashed, so this doesn't necessarily imply a 1-to-1 mapping between
 * inputs and outputs.  This is what you want to use when you're
 * trying to distribute data broadly across a cluster of machines.</p>
 * 
 * <p>The "One" mode is not yet supported.  The "One" mode, when implemented,
 * will allow each different named output to recieve exactly one share of the
 * input data.  The main use case for this is to support building document-distributed
 * indexes.</p>
 * 
 * @see org.galagosearch.tupleflow.execution.Connection
 * @see org.galagosearch.tupleflow.execution.Job
 * @author trevor
 */
public enum ConnectionAssignmentType {
    Each {
        @Override
        public String toString() {
            return "each";
        }
    },
    One {
        @Override
        public String toString() {
            return "one";
        }
    },
    Combined {
        @Override
        public String toString() {
            return "combined";
        }
    }
};
