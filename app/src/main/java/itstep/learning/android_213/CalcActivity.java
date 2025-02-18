package itstep.learning.android_213;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

public class CalcActivity extends AppCompatActivity {
    private final int maxDigits = 10;
    private TextView tvResult;
    private TextView tvExpression;
    private String zeroDigit;
    private String dotSymbol;
    private String minusSymbol;
    private boolean needClear = false;
    private boolean isErrorDisplayed = false;
    private String currentExpression = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);
        findViewById(R.id.calc_btn_0).setOnClickListener(this::onDigitClick);
        findViewById(R.id.calc_btn_1).setOnClickListener(this::onDigitClick);
        findViewById(R.id.calc_btn_2).setOnClickListener(this::onDigitClick);
        findViewById(R.id.calc_btn_3).setOnClickListener(this::onDigitClick);
        findViewById(R.id.calc_btn_4).setOnClickListener(this::onDigitClick);
        findViewById(R.id.calc_btn_5).setOnClickListener(this::onDigitClick);
        findViewById(R.id.calc_btn_6).setOnClickListener(this::onDigitClick);
        findViewById(R.id.calc_btn_7).setOnClickListener(this::onDigitClick);
        findViewById(R.id.calc_btn_8).setOnClickListener(this::onDigitClick);
        findViewById(R.id.calc_btn_9).setOnClickListener(this::onDigitClick);
        findViewById(R.id.calc_btn_C_func).setOnClickListener(this::onClearClick);
        findViewById(R.id.calc_btn_dot).setOnClickListener(this::onDotClick);
        findViewById(R.id.calc_btn_pm).setOnClickListener(this::onPmClick);
        findViewById(R.id.calc_btn_backspace).setOnClickListener(this::onBackspaceClick);
        findViewById(R.id.calc_btn_inv).setOnClickListener(this::onInverseClick);

        findViewById(R.id.calc_btn_add).setOnClickListener(this::onOperationClick);
        findViewById(R.id.calc_btn_sub).setOnClickListener(this::onOperationClick);
        findViewById(R.id.calc_btn_mul).setOnClickListener(this::onOperationClick);
        findViewById(R.id.calc_btn_div).setOnClickListener(this::onOperationClick);
        findViewById(R.id.calc_btn_sqrt).setOnClickListener(this::onSqrtClick);
        findViewById(R.id.calc_btn_sqr).setOnClickListener(this::onSquareClick);

        findViewById(R.id.calc_btn_equal).setOnClickListener(this::onEqualsClick);

        tvResult     = findViewById(R.id.calc_tv_result);
        tvExpression = findViewById(R.id.calc_tv_expression);
        zeroDigit    = getString(R.string.calc_btn_0);
        dotSymbol    = getString(R.string.calc_btn_dot);
        minusSymbol  = getString(R.string.calc_btn_sub);

        onClearClick(null);
    }

    // region Config change

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("tvResult", tvResult.getText());
        outState.putBoolean("needClear", needClear);
        outState.putBoolean("isErrorDisplayed", isErrorDisplayed);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tvResult.setText(savedInstanceState.getCharSequence("tvResult"));
        needClear = savedInstanceState.getBoolean("needClean");
        isErrorDisplayed = savedInstanceState.getBoolean("isErrorDisplayed");
    }

    //endregion

    private void onClearClick(View view){
        tvResult.setText(zeroDigit);
        tvExpression.setText("");
        isErrorDisplayed = false;
    }
    private void onInverseClick(View view){
        if(isErrorDisplayed) return;
        String resText=tvResult.getText().toString();
        tvExpression.setText(getString(R.string.calc_inv_tpl, resText));
        double x = parseResult(resText);
        if (x == 0) {
            resText = getString(R.string.calc_err_div_zero);
            isErrorDisplayed = true;
        }
        else{
            resText = toResult(1.0/x);
        }
        tvResult.setText(resText);
        needClear = true;
    }
    private void onDotClick(View view){
        String resText=tvResult.getText().toString();
        if(resText.contains(dotSymbol)){
            return;
        }
        resText +=dotSymbol;
        tvResult.setText(resText);
    }
    private void onBackspaceClick(View view){
        String resText=tvResult.getText().toString();
        int len = resText.length();
        if(len <= 1){
            resText= zeroDigit;
        }
        else{
            resText = resText.substring(0,len-1);
            if(resText.equals(minusSymbol)){
                resText = zeroDigit;
            }
        }
        tvResult.setText(resText);
    }
    private void onPmClick(View view){
        String resText=tvResult.getText().toString();
        if(resText.startsWith(minusSymbol)){
            resText = resText.substring(1);
        }
        else if(! resText.equals(zeroDigit)){
            resText =minusSymbol + resText;
        }
        tvResult.setText(resText);
    }
    private void onDigitClick(View view){
        String resText=tvResult.getText().toString();
        if(needClear || isErrorDisplayed){
            resText = "";
            tvExpression.setText("");

        }
        needClear = false;
        isErrorDisplayed = false;
        if(resText.equals(zeroDigit)){
            resText ="";
        }
        if(digitLength(resText)<maxDigits){
            resText += ((Button) view).getText();
        }
        tvResult.setText(resText);
    }
//    private int digitLength(String resText) {
//        int len = resText.length();
//        if(resText.contains(dotSymbol)){
//            len-=1;
//        }
//        if(resText.startsWith(minusSymbol)){
//            len-=1;
//        }
//        return len;
//    }
//    private String toResult(double x){
//        String res =  String.valueOf(x)
//                .replace(".", dotSymbol)
//                .replace("-",minusSymbol)
//                .replaceAll("0",zeroDigit) ;
//        if(digitLength(res)>maxDigits){
//            res = res.substring(0, maxDigits);
//        }
//        return res;
//    }
//    private double parseResult(String resText){
//        return Double.parseDouble(
//       resText
//               .replace(dotSymbol,".")
//               .replace(minusSymbol,"-")
//               .replaceAll(zeroDigit,"0") );
//
//    }
    private void onOperationClick(View view) {
        if (isErrorDisplayed) return;
        String operation = ((Button) view).getText().toString();
        String resText = tvResult.getText().toString();
        currentExpression += resText + " " + operation + " ";
        tvExpression.setText(currentExpression);
        tvResult.setText(zeroDigit);
    }

    private void onSqrtClick(View view) {
        if (isErrorDisplayed) return;
        String resText = tvResult.getText().toString();
        double number = parseResult(resText);
        if (number < 0) {
            resText = getString(R.string.calc_err_sqrt);
            isErrorDisplayed = true;
        } else {
            resText = toResult(Math.sqrt(number));
        }
        tvResult.setText(resText);
        needClear = true;
    }
    private void onSquareClick(View view) {
        if (isErrorDisplayed) return;
        String resText = tvResult.getText().toString();
        double number = parseResult(resText);
        resText = toResult(number * number);
        tvResult.setText(resText);
        needClear = true;
    }

    private void onEqualsClick(View view) {
        if (isErrorDisplayed) return;
        String resText = tvResult.getText().toString();
        currentExpression += resText;
        tvExpression.setText(currentExpression);
        try {
            double result = evaluateExpression(currentExpression);
            resText = toResult(result);
            tvResult.setText(resText);
            currentExpression = "";
        } catch (Exception e) {
            tvResult.setText(getString(R.string.calc_err_generic));
            isErrorDisplayed = true;
        }
    }

    private double evaluateExpression(String expression) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operations = new Stack<>();

        char[] tokens = expression.toCharArray();
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i] == ' ') {
                continue;
            }
            if (tokens[i] >= '0' && tokens[i] <= '9') {
                StringBuilder num = new StringBuilder();
                while (i < tokens.length && tokens[i] >= '0' && tokens[i] <= '9') {
                    num.append(tokens[i++]);
                }
                numbers.push(Double.parseDouble(num.toString()));
            } else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/') {
                while (!operations.isEmpty() && hasPrecedence(tokens[i], operations.peek())) {
                    numbers.push(operate(operations.pop(), numbers.pop(), numbers.pop()));
                }
                operations.push(tokens[i]);
            }
        }

        while (!operations.isEmpty()) {
            numbers.push(operate(operations.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false;
        }
        return true;
    }

    private double operate(char op, double b, double a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new ArithmeticException("Cannot divide by zero");
                }
                return a / b;
        }
        return 0;
    }

    private String toResult(double result) {
        if (result % 1 == 0) {
            return String.format("%d", (int) result);
        } else {
            return String.format("%s", result);
        }
    }

    private double parseResult(String resultText) {
        try {
            return Double.parseDouble(resultText);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int digitLength(String text) {
        return text.replace(".", "").replace("-", "").length();
    }

}