package org.testpackage.example.coverage;

/**
 * @author rnorth
 */
public class RestServerDummyService {

    public String doSomething() {

        StringBuffer sb = new StringBuffer();

        try {
            for (int i=0; i < 42; i++) {
                if (i % 3 == 0) {
                    sb.append(i);
                } else if (i % 8 == 0) {
                    return sb.toString();
                }

                if (i == 80) {
                    sb.append("Will never reach here");
                }
            }
        } catch (Exception e) {
            sb.append("Unlikely to ever be hit!");
        }

        // Will never reach here anyway
        return sb.toString();
    }

    public void doSomethingElse() {
        if (1 + 1 == 3) {
            throw new IllegalStateException();
        } else {
            System.out.println("reached line in RestServerDummyService.doSomethingElse()");
        }
    }
}
