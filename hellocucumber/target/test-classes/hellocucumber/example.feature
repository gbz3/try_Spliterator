Feature: 可変長レコードを変換する

  Scenario Outline: The example
    Given 可変長レコード is "<input>"
    When all step definitions are implemented
    Then 変換結果 is "<output>"

  Examples:
    | input | output |
    | A003AAAB002BB | AAA:BB |
