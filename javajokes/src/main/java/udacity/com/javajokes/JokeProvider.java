package udacity.com.javajokes;

public class JokeProvider {

    private static final String JOKE_TO_DISPLAY
            = "An Android app walks into a bar. Bartender asks, \"Can I get you a drink?\" " +
            "The app looks disappointed and says, \"That wasn't my intent.\"";

    /**
     * This function is to get a joke. The joke is from this source:
     * https://www.quora.com/What-are-some-of-the-best-Android-jokes
     * @return a joke as test
     */
    public String getJoke() {
        return JOKE_TO_DISPLAY;
    }
}
