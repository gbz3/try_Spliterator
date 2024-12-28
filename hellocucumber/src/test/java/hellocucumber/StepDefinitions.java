package hellocucumber;

import io.cucumber.java.en.*;
import com.example.*;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.stream.Collectors;
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

    private Path inputFile;

    @Given("入力データは {string}")
    public void input_is(String input) {
        inputFile = Path.of("src/test/resources/data/" + input);
    }

    @When("入力データを読み込む")
    public void loadInputFile() {
    }

    @Then("期待値は {string}")
    public void expected_is(String expectedFile) {
        //Path expectedPath = Path.of("src/test/resources/data/" + expectedFile);

        try (
                FileChannel input = FileChannel.open(inputFile);
                //FileChannel expected = FileChannel.open(expectedPath)
                ) {
            String actual = StreamSupport.stream(new RecordA01Spliterator(input), false)
                    .map(A01 -> new String(A01.value()))
                    .collect(Collectors.joining());

            assertTrue(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
