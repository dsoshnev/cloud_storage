package common;

import java.util.ArrayList;
import java.util.List;

public class StorageCommand extends Command {
    private final List<String> params = new ArrayList<>();
    private final List<String> results = new ArrayList<>();

    public StorageCommand(CommandType type, String param1) {
        setType(type);
        this.params.add(param1);
        this.params.add("");
    }

    public String getParam1() {
        return params.isEmpty() ? "" : this.params.get(0);
    }

    public String getParam2() {
        return params.size() < 2 ? "" : this.params.get(1);
    }

    public long getLongParam2() {
        try {
            return Long.parseLong(getParam2());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    public void setParam1(String param1) {
        this.params.set(0, param1);
    }

    public void setParam2(String param2) {
        this.params.set(1, param2);
    }

    public void setLongParam2(long param2) {
        setParam2(String.valueOf(param2));
    }

    public List<String> getParams() {
        return params;
    }

    public String getResult1() {
        return results.isEmpty() ? "" : results.get(0);
    }

    public List<String> getResults() {
        return results;
    }
    public void setResult1(String result1) {
        this.results.clear();
        this.results.add(result1);
    }

    public void setResults(List<String> results) {
        this.results.clear();
        if (results != null) {
            this.results.addAll(results);
        }
    }

    public void setLongResult1(long result1) {
        setResult1(String.valueOf(result1));
    }

    public long getLongResult1() {
        try {
            return Long.parseLong(getResult1());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    @Override
    public String toString() {
        return "StorageCommand{" +
                "type='" + getType() + '\'' +
                "params='" + getParams() + '\'' +
                "results='" + getResults() + '\'' +
                '}';
    }


}
