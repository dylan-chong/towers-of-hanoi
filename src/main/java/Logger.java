/**
 * Created by Dylan on 18/05/17.
 */
public abstract class Logger {
    private int depth = 0;

    public void startLog() {
        privateLogLine("START");
    }

    public void endLog() {
        privateLogLine("END");
    }

    public void logStartParseNode(ParsableNode<?> node) {
        privateLogLine(getSpaces() + "{ (" + node.toString() + ")");
        depth++;
    }

    public void logEndParseNode(ParsableNode<?> node) {
        depth--;
        privateLogLine(getSpaces() + "} (" + node.toString() + ")");
    }

    protected abstract void privateLogLine(String line);

    private String getSpaces() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            stringBuilder.append("  ");
        }
        return stringBuilder.toString();
    }

    public static class SystemOutputLogger extends Logger {
        @Override
        protected void privateLogLine(String line) {
            System.out.println(line);
        }
    }

    public static class StubLogger extends Logger {
        @Override
        protected void privateLogLine(String line) {
            // Do nothing
        }
    }
}
