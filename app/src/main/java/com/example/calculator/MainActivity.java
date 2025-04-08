package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvResult, tvExpression;
    private Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    private Button btnDot, btnAdd, btnSub, btnMul, btnDiv, btnMod, btnDel, btnEq, btnBack;

    private StringBuilder currentExpression = new StringBuilder();
    private boolean lastInputIsOperator = false;
    private boolean justEvaluated = false;

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

        tvResult = findViewById(R.id.tvResult);
        tvExpression = findViewById(R.id.tvExpression);

        btn0 = findViewById(R.id.btn0);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);
        btnDot = findViewById(R.id.btnDot);
        btnAdd = findViewById(R.id.btnAdd);
        btnSub = findViewById(R.id.btnSub);
        btnMul = findViewById(R.id.btnMul);
        btnDiv = findViewById(R.id.btnDiv);
        btnMod = findViewById(R.id.btnMod);
        btnDel = findViewById(R.id.btnDel);
        btnEq = findViewById(R.id.btnEq);
        btnBack = findViewById(R.id.btnBack);

        Button[] buttons = {
                btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9,
                btnDot, btnAdd, btnSub, btnMul, btnDiv, btnMod, btnDel, btnEq, btnBack
        };

        for (Button btn : buttons) {
            btn.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        Button btn = (Button) v;

        if (btn == btnDel) {
            currentExpression.setLength(0);
            tvExpression.setText("");
            tvResult.setText("0");
            justEvaluated = false;
            lastInputIsOperator = false;
            return;
        }

        if (btn == btnBack) {
            if (currentExpression.length() > 0) {
                currentExpression.deleteCharAt(currentExpression.length() - 1);
                tvResult.setText(currentExpression.toString());
                if (currentExpression.length() > 0) {
                    char lastChar = currentExpression.charAt(currentExpression.length() - 1);
                    lastInputIsOperator = isOperator(lastChar);
                } else {
                    lastInputIsOperator = false;
                }
            }
            return;
        }

        if (btn == btnEq) {
            try {
                String expr = currentExpression.toString().replace("×", "*").replace("÷", "/");
                double result = eval(expr);
                tvExpression.setText(currentExpression.toString());
                tvResult.setText(formatResult(result));
                currentExpression.setLength(0);
                currentExpression.append(result);
                justEvaluated = true;
            } catch (Exception e) {
                tvResult.setText("Math Error");
            }
            return;
        }

        if (btn == btnAdd || btn == btnSub || btn == btnMul || btn == btnDiv || btn == btnMod) {
            if (!lastInputIsOperator && currentExpression.length() > 0) {
                currentExpression.append(getOperatorSymbol(btn));
                lastInputIsOperator = true;
                justEvaluated = false;
            }
        } else {
            if (justEvaluated) {
                currentExpression.setLength(0);
                tvExpression.setText("");
                justEvaluated = false;
            }
            currentExpression.append(btn.getText().toString());
            lastInputIsOperator = false;
        }

        tvResult.setText(currentExpression.toString());
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '×' || c == '÷' || c == '%';
    }

    private String getOperatorSymbol(Button btn) {
        if (btn == btnAdd) return "+";
        if (btn == btnSub) return "-";
        if (btn == btnMul) return "×";
        if (btn == btnDiv) return "÷";
        if (btn == btnMod) return "%";
        return "";
    }

    private String formatResult(double result) {
        if (result == (long) result) {
            return String.format("%d", (long) result); // Hiện số nguyên
        } else {
            String text = String.valueOf(result);
            if (text.length() > 15) {
                text = text.substring(0, 15); // Giới hạn tối đa 12 ký tự để vừa màn hình
            }
            return text;
        }
    }

    private double eval(String expr) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expr.length()) ? expr.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expr.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else if (eat('%')) x %= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expr.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                return x;
            }
        }.parse();
    }
}
