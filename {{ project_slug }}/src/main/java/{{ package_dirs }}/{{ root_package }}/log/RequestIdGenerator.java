package {{ base_package }}.{{ root_package }}.log;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

@Component("requestIdGenerator")
public class RequestIdGenerator implements Supplier<String> {
  private static final char[] CHARS_ARRAY = "abcdef0123456789".toCharArray();

  @Override
  public String get() {
    ThreadLocalRandom random = ThreadLocalRandom.current();
    char[] chars = new char[16];
    for (int i = 0; i < CHARS_ARRAY.length; i++) {
      chars[i] = CHARS_ARRAY[random.nextInt(CHARS_ARRAY.length)];
    }
    return new String(chars);
  }
}
