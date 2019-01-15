package isocline.clockwork;

import java.util.HashMap;
import java.util.Map;

public class BasicWorkSession implements WorkSession {

    private Map<String, Object> map = new HashMap<String, Object>();


    @Override
    public void setAttribute(String key, Object object) {

        this.map.put(key, object);
    }

    @Override
    public Object getAttriute(String key) {
        return this.map.get(key);
    }

    @Override
    public Object removeAttribute(String key) {
        return this.map.remove(key);
    }

    @Override
    public void onError(Work work, Throwable error) {

    }

    @Override
    public void beforeExcute(Work work) {

    }

    @Override
    public void afterExcute(Work work) {

    }
}
