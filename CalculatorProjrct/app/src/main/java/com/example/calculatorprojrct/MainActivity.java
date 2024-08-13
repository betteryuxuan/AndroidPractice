package com.example.calculatorprojrct;

import static java.lang.Character.isDigit;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.math.BigDecimal;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_result;
    private ScrollView scrollView;
    private TextView tvResult;
    private String expression = "";
    private String showText = "";
    private String history = "";
    private boolean lastInputWasResult = false;
    private Button btn_leftTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv_result = findViewById(R.id.tv_result);
        scrollView = findViewById(R.id.scroll_view);
        tvResult = findViewById(R.id.tv_result);
        btn_leftTop = findViewById(R.id.btn_leftTop);

        btn_leftTop.setStateListAnimator(null);

        // 启用TextView滚动功能
        tvResult.setMovementMethod(new ScrollingMovementMethod());

        findViewById(R.id.btn_leftbracket).setOnClickListener(this);
        findViewById(R.id.btn_rightbracket).setOnClickListener(this);
        findViewById(R.id.btn_plus).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_seven).setOnClickListener(this);
        findViewById(R.id.btn_eight).setOnClickListener(this);
        findViewById(R.id.btn_nine).setOnClickListener(this);
        findViewById(R.id.btn_minus).setOnClickListener(this);
        findViewById(R.id.btn_four).setOnClickListener(this);
        findViewById(R.id.btn_five).setOnClickListener(this);
        findViewById(R.id.btn_six).setOnClickListener(this);
        findViewById(R.id.btn_multiply).setOnClickListener(this);
        findViewById(R.id.btn_one).setOnClickListener(this);
        findViewById(R.id.btn_two).setOnClickListener(this);
        findViewById(R.id.btn_three).setOnClickListener(this);
        findViewById(R.id.btn_divide).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.btn_zero).setOnClickListener(this);
        findViewById(R.id.btn_point).setOnClickListener(this);
        findViewById(R.id.btn_equal).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // 判断Android版本是否大于等于API 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect vibrationEffect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE);
            vibrator.vibrate(vibrationEffect);
        } else {
            vibrator.vibrate(1);
        }


//        //滚动到底部
//        tvResult.post(() -> {
//            // 将TextView滚动到底部
//            int scrollAmount = tvResult.getLayout().getLineTop(tvResult.getLineCount()) - tvResult.getHeight();
//            if (scrollAmount > 0) {
//                tvResult.scrollTo(0, scrollAmount);
//            } else {
//                tvResult.scrollTo(0, 0);
//            }
//        });

        String input_text = ((TextView) v).getText().toString();
        if (v.getId() == R.id.btn_equal) {
            try {
                String res = calculateExpression(expression);
                // 保存结果和历史记录
                history += showText + " = " + res + "\n\n";
                refreshText(res);
//                refreshText(showText + "\n= " + res);
                // 将计算结果保存为新的表达式起点
                expression = res;
                showText = res;
                lastInputWasResult = true;
            } catch (Exception e) {
                refreshText("Error");
                expression = "";
                showText = "";
                lastInputWasResult = false;
            }
        } else if (v.getId() == R.id.btn_cancel) {
            if (expression.length() == 1) {
                expression = "";
                refreshText("0");
            }
            if (!expression.isEmpty()) {
                expression = expression.substring(0, expression.length() - 1);
                refreshText(showText.substring(0, showText.length() - 1));
            }
            lastInputWasResult = false;
        } else if (v.getId() == R.id.btn_clear) {
            if (expression.isEmpty()) {
                history = "";
                refreshText("0");
            } else {
                clear();
            }
            lastInputWasResult = false;
        } else {

            // 如果上一次输入是结果并且当前输入是数字，就重置表达式
            if (lastInputWasResult && (myIsDigit(input_text) || input_text.equals("."))) {
                expression = "";
                showText = "";
                lastInputWasResult = false;
            }

            // 判断输入是否为零，并且不是以小数点开头的情况下删除开头的零
            if (input_text.equals("0")) {
                if (expression.isEmpty() || expression.equals("0")) {
                    // 如果表达式为空或者已经是0，什么都不做
                    return;
                }
                if (expression.endsWith("0") && !expression.contains(".") && !isOperator(expression.charAt(expression.length() - 1)) && expression.length() == 1) {
                    // 如果表达式以0结尾且没有小数点或运算符，什么都不做
                    return;
                }
            }

            //输入不是零，默认是0，要把0先删了
            if (!input_text.equals("0") && showText.equals("0")) {
                showText = "";
            }

            //自动补0,小数点
            if (input_text.equals(".")) {
                //表达式为空，结尾是0，操作符，左括号，点击点后自动补0
                if (expression.isEmpty()
                        || isOperator(expression.charAt(expression.length() - 1))
                        || expression.endsWith("(")) {
                    expression = expression + "0" + input_text;
                    refreshText(showText + "0" + input_text);
                    return;
                }
            }

//            //自动补0,负数计算
//            if (input_text.equals("-")) {
//                if (expression.isEmpty() || expression.endsWith("(")
//                        || isOperator(expression.charAt(expression.length() - 1))) {
//                    expression = expression + "0" + input_text;
//                    refreshText(showText + "0" + input_text);
//                    return;
//                }
//            }

            //有加减乘除时点击其他的是更换
            if (isOperator(input_text.charAt(0))) {
                if (!expression.isEmpty() && isOperator(expression.charAt(expression.length() - 1))) {
                    expression = expression.substring(0, expression.length() - 1) + input_text;
                    refreshText(showText.substring(0, showText.length() - 1) + input_text);
                    return;
                }
                lastInputWasResult = false;
            }


            // 添加输入到表达式中
            expression += input_text;
            refreshText(showText + input_text);
        }
    }

    private boolean myIsDigit(String inputText) {
        return inputText.matches("\\d+");
    }

    // 刷新文本显示并更新历史记录
    private void refreshText(String text) {
        showText = text;
        tv_result.setText(history + showText);
    }


    private void clear() {
        refreshText("0");
        expression = "";
    }

    // 计算表达式
    private String calculateExpression(String expr) {
        // 存储数字的栈
        Stack<BigDecimal> numbers = new Stack<>();
        // 存储操作符的栈
        Stack<Character> operators = new Stack<>();
        // 用于临时存储正在形成的数字
        StringBuilder numberBuffer = new StringBuilder();

        // 遍历表达式中的每个字符
        for (int i = 0; i < expr.length(); i++) {
            char ch = expr.charAt(i);

            // 如果字符是数字或小数点
            if (isDigit(ch) || ch == '.') {
                numberBuffer.append(ch);
                // 如果字符是左括号
            } else if (ch == '(') {
                operators.push(ch);
                // 如果字符是右括号，就一直把操作符栈中的元素弹出，直到遇到左括号
            } else if (ch == ')') {
                // 如果当前有未完成的数字，将其压入数字栈
                if (numberBuffer.length() > 0) {
                    numbers.push(new BigDecimal(numberBuffer.toString()));
                    numberBuffer.setLength(0);
                }
                // 弹出操作符栈中的元素，直到遇到左括号
                while (!operators.isEmpty() && operators.peek() != '(') {
                    calculateTop(numbers, operators);
                }
                // 弹出左括号
                operators.pop();

                // 如果字符是操作符，判断操作符优先级
                // 当前操作符和栈顶操作符的优先级比较，当前操作符优先级小于等于栈顶操作符，就弹出两个数字进行计算，直到大于栈顶操作符优先级
            } else if (isOperator(ch)) {
                // 如果当前有未完成的数字，将其压入数字栈
                if (numberBuffer.length() > 0) {
                    numbers.push(new BigDecimal(numberBuffer.toString()));
                    numberBuffer.setLength(0);
                }
                // 特殊处理负号情况
                // 判断操作符前是否是负号
                if (ch == '-' && (i == 0 || isOperator(expr.charAt(i - 1)) || expr.charAt(i - 1) == '(')) {
                    numberBuffer.append(ch);
                } else {
                    // 弹出操作符栈中优先级不小于当前操作符的元素，并进行计算
                    while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(ch)) {
                        calculateTop(numbers, operators);
                    }
                    // 将当前操作符压入操作符栈
                    operators.push(ch);
                }
            }
        }

        // 如果还有未完成的数字，将其压入数字栈
        if (numberBuffer.length() > 0) {
            numbers.push(new BigDecimal(numberBuffer.toString()));
        }

        // 弹出所有剩余的操作符，并进行计算
        while (!operators.isEmpty()) {
            calculateTop(numbers, operators);
        }

        // 返回计算结果
        return numbers.pop().toString();
    }


    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '×' || ch == '÷';
    }

    private int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '×':
            case '÷':
                return 2;
        }
        return 0;
    }

    private void calculateTop(Stack<BigDecimal> numbers, Stack<Character> operators) {
        BigDecimal b = numbers.pop();
        BigDecimal a = numbers.pop();
        char operator = operators.pop();
        switch (operator) {
            case '+':
                numbers.push(a.add(b));
                break;
            case '-':
                numbers.push(a.subtract(b));
                break;
            case '×':
                numbers.push(a.multiply(b));
                break;
            case '÷':
                numbers.push(a.divide(b, 2, BigDecimal.ROUND_HALF_UP));
                break;
        }
    }

    public void sendHistory(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        intent.putExtra("history", history);
        startActivity(intent);
    }
}
