package hellocucumber;

import io.cucumber.java.en.*;

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
    }

    @Then("変換結果 is {string}")
    public void theScenarioPasses(String output) {
    }

}
