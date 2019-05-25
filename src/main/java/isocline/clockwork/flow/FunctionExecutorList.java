package isocline.clockwork.flow;

import java.util.ArrayList;
import java.util.List;



public class FunctionExecutorList {

    private int index = -1;

    private List<FunctionExecutor> functionExecutorList = new ArrayList();


    FunctionExecutorList() {

    }


    public int size() {
        return this.functionExecutorList.size();
    }

    public void add(FunctionExecutor functionExecutor) {

        this.functionExecutorList.add(functionExecutor);
    }

    public FunctionExecutor get(int index) {
        return this.functionExecutorList.get(index);
    }

    public synchronized Wrapper getNextstepFunctionExecutor() {
        index++;

        if (index >= this.functionExecutorList.size()) {
            index = -1;
            return null;
        }

        FunctionExecutor functionExecutor = this.functionExecutorList.get(index);
        boolean hasNext = hasNext();

        return new Wrapper(functionExecutor, hasNext);

    }


    private  boolean hasNext() {
        int size = this.functionExecutorList.size();


        if (size>0 && index < size) {
            return true;
        }

        this.index = -1;

        return false;
    }


    final static public class Wrapper {
        private FunctionExecutor functionExecutor;
        private boolean hasNext = false;

        Wrapper(FunctionExecutor functionExecutor, boolean hasNext) {
            this.functionExecutor = functionExecutor;
            this.hasNext = hasNext;
        }

        public FunctionExecutor getFunctionExecutor() {
            return this.functionExecutor;
        }

        public boolean hasNext() {
            return this.hasNext;
        }


    }
}
