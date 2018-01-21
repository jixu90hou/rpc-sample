package hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.junit.Test;
class Result{
    private Object data;
    private String errorMessage;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "Result{" + "data=" + data + ", errorMessage='" + errorMessage + '\'' + '}';
    }
}
public class CommandHelloWorld extends HystrixCommand<Result> {
    private final String name;

    public CommandHelloWorld(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.name = name;
    }

    @Override
    protected Result getFallback() {
        Result result=new Result();
        result.setErrorMessage("encounter error......");
        return result;
    }

    @Override
    protected Result run() throws Exception {
        Result result=new Result();
        result.setData("Hello "+this.name);
        int a=10/0;
        return result;
    }
    public static class UnitTest{
        @Test
        public void testSynchronous(){
            Result result=new CommandHelloWorld("World").execute();
            System.out.println(result);
           // assertEquals("Hello World",new CommandHelloWorld("World").execute());
        }
    }
}
