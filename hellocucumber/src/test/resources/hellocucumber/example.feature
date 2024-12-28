Feature: 可変長レコードを変換する

  Scenario Outline: The example
    Given 可変長レコード is "<input>"
    When all step definitions are implemented
    Then 変換結果 is "<output>"

  Examples:
    | input | output |
    | AA003AAABB002BB | AAA:BB |

  Scenario Outline: ファイル名指定
    Given 入力データは "<input>"
    When 入力データを読み込む
    Then 期待値は "<expected>"

  Examples:
    | input | expected |
    | A001_input01 | A001_expected01 |
