package isocline.clockwork;

public interface WorkSession {


    void onError(Throwable error, Work work);


    void beforeExcute(Work work);


    void afterExcute(Work work);


    void setAttribute(String key, Object object);

    Object getAttriute(String key);

    Object removeAttribute(String key);

}
