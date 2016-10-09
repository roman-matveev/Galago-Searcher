// BSD License (http://www.galagosearch.org/license)

package org.galagosearch.tupleflow.execution;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 *
 * @author trevor
 */
public class StageInstanceDescription implements Serializable {

    /// A stage object (probably genereated by the JobConstructor parser)
    Stage stage;
    /// The index of this job (between 0 and n-1 if there are n instances)
    int index;

    /// A map from stage input names to data pipe structures (note that the input to a pipe connects to a stage output)
    private Map<String, PipeOutput> readers;
    private /// A map from stage input names to data pipe structures (note that the input to a pipe connects to a stage output)
    Map<String, PipeInput> writers;

    /// The URL of the Master server for this job.
    String masterURL;

    public StageInstanceDescription(
            Stage stage, int index,
            Map<String, PipeInput> pipeInputs,
            Map<String, PipeOutput> pipeOutputs,
            String masterURL) {
        this.stage = stage;
        this.index = index;
        this.writers = pipeInputs;
        this.readers = pipeOutputs;
        this.masterURL = masterURL;
    }

    /**
     * @return the readers
     */
    public Map<String, PipeOutput> getReaders() {
        return readers;
    }

    /**
     * @return the writers
     */
    public Map<String, PipeInput> getWriters() {
        return writers;
    }

    public static class PipeInput implements Serializable {
        private int index;
        private DataPipe pipe;

        public PipeInput(DataPipe pipe, int index) {
            this.index = index;
            this.pipe = pipe;
        }

        public String[] getFileNames() {
            return pipe.getInputFileNames(index);
        }

        public DataPipe getPipe() {
            return pipe;
        }
    }

    public static class PipeOutput implements Serializable {
        private int start;
        private int stop;
        private DataPipe pipe;

        public PipeOutput(DataPipe pipe, int index) {
            this(pipe, index, index + 1);
        }

        public PipeOutput(DataPipe pipe, int start, int stop) {
            this.start = start;
            this.stop = stop;
            this.pipe = pipe;
        }

        public String[] getFileNames() {
            ArrayList<String[]> allFilenames = new ArrayList();
            int totalNames = 0;

            for (int i = start; i < stop; i++) {
                String[] batch = pipe.getOutputFileNames(i);
                totalNames += batch.length;
                allFilenames.add(batch);
            }

            String[] result = new String[totalNames];
            int spot = 0;

            for (String[] batch : allFilenames) {
                System.arraycopy(batch, 0, result, spot, batch.length);
                spot += batch.length;
            }

            return result;
        }

        public DataPipe getPipe() {
            return pipe;
        }
    }

    public String getName() {
        return stage.name;
    }

    public int getIndex() {
        return index;
    }

    public String getPath() {
        return getName() + File.separator + getIndex();
    }

    public String getMasterURL() {
        return masterURL;
    }

    public boolean writerExists(String specification, String className, String[] order) {
        PipeInput input = getWriters().get(specification);
        if (input == null) return false;
        return input.getPipe().getClassName().equals(className) &&
               Arrays.equals(input.getPipe().getOrder(), order);
    }

    public boolean readerExists(String specification, String className, String[] order) {
        PipeOutput output = getReaders().get(specification);
        if (output == null) return false;
        return output.getPipe().getClassName().equals(className) &&
               Arrays.equals(output.getPipe().getOrder(), order);
    }

    public Stage getStage() {
        return stage;
    }
}
