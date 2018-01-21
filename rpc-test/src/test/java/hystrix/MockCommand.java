package hystrix;

abstract class Command<R> {
    abstract R run() throws Exception;

    protected R getFallback() {
        return null;
    }
    R execute() {
        try {
            return this.run();
        } catch (Exception e) {
            return getFallback();
        }
    }
}

class HelloCommand extends Command<String> {
    private final String name;

    public HelloCommand(String name) {
        this.name = name;
    }

    @Override
    protected String getFallback() {
        return "error.......";
    }

    @Override
    String run() {
        int a = 10 / 0;
        return "Hello " + name;
    }
}

public class MockCommand {
    public static void main(String[] args) {
        String result = new HelloCommand("zhang").execute();
        System.out.println("result:" + result);
    }
}
