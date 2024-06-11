import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

@RequiredArgsConstructor
@Getter
@ToString
public class Client {
    private String name = "";
    private BufferedInputStream in;
    private BufferedOutputStream out;
    @NonNull
    private long socketPort;

    public Client setName(String name) {
        this.name = name;
        return this;
    }

    public Client setIn(BufferedInputStream in) {
        this.in = in;
        return this;
    }

    public Client setOut(BufferedOutputStream out) {
        this.out = out;
        return this;
    }
}
