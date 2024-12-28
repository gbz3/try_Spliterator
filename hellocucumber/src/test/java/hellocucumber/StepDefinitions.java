package hellocucumber;

import io.cucumber.java.en.*;
import com.example.*;

import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class StepDefinitions {

    private String input;
    private String output;

    @Given("可変長レコード is {string}")
    public void anExampleScenario(String input) {
        this.input = input;
    }

    @When("all step definitions are implemented")
    public void allStepDefinitionsAreImplemented() {
        StreamSupport.stream(new TaggedRecordSpliterator(input), false)
                .forEach(rec -> {
                    System.out.println("Tag: " + rec.getTag() + " [" + rec.getValue() + "]");
                });
    }

    @Then("変換結果 is {string}")
    public void theScenarioPasses(String output) {
    }

}
