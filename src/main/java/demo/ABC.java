package demo;

import org.springframework.context.annotation.PropertySource;

/**
 * @author liuzhenyuan
 * @version Last modified 15/4/24
 * @since 0.0.1
 */
@PropertySource("a")
public class ABC {
    private long id;
    private long cd;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCd() {
        return cd;
    }

    public void setCd(long cd) {
        this.cd = cd;
    }
}
